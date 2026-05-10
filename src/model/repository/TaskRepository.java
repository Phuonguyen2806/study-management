package model.repository;

import model.database.DatabaseConnection;
import model.entity.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class TaskRepository {
    public List<Task> getTasksByUserId(int userId) {
        List<Task> taskList = new ArrayList<>();
        // Dùng dấu ? để truyền tham số an toàn
        String sql = "SELECT * FROM TASK WHERE UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Truyền giá trị vào dấu ? thứ nhất
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Đọc dữ liệu từng cột và tạo đối tượng Task
                    Task task = new Task(
                            rs.getInt("TaskID"),
                            rs.getNString("Title"),
                            rs.getNString("Description"),
                            rs.getTimestamp("Deadline"),
                            rs.getNString("Priority"),
                            rs.getInt("EstPomo"),
                            rs.getInt("CompPomo"),
                            rs.getNString("State")
                    );
                    taskList.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách Task: " + e.getMessage());
        }
        return taskList;
    }
}
