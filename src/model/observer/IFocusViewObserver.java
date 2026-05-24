package model.observer;

import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;

public interface IFocusViewObserver {
    void updateTime(int timeLeft); // Cập nhật đồng hồ mỗi giây

    void updateState(FocusStatus state, SessionType type, Task task); // Cập nhật các nút bấm, tiêu đề
}
