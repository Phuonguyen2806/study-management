package model.entity;

public enum FocusStatus {
    IDLE, // Trạng thái chờ
    RUNNING, // Đồng hồ đang chạy
    PAUSED, // Đang tạm dừng
    CONFIRMING_STOP // Đang chờ xác nhận dừng
}
