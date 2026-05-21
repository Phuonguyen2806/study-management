package controller;

import model.entity.FocusState;
import model.entity.SessionType;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.ITaskRepository;
import model.repository.TaskRepositoryImpl;
import service.ProgressTrackingService;
import view.FocusPanel;

import javax.swing.JOptionPane;
import java.util.List;

public class FocusController {
    private FocusPanel view;
    private FocusSessionManager sessionManager;
    private ITaskRepository taskRepository;

    public FocusController(FocusPanel view) {
        this.view = view;

        // 1. Khởi tạo Repository và load dữ liệu
        this.taskRepository = new TaskRepositoryImpl();
        this.taskRepository.init("data/tasks.txt"); // Đường dẫn tới file data của bạn

        // 2. Khởi tạo Manager đếm giờ
        this.sessionManager = new FocusSessionManager();

        // 3. Đăng ký Dịch vụ theo dõi ngầm (Observer)
        ProgressTrackingService progressService = new ProgressTrackingService(this.taskRepository);
        this.sessionManager.addObserver(progressService);

        // 4. Lắng nghe Manager thay đổi để yêu cầu View vẽ lại
        this.sessionManager.setCallbacks(
                this::updateViewTime,
                this::updateViewState
        );
    }

    public void initFocusView() {
        view.resetViewToIdle();
    }

    public void handleSelectTaskClick() {
        // Lấy danh sách Task chưa xong
        List<Task> pendingTasks = taskRepository.findTasksByStatus(TaskStatus.PENDING.name());
        List<Task> inProgressTasks = taskRepository.findTasksByStatus(TaskStatus.IN_PROGRESS.name());
        pendingTasks.addAll(inProgressTasks);

        Task selectedTask = view.showTaskSelectionDialog(pendingTasks);
        if (selectedTask != null) {
            int est = view.showEstimateDialog();
            sessionManager.startSession(selectedTask, est); // Phát lệnh chạy
        }
    }

    public void handleActionClick() {
        FocusState state = sessionManager.getCurrentState();
        if (state == FocusState.RUNNING) {
            sessionManager.pauseTimer();
        } else if (state == FocusState.PAUSED) {
            sessionManager.resumeTimer();
        }
    }

    public void handleStopClick() {
        // Nếu đang là giờ nghỉ thì gọi hàm Bỏ qua nghỉ
        if (sessionManager.getCurrentSessionType() != SessionType.FOCUS) {
            handleSkipBreakClick();
            return;
        }

        // Nếu đang là giờ học thì xác nhận dừng
        sessionManager.stopSessionConfirm();
        boolean confirm = view.showConfirmStopDialog();
        sessionManager.stopSession(confirm);
    }

    private void handleSkipBreakClick() {
        sessionManager.pauseTimer(); // Tạm dừng để hỏi
        boolean confirm = view.showConfirmSkipBreakDialog();
        if (confirm) {
            sessionManager.skipBreak();
        } else {
            sessionManager.resumeTimer();
        }
    }

    public void handleCompleteEarlyClick() {
        sessionManager.pauseTimer();
        boolean confirm = view.showConfirmCompleteDialog();
        if(confirm) {
            Task currentTask = sessionManager.getCurrentTask();
            if(currentTask != null) {
                currentTask.setStatus(TaskStatus.DONE);
            }
            // Gọi dừng session ngay lập tức
            sessionManager.stopSession(true);
            JOptionPane.showMessageDialog(view, "Chúc mừng bạn đã hoàn thành công việc!");
        } else {
            sessionManager.resumeTimer();
        }
    }

    // --- Các hàm cập nhật ngược lên View ---

    private void updateViewTime() {
        int timeLeft = sessionManager.getTimeLeft();
        view.updateTimeLabel(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
    }

    private void updateViewState() {
        FocusState state = sessionManager.getCurrentState();
        SessionType type = sessionManager.getCurrentSessionType();
        Task task = sessionManager.getCurrentTask();
        view.syncState(state, type, task);
    }
}