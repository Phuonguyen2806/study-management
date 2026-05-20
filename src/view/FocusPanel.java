package view;

import javax.swing.*;
import java.awt.*;

public class FocusPanel extends JPanel {
    private JButton btnSelectTask;
    private JLabel lblTaskProgress;
    private JButton btnModeFocus, btnModeShortBreak, btnModeLongBreak;
    private JLabel lblTime;

    private JButton btnAction;
    private JButton btnStop;
    private JButton btnCompleteTask;

    // Biến logic đồng hồ & nghiệp vụ
    private Timer timer;
    private final int TIME_FOCUS = 25 * 60;
    private final int TIME_SHORT_BREAK = 5 * 60;
    private final int TIME_LONG_BREAK = 15 * 60;
    private int timeLeft = TIME_FOCUS;

    private boolean isFocusMode = true; // Cờ kiểm tra xem có đang ở chế độ tập trung không
    private int estimatedSessions = 0;
    private int completedSessions = 0;

    // Màu sắc chủ đạo dùng chung
    private final Color primaryBlue = new Color(0, 102, 204);
    private final Color lightGrayBorder = new Color(200, 200, 200);
    private final Color dangerRed = new Color(220, 53, 69);
    private final Color successGreen = new Color(40, 167, 69);

    public FocusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Nút Thêm/Chọn Công Việc
        btnSelectTask = new JButton("+ Nhấn để chọn công việc");
        btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSelectTask.setForeground(primaryBlue);
        btnSelectTask.setBackground(new Color(230, 242, 255));
        btnSelectTask.setOpaque(true);
        btnSelectTask.setFocusPainted(false);
        btnSelectTask.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryBlue, 1, true),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        btnSelectTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelectTask.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTaskProgress = new JLabel("Dự kiến: 0 phiên | Đã làm: 0 phiên");
        lblTaskProgress.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTaskProgress.setForeground(Color.GRAY);
        lblTaskProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTaskProgress.setVisible(false);

        // Chế độ thời gian
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        modePanel.setBackground(Color.WHITE);

        Font modeFont = new Font("Segoe UI", Font.BOLD, 16);

        btnModeFocus = new JButton("Tập trung (25p)");
        btnModeShortBreak = new JButton("Nghỉ ngắn (5p)");
        btnModeLongBreak = new JButton("Nghỉ dài (15p)");

        JButton[] modeBtns = {btnModeFocus, btnModeShortBreak, btnModeLongBreak};
        for (JButton btn : modeBtns) {
            btn.setFont(modeFont);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            modePanel.add(btn);
        }

        // Cài đặt nút mặc định ban đầu là nút Tập trung
        setActiveModeStyle(btnModeFocus);

        // Đồng hồ
        lblTime = new JLabel("25:00");
        lblTime.setFont(new Font("Arial", Font.BOLD, 120));
        lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Các nút điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlPanel.setBackground(Color.WHITE);

        // Nút Bắt đầu
        btnAction = new JButton("Bắt đầu");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAction.setBackground(primaryBlue);
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAction.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        // Nút Dừng lại
        btnStop = new JButton("Dừng lại");
        btnStop.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStop.setBackground(dangerRed);
        btnStop.setForeground(Color.WHITE);
        btnStop.setFocusPainted(false);
        btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStop.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnStop.setVisible(false);

        controlPanel.add(btnAction);
        controlPanel.add(btnStop);

        // Nút Hoàn thành
        btnCompleteTask = new JButton("Hoàn thành công việc");
        btnCompleteTask.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCompleteTask.setBackground(successGreen);
        btnCompleteTask.setForeground(Color.WHITE);
        btnCompleteTask.setFocusPainted(false);
        btnCompleteTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompleteTask.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCompleteTask.setBorder(BorderFactory.createEmptyBorder(14, 45, 14, 45));
        btnCompleteTask.setVisible(false);

        // ---------------- CẤU TRÚC LAYOUT ----------------
        add(Box.createVerticalGlue()); // Khoảng trống co giãn trên cùng

        // CỤM 1: Thông tin công việc
        add(btnSelectTask);
        add(Box.createVerticalStrut(10));
        add(lblTaskProgress);

        add(Box.createVerticalGlue()); // Khoảng trống co giãn giữa cụm 1 và 2

        // CỤM 2: Đồng hồ
        add(modePanel);
        add(Box.createVerticalStrut(30)); // Đã tăng từ 15 lên 30 cho thoáng phần trên
        add(lblTime);
        add(Box.createVerticalStrut(40)); // Đã tăng từ 15 lên 40 cho thoáng phần dưới
        add(controlPanel);

        add(Box.createVerticalGlue()); // Khoảng trống co giãn giữa cụm 2 và 3

        // CỤM 3: Hoàn thành
        add(btnCompleteTask);

        add(Box.createVerticalGlue()); // Khoảng trống co giãn dưới cùng
        // -----------------------------------------------------

        initTimerLogic();
        initTaskLogic();
    }

    // Hàm cập nhật màu sắc để làm nổi bật nút chế độ đang được chọn
    private void setActiveModeStyle(JButton activeBtn) {
        JButton[] modeBtns = {btnModeFocus, btnModeShortBreak, btnModeLongBreak};
        for (JButton btn : modeBtns) {
            if (btn == activeBtn) {
                // Nút đang chọn: Nền xanh nhạt, chữ và viền xanh đậm
                btn.setBackground(new Color(230, 242, 255));
                btn.setForeground(primaryBlue);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryBlue, 2),
                        BorderFactory.createEmptyBorder(7, 14, 7, 14)
                ));
            } else {
                // Các nút khác: Nền trắng, chữ xám đen, viền xám nhạt
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.DARK_GRAY);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(lightGrayBorder, 1),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        }
    }

    private void updateProgressLabel() {
        lblTaskProgress.setText(String.format("Dự kiến: %d phiên | Đã làm: %d phiên", estimatedSessions, completedSessions));
    }

    private void initTaskLogic() {
        btnSelectTask.addActionListener(e -> {
            String[] dummyTasks = {
                    "Làm bài tập Java Swing",
                    "Thiết kế giao diện Figma",
                    "Ôn thi cuối kỳ môn CSDL",
                    "Đọc sách Clean Code"
            };

            String selectedTask = (String) JOptionPane.showInputDialog(
                    this,
                    "Vui lòng chọn một công việc chưa hoàn thành:",
                    "Chọn công việc",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    dummyTasks,
                    dummyTasks[0]
            );

            if (selectedTask != null && !selectedTask.trim().isEmpty()) {
                String inputStr = JOptionPane.showInputDialog(
                        this,
                        "Nhập số phiên Pomodoro dự kiến (ví dụ: 2):",
                        "Ước tính",
                        JOptionPane.QUESTION_MESSAGE
                );

                try {
                    estimatedSessions = Integer.parseInt(inputStr);
                    if (estimatedSessions <= 0) estimatedSessions = 1;
                } catch (NumberFormatException ex) {
                    estimatedSessions = 1;
                }

                completedSessions = 0;

                btnSelectTask.setText(selectedTask);
                btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 26));
                btnSelectTask.setForeground(Color.BLACK);
                btnSelectTask.setBackground(Color.WHITE);
                btnSelectTask.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                updateProgressLabel();
                lblTaskProgress.setVisible(true);
                btnCompleteTask.setVisible(true);
            }
        });

        btnCompleteTask.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đánh dấu hoàn thành công việc này?",
                    "Xác nhận hoàn thành",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                btnSelectTask.setText("+ Nhấn để chọn công việc");
                btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 18));
                btnSelectTask.setForeground(primaryBlue);
                btnSelectTask.setBackground(new Color(230, 242, 255));
                btnSelectTask.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryBlue, 1, true),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                ));

                lblTaskProgress.setVisible(false);
                btnCompleteTask.setVisible(false);

                estimatedSessions = 0;
                completedSessions = 0;

                resetToInitialState();
                JOptionPane.showMessageDialog(this, "Công việc đã được chuyển sang danh sách Hoàn thành!");
            }
        });
    }

    private void initTimerLogic() {
        timer = new Timer(1000, e -> {
            if (timeLeft > 0) {
                timeLeft--;
                updateTimeLabel();
            } else {
                timer.stop();

                if (isFocusMode && !btnSelectTask.getText().startsWith("+")) {
                    completedSessions++;
                    updateProgressLabel();
                    JOptionPane.showMessageDialog(this, "Hết thời gian! Chúc mừng bạn đã hoàn thành 1 phiên tập trung.");
                } else if (isFocusMode) {
                    JOptionPane.showMessageDialog(this, "Hết thời gian tập trung!");
                } else {
                    JOptionPane.showMessageDialog(this, "Hết thời gian nghỉ ngơi, quay lại làm việc thôi!");
                }

                resetToInitialState();
            }
        });

        btnAction.addActionListener(e -> {
            String text = btnAction.getText();
            if (text.equals("Bắt đầu")) {
                timer.start();
                btnAction.setText("Tạm dừng");
                btnStop.setVisible(true);
            }
            else if (text.equals("Tạm dừng")) {
                timer.stop();
                btnAction.setText("Tiếp tục");
            }
            else if (text.equals("Tiếp tục")) {
                timer.start();
                btnAction.setText("Tạm dừng");
            }
        });

        btnStop.addActionListener(e -> {
            timer.stop();
            int choice = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn thoát phiên này không? Thời gian của phiên này sẽ không được tính.",
                    "Xác nhận dừng",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                resetToInitialState();
            } else {
                btnAction.setText("Tiếp tục");
            }
        });

        btnModeFocus.addActionListener(e -> {
            setTimerMode(TIME_FOCUS);
            isFocusMode = true;
            setActiveModeStyle(btnModeFocus);
        });
        btnModeShortBreak.addActionListener(e -> {
            setTimerMode(TIME_SHORT_BREAK);
            isFocusMode = false;
            setActiveModeStyle(btnModeShortBreak);
        });
        btnModeLongBreak.addActionListener(e -> {
            setTimerMode(TIME_LONG_BREAK);
            isFocusMode = false;
            setActiveModeStyle(btnModeLongBreak);
        });
    }

    private void resetToInitialState() {
        timer.stop();
        if(isFocusMode) timeLeft = TIME_FOCUS;
        else if(btnModeShortBreak.getForeground().equals(primaryBlue)) timeLeft = TIME_SHORT_BREAK;
        else timeLeft = TIME_LONG_BREAK;

        updateTimeLabel();
        btnAction.setText("Bắt đầu");
        btnStop.setVisible(false);
    }

    private void setTimerMode(int seconds) {
        timer.stop();
        timeLeft = seconds;
        updateTimeLabel();
        btnAction.setText("Bắt đầu");
        btnStop.setVisible(false);
    }

    private void updateTimeLabel() {
        int m = timeLeft / 60;
        int s = timeLeft % 60;
        lblTime.setText(String.format("%02d:%02d", m, s));
    }
}