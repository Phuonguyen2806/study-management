package model.observer;

// Bộ phát tin: Quản lý các dịch vụ cần chạy ngầm khi hết giờ (Lưu file, báo chuông...)
public interface FocusSessionObserver {
    // Hàm này được gọi khi một phiên (Tập trung / Nghỉ) kết thúc, bị hủy hoặc dừng sớm
    void onSessionCompleted(FocusSessionEvent event);
}
