package model.strategy;

import model.entity.Task;
import model.entity.User;
import service.EmailService;

public class EmailStrategy implements NotificationStrategy {
    private EmailService emailService = new EmailService();
    private User user = new User();
        @Override
        public boolean send(String message, Task task) {
            EmailService emailService = new EmailService();
            // Gọi trực tiếp hàm đã gộp
            return emailService.sendReminderEmail(String.valueOf(task.getUserId()), "Nhắc nhở công việc", message);
        }
}