package model.repository;

import model.entity.Task;

import java.util.List;

public interface ITaskRepository {
    void init(String filePath); // Khởi tạo và đọc file
    List<Task> findTasksByStatus(String status); // Tìm công việc theo trạng thái
    void updateTask(Task task); // Cập nhật công việc (khi hoàn thành phiên)
    void addTask(Task task); // Thêm công việc mới
}
