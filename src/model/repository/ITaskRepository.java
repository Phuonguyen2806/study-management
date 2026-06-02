package model.repository;

import model.entity.Task;

import java.util.List;

public interface ITaskRepository {
    public void init(String filePath); // Khởi tạo và đọc file
    public List<Task> findTasksByStatus(String status); // Tìm công việc theo trạng thái
    public boolean save(Task task, int userId);
    public List<Task> getAllTasks();
    public boolean update(Task updatedTask);
    public boolean delete(int taskId);
}
