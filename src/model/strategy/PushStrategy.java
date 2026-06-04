package model.strategy;

import model.entity.Task;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.awt.Toolkit;

public class PushStrategy implements NotificationStrategy {
    @Override
    public boolean send(String message, Task task) {
        System.out.println("DEBUG: PushStrategy đang gọi showMessageDialog...");

        // Cách này không dùng invokeLater mà chạy trực tiếp,
        // đôi khi nó hiệu quả hơn khi không có JFrame chính
        try {
            JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog("Nhắc nhở công việc");

            // Ép buộc hiển thị lên trên tất cả
            dialog.setAlwaysOnTop(true);

            // Lệnh quan trọng: Đưa thông báo ra giữa màn hình
            dialog.setLocationRelativeTo(null);

            // Cho phép dialog hiển thị mà không làm "treo" luồng chính của chương trình
            dialog.setModal(false);
            dialog.setVisible(true);

            // Thêm âm thanh hệ thống để báo hiệu (tùy chọn)
            Toolkit.getDefaultToolkit().beep();

            return true;
        } catch (Exception e) {
            System.err.println("DEBUG: Lỗi UI - " + e.getMessage());
            return false;
        }
    }
}