package controller;

import model.FocusSessionManager;
import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import model.repository.TaskRepositoryImpl;
import model.repository.UserRepository;
import service.ProgressTrackingService;
import service.SessionFinishedNotificationService;
import view.FocusPanel;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class FocusController implements IFocusController {
    private FocusPanel view;
    private FocusSessionManager sessionManager;
    private ITaskRepository taskRepository;
    private IUserRepository userRepository;

    // Hàm khởi tạo: Kết nối View, nạp file dữ liệu và cắm các bộ lắng nghe (Observer)
    public FocusController(FocusPanel view) {
        this.view = view;
        this.taskRepository = new TaskRepositoryImpl();
        this.taskRepository.init("data/tasks.txt");
        userRepository = new UserRepository();
        this.sessionManager = new FocusSessionManager();

        // 1. Cắm ổ cắm History để ghi file khi kết thúc
        ProgressTrackingService progressService = new ProgressTrackingService(this.taskRepository);
        this.sessionManager.addFocusSessionObserver(progressService);

        // 2. Cắm ổ cắm View để giao diện tự động nhảy số theo thời gian thực
        this.sessionManager.addViewObserver(this.view);

        // 3. Cắm ổ cắm Âm thanh để đánh chuông khi hết giờ
        this.sessionManager.addFocusSessionObserver(new SessionFinishedNotificationService());
    }

    // Nút [Chọn công việc]: Lọc file và chỉ hiển thị task của riêng user đang đăng nhập
    @Override
    public void handleSelectTaskClick() {
        this.taskRepository.init("data/tasks.txt");
        List<Task> allTasks = taskRepository.getAllTasks();

        List<Task> userTasks = new ArrayList<>();
        int loggedInId = userRepository.getLoggedInUserId();
        for (Task task : allTasks) {
            if (task.getUserId() == loggedInId && task.getStatus() != TaskStatus.DONE) {
                userTasks.add(task);
            }
        }
        Task selectedTask = view.showTaskSelectionDialog(userTasks);
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
            if (sessionManager.getElapsedTime() < 10) {
                int choice = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn chưa học đủ 10 giây. Nếu dừng lại lúc này, phiên học sẽ KHÔNG ĐƯỢC GHI NHẬN!\nBạn có chắc chắn muốn dừng không?",
                        "Cảnh báo dừng quá sớm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                confirm = (choice == JOptionPane.YES_OPTION);
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
        if (sessionManager.getElapsedTime() < 10) {
            JOptionPane.showMessageDialog(
                    null,
                    "Phiên học chưa đủ 10 giây. Bạn không thể hoàn thành công việc lúc này!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return; // Dừng lại, không thực hiện tiếp
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
                this.taskRepository.update(currentTask);
            }

            sessionManager.stopSession(true);
            sessionManager.clearTask();

            JOptionPane.showMessageDialog(null, "Chúc mừng bạn đã hoàn thành công việc!");
        } else {
            sessionManager.resumeTimer();
        }
    }
}