package model;

import model.entity.*;
import model.observer.*;
import model.repository.IUserRepository;
import model.repository.UserRepository;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Lớp quản lý logic cốt lõi của phiên làm việc (Model).
 * Đóng vai trò là Subject trong Observer Pattern cho cả Giao diện và Lịch sử.
 */

public class FocusSessionManager implements FocusViewSubject, FocusSessionSubject {
    // 1. Observer lưu Data (Service)
    private List<FocusSessionObserver> historyObservers = new ArrayList<>();
    // 2. Observer cập nhật Giao diện (View)
    private List<FocusViewObserver> viewObservers = new ArrayList<>();

    private FocusStatus currentState = FocusStatus.IDLE;
    private SessionType currentSessionType = SessionType.FOCUS;
    private int sessionCount = 0; // Đếm số phiên tập trung để chuyển từ nghỉ ngắn sang nghỉ dài

    private final int TIME_FOCUS = 1 * 60;
    private final int TIME_SHORT_BREAK = 5 * 60;
    private final int TIME_LONG_BREAK = 15 * 60;

    private int timeLeft;
    private Timer timer;
    private Task currentTask;
    private Date sessionStartTime; // đồng hồ phải tự nhớ lúc nó bắt đầu (lúc bấm nút) để đến khi hết giờ, nó mới tạo dữ liệu StudySession được.
    private IUserRepository userRepository= new UserRepository();

    public FocusSessionManager() {
        this.timeLeft = TIME_FOCUS;
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                notifyTimeChanged(); // Thông báo View cập nhật con số hiển thị
            } else {
                handleTimeOver(); // Xử lý khi đồng hồ về 00:00
            }
        });
    }

    // ==========================================
    // QUẢN LÝ OBSERVER: VIEW (Giao diện)
    // ==========================================

    @Override
    public void addViewObserver(FocusViewObserver o) {
        viewObservers.add(o);
        // Ngay khi View kết nối, cập nhật ngay trạng thái và thời gian hiện tại
        o.updateState(currentState, currentSessionType, currentTask);
        o.updateTime(timeLeft);
    }

    @Override
    public void removeViewObserver(FocusViewObserver o) {
        viewObservers.remove(o);
    }

    @Override
    public void notifyTimeChanged() {
        for (FocusViewObserver o : viewObservers) {
            o.updateTime(timeLeft);
        }
    }

    @Override
    public void notifyStateChanged() {
        for (FocusViewObserver o : viewObservers) {
            o.updateState(currentState, currentSessionType, currentTask);
        }
        notifyTimeChanged(); // Luôn cập nhật lại con số đồng hồ khi trạng thái đổi
    }

    // ==========================================
    // QUẢN LÝ OBSERVER: HISTORY (Lưu lịch sử)
    // ==========================================

    @Override
    public void addFocusSessionObserver(FocusSessionObserver o) {
        historyObservers.add(o);
    }

    @Override
    public void removeFocusSessionObserver(FocusSessionObserver o) {
        historyObservers.remove(o);
    }

    @Override
    public void notifyFocusSessionObservers(FocusSessionEvent event) {
        for (FocusSessionObserver o : historyObservers) {
            o.onSessionCompleted(event);
        }
    }


    // ==========================================
    // CÁC HÀM XỬ LÝ LOGIC NGHIỆP VỤ
    // ==========================================

    /**
     * Gán công việc cụ thể cho phiên làm việc hiện tại.
     *
     * @param task              Đối tượng công việc được chọn.
     * @param estimatedSessions Số phiên dự kiến người dùng nhập vào.
     */
    public void setTask(Task task, int estimatedSessions) {
        this.currentTask = task;
        this.currentTask.setEstPomo(estimatedSessions);
        notifyStateChanged();
    }

    /**
     * Thay đổi chế độ phiên (Tập trung/Nghỉ ngắn/Nghỉ dài) thủ công khi đang ở trạng thái chờ.
     */
    public void setSessionType(SessionType type) {
        if (currentState == FocusStatus.IDLE) {
            this.currentSessionType = type;
            this.timeLeft = getPlannedTime(type);
            notifyStateChanged();
        }
    }

    /**
     * Bắt đầu chạy đồng hồ đếm ngược cho phiên hiện tại.
     * Cập nhật trạng thái Task sang "Đang thực hiện".
     */
    public void startSession() {
        if (this.currentTask != null && this.currentTask.getStatus() == TaskStatus.PENDING) {
            this.currentTask.setStatus(TaskStatus.IN_PROGRESS);
        }

        this.currentState = FocusStatus.RUNNING;
        // Ghi lại thời điểm thực tế bắt đầu bấm nút để làm mốc cho lịch sử
        this.sessionStartTime = new Date();
        timer.start();
        notifyStateChanged();
    }

    /**
     * Tạm dừng đồng hồ đếm ngược.
     */
    public void pauseTimer() {
        if (currentState == FocusStatus.RUNNING) {
            timer.stop();
            currentState = FocusStatus.PAUSED;
            notifyStateChanged();
        }
    }

    /**
     * Tiếp tục chạy đồng hồ sau khi đã tạm dừng.
     */
    public void resumeTimer() {
        if (currentState == FocusStatus.PAUSED) {
            currentState = FocusStatus.RUNNING;
            timer.start();
            notifyStateChanged();
        }
    }

    /**
     * Kết thúc phiên làm việc sớm theo yêu cầu của người dùng.
     * Kiểm tra điều kiện thời gian tích lũy để phân loại phiên hợp lệ hay bị hủy.
     *
     * @param isConfirmed Kết quả xác nhận từ hộp thoại người dùng.
     */
    public void stopSession(boolean isConfirmed) {
        if (isConfirmed) {
            timer.stop();

            // Tính thời gian thực tế đã trôi qua (giây)
            int duration = getPlannedTime(currentSessionType) - timeLeft;

            // 1. Đặt trạng thái mặc định khi dừng sớm là STOPPED_EARLY
            SessionStatus status = SessionStatus.STOPPED_EARLY;

            // 2. THÊM ĐIỀU KIỆN: Nếu là phiên TẬP TRUNG và chưa đủ 5 phút (5 * 60 = 300 giây)
            if (currentSessionType == SessionType.FOCUS && duration < 10) {
                status = SessionStatus.CANCELED; // Chuyển thành Bị hủy
            }

            // Tạo bản ghi với trạng thái đã qua bộ lọc điều kiện
            StudySession sessionRecord = createStudySessionRecord(duration, status);
            notifyFocusSessionObservers(new FocusSessionEvent(currentTask, sessionRecord));
            resetToIdle();
        } else {
            currentState = FocusStatus.PAUSED;
            notifyStateChanged();
        }
    }

    /**
     * Xử lý tự động khi đồng hồ đếm về 0.
     * Lưu lịch sử hoàn thành và chuyển đổi chế độ làm việc/nghỉ ngơi.
     */
    private void handleTimeOver() {
        timer.stop();
        int duration = getPlannedTime(currentSessionType);

        if (currentSessionType == SessionType.FOCUS) {
            sessionCount++;
            if (currentTask != null) currentTask.incrementCompPomo();

            // Lưu lịch sử phiên tập trung đã hoàn thành
            StudySession sessionRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyFocusSessionObservers(new FocusSessionEvent(currentTask, sessionRecord));

            // Chuyển giao diện sang chế độ nghỉ nhưng giữ ở trạng thái IDLE để chờ người dùng sẵn sàng
            currentState = FocusStatus.IDLE;
            if (sessionCount >= 4) {
                currentSessionType = SessionType.LONG_BREAK;
                timeLeft = TIME_LONG_BREAK;
                sessionCount = 0;
            } else {
                currentSessionType = SessionType.SHORT_BREAK;
                timeLeft = TIME_SHORT_BREAK;
            }
        } else {
            // Lưu lịch sử phiên nghỉ đã hoàn thành
            StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyFocusSessionObservers(new FocusSessionEvent(null, breakRecord));
            resetToIdle();
        }
        notifyStateChanged();
    }

    /**
     * Bỏ qua phiên nghỉ ngơi hiện tại.
     * Chế độ này không lưu vào cơ sở dữ liệu để tránh dữ liệu rác.
     */
    public void skipBreak() {
        timer.stop();
        resetToIdle();
    }

    /**
     * Gỡ bỏ Task hiện tại ra khỏi Manager (sau khi hoàn thành hoặc dừng hẳn).
     */
    public void clearTask() {
        this.currentTask = null;
        notifyStateChanged();
    }

    /**
     * Hàm nhà máy tạo ra đối tượng StudySession để lưu vào lịch sử.
     *
     * @param duration Thời gian thực tế đã sử dụng (giây).
     * @param status   Trạng thái kết thúc của phiên.
     *
     */
    private StudySession createStudySessionRecord(int duration, SessionStatus status) { //Truyền vào 2 tham số này vì 2 thông số này luôn thay đổi. Nếu hoàn thành đủ 25 phút, duration là 25 và status là COMPLETED. Nhưng nếu đang làm 10 phút mà bấm dừng, duration chỉ là 10 và status phải là STOPPED_EARLY. Do đó phải truyền vào làm tham số để hàm nó biết mà tạo lịch sử cho đúng.
        Date endTime = new Date();
        IUserRepository userRepository = new UserRepository();
        int loggedInId = userRepository.getLoggedInUserId();
        Integer taskId = (currentTask != null) ? currentTask.getTaskId() : null;
        int sessionId = (int) (System.currentTimeMillis() % 100000);

        return new StudySession(sessionId, loggedInId, taskId, sessionStartTime, endTime, duration, currentSessionType, status);
    }

    /**
     * Reset hệ thống về trạng thái nghỉ, mặc định sẵn sàng cho phiên tập trung mới.
     */
    private void resetToIdle() {
        currentState = FocusStatus.IDLE;
        currentSessionType = SessionType.FOCUS;
        this.timeLeft = TIME_FOCUS;
        notifyStateChanged();
    }

    /**
     * Lấy thời gian quy định theo từng loại phiên.
     */
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

    /**
     * Lấy thời gian thực tế đã trôi qua của phiên hiện tại (tính bằng giây).
     * Dùng để kiểm tra điều kiện tối thiểu khi hoàn thành sớm.
     */
    public int getElapsedTime() {
        return getPlannedTime(currentSessionType) - timeLeft;
    }
}