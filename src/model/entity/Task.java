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

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "Task: " + title + " [" + status + "] - Priority: " + priority;
    }

}


