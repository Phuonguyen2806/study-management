package model;

import model.entity.*;
import model.observer.FocusSessionEvent;
import model.observer.IFocusViewObserver;
import model.observer.ISessionHistoryObserver;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FocusSessionManager {
    // 1. Observer lưu Data (Service)
    private List<ISessionHistoryObserver> historyObservers = new ArrayList<>();
    // 2. Observer cập nhật Giao diện (View)
    private List<IFocusViewObserver> viewObservers = new ArrayList<>();

    private FocusStatus currentState = FocusStatus.IDLE;
    private SessionType currentSessionType = SessionType.FOCUS;
    private int sessionCount = 0;

    private final int TIME_FOCUS = 25 * 60;
    private final int TIME_SHORT_BREAK = 5 * 60;
    private final int TIME_LONG_BREAK = 15 * 60;

    private int timeLeft;
    private Timer timer;
    private Task currentTask;
    private Date sessionStartTime;

    public FocusSessionManager() {
        this.timeLeft = TIME_FOCUS;
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                notifyTimeChanged(); // Báo cho View biết thời gian đã giảm 1 giây
            } else {
                handleTimeOver();
            }
        });
    }

    // ==========================================
    // QUẢN LÝ OBSERVER: VIEW (Giao diện)
    // ==========================================
    public void addViewObserver(IFocusViewObserver o) {
        viewObservers.add(o);
        // Gửi trạng thái ngay lần đầu đăng ký để View vẽ giao diện ban đầu
        o.updateState(currentState, currentSessionType, currentTask);
        o.updateTime(timeLeft);
    }

    public void removeViewObserver(IFocusViewObserver o) {
        viewObservers.remove(o);
    }

    private void notifyTimeChanged() {
        for (IFocusViewObserver o : viewObservers) {
            o.updateTime(timeLeft);
        }
    }

    private void notifyStateChanged() {
        for (IFocusViewObserver o : viewObservers) {
            o.updateState(currentState, currentSessionType, currentTask);
        }
        notifyTimeChanged(); // Cập nhật lại số trên đồng hồ luôn
    }

    // ==========================================
    // QUẢN LÝ OBSERVER: HISTORY (Lưu lịch sử)
    // ==========================================
    public void addHistoryObserver(ISessionHistoryObserver o) {
        historyObservers.add(o);
    }

    public void removeHistoryObserver(ISessionHistoryObserver o) {
        historyObservers.remove(o);
    }

    private void notifyHistoryObservers(FocusSessionEvent event) {
        for (ISessionHistoryObserver o : historyObservers) {
            o.onSessionCompleted(event);
        }
    }


    // ==========================================
    // CÁC HÀM XỬ LÝ LOGIC NGHIỆP VỤ
    // =======================================
    // Nạp Task vào nhưng chưa chạy đồng hồ
    public void setTask(Task task, int estimatedSessions) {
        this.currentTask = task;
        this.currentTask.setEstPomo(estimatedSessions);
        notifyStateChanged();
    }

    // Đổi chế độ thủ công khi đang dừng
    public void setSessionType(SessionType type) {
        if (currentState == FocusStatus.IDLE) {
            this.currentSessionType = type;
            this.timeLeft = getPlannedTime(type);
            notifyStateChanged();
        }
    }

    // Nhấn nút Bắt đầu
    public void startSession() {
        if (this.currentTask != null && this.currentTask.getStatus() == TaskStatus.PENDING) {
            this.currentTask.setStatus(TaskStatus.IN_PROGRESS);
        }

        this.currentState = FocusStatus.RUNNING;
        this.sessionStartTime = new Date();
        timer.start();
        notifyStateChanged();
    }

    public void pauseTimer() {
        if (currentState == FocusStatus.RUNNING) {
            timer.stop();
            currentState = FocusStatus.PAUSED;
            notifyStateChanged();
        }
    }

    public void resumeTimer() {
        if (currentState == FocusStatus.PAUSED) {
            currentState = FocusStatus.RUNNING;
            timer.start();
            notifyStateChanged();
        }
    }

    public void stopSessionConfirm() {
        currentState = FocusStatus.CONFIRMING_STOP;
        notifyStateChanged();
    }

    public void stopSession(boolean isConfirmed) {
        if (isConfirmed) {
            timer.stop();
            int duration = getPlannedTime(currentSessionType) - timeLeft;
            StudySession sessionRecord = createStudySessionRecord(duration, SessionStatus.STOPPED_EARLY);
            notifyHistoryObservers(new FocusSessionEvent(currentTask, sessionRecord));
            resetToIdle();
        } else {
            currentState = FocusStatus.PAUSED;
            notifyStateChanged();
        }
    }

    private void handleTimeOver() {
        timer.stop();
        int duration = getPlannedTime(currentSessionType);

        if (currentSessionType == SessionType.FOCUS) {
            sessionCount++;
            if (currentTask != null) currentTask.incrementCompPomo();

            StudySession sessionRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyHistoryObservers(new FocusSessionEvent(currentTask, sessionRecord));

            currentState = FocusStatus.RUNNING;
            if (sessionCount >= 4) {
                currentSessionType = SessionType.LONG_BREAK;
                timeLeft = TIME_LONG_BREAK;
                sessionCount = 0;
            } else {
                currentSessionType = SessionType.SHORT_BREAK;
                timeLeft = TIME_SHORT_BREAK;
            }
            this.sessionStartTime = new Date();
            timer.start();
        } else {
            StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyHistoryObservers(new FocusSessionEvent(null, breakRecord));
            resetToIdle();
        }
        notifyStateChanged();
    }

    public void skipBreak() {
        timer.stop();
        int duration = getPlannedTime(currentSessionType) - timeLeft;
        StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.CANCELED);
        notifyHistoryObservers(new FocusSessionEvent(null, breakRecord));
        resetToIdle();
    }

    // Xóa Task khi đã hoàn thành xong
    public void clearTask() {
        this.currentTask = null;
        notifyStateChanged();
    }

    private StudySession createStudySessionRecord(int duration, SessionStatus status) {
        Date endTime = new Date();
        int userId = (currentTask != null) ? currentTask.getUserId() : 1;
        Integer taskId = (currentTask != null) ? currentTask.getTaskId() : null;
        int sessionId = (int) (System.currentTimeMillis() % 100000);
        return new StudySession(sessionId, userId, taskId, sessionStartTime, endTime, duration, currentSessionType, status);
    }

    private void resetToIdle() {
        currentState = FocusStatus.IDLE;
        // Tự động chuyển qua Focus sau khi nghỉ xong
        currentSessionType = SessionType.FOCUS;
        timeLeft = TIME_FOCUS;
        notifyStateChanged();
    }

    private int getPlannedTime(SessionType type) {
        if (type == SessionType.FOCUS) return TIME_FOCUS;
        if (type == SessionType.SHORT_BREAK) return TIME_SHORT_BREAK;
        return TIME_LONG_BREAK;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public FocusStatus getCurrentState() {
        return currentState;
    }

    public SessionType getCurrentSessionType() {
        return currentSessionType;
    }

    public Task getCurrentTask() {
        return currentTask;
    }
}