package model.observer;

public interface ISessionHistoryObserver {
    // Hàm này được gọi khi một phiên (Tập trung / Nghỉ) kết thúc, bị hủy hoặc dừng sớm
    void onSessionCompleted(FocusSessionEvent event);
}
