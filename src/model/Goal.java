package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity: Goal — class diagram
 *
 * Fields:
 *   -goalID   : int
 *   -title    : String
 *   -startDate: LocalDate
 *   -endDate  : LocalDate
 *   -status   : GoalStatus
 *   -type     : GoalType
 *
 * Methods:
 *   +updateProgress(value: int): void
 *   +updateStatus(): void
 *   +evaluate(StatisticsService statsService, User user)
 */
public class Goal {

    private int           goalID;
    private String        title;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private GoalStatus    status;
    private GoalType      type;

    /** Giá trị mục tiêu (ví dụ: 3 giờ, 10 bài tập) */
    private double        targetValue;

    /** Đơn vị hiển thị ("hours", "tasks", "sessions") */
    private String        unit;

    /** Tiến độ hiện tại (raw value, cùng đơn vị targetValue) */
    private double        currentValue;

    /** Thời điểm hoàn thành thực tế (Variation #3) */
    private LocalDateTime completedAt;

    // ─────────────────────────────────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────────────────────────────────
    public Goal() {
        this.status = GoalStatus.IN_PROGRESS;
    }

    public Goal(int goalID, String title, GoalType type,
                LocalDate startDate, LocalDate endDate,
                double targetValue, String unit) {
        this.goalID      = goalID;
        this.title       = title;
        this.type        = type;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.targetValue = targetValue;
        this.unit        = unit;
        this.status      = GoalStatus.IN_PROGRESS;
        this.currentValue = 0;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Business methods — sequence diagram step 7
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * updateProgress — cập nhật giá trị tiến độ thực tế
     * Được gọi khi người dùng nhấn +1 / -1 trên giao diện
     */
    public void updateProgress(int delta) {
        this.currentValue = Math.max(0, this.currentValue + delta);
    }

    /**
     * updateStatus — cập nhật trạng thái dựa trên currentValue và endDate
     * Giai đoạn 3, step 8 trong sequence diagram
     */
    public void updateStatus() {
        LocalDate today = LocalDate.now();

        if (currentValue >= targetValue) {
            // Variation #3: đạt điều kiện hoàn thành
            this.status      = GoalStatus.ACHIEVED;
            this.completedAt = LocalDateTime.now();
        } else if (endDate != null && today.isAfter(endDate)) {
            // Variation #4: hết hạn chưa đạt
            this.status = GoalStatus.FAILED;
        } else {
            // Variation #2: chưa đủ dữ liệu
            this.status = GoalStatus.IN_PROGRESS;
        }
    }

    /**
     * evaluate — logic đánh giá chính, gọi từ GoalService
     * Tham số statsService & user dùng khi tích hợp StatisticsService thật
     */
    public void evaluate(double statsValue) {
        this.currentValue = statsValue;
        updateStatus();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Computed helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Phần trăm hoàn thành (0–100) */
    public int getProgressPercent() {
        if (targetValue <= 0) return 0;
        return (int) Math.min(100, (currentValue / targetValue) * 100);
    }

    /** Chuỗi tiến độ hiển thị, ví dụ "1 / 3 hours" */
    public String getProgressLabel() {
        String cur = (currentValue == (long) currentValue)
                ? String.valueOf((long) currentValue)
                : String.format("%.1f", currentValue);
        String tgt = (targetValue == (long) targetValue)
                ? String.valueOf((long) targetValue)
                : String.format("%.1f", targetValue);
        return cur + " / " + tgt + " " + (unit != null ? unit : "");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Getters & Setters
    // ─────────────────────────────────────────────────────────────────────────
    public int           getGoalID()       { return goalID; }
    public String        getTitle()        { return title; }
    public LocalDate     getStartDate()    { return startDate; }
    public LocalDate     getEndDate()      { return endDate; }
    public GoalStatus    getStatus()       { return status; }
    public GoalType      getType()         { return type; }
    public double        getTargetValue()  { return targetValue; }
    public String        getUnit()         { return unit; }
    public double        getCurrentValue() { return currentValue; }
    public LocalDateTime getCompletedAt()  { return completedAt; }

    public void setGoalID(int id)              { this.goalID       = id; }
    public void setTitle(String t)             { this.title        = t; }
    public void setStartDate(LocalDate d)      { this.startDate    = d; }
    public void setEndDate(LocalDate d)        { this.endDate      = d; }
    public void setStatus(GoalStatus s)        { this.status       = s; }
    public void setType(GoalType t)            { this.type         = t; }
    public void setTargetValue(double v)       { this.targetValue  = v; }
    public void setUnit(String u)              { this.unit         = u; }
    public void setCurrentValue(double v)      { this.currentValue = v; }
    public void setCompletedAt(LocalDateTime d){ this.completedAt  = d; }
}
