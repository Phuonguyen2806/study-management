package model.strategy;
import model.entity.Task;
import javax.swing.*;

public class PushStrategy implements NotificationStrategy {
    @Override
    public boolean send(String message, Task task) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Thông báo nhắc nhở", JOptionPane.INFORMATION_MESSAGE);
        });
        return true;
    }
}