package controller;

import model.repository.IReminderRepository;
import model.repository.ITaskRepository;
import model.repository.ReminderRepository; // Import đúng class cài đặt
import service.ReminderService;
import view.MainFrame;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.repository.TaskRepositoryImpl;

public class ReminderController {
    private final ReminderService reminderService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ITaskRepository taskRepository;
    public ReminderController(ITaskRepository taskRepository, ReminderService reminderService) {
        this.taskRepository = taskRepository;
        this.reminderService = reminderService;
    }

    public void startCheckingReminders(MainFrame mainFrame) {
        // Lên lịch chạy sau mỗi 1 phút
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 1. Tự động load lại dữ liệu mới nhất từ file
                taskRepository.refresh();
                boolean isAppActive = mainFrame.isVisible();
                reminderService.executeReminderWorkflow(isAppActive);
            } catch (Exception e) {
                System.err.println("Lỗi trong scheduler: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}