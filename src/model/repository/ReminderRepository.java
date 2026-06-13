package model.repository;

import config.AppConstants;
import model.entity.ReminderLog;
import model.entity.Task;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReminderRepository implements IReminderRepository {
    private final String FILE_PATH = AppConstants.FILE_REMINDERS;
    private String filePath;

    public ReminderRepository() {
        init(FILE_PATH);
    }

    @Override
    public void init(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi tạo file: " + e.getMessage());
        }
    }

    @Override
    public void saveReminder(ReminderLog log) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String line = String.format("%d|%s|%s|%d|%d",
                log.getReminderId(),
                sdf.format(log.getTimeSent()),
                log.getMethod(),
                log.getIsSent(),
                log.getTaskId()
        );
        System.out.println(line);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            File file = new File(filePath);
            if (file.length() > 0) {
                writer.newLine(); // Đảm bảo bắt đầu bằng một dòng mới
            }
            writer.write(line);
            writer.flush();
        } catch (IOException e) {
        }
    }

    @Override
    public List<ReminderLog> getAllLogs() {
        List<ReminderLog> logs = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return logs;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ReminderLog log = ReminderLog.parse(line);
                if (log != null) logs.add(log);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }
    @Override
    public boolean isTaskSuccessfullySent(int taskId) {
        List<ReminderLog> allLogs = getAllLogs();
        for (ReminderLog log : allLogs) {
            // Chỉ cần tìm thấy 1 lần thành công (isSent == 1) là dừng
            if (log.getTaskId() == taskId && log.getIsSent() == 1) {
                return true; // Đã từng gửi thành công
            }
        }
        return false; // Nếu không tìm thấy log thành công, nghĩa là cần phải gửi tiếp
    }
    public ReminderLog getLastLogForTask(int taskId) {
        List<ReminderLog> allLogs = getAllLogs();
        ReminderLog latestLog = null;

        for (ReminderLog log : allLogs) {
            if (log.getTaskId() == taskId) {
                // Nếu đây là lần đầu thấy hoặc log này mới hơn log trước đó
                if (latestLog == null || log.getTimeSent().getTime() > latestLog.getTimeSent().getTime()) {
                    latestLog = log;
                }
            }
        }
        return latestLog; // Trả về log mới nhất, hoặc null nếu chưa có lần thử nào
    }
}