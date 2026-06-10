package model.strategy;

import model.entity.Task;
import model.repository.IUserRepository;
import service.EmailService;

public class EmailStrategy implements NotificationStrategy {
    private final EmailService emailService;

    // Constructor nhận vào userRepository để EmailService sử dụng
    public EmailStrategy(IUserRepository userRepository) {
        this.emailService = new EmailService(userRepository);
    }

    @Override
    public boolean send(String message, Task task) {
        // Truyền thẳng task.getUserId() (kiểu int) thay vì String
        return emailService.sendReminderEmail(task.getUserId(), "Nhắc nhở công việc", message);
    }
}