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

    public Task(int taskId, String title, String description, Date deadline, String priority, int estPomo, int compPomo, String state) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estPomo = estPomo;
        this.compPomo = compPomo;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Task: " + title + " [" + state + "] - Priority: " + priority;
    }
}
