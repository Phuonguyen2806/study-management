package model.entity;

import java.util.Date;

public class Reminder {
    private int reminderId;
    private Date timeSent;
    private String method; // PUSH hoặc EMAIL
    private int isSent; // 1: Thành công, 0: Thất bại
    private int taskId;

    // Constructor đầy đủ
    public Reminder(int reminderId, Date timeSent, String method, int isSent, int taskId) {
        this.reminderId = reminderId;
        this.timeSent = timeSent;
        this.method = method;
        this.isSent = isSent;
        this.taskId = taskId;
    }

    // Constructor mặc định
    public Reminder() {
    }

    // Các hàm Getter và Setter
    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int logId) {
        this.reminderId = logId;
    }

    public Date getTimestamp() {
        return timeSent;
    }

    public void setTimestamp(Date timestamp) {
        this.timeSent = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getIsSent() {
        return isSent;
    }

    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

}