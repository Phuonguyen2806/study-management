package model;

import model.entity.Priority;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskManager {
    private ITaskRepository repository;
    private IUserRepository userRepository;

    public TaskManager(ITaskRepository repository,  IUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void addTask(String title, String description, String deadlineStr, String priority, String status) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề không được để trống!");
        }
        Date deadline = parseDate(deadlineStr);
        if (deadline.before(new Date())) {
            throw new IllegalArgumentException("Không được chọn ngày trong quá khứ!");
        }
        int userId = userRepository.getLoggedInUserId();
        if (userId == -1) {
            throw new IllegalStateException("Không tìm thấy người dùng đăng nhập!");
        }
        int nextId = generateNextTaskId();
        Task task = new Task(nextId, title, description, deadline, Priority.valueOf(priority), 0, 0, TaskStatus.valueOf(status), userId);
        if (!repository.save(task)) {
            throw new IllegalStateException("Không thể lưu công việc!");
        }
    }

    public void deleteTask(int taskId) {
        int userId = userRepository.getLoggedInUserId();
        Task task = repository.findTaskById(taskId, userId);
        if(task == null){
            throw new IllegalArgumentException("Không tìm thấy công việc!");
        }
        if(!repository.delete(taskId, userId)){
            throw new IllegalStateException("Không thể xóa công việc!");
        }
    }

    public void updateTask(int taskId, String title, String description, String deadlineStr, String priority, String status) {
        int userId = userRepository.getLoggedInUserId();
        Task task = repository.findTaskById(taskId, userId);
        if(task == null){
            throw new IllegalArgumentException("Không tìm thấy công việc!");
        }
        if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("Tiêu đề không được để trống!");
        }
        Date deadline = parseDate(deadlineStr);
        task.setTitle(title);
        task.setDescription(description);
        task.setDeadline(deadline);
        task.setPriority(Priority.valueOf(priority));
        task.setStatus(TaskStatus.valueOf(status));
        if(!repository.update(task)){
            throw new IllegalStateException("Không thể cập nhật công việc!");
        }
    }

    public List<Task> getTasksByUserAndPriority(int userId, String priority) {
        List<Task> result = new ArrayList<>();
        for (Task task : repository.getAllTasks()) {
            if (task.isUserTask(userId)) {
                if ("ALL".equals(priority) || priority == null || priority.isEmpty() || task.isPriority(priority)) {
                    result.add(task);
                }
            }
        }
        return result;
    }

    public void updateOverdueTasks() {
        for (Task task : repository.getAllTasks()) {
            if (task.checkOverdue()) {
                repository.update(task);
            }
        }
    }

    public int generateNextTaskId() {
        int maxId = 0;
        for(Task task : repository.getAllTasks()) {
            if(task.getTaskId() > maxId) {
                maxId = task.getTaskId();
            }
        }
        return maxId + 1;
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ngày không hợp lệ!");
        }
    }

    public Task getTaskById(int taskId) {
        int userId = userRepository.getLoggedInUserId();
        Task task = repository.findTaskById(taskId, userId);
        if(task == null){
            throw new IllegalArgumentException("Không tìm thấy công việc!");
        }
        return task;
    }


}
