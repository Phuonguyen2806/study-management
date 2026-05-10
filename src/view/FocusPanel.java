package view;

import javax.swing.*;
import java.awt.*;

public class FocusPanel extends JPanel {
    private JButton btnSelectTask;
    private JLabel lblTaskProgress;
    private JButton btnModeFocus, btnModeShortBreak, btnModeLongBreak;
    private JLabel lblTime;

    // Các nút điều khiển chính
    private JButton btnAction;
    private JButton btnStop;
    private JButton btnCompleteTask;

    // Biến logic đồng hồ
    private Timer timer;
    private final int TIME_FOCUS = 25 * 60;
    private final int TIME_SHORT_BREAK = 5 * 60;
    private final int TIME_LONG_BREAK = 15 * 60;
    private int timeLeft = TIME_FOCUS;

    public FocusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Nút Thêm/Chọn Công Việc
        btnSelectTask = new JButton("+ Nhấn để chọn công việc");
        btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnSelectTask.setForeground(new Color(0, 102, 204));
        btnSelectTask.setBackground(new Color(230, 242, 255));
        btnSelectTask.setOpaque(true);
        btnSelectTask.setFocusPainted(false);
        btnSelectTask.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        btnSelectTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelectTask.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTaskProgress = new JLabel("Dự kiến: 2 phiên | Đã làm: 0 phiên");
        lblTaskProgress.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTaskProgress.setForeground(Color.GRAY);
        lblTaskProgress.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTaskProgress.setVisible(false);

        // Chế độ thời gian
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        modePanel.setBackground(Color.WHITE);

        Font modeFont = new Font("Segoe UI", Font.BOLD, 16);
        Insets modeMargin = new Insets(8, 15, 8, 15);

        btnModeFocus = new JButton("Tập trung (25p)");
        btnModeFocus.setFont(modeFont);
        btnModeFocus.setMargin(modeMargin);

        btnModeShortBreak = new JButton("Nghỉ ngắn (5p)");
        btnModeShortBreak.setFont(modeFont);
        btnModeShortBreak.setMargin(modeMargin);

        btnModeLongBreak = new JButton("Nghỉ dài (15p)");
        btnModeLongBreak.setFont(modeFont);
        btnModeLongBreak.setMargin(modeMargin);

        modePanel.add(btnModeFocus);
        modePanel.add(btnModeShortBreak);
        modePanel.add(btnModeLongBreak);

        // Đồng hồ
        lblTime = new JLabel("25:00");
        lblTime.setFont(new Font("Arial", Font.BOLD, 120));
        lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Các nút điều khiển
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        controlPanel.setBackground(Color.WHITE);

        btnAction = new JButton("Bắt đầu");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAction.setMargin(new Insets(10, 40, 10, 40));
        btnAction.setBackground(Color.BLACK);
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnStop = new JButton("Dừng lại");
        btnStop.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStop.setMargin(new Insets(10, 30, 10, 30));
        btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStop.setVisible(false);

        controlPanel.add(btnAction);
        controlPanel.add(btnStop);

        // Nút Hoàn thành
        btnCompleteTask = new JButton("Hoàn thành Task");
        btnCompleteTask.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCompleteTask.setMargin(new Insets(8, 20, 8, 20));
        btnCompleteTask.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCompleteTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompleteTask.setVisible(false);


        add(Box.createVerticalGlue()); // Lực đẩy từ trần nhà xuống
        add(btnSelectTask);
        add(Box.createVerticalStrut(10));
        add(lblTaskProgress);
        add(Box.createVerticalStrut(30));
        add(modePanel);
        add(Box.createVerticalStrut(5));
        add(lblTime);
        add(Box.createVerticalStrut(10));
        add(controlPanel);
        add(Box.createVerticalStrut(30));
        add(btnCompleteTask);
        add(Box.createVerticalGlue()); // Lực đẩy từ sàn nhà lên

        initTimerLogic();
        initTaskLogic();
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
                btnSelectTask.setText(selectedTask);
                btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 26));
                btnSelectTask.setForeground(Color.BLACK);
                btnSelectTask.setBackground(Color.WHITE);
                btnSelectTask.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
                btnSelectTask.setForeground(new Color(0, 102, 204));
                btnSelectTask.setBackground(new Color(230, 242, 255));
                btnSelectTask.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 102, 204), 1, true),
                        BorderFactory.createEmptyBorder(10, 30, 10, 30)
                ));

                lblTaskProgress.setVisible(false);
                btnCompleteTask.setVisible(false);

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
                JOptionPane.showMessageDialog(this, "Hết thời gian! Chúc mừng bạn đã hoàn thành phiên.");
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
                    "Bạn có chắc chắn muốn thoát phiên này không?",
                    "Xác nhận dừng",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                resetToInitialState();
            } else {
                btnAction.setText("Tiếp tục");
            }
        });

        btnModeFocus.addActionListener(e -> setTimerMode(TIME_FOCUS));
        btnModeShortBreak.addActionListener(e -> setTimerMode(TIME_SHORT_BREAK));
        btnModeLongBreak.addActionListener(e -> setTimerMode(TIME_LONG_BREAK));
    }

    private void resetToInitialState() {
        timer.stop();
        timeLeft = TIME_FOCUS;
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