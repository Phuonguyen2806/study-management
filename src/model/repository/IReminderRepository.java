package model.repository;

import model.entity.ReminderLog;

import java.util.List;

public interface IReminderRepository {
     void init(String filePath);
    // Lưu lịch sử gửi thông báo (Email hoặc Push)
     void saveReminder(ReminderLog log);
    // Lấy lịch sử nhắc nhở của một task cụ thể (để kiểm tra xem đã gửi chưa)
     List<ReminderLog> getAllLogs();
    boolean isTaskSuccessfullySent(int taskId);
    ReminderLog getLastLogForTask(int taskId);
}
