package service;

import model.entity.SessionType;
import model.entity.StudySession;
import model.observer.FocusSessionEvent;
import model.observer.FocusSessionObserver;
import model.repository.IStudySessionRepository;
import model.repository.ITaskRepository;

// Dịch vụ chạy ngầm: Lắng nghe sự kiện hết giờ để lưu lịch sử và cập nhật số phiên của Task
public class ProgressTrackingService implements FocusSessionObserver {
    private ITaskRepository taskRepository;
    private IStudySessionRepository sessionRepository;

    // Truyền repository vào để có thể cập nhật Task
    public ProgressTrackingService(ITaskRepository taskRepository, IStudySessionRepository sessionRepository) {
        this.taskRepository = taskRepository;
        this.sessionRepository = sessionRepository;
    }

    // [Observer Pattern] Tự động kích hoạt khi một phiên (Học/Nghỉ) dừng lại hoặc chạy hết giờ
    @Override
    public void onSessionCompleted(FocusSessionEvent event) {
        StudySession session = event.getStudySession();

        // 1. Ghi lịch sử phiên học (StudySession) xuống file text
        sessionRepository.save(session);

        // 2. Nếu là phiên Focus và có chọn Task, cập nhật số phiên của Task đó vào file tasks.txt
        if (session.getSessionType() == SessionType.FOCUS && event.getTask() != null) {
            System.out.println(">>> [Observer] Đang cập nhật tiến độ cho Task: " + event.getTask().getTitle());
            taskRepository.update(event.getTask());
        }
    }
}