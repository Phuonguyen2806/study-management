package controller;

import model.FocusSessionManager;
import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.IStudySessionRepository;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import service.ProgressTrackingService;
import service.SessionFinishedNotificationService;
import view.FocusPanel;

import java.util.ArrayList;
import java.util.List;

public class FocusController implements IFocusController {
    private FocusPanel view;
    private FocusSessionManager sessionManager;
    private ITaskRepository taskRepository;
    private IUserRepository userRepository;
    private IStudySessionRepository studySessionRepository;

    // Hàm khởi tạo: Kết nối View, nạp file dữ liệu và cắm các bộ lắng nghe (Observer)
    public FocusController(FocusPanel view,ITaskRepository taskRepository, IUserRepository userRepository,IStudySessionRepository studySessionRepository) {
        this.view = view;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.studySessionRepository = studySessionRepository;
        this.sessionManager = new FocusSessionManager(userRepository);

        // 1. Cắm ổ cắm History để ghi file khi kết thúc
        ProgressTrackingService progressService = new ProgressTrackingService(this.taskRepository, this.studySessionRepository);
        this.sessionManager.addFocusSessionObserver(progressService);

        // 2. Cắm ổ cắm View để giao diện tự động nhảy số theo thời gian thực
        this.sessionManager.addViewObserver(this.view);

        // 3. Cắm ổ cắm Âm thanh để đánh chuông khi hết giờ
        SessionFinishedNotificationService sessionFinishedNotificationService =new SessionFinishedNotificationService();
        this.sessionManager.addFocusSessionObserver(sessionFinishedNotificationService);
    }

    // Nút [Chọn công việc]: Lọc file và chỉ hiển thị task của riêng user đang đăng nhập
    @Override
    public void handleSelectTaskClick() {
        int loggedInId = userRepository.getLoggedInUserId();
        // 1. Dùng hàm findTasksByUserId của Repository để lấy danh sách Task của user hiện tại
        List<Task> userTasks = taskRepository.findTasksByUserId(loggedInId);
        // 2. Lọc bỏ các Task đã hoàn thành (DONE) trước khi đưa lên View
        List<Task> pendingTasks = new ArrayList<>();
        for (Task task : userTasks) {
            if (task.getStatus() != TaskStatus.DONE) {
                pendingTasks.add(task);
            }
        }
        Task selectedTask = view.showTaskSelectionDialog(pendingTasks);
        if (selectedTask != null) {
            int est;

            // Nếu số phiên dự kiến của Task nhỏ hơn hoặc bằng 0 -> Chưa từng đặt -> Hiện dialog hỏi
            if (selectedTask.getEstPomo() <= 0) {
                est = view.showEstimateDialog();
            } else {
                // Nếu đã lớn hơn 0 -> Đã đặt rồi -> Lấy luôn cấu hình cũ, không hỏi nữa
                est = selectedTask.getEstPomo();
            }
            // 1. Cập nhật số phiên dự kiến vào đối tượng trên RAM
            sessionManager.setTask(selectedTask, est);

            // 2. Ra lệnh cho repository lưu ngay số dự kiến này xuống file tasks.txt
            this.taskRepository.update(selectedTask);
        }
    }

    // Tab chuyển chế độ: Đổi thủ công giữa Tập trung (25p) / Nghỉ ngắn (5p) / Nghỉ dài (15p)
    @Override
    public void handleModeChange(SessionType type) {
        sessionManager.setSessionType(type);
    }

    // Nút bấm chính: Tự động đổi chức năng [Bắt đầu] -> [Tạm dừng] -> [Tiếp tục] theo trạng thái đồng hồ
    @Override
    public void handleActionClick() {
        FocusStatus state = sessionManager.getCurrentState();

        if (state == FocusStatus.IDLE) {
            sessionManager.startSession(); // Đang chờ -> Chạy đồng hồ
        } else if (state == FocusStatus.RUNNING) {
            sessionManager.pauseTimer(); // Đang chạy -> Tạm dừng
        } else if (state == FocusStatus.PAUSED) {
            sessionManager.resumeTimer(); // Đang dừng -> Chạy tiếp
        }
    }

    // Nút [Dừng lại / Bỏ qua]: Hiện thông báo xác nhận để hủy phiên học hoặc bỏ qua giờ giải lao
    @Override
    public void handleStopClick() {
        sessionManager.pauseTimer(); // Tạm dừng đồng hồ để chờ người dùng xác nhận

        boolean confirm;

        // Kiểm tra nếu đang trong phiên học (FOCUS)
        if (sessionManager.getCurrentSessionType() == SessionType.FOCUS) {
            // Nếu chưa học đủ 10 giây -> Hiện cảnh báo không ghi nhận lịch sử
            if (!sessionManager.isSessionValidForRecord()) {
                confirm = view.showConfirmStopTooEarlyDialog();
            } else {
                // Nếu đã học trên 10 giây -> Hiện hộp thoại hỏi dừng sớm bình thường
                confirm = view.showConfirmStopDialog();
            }
        } else {
            // Nếu đang ở phiên nghỉ -> Hiện hộp thoại bỏ qua giờ nghỉ
            confirm = view.showConfirmSkipBreakDialog();
        }

        // Xử lý khi người dùng đồng ý dừng
        if (confirm) {
            if (sessionManager.getCurrentSessionType() == SessionType.FOCUS) {
                sessionManager.stopSession(true); // Dừng phiên học
                sessionManager.clearTask();
            } else {
                sessionManager.skipBreak();       // Bỏ qua phiên nghỉ
            }
        } else {
            // Nếu bấm nhầm/Hủy dừng -> Cho đồng hồ chạy tiếp tục
            if (sessionManager.getCurrentState() == FocusStatus.PAUSED) {
                sessionManager.resumeTimer();
            }
        }
    }

    // Nút [Hoàn thành công việc]: Đổi task sang DONE, ghi đè file lưu trữ (Chặn nếu chưa học đủ 10 giây)
    @Override
    public void handleCompleteEarlyClick() {
        // Chặn không cho bấm hoàn thành nếu phiên học chưa chạy được 10 giây
        if (!sessionManager.isSessionValidForRecord()) {
            sessionManager.pauseTimer();
            view.showWarningSessionTooShort();
            sessionManager.resumeTimer();
            return;
        }

        // Nếu đủ 10 giây mới tiếp tục
        sessionManager.pauseTimer();
        boolean confirm = view.showConfirmCompleteDialog();

        if (confirm) {
            Task currentTask = sessionManager.getCurrentTask();
            if (currentTask != null) {
                // 1. Cập nhật trạng thái trong bộ nhớ RAM của module Focus
                currentTask.setStatus(TaskStatus.DONE);

                // 2. Gọi repository lưu ngay trạng thái DONE xuống file data/tasks.txt
//                this.taskRepository.update(currentTask);
            }

            sessionManager.stopSession(true);
            sessionManager.clearTask();

            view.showCompletionSuccess();
        } else {
            sessionManager.resumeTimer();
        }
    }
}