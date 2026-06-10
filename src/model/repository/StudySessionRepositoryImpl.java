package model.repository;

import config.AppConstants;
import model.entity.StudySession;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class StudySessionRepositoryImpl implements IStudySessionRepository {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void save(StudySession session) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AppConstants.FILE_STUDY_SESSIONS, true))) {
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
            System.out.println(">>> [Repository] Đã lưu lịch sử phiên học xuống file.");

        } catch (IOException e) {
            System.err.println("Lỗi ghi file lịch sử: " + e.getMessage());
        }
    }
}
