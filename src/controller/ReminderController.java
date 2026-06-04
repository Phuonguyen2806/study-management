package controller;

import model.entity.Task;
import service.ReminderService;
import java.util.List;

public class ReminderController {
    private ReminderService reminderService;

    public ReminderController() {
        this.reminderService = new ReminderService();
    }

    /**
     * Phương thức chính để kích hoạt kiểm tra nhắc nhở.
     * Được gọi bởi MainController hoặc Timer định kỳ.
     */
    public void startCheckingReminders(List<Task> userTasks, boolean isAppOpen) {
        System.out.println("[Controller] Bắt đầu quy trình kiểm tra nhắc nhở...");

        // Chuyển tiếp nhiệm vụ cho tầng Service (Phase: Processing)
        reminderService.runReminderCheck(userTasks, isAppOpen);

        // Sau khi Service xử lý xong, Controller cập nhật lại UI (Phase: UI Update)
        updateMainUI();

        System.out.println("[Controller] Quy trình kiểm tra kết thúc.");
    }

    private void updateMainUI() {
        // Gọi lệnh để MainController làm mới giao diện
        // Ví dụ: MainController.refreshTaskTable();

        System.out.println("[Controller] Giao diện đã được làm mới.");
    }
}