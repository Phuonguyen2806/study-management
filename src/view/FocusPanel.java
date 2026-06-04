package view;

import controller.IFocusController;
import model.entity.FocusStatus;
import model.entity.SessionType;
import model.entity.Task;
import model.observer.FocusViewObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FocusPanel extends JPanel implements FocusViewObserver {
    private JButton btnSelectTask;
    private JLabel lblTaskProgress;
    private JButton btnModeFocus, btnModeShortBreak, btnModeLongBreak;
    private JLabel lblTime;

    private JButton btnAction;
    private JButton btnStop;
    private JButton btnCompleteTask;

    private IFocusController controller;

    private final Color primaryBlue = new Color(0, 102, 204);
    private final Color lightGrayBorder = new Color(200, 200, 200);
    private final Color dangerRed = new Color(220, 53, 69);
    private final Color successGreen = new Color(40, 167, 69);

    public FocusPanel() {
        initComponents();
    }

    public void setController(IFocusController controller) {
        this.controller = controller;
        this.setupEvents();
    }

    // ==========================================
    // ÁP DỤNG COMPOSITE PATTERN: Ráp nối các phần tử
    // ==========================================
    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // Lắp ráp các Node con vào Tree (Root Panel)
        add(Box.createVerticalGlue());
        add(createTaskSection());    // Node 1: Khu vực hiển thị Task
        add(Box.createVerticalStrut(20));
        add(createTimerSection());   // Node 2: Khu vực đồng hồ và chế độ
        add(Box.createVerticalStrut(40));
        add(createControlSection()); // Node 3: Khu vực nút điều khiển
        add(Box.createVerticalGlue());
    }

    // --- NODE 1: Khu vực Task ---
    private JPanel createTaskSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

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

        panel.add(btnSelectTask);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblTaskProgress);
        return panel;
    }

    // --- NODE 2: Khu vực Đồng hồ ---
    private JPanel createTimerSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

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

        lblTime = new JLabel("25:00");
        lblTime.setFont(new Font("Arial", Font.BOLD, 120));
        lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(modePanel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(lblTime);
        return panel;
    }

    // --- NODE 3: Khu vực Điều khiển ---
    private JPanel createControlSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel btnRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRowPanel.setBackground(Color.WHITE);

        btnAction = new JButton("Bắt đầu");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnAction.setBackground(primaryBlue);
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAction.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        btnStop = new JButton("Dừng lại");
        btnStop.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStop.setBackground(dangerRed);
        btnStop.setForeground(Color.WHITE);
        btnStop.setFocusPainted(false);
        btnStop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStop.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        btnStop.setVisible(false);

        btnRowPanel.add(btnAction);
        btnRowPanel.add(btnStop);

        btnCompleteTask = new JButton("Hoàn thành công việc");
        btnCompleteTask.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCompleteTask.setBackground(successGreen);
        btnCompleteTask.setForeground(Color.WHITE);
        btnCompleteTask.setFocusPainted(false);
        btnCompleteTask.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompleteTask.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCompleteTask.setBorder(BorderFactory.createEmptyBorder(14, 45, 14, 45));
        btnCompleteTask.setVisible(false);

        panel.add(btnRowPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(btnCompleteTask);
        return panel;
    }

    private void setupEvents() {
        btnSelectTask.addActionListener(e -> controller.handleSelectTaskClick());
        btnAction.addActionListener(e -> controller.handleActionClick());
        btnStop.addActionListener(e -> controller.handleStopClick());
        btnCompleteTask.addActionListener(e -> controller.handleCompleteEarlyClick());

        btnModeFocus.addActionListener(e -> controller.handleModeChange(SessionType.FOCUS));
        btnModeShortBreak.addActionListener(e -> controller.handleModeChange(SessionType.SHORT_BREAK));
        btnModeLongBreak.addActionListener(e -> controller.handleModeChange(SessionType.LONG_BREAK));
    }

    public Task showTaskSelectionDialog(List<Task> tasks) {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có công việc nào đang chờ!");
            return null;
        }
        return (Task) JOptionPane.showInputDialog(
                this, "Chọn công việc:", "Danh sách Task",
                JOptionPane.QUESTION_MESSAGE, null, tasks.toArray(), tasks.get(0));
    }

    public int showEstimateDialog() {
        String input = JOptionPane.showInputDialog(this, "Nhập số phiên dự kiến:");
        try {
            return Math.max(1, Integer.parseInt(input));
        } catch (Exception e) {
            return 1;
        }
    }

    public boolean showConfirmStopDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Bạn muốn kết thúc hẳn phiên làm việc này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public boolean showConfirmSkipBreakDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Bạn muốn bỏ qua giờ nghỉ ngơi?", "Bỏ qua", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    public boolean showConfirmCompleteDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Xác nhận hoàn thành bài tập sớm?", "Hoàn thành", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    private void setActiveModeStyle(JButton activeBtn) {
        JButton[] modeBtns = {btnModeFocus, btnModeShortBreak, btnModeLongBreak};
        for (JButton btn : modeBtns) {
            if (btn == activeBtn) {
                btn.setBackground(new Color(230, 242, 255));
                btn.setForeground(primaryBlue);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryBlue, 2),
                        BorderFactory.createEmptyBorder(7, 14, 7, 14)
                ));
            } else {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.DARK_GRAY);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(lightGrayBorder, 1),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        }
    }

    @Override
    public void updateTime(int timeLeft) {
        String timeStr = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
        lblTime.setText(timeStr);
    }

    @Override
    public void updateState(FocusStatus state, SessionType type, Task task) {
        if (state == FocusStatus.IDLE) {
            btnAction.setText("Bắt đầu");
            btnStop.setVisible(false);
        } else if (state == FocusStatus.RUNNING) {
            btnAction.setText("Tạm dừng");
            btnStop.setVisible(true);
        } else if (state == FocusStatus.PAUSED) {
            btnAction.setText("Tiếp tục");
            btnStop.setVisible(true);
        }

        if (type == SessionType.FOCUS) {
            setActiveModeStyle(btnModeFocus);
            btnStop.setText("Dừng lại");
            btnStop.setVisible(state != FocusStatus.IDLE);
        } else if (type == SessionType.SHORT_BREAK) {
            setActiveModeStyle(btnModeShortBreak);
            btnStop.setText("Bỏ qua");
            btnStop.setVisible(true);
        } else {
            setActiveModeStyle(btnModeLongBreak);
            btnStop.setText("Bỏ qua");
            btnStop.setVisible(true);
        }

        if (task != null) {
            btnSelectTask.setText(task.getTitle());
            btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 26));
            btnSelectTask.setForeground(Color.BLACK);
            btnSelectTask.setBackground(Color.WHITE);
            btnSelectTask.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
            lblTaskProgress.setText(String.format("Dự kiến: %d phiên | Đã làm: %d phiên", task.getEstPomo(), task.getCompPomo()));
            lblTaskProgress.setVisible(true);
            btnCompleteTask.setVisible(type == SessionType.FOCUS);
        } else {
            btnSelectTask.setText("+ Nhấn để chọn công việc");
            btnSelectTask.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btnSelectTask.setForeground(primaryBlue);
            btnSelectTask.setBackground(new Color(230, 242, 255));
            btnSelectTask.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryBlue, 0),
                    BorderFactory.createEmptyBorder(15, 50, 15, 50)
            ));
            lblTaskProgress.setVisible(false);
            btnCompleteTask.setVisible(false);
        }
    }
}