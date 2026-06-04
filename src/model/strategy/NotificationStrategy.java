package model.strategy;
import model.entity.Task;

public interface NotificationStrategy {
    boolean send(String message, Task task);
}
