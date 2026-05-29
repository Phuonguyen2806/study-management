package model.entity;

import java.util.Date;

public class Task {
    private int taskId;
    private String title;
    private String description;
    private Date deadline;
    private String priority;
    private int estPomo;
    private int compPomo;
    private String state;
    private int userId;

    // dùng khi đọc từ file lên (đã có sẵn taskId)
    public Task(int taskId, String title, String description, Date deadline, String priority, int estPomo, int compPomo, String state, int userId) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
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
        return "Task: " + title + " [" + state + "] - Priority: " + priority;
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
