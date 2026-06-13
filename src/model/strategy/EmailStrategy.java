package model.strategy;

import model.entity.Task;
import model.repository.IUserRepository;
import service.EmailService;

public class EmailStrategy implements NotificationStrategy {
    private final EmailService emailService;
    public EmailStrategy(IUserRepository userRepository) {
        this.emailService = new EmailService(userRepository);
    }
    @Override
    public boolean send(String message, Task task) {
        return emailService.sendReminderEmail(task.getUserId(), "Nhắc nhở công việc", message);
    }
}