package model.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Goal {
    private int goalID;
    private String title;
    private LocalDate date;
    private GoalStatus status;
    private double targetValue;
    private String unit;
    private double currentValue;
    private LocalDateTime completedAt;

    public Goal() {
        this.status = GoalStatus.IN_PROGRESS;
        this.date = LocalDate.now();
    }

    public Goal(int goalID, String title, LocalDate date,
                double targetValue, String unit) {
        this.goalID = goalID;
        this.title = title;
        this.date = date;
        this.targetValue = targetValue;
        this.unit = unit;
        this.status = GoalStatus.IN_PROGRESS;
        this.currentValue = 0.00000;
    }

    public void updateStatus() {
        LocalDate today = LocalDate.now();
        // 1. Nếu đã đạt hoặc vượt chỉ tiêu -> Chắc chắn là ACHIEVED
        if (currentValue >= targetValue) {
            if (this.status != GoalStatus.ACHIEVED) {
                this.status = GoalStatus.ACHIEVED;
                this.completedAt = LocalDateTime.now();
            }
            return; // Dừng lại luôn
        }
        // 2. Nếu CHƯA đạt chỉ tiêu, kiểm tra mốc thời gian
        if (date.isBefore(today)) {
            // Đã qua ngày hôm đó rồi mà vẫn chưa đạt -> Thất bại
            this.status = GoalStatus.FAILED;
        } else {
            // Thuộc về ngày hôm nay hoặc tương lai và chưa đạt -> Vẫn đang làm
            this.status = GoalStatus.IN_PROGRESS;
        }
    }

    //Đánh giá và cập nhật tiến độ dựa trên số liệu từ StatisticsService
    public void evaluate(double newValue) {
        this.currentValue = newValue;
        this.updateStatus();
    }

    // Helpers hiển thị giao diện
    public int getProgressPercent() {
        if (targetValue <= 0) return 0;
        return (int) Math.min(100, (currentValue / targetValue) * 100);
    }

    public String getProgressLabel() {
        String cur = String.valueOf(currentValue);
        String tgt = String.valueOf(targetValue);
        return cur + " / " + tgt + " " + (unit != null ? unit : "");

    }

    public int getGoalID() {
        return goalID;
    }


    public String getTitle() {
        return title;
    }


    public LocalDate getTargetDate() {
        return date;
    }


    public GoalStatus getStatus() {
        return status;
    }


    public void setStatus(GoalStatus status) {
        this.status = status;
    }


    public double getTargetValue() {
        return targetValue;
    }


    public String getUnit() {
        return unit;
    }


    public double getCurrentValue() {
        return currentValue;
    }


    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }
}







