package model.entity;

import java.util.Date;

public class Task {
    private int taskId;
    private int userId;
    private String title;
    private String description;
    private Date deadline;
    private Priority priority;
    private int estPomo;
    private int compPomo;
    private TaskStatus status;

    // dùng khi đọc từ file lên (đã có sẵn taskId)
    public Task(int taskId, String title, String description, Date deadline, Priority priority, int estPomo, int compPomo, TaskStatus status, int userId) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
        this.status = status;
        this.userId = userId;
    }

    // KHỞI TẠO MỚI (Bổ sung dùng khi thêm từ Form - Chưa có taskId)
    public Task(String title, String description, Date deadline, Priority priority, int estPomo, int compPomo, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
        this.status = status;
    }
    public int getTaskId() { return taskId; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public Date getDeadline() { return deadline; }

    public Priority getPriority() { return priority; }

    public int getEstPomo() { return estPomo; }

    public int getCompPomo() { return compPomo; }

    public int getUserId() { return userId; }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setDeadline(Date deadlineDate) {
        this.deadline = deadlineDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setEstPomo(int estPomo) {
        this.estPomo = estPomo;
    }

    public void setCompPomo(int compPomo) {
        this.compPomo = compPomo;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void incrementCompPomo() {
        this.compPomo++;
    }

    public boolean isUserTask(int userId) {
        return this.userId == userId;
    }
    public boolean checkOverdue() {
        if ((status == TaskStatus.PENDING  || status == TaskStatus.IN_PROGRESS) && deadline.before(new Date())) {
            status = TaskStatus.OVERDUE;
            return true;
        }

        return false;
    }

    public boolean isTaskID(int taskId) {
        return this.taskId == taskId;
    }

    public boolean checkIDTask(Task task) {
        return this.taskId == task.getTaskId();
    }

    public boolean checkUserTask(Task task) {
        return this.userId == task.getUserId();
    }

    public boolean isStatus(String status) {
        return this.status.name().equalsIgnoreCase(status);
    }

    public boolean isOverdue() {
       return status == TaskStatus.OVERDUE;
    }

    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    public boolean isPriority(String priority) {
        return this.priority.name()
                .equalsIgnoreCase(priority);
    }

    @Override
    public String toString() {
        return "Task: " + title + " [" + status + "] - Priority: " + priority;
    }

}


