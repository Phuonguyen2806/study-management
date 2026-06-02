package controller;

import model.FocusSessionManager;
import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.ITaskRepository;
import model.repository.TaskRepositoryImpl;
import service.ProgressTrackingService;
import view.FocusPanel;

import javax.swing.JOptionPane;
import java.util.List;

public class FocusController implements IFocusController {
    private FocusPanel view;
    private FocusSessionManager sessionManager;
    private ITaskRepository taskRepository;

    public FocusController(FocusPanel view) {
        this.view = view;
        this.taskRepository = new TaskRepositoryImpl();
        this.taskRepository.init("data/tasks.txt");
        this.sessionManager = new FocusSessionManager();

        // 1. Cắm ổ cắm History để ghi file khi kết thúc
        ProgressTrackingService progressService = new ProgressTrackingService(this.taskRepository);
        this.sessionManager.addHistoryObserver(progressService);

        // 2. Cắm ổ cắm View để giao diện tự động nhảy số theo thời gian thực
        this.sessionManager.addViewObserver(this.view);
    }

    @Override
    public void initFocusView() {
        // Có thể để trống!
        // Vì ngay khi gọi hàm addViewObserver ở trên, Model đã tự động
        // gửi trạng thái đầu tiên sang cho View vẽ giao diện rồi.
    }

    @Override
    public void handleSelectTaskClick() {
        List<Task> pendingTasks = taskRepository.findTasksByStatus(TaskStatus.PENDING.name());
        List<Task> inProgressTasks = taskRepository.findTasksByStatus(TaskStatus.IN_PROGRESS.name());
        pendingTasks.addAll(inProgressTasks);

        Task selectedTask = view.showTaskSelectionDialog(pendingTasks);
        if (selectedTask != null) {
            int est = view.showEstimateDialog();
            sessionManager.setTask(selectedTask, est);
        }
    }

    @Override
    public void handleModeChange(SessionType type) {
        sessionManager.setSessionType(type);
    }

    @Override
    public void handleActionClick() {
        FocusStatus state = sessionManager.getCurrentState();

        if (state == FocusStatus.IDLE) {
            sessionManager.startSession();
        } else if (state == FocusStatus.RUNNING) {
            sessionManager.pauseTimer();
        } else if (state == FocusStatus.PAUSED) {
            sessionManager.resumeTimer();
        }
    }

    @Override
    public void handleStopClick() {
        sessionManager.pauseTimer();

        boolean confirm;
        if (sessionManager.getCurrentSessionType() == SessionType.FOCUS) {
            confirm = view.showConfirmStopDialog();
        } else {
            confirm = view.showConfirmSkipBreakDialog();
        }

        if (confirm) {
            if (sessionManager.getCurrentSessionType() == SessionType.FOCUS) {
                sessionManager.stopSession(true);
            } else {
                sessionManager.skipBreak();
            }
            sessionManager.clearTask();
        } else {
            sessionManager.resumeTimer();
        }
    }

    @Override
    public void handleCompleteEarlyClick() {
        sessionManager.pauseTimer();
        boolean confirm = view.showConfirmCompleteDialog();

        if (confirm) {
            Task currentTask = sessionManager.getCurrentTask();
            if (currentTask != null) {
                currentTask.setStatus(TaskStatus.DONE);
            }

            sessionManager.stopSession(true);
            sessionManager.clearTask();

            JOptionPane.showMessageDialog(null, "Chúc mừng bạn đã hoàn thành công việc!");
        } else {
            sessionManager.resumeTimer();
        }
    }
}