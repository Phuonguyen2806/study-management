package model.entity;

import java.util.Date;

public class StudySession {
    private int sessionId;
    private int userId;
    private Integer taskId;   // Dùng Integer thay vì int để cho phép giá trị null
    private Date startTime;
    private Date endTime;
    private int durationSeconds;
    private SessionType sessionType;
    private SessionStatus status;

    public StudySession(int sessionId, int userId, Integer taskId, Date startTime, Date endTime, int durationSeconds, SessionType sessionType, SessionStatus status) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationSeconds = durationSeconds;
        this.sessionType = sessionType;
        this.status = status;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return durationSeconds;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public SessionStatus getStatus() {
        return status;
    }
}
