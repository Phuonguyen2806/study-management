package model.entity;

import java.util.Date;

public class Task {
    private int taskId;
    private int userId; // Thêm thuộc tính userId theo yêu cầu
    private String title;
    private String description;
    private Date deadline;
    private String priority;
    private int estPomo;
    private int compPomo;
    private String state;
    private int userId;
    private TaskStatus status;

    // dùng khi đọc từ file lên (đã có sẵn taskId)
    public Task(int taskId, String title, String description, Date deadline, String priority, int estPomo, int compPomo, String state, int userId) {
    public Task(int taskId, String title, String description, Date deadline, String priority, int estPomo, int compPomo, TaskStatus status, int userId) {
        this.taskId = taskId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
        this.status = status;
        this.state = state;
        this.userId = userId;
    }

    // KHỞI TẠO MỚI (Bổ sung dùng khi thêm từ Form - Chưa có taskId)
    public Task(String title, String description, Date deadline, String priority, int estPomo, int compPomo, String state) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
        this.state = state;
    }
    public int getTaskId() { return taskId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getDeadline() { return deadline; }
    public String getPriority() { return priority; }
    public int getEstPomo() { return estPomo; }
    public int getCompPomo() { return compPomo; }
    public String getState() { return state; }
    public int getUserId() { return userId; }

    @Override
    public String toString() {
        return "Task: " + title + " [" + status + "] - Priority: " + priority;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getPriority() {
        return priority;
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

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setState(String status) {
        this.state = status;
    }
}

    public int getEstPomo() {
        return estPomo;
    }

    public int getCompPomo() {
        return compPomo;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public void setPriority(String priority) {
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
}


