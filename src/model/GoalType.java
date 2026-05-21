package model;

/**
 * Enumeration GoalType — class diagram
 * Phân loại mục tiêu theo chu kỳ
 */
public enum GoalType {
    DAILY("Hàng ngày"),
    WEEKLY("Hàng tuần"),
    MONTHLY("Hàng tháng");

    private final String displayName;

    GoalType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
