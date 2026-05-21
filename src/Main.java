import controller.MainController;
import model.database.DatabaseConnection;
import model.entity.Task;
import model.repository.TaskRepository;
import view.MainFrame;

import java.util.List;
import java.sql.*;
public class Main {
    public static void main(String[] args) {
        MainFrame view = new MainFrame();
        MainController controller = new MainController(view);
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {

                // Kiểm tra kết nối Database
                if (DatabaseConnection.getConnection() != null) {
                    System.out.println(">>> Hệ thống: Database đã sẵn sàng.");

                    // Test dữ liệu (Sau này phần này sẽ nằm sau bước Đăng nhập)
                    loadTestData();
                } else {
                    System.err.println(">>> Hệ thống: Lỗi kết nối Database!");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void loadTestData() {
        TaskRepository repo = new TaskRepository();
        List<Task> list = repo.getTasksByUserId(1);

        System.out.println("--- Danh sách Task của User 1 ---");
        for (Task t : list) {
            System.out.println(t);
        }
    }
}
