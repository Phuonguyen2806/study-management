package model.repository;

import model.entity.Task;

import java.util.List;

public interface ITaskRepository {
    public boolean save(Task task, int userId);
    public List<Task> getAllTasks();
    public boolean update(Task updatedTask);
}
