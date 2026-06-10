package model.entity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ReminderLog {
    private int reminderId;
    private Date timeSent;
    private String method; // PUSH hoặc EMAIL
    private int isSent; // 1: Thành công, 0: Thất bại
    private int taskId;

    // Constructor đầy đủ
    public ReminderLog(int reminderId, Date timeSent, String method, int isSent, int taskId) {
        this.reminderId = reminderId;
        this.timeSent = timeSent;
        this.method = method;
        this.isSent = isSent;
        this.taskId = taskId;
    }

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int reminderId) {
        this.reminderId = reminderId;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
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

    public static ReminderLog parse(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 5) return null;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            return new ReminderLog(
                    Integer.parseInt(parts[0]),        // reminderId
                    sdf.parse(parts[1]),               // timeSent (Date)
                    parts[2],                          // method
                    Integer.parseInt(parts[3]),        // isSent (int)
                    Integer.parseInt(parts[4])         // taskId
            );
        } catch (Exception e) {
            return null; // Trả về null nếu định dạng file bị lỗi
        }
    }
    public LocalDateTime getTime() {
        // Chuyển đổi Date sang Instant, sau đó sang LocalDateTime
        return timeSent.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
