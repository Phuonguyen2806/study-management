package model.strategy;

import model.entity.Task;

import javax.swing.*;
import java.awt.Toolkit;

public class PushStrategy implements NotificationStrategy {
    @Override
    public boolean send(String message, Task task) {
//        System.out.println("DEBUG: Gửi notification thành công qua Push: " + message);
//        return true;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, message, "Thông báo nhắc nhở", JOptionPane.INFORMATION_MESSAGE);
        });
        System.out.println("DEBUG: Push đã được gửi lên hàng đợi hiển thị (EDT).");
        return true;
    }
}