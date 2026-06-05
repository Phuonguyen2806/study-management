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

    // Hàm khởi tạo (Constructor): Thiết lập giao diện ban đầu
    public FocusPanel() {
        initComponents();
    }

    // Gán bộ điều khiển (Controller) và kích hoạt cài đặt sự kiện nút bấm
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

    // Node 1: Tạo vùng hiển thị công việc (Nút bấm chọn Task và Chữ hiển thị tiến độ)
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

    // Node 2: Tạo vùng Đồng hồ (Nút chọn 3 chế độ Pomo và Chữ số đếm ngược thời gian)
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

    // Node 3: Tạo vùng Nút chức năng (Bắt đầu/Tạm dừng, Dừng lại, Hoàn thành công việc)
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

    // Kết nối các nút bấm trên giao diện tới các hàm xử lý tương ứng trong Controller
    private void setupEvents() {
        btnSelectTask.addActionListener(e -> controller.handleSelectTaskClick());
        btnAction.addActionListener(e -> controller.handleActionClick());
        btnStop.addActionListener(e -> controller.handleStopClick());
        btnCompleteTask.addActionListener(e -> controller.handleCompleteEarlyClick());

        btnModeFocus.addActionListener(e -> controller.handleModeChange(SessionType.FOCUS));
        btnModeShortBreak.addActionListener(e -> controller.handleModeChange(SessionType.SHORT_BREAK));
        btnModeLongBreak.addActionListener(e -> controller.handleModeChange(SessionType.LONG_BREAK));
    }

    // Bật hộp thoại danh sách công việc dạng thả xuống (Dropdown) để người dùng chọn
    public Task showTaskSelectionDialog(List<Task> tasks) {
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có công việc nào đang chờ!");
            return null;
        }
        return (Task) JOptionPane.showInputDialog(
                this, "Chọn công việc:", "Danh sách Task",
                JOptionPane.QUESTION_MESSAGE, null, tasks.toArray(), tasks.get(0));
    }

    // Bật hộp thoại yêu cầu nhập số lượng phiên Pomodoro dự kiến cho Task vừa chọn
    public int showEstimateDialog() {
        String input = JOptionPane.showInputDialog(this, "Nhập số phiên dự kiến:");
        try {
            return Math.max(1, Integer.parseInt(input));
        } catch (Exception e) {
            return 1;
        }
    }
    // Bật hộp thoại hỏi xác nhận khi người dùng chủ động bấm "Dừng lại" phiên học tập
    public boolean showConfirmStopDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Bạn muốn kết thúc hẳn phiên làm việc này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    // Bật hộp thoại hỏi xác nhận khi người dùng muốn bỏ qua thời gian nghỉ giải lao
    public boolean showConfirmSkipBreakDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Bạn muốn bỏ qua giờ nghỉ ngơi?", "Bỏ qua", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    // Bật hộp thoại hỏi xác nhận khi người dùng muốn báo hoàn thành công việc sớm hơn 25 phút
    public boolean showConfirmCompleteDialog() {
        int res = JOptionPane.showConfirmDialog(this, "Xác nhận hoàn thành bài tập sớm?", "Hoàn thành", JOptionPane.YES_NO_OPTION);
        return res == JOptionPane.YES_OPTION;
    }

    // Đổi màu sắc, độ dày viền của nút chế độ (Focus/Break) đang được chọn để làm nổi bật
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

    // [Observer Pattern] Nhận số giây đếm ngược từ Model và vẽ lại chữ hiển thị (mm:ss)
    @Override
    public void updateTime(int timeLeft) {
        String timeStr = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60);
        lblTime.setText(timeStr);
    }

    // [Observer Pattern] Tự động ẩn/hiện nút, đổi chữ, đổi kích thước giao diện khi trạng thái hệ thống thay đổi
    @Override
    public void updateState(FocusStatus state, SessionType type, Task task) {
        // Đổi chữ hiển thị trên nút chức năng chính theo trạng thái Timer
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

        // Định dạng tiêu đề cho nút Hủy tùy theo đang học hay đang nghỉ giải lao
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

        // Thay đổi toàn bộ giao diện chữ viết khu vực hiển thị Task dựa trên việc có Task nào đang chọn hay không
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