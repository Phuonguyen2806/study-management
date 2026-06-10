package model.entity;


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



