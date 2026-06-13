package model.dto;

import model.entity.Task;
import model.entity.TaskStatus;

import java.util.List;
import java.util.Map;

public class DailyStats {
    private double todayFocusTime;                 // Tổng thời gian học trong ngày (tính bằng Giờ)
    private int pomodoroCount;                    // Số phiên Pomodoro hoàn thành thành công
    private Map<TaskStatus, Integer> taskStatusMap;// Số lượng Task theo từng trạng thái (Done, Pending...)

    public DailyStats(double todayFocusTime, int pomodoroCount, Map<TaskStatus, Integer> taskStatusMap) {
        this.todayFocusTime = todayFocusTime;
        this.pomodoroCount = pomodoroCount;
        this.taskStatusMap = taskStatusMap;
    }
    // Getters và Setters
    public double getTodayFocusTime() { return todayFocusTime; }
    public int getPomodoroCount() { return pomodoroCount; }
}
