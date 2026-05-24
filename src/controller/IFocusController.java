package controller;

import model.entity.SessionType;

public interface IFocusController {
    void initFocusView();
    void handleSelectTaskClick();
    void handleModeChange(SessionType type);
    void handleActionClick();
    void handleStopClick();
    void handleCompleteEarlyClick();
}
