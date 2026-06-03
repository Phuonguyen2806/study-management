package service;

import model.entity.SessionType;
import model.entity.StudySession;
import model.observer.FocusSessionEvent;
import model.observer.SessionHistoryObserver;
import model.repository.ITaskRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ProgressTrackingService implements SessionHistoryObserver {
    private ITaskRepository taskRepository;
    private final String SESSION_FILE_PATH = "data/studysessions.txt"; // Đường dẫn lưu lịch sử
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Truyền repository vào để có thể cập nhật Task
    public ProgressTrackingService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void onSessionCompleted(FocusSessionEvent event) {
        StudySession session = event.getStudySession();

        // 1. Ghi lịch sử phiên học (StudySession) xuống file text
        saveSessionToFile(session);

        // 2. Nếu là phiên Focus và có chọn Task, cập nhật số phiên của Task đó vào file tasks.txt
        if (session.getSessionType() == SessionType.FOCUS && event.getTask() != null) {
            System.out.println(">>> [Observer] Đang cập nhật tiến độ cho Task: " + event.getTask().getTitle());
            taskRepository.update(event.getTask());
        }
    }

    // Hàm ghi nối (append) dữ liệu vào cuối file
    private void saveSessionToFile(StudySession session) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SESSION_FILE_PATH, true))) {
            String startTimeStr = (session.getStartTime() != null) ? dateFormat.format(session.getStartTime()) : "";
            String endTimeStr = (session.getEndTime() != null) ? dateFormat.format(session.getEndTime()) : "";
            String taskIdStr = (session.getTaskId() != null) ? String.valueOf(session.getTaskId()) : "";

            // Format: sessionId|userId|taskId|startTime|endTime|duration|sessionType|status
            String line = String.format("%d|%d|%s|%s|%s|%d|%s|%s",
                    session.getSessionId(),
                    session.getUserId(),
                    taskIdStr,
                    startTimeStr,
                    endTimeStr,
                    session.getDuration(),
                    session.getSessionType().name(),
                    session.getStatus().name()
            );

            bw.newLine();
            bw.write(line);
            System.out.println(">>> [Observer] Đã lưu lịch sử phiên học xuống file.");

        } catch (IOException e) {
            System.err.println("Lỗi ghi file lịch sử: " + e.getMessage());
        }
    }
}