package model.observer;

// Người phát: Quản lý các dịch vụ cần biết khi nào phiên học kết thúc
public interface FocusSessionSubject {
    // Thêm một dịch vụ vào danh sách chờ nhận thông báo
    void addFocusSessionObserver(FocusSessionObserver o);

    // Xóa dịch vụ khỏi danh sách nhận thông báo
    void removeFocusSessionObserver(FocusSessionObserver o);

    // Phát loa thông báo cho tất cả dịch vụ biết phiên học/nghỉ đã kết thúc
    void notifyFocusSessionObservers(FocusSessionEvent event);
}
