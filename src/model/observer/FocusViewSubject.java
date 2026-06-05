package model.observer;

// Người phát: Quản lý danh sách các màn hình cần cập nhật
public interface FocusViewSubject {
    // Đăng ký người quan sát giao diện
    void addViewObserver(FocusViewObserver o);

    // Hủy đăng ký người quan sát giao diện
    void removeViewObserver(FocusViewObserver o);

    // Phát tín hiệu khi đồng hồ đếm ngược giảm giây
    void notifyTimeChanged();

    // Phát tín hiệu khi có sự thay đổi trạng thái (IDLE, RUNNING, PAUSED...) hoặc đổi Task
    void notifyStateChanged();
}
