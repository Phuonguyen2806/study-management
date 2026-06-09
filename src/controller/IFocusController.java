package controller;

import model.entity.SessionType;
import model.entity.Task;

public interface IFocusController {
    void handleSelectTaskClick();
    void handleModeChange(SessionType type);
    void handleActionClick();
    void handleStopClick();
    void handleCompleteEarlyClick();
}
