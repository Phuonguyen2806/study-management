package service;

import model.entity.Reminder;
import model.entity.Task;
import model.strategy.NotificationStrategy;
import model.strategy.PushStrategy;
import model.strategy.EmailStrategy;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class ReminderService {
    private NotificationStrategy strategy;
    private final MotivationService motivationService = new MotivationService();
    private static final long REMINDER_THRESHOLD_MINUTES = 60;

    public void setStrategy(NotificationStrategy strategy) {
        this.strategy = strategy;
    }

    private long calculateTimeDiff(Date deadline) {
        if (deadline == null) return -999;
        LocalDateTime deadlineLDT = deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ChronoUnit.MINUTES.between(LocalDateTime.now(), deadlineLDT);
    }

    public void runReminderCheck(List<Task> userTasks, boolean isAppOpen) {
        if (userTasks == null || userTasks.isEmpty()) {
            System.out.println("DEBUG: Danh sách task rỗng.");
            return;
        }

        for (Task task : userTasks) {
            long diff = calculateTimeDiff(task.getDeadline());
            System.out.println("DEBUG: Task " + task.getTitle() + " | Diff: " + diff + " phút");

            if (isWithinThreshold(diff)) {
                String message = "Nhắc nhở: " + task.getTitle() + ". " + motivationService.getRandomQuote();
                setStrategy(isAppOpen ? new PushStrategy() : new EmailStrategy());

                try {
                    System.out.println("DEBUG: Đang gọi strategy.send()...");
                    boolean success = strategy.send(message, task);

                    Reminder newLog = new Reminder();
                    newLog.setReminderId(getNextId());
                    newLog.setTimestamp(new Date());
                    newLog.setMethod(isAppOpen ? "PUSH" : "EMAIL");
                    newLog.setIsSent(success ? 1 : 0);
                    newLog.setTaskId(Integer.parseInt(String.valueOf(task.getTaskId())));

                    saveReminderToFile(newLog);
                    System.out.println("DEBUG: Gửi thành công: " + success);
                } catch (Exception e) {
                    System.err.println("Lỗi hệ thống: " + e.getMessage());
                }
            }
        }
    }

    private boolean isWithinThreshold(long minutesRemaining) {
        return minutesRemaining >= -5 && minutesRemaining <= REMINDER_THRESHOLD_MINUTES;
    }

    // Các hàm getNextId() và saveReminderToFile() giữ nguyên như cũ...
    private int getNextId() { /* ... code cũ của bạn ... */ return 1; }
    public void saveReminderToFile(Reminder r) { /* ... code cũ của bạn ... */ }
}