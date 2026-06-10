package model;

import config.AppConstants;
import model.entity.*;
import model.observer.*;
import model.repository.IUserRepository;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Lớp quản lý logic cốt lõi của phiên làm việc (Model).
 * Đóng vai trò là Subject trong Observer Pattern cho cả Giao diện và Lịch sử.
 */

public class FocusSessionManager implements FocusViewSubject, FocusSessionSubject {
    // 1. Observer thông báo Phiên học kết thúc để Cập nhật file .txt và Phát chuông thông báo
    private List<FocusSessionObserver> focusSessionObservers = new ArrayList<>();
    // 2. Observer cập nhật Giao diện (View)
    private List<FocusViewObserver> viewObservers = new ArrayList<>();

    private FocusStatus currentState = FocusStatus.IDLE;
    private SessionType currentSessionType = SessionType.FOCUS;
    private int sessionCount = 0; // Đếm số phiên tập trung để chuyển từ nghỉ ngắn sang nghỉ dài

    private int timeLeft;
    private Timer timer;
    private Task currentTask;
    private Date sessionStartTime; // đồng hồ phải tự nhớ lúc nó bắt đầu (lúc bấm nút) để đến khi hết giờ, nó mới tạo dữ liệu StudySession được.
    private final IUserRepository userRepository;

    public FocusSessionManager(IUserRepository userRepository) {
        this.timeLeft = AppConstants.TIME_FOCUS;
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                notifyTimeChanged(); // Thông báo View cập nhật con số hiển thị
            } else {
                handleTimeOver(); // Xử lý khi đồng hồ về 00:00
            }
        });
        this.userRepository = userRepository;
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
    }

    // ==========================================
    // QUẢN LÝ OBSERVER: HISTORY (Lưu lịch sử)
    // ==========================================

    @Override
    public void addFocusSessionObserver(FocusSessionObserver o) {
        focusSessionObservers.add(o);
    }

    @Override
    public void removeFocusSessionObserver(FocusSessionObserver o) {
        focusSessionObservers.remove(o);
    }

    @Override
    public void notifyFocusSessionObservers(FocusSessionEvent event) {
        for (FocusSessionObserver o : focusSessionObservers) {
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
            notifyTimeChanged();
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
            int duration = getElapsedTime();

            // Mặc định là dừng sớm
            SessionStatus status = SessionStatus.STOPPED_EARLY;

            // ĐIỀU KIỆN: Nếu thời gian thực tế chưa đủ 10 giây -> Đánh dấu là Bị hủy
            if (!isSessionValidForRecord()) {
                status = SessionStatus.CANCELED;
            }

            // Ghi nhận lịch sử (Vẫn lưu file nhưng trạng thái là CANCELED)
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
            if (sessionCount >= AppConstants.SESSIONS_BEFORE_LONG_BREAK) {
                currentSessionType = SessionType.LONG_BREAK;
                timeLeft = AppConstants.TIME_LONG_BREAK;
                sessionCount = 0;
            } else {
                currentSessionType = SessionType.SHORT_BREAK;
                timeLeft = AppConstants.TIME_SHORT_BREAK;
            }
        } else {
            // Lưu lịch sử phiên nghỉ đã hoàn thành
            StudySession breakRecord = createStudySessionRecord(duration, SessionStatus.COMPLETED);
            notifyFocusSessionObservers(new FocusSessionEvent(null, breakRecord));
            resetToIdle();
        }
        notifyStateChanged();
        notifyTimeChanged();
    }

    /**
     * Bỏ qua phiên nghỉ ngơi hiện tại.
     * Nếu thời gian đã chạy (đếm giây) thì lưu lịch sử, nếu chưa chạy thì không lưu.
     */
    public void skipBreak() {
        timer.stop();

        // Tính thời gian thực tế người dùng đã nghỉ (giây)
        int duration = getElapsedTime();

        // ĐIỀU KIỆN: Nếu thời gian đã đếm (lớn hơn 0 giây) thì tiến hành lưu file
        if (duration > 0) {
            SessionStatus status = SessionStatus.STOPPED_EARLY;

            // Bộ lọc bảo vệ: Nếu bấm bỏ qua quá sớm khi chưa đủ 10 giây -> Tính là Bị hủy (CANCELED)
            if (!isSessionValidForRecord()) {
                status = SessionStatus.CANCELED;
            }

            // Tạo bản ghi và phát tín hiệu cho ProgressTrackingService tự động ghi xuống file studysessions.txt
            StudySession sessionRecord = createStudySessionRecord(duration, status);
            notifyFocusSessionObservers(new FocusSessionEvent(null, sessionRecord));
            System.out.println(">>> [Model] Đã lưu lịch sử dừng sớm cho phiên nghỉ.");
        }

        // Reset hệ thống về trạng thái chờ của phiên làm việc mới
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
     */
    private StudySession createStudySessionRecord(int duration, SessionStatus status) { //Truyền vào 2 tham số này vì 2 thông số này luôn thay đổi. Nếu hoàn thành đủ 25 phút, duration là 25 và status là COMPLETED. Nhưng nếu đang làm 10 phút mà bấm dừng, duration chỉ là 10 và status phải là STOPPED_EARLY. Do đó phải truyền vào làm tham số để hàm nó biết mà tạo lịch sử cho đúng.
        Date endTime = new Date();
        int loggedInId = userRepository.getLoggedInUserId();
        Integer taskId = (currentTask != null) ? currentTask.getTaskId() : null;
        int sessionId = Math.abs(UUID.randomUUID().hashCode());

        return new StudySession(sessionId, loggedInId, taskId, sessionStartTime, endTime, duration, currentSessionType, status);
    }

    /**
     * Reset hệ thống về trạng thái nghỉ, mặc định sẵn sàng cho phiên tập trung mới.
     */
    private void resetToIdle() {
        currentState = FocusStatus.IDLE;
        currentSessionType = SessionType.FOCUS;
        this.timeLeft = AppConstants.TIME_FOCUS;
        notifyStateChanged();
        notifyTimeChanged();
    }

    /**
     * Lấy thời gian quy định theo từng loại phiên.
     */
    private int getPlannedTime(SessionType type) {
        if (type == SessionType.FOCUS) return AppConstants.TIME_FOCUS;
        if (type == SessionType.SHORT_BREAK) return AppConstants.TIME_SHORT_BREAK;
        return AppConstants.TIME_LONG_BREAK;
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

    public boolean isSessionValidForRecord() {
        return getElapsedTime() >= AppConstants.MIN_VALID_SESSION_SECONDS;
    }
}