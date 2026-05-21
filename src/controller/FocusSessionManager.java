package controller;

import model.entity.*;
import model.observer.FocusSessionEvent;
import model.observer.Observer;
import model.observer.Subject;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FocusSessionManager implements Subject {
    private List<Observer> observers = new ArrayList<>();

    // SỬA LỖI: Đổi SessionStatus thành FocusState cho biến quản lý trạng thái đồng hồ
    private FocusState currentState = FocusState.IDLE;
    private SessionType currentSessionType = SessionType.FOCUS;
    private int sessionCount = 0;

    // Thời gian chuẩn
    private final int TIME_FOCUS = 25 * 60;
    private final int TIME_SHORT_BREAK = 5 * 60;
    private final int TIME_LONG_BREAK = 15 * 60;

    private int timeLeft;
    private Timer timer;
    private Task currentTask;
    private Date sessionStartTime;

    private Runnable onTickCallback;
    private Runnable onStateChangedCallback;

    public FocusSessionManager() {
        this.timeLeft = TIME_FOCUS;
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                if (onTickCallback != null) onTickCallback.run();
            } else {
                handleTimeOver();
            }
        });
    }

    public void setCallbacks(Runnable onTick, Runnable onStateChanged) {
        this.onTickCallback = onTick;
        this.onStateChangedCallback = onStateChanged;
    }

    public void startSession(Task task, int estimatedSessions) {
        this.currentTask = task;
        this.currentTask.setEstPomo(estimatedSessions);
        if (this.currentTask.getStatus() == TaskStatus.PENDING) {
            this.currentTask.setStatus(TaskStatus.IN_PROGRESS);
        }

        // SỬA LỖI: Dùng FocusState.RUNNING
        this.currentState = FocusState.RUNNING;
        this.currentSessionType = SessionType.FOCUS;
        this.timeLeft = TIME_FOCUS;
        this.sessionStartTime = new Date();

        timer.start();
        notifyStateChanged();
    }

    public void pauseTimer() {
        // SỬA LỖI: Dùng FocusState.RUNNING (Nó bao hàm cả việc đang chạy Focus hay Break)
        if (currentState == FocusState.RUNNING) {
            timer.stop();
            currentState = FocusState.PAUSED;
            notifyStateChanged();
        }
    }

    public void resumeTimer() {
        if (currentState == FocusState.PAUSED) {
            // SỬA LỖI: Chỉ cần khôi phục lại trạng thái RUNNING
            currentState = FocusState.RUNNING;
            timer.start();
            notifyStateChanged();
        }
    }

    public void stopSessionConfirm() {
        currentState = FocusState.CONFIRMING_STOP;
        notifyStateChanged();
    }

    public void stopSession(boolean isConfirmed) {
        if (isConfirmed) {
            timer.stop();
            int duration = getPlannedTime(currentSessionType) - timeLeft;

            // Ghi lịch sử là STOPPED_EARLY
            StudySession sessionRecord = createStudySessionRecord(duration, SessionStatus.STOPPED_EARLY);
            notifyObservers(new FocusSessionEvent(currentTask, sessionRecord));

            resetToIdle();
        } else {
            currentState = FocusState.PAUSED;
            notifyStateChanged();
        }
    }

    private void handleTimeOver() {
        timer.stop();
        int duration = getPlannedTime(currentSessionType);

        // SỬA LỖI: Dựa vào currentSessionType để biết là vừa hết giờ học hay hết giờ nghỉ
        if (currentSessionType == SessionType.FOCUS) {
            sessionCount++;
            if(currentTask != null) currentTask.incrementCompPomo();

            // Ghi lịch sử là COMPLETED
            StudySession sessionRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyObservers(new FocusSessionEvent(currentTask, sessionRecord));

            // Chuyển sang giờ nghỉ (Đồng hồ vẫn tiếp tục RUNNING)
            currentState = FocusState.RUNNING;
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
            // Vừa hết giờ nghỉ
            StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyObservers(new FocusSessionEvent(null, breakRecord));
            resetToIdle();
        }
        notifyStateChanged();
    }

    public void skipBreak() {
        timer.stop();
        int duration = getPlannedTime(currentSessionType) - timeLeft;

        // Ghi lịch sử là CANCELED
        StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.CANCELED);
        notifyObservers(new FocusSessionEvent(null, breakRecord));

        resetToIdle();
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
        currentState = FocusState.IDLE;
        currentSessionType = SessionType.FOCUS;
        timeLeft = TIME_FOCUS;
        currentTask = null;
        notifyStateChanged();
    }

    private int getPlannedTime(SessionType type) {
        if(type == SessionType.FOCUS) return TIME_FOCUS;
        if(type == SessionType.SHORT_BREAK) return TIME_SHORT_BREAK;
        return TIME_LONG_BREAK;
    }

    private void notifyStateChanged() {
        if (onStateChangedCallback != null) onStateChangedCallback.run();
    }

    public int getTimeLeft() { return timeLeft; }
    public FocusState getCurrentState() { return currentState; }
    public SessionType getCurrentSessionType() { return currentSessionType; } // Đã thêm hàm này cho View
    public Task getCurrentTask() { return currentTask; }

    @Override
    public void addObserver(Observer o) { observers.add(o); }
    @Override
    public void removeObserver(Observer o) { observers.remove(o); }
    @Override
    public void notifyObservers(FocusSessionEvent event) {
        for (Observer o : observers) o.onSessionCompleted(event);
    }
}