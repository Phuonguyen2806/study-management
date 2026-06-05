package model.observer;

import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;

// Người nghe: Nhận lệnh từ Model để vẽ lại giao diện
public interface FocusViewObserver {
    // Cập nhật con số thời gian (mm:ss) hiển thị trên đồng hồ
    void updateTime(int timeLeft);

    // Cập nhật trạng thái nút bấm (Bắt đầu/Tạm dừng) và thông tin công việc
    void updateState(FocusStatus state, SessionType type, Task task);
}
