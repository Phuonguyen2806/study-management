package model.repository;

import model.entity.Task;

import java.util.List;

public interface ITaskRepository {
    public void init(String filePath); // Khởi tạo và đọc file
    public List<Task> getAllTasks();
    public List<Task> findTasksByStatus(String status); // Tìm công việc theo trạng thái
    public Task findTaskById(int taskId);
    public boolean save(Task task, int userId);//hàm thêm task hoặc lưu task
    public boolean update(Task updatedTask);
    public boolean delete(int taskId);
    public void refresh();
}
