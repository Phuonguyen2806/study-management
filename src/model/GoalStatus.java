package model;

/**
 * Enumeration GoalStatus — class diagram
 * Các trạng thái hợp lệ của một mục tiêu học tập
 */
public enum GoalStatus {
    IN_PROGRESS("Đang thực hiện"),
    ACHIEVED("Hoàn thành"),
    FAILED("Thất bại");

    private final String displayName;

    GoalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
 