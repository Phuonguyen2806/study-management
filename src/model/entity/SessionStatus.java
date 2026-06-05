package model.entity;

public enum SessionStatus {
    COMPLETED, // Hoàn thành trọn vẹn thời gian 1 phiên học
    STOPPED_EARLY, //Hoàn thành sớm / Dừng sớm
    CANCELED // Bị hủy bỏ (bấm dừng quá sớm khi chưa đủ thời gian tối thiểu—ví dụ dưới 10 giây—hệ thống coi như phiên này bị hủy bỏ).
}
