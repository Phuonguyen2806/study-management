package model.repository;

import model.entity.Task;

import java.util.List;

public interface ITaskRepository {
    public void init(String filePath); // Khởi tạo và đọc file
    public List<Task> getAllTasks();
    public List<Task> findTasksByStatus(String status, int userID); // Tìm công việc theo trạng thái
    public Task findTaskById(int taskId, int userId);
    public List<Task> findTasksByUserId(int userId);
    public boolean save(Task task);//hàm thêm task hoặc lưu task
    public boolean update(Task updatedTask);
    public boolean delete(int taskId,int userId);
    public void refresh();

}
