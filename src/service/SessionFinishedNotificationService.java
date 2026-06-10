package service;

import config.AppConstants;
import model.entity.SessionStatus;
import model.observer.FocusSessionEvent;
import model.entity.SessionType;
import model.observer.FocusSessionObserver;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;

/**
 * SERVICE: SessionFinishedNotificationService
 * Nhiệm vụ: Thông báo bằng âm thanh dài và hiển thị hộp thoại khi kết thúc phiên.
 */

public class SessionFinishedNotificationService implements FocusSessionObserver {

    public SessionFinishedNotificationService() {
    }

    @Override
    public void onSessionCompleted(FocusSessionEvent event) {
        // CHỈ xử lý nếu phiên học/nghỉ thực sự hoàn thành (hết giờ)
        if (event.getStudySession().getStatus() == SessionStatus.COMPLETED) {

            // 1. Phát tiếng reng dài
            playLongAlarm();

            // 2. Xác định nội dung thông báo
            String message = (event.getStudySession().getSessionType() == SessionType.FOCUS)
                    ? "Phiên học đã hoàn thành. Hãy nghỉ ngơi một chút nhé!"
                    : "Thời gian nghỉ đã hết. Hãy bắt đầu phiên học tiếp theo";

            // 3. Hiện thông báo trên màn hình
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        null,
                        message,
                        "Pomo Focus Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }
        // Nếu status là STOPPED_EARLY (do bấm dừng hoặc hoàn thành sớm),
        // hàm này sẽ không làm gì cả, tránh hiện thông báo sai ngữ cảnh.
    }


    private void playLongAlarm() {
        try {
            File audioFile = new File(AppConstants.FILE_ALARM);
            if (!audioFile.exists()) {
                System.err.println("Không tìm thấy file chuông tại: " + AppConstants.FILE_ALARM);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {
            System.err.println("Lỗi phát âm thanh: " + e.getMessage());
        }
    }
}
