package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_BG = new Color(245, 247, 251);

    // Thành phần Thống kê Ngày
    private JLabel lblDailyStudyTime, lblPomodoroCount, lblPendingTasks, lblInProgressTasks, lblDoneTasks, lblOverdueTasks;

    // Thành phần Thống kê Tuần
    private JLabel lblWeeklyAvgTime, lblWeeklyCompletionRate;
    private JPanel pnlBarChart, pnlPieChart;
    private JTable tblUpcomingTasks;
    private DefaultTableModel tableModel;

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabPane.addTab("Thống kê Ngày", createDailyView());
        tabPane.addTab("Thống kê Tuần", createWeeklyView());

        add(tabPane, BorderLayout.CENTER);
    }
    public void showNoDataMessage() {
        // Variation #1: Không có dữ liệu [cite: 223]
        JOptionPane.showMessageDialog(this,
                "Bạn chưa có dữ liệu học tập trong khoảng thời gian này. Hãy bắt đầu phiên Pomodoro đầu tiên!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }


    public void showError(String message) {
        // Variation #2: Lỗi kết nối [cite: 224]
        JOptionPane.showMessageDialog(this, "Lỗi: " + message, "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
    }

    // --- CÁC PHƯƠNG THỨC HIỂN THỊ THEO YÊU CẦU ---

    /** THỐNG KÊ NGÀY */
    public void displayDailyStudyTime(double time) {
        lblDailyStudyTime.setText(time + " giờ");
    }

    public void displayPomodoroCount(int count) {
        lblPomodoroCount.setText(count + " Pomo");
    }

    public void displayTaskStatus(Map<String, Integer> stats) {
        // Giả sử stats chứa "total" và "completed"
        lblPendingTasks.setText(String.valueOf(stats.getOrDefault("PENDING", 0)));
        lblInProgressTasks.setText(String.valueOf(stats.getOrDefault("IN_PROGRESS", 0)));
        lblDoneTasks.setText(String.valueOf(stats.getOrDefault("DONE", 0)));
        lblOverdueTasks.setText(String.valueOf(stats.getOrDefault("OVERDUE", 0)));
    }

    /** THỐNG KÊ TUẦN */
    public void displayWeeklyStudyTime(double avgTime) {
        lblWeeklyAvgTime.setText(avgTime + " giờ/ngày");
    }

    public void showStudyTimeChart(Map<String, Double> studyTimeByDay) {
        // Logic: Cập nhật dữ liệu cho biểu đồ cột (Bar Chart)
        pnlBarChart.repaint();
        System.out.println("Đang vẽ biểu đồ cột cho: " + studyTimeByDay);
    }

    public void showTaskCompletionPieChart(double completionRate) {
        lblWeeklyCompletionRate.setText(completionRate + "%");
        // Logic: Cập nhật dữ liệu cho biểu đồ tròn (Pie Chart)
        pnlPieChart.repaint();
    }

    public void displayUpcomingTasks(List<Object[]> upcomingTasks) {
        tableModel.setRowCount(0);
        for (Object[] task : upcomingTasks) {
            tableModel.addRow(task);
        }
    }

    // --- GIAO DIỆN CHI TIẾT ---

    private JPanel createDailyView() {
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        gridPanel.setBackground(COLOR_BG);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Hàng 1: Các màu xanh dương, cam, xanh teal
        gridPanel.add(createStatCard("Tổng giờ học", lblDailyStudyTime = new JLabel("0.0"), "📅", new Color(0, 102, 204)));
        gridPanel.add(createStatCard("Số Pomodoro", lblPomodoroCount = new JLabel("0"), "🍅", new Color(255, 87, 34)));
        gridPanel.add(createStatCard("Đang làm", lblInProgressTasks = new JLabel("0"), "⏳", new Color(0, 150, 136)));

        // Hàng 2: Các màu xám, xanh lá, đỏ
        gridPanel.add(createStatCard("Chưa làm", lblPendingTasks = new JLabel("0"), "📋", new Color(108, 117, 125)));
        gridPanel.add(createStatCard("Hoàn thành", lblDoneTasks = new JLabel("0"), "✅", new Color(40, 167, 69)));
        gridPanel.add(createStatCard("Trễ hạn", lblOverdueTasks = new JLabel("0"), "⚠️", new Color(220, 53, 69)));

        return gridPanel;
    }
    private JPanel createWeeklyView() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 1. Top Cards: Tóm tắt tuần
        JPanel topCards = new JPanel(new GridLayout(1, 2, 20, 0));
        topCards.setOpaque(false);
        topCards.add(createStatCard("Trung bình thời gian học", lblWeeklyAvgTime = new JLabel("0.0"), "📈", new Color(0, 102, 204)));
        topCards.add(createStatCard("Tỷ lệ hoàn thành tuần", lblWeeklyCompletionRate = new JLabel("0%"), "🎯", new Color(0, 102, 204)));
        mainPanel.add(topCards, BorderLayout.NORTH);

        // 2. Center: Biểu đồ - SỬ DỤNG GRIDLAYOUT
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartPanel.setOpaque(false);
        // Tăng chiều cao tối thiểu cho khu vực biểu đồ để hình tròn không bị dẹt
        chartPanel.setPreferredSize(new Dimension(0, 300));

        // BIỂU ĐỒ CỘT
        pnlBarChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // --- DỮ LIỆU GIẢ ĐỂ XEM GIAO DIỆN ---
                String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
                double[] hours = {2.5, 4.0, 1.5, 5.5, 3.0, 6.5, 2.0}; // Số giờ học mẫu
                double maxHours = 8.0; // Mốc cao nhất để tính tỷ lệ cột

                int n = days.length;
                int margin = 30;
                int spacing = 15;
                int availableWidth = getWidth() - (2 * margin);
                int barWidth = (availableWidth - (spacing * (n - 1))) / n;
                int baseLineY = getHeight() - 40;
                int maxHeight = getHeight() - 100;

                for (int i = 0; i < n; i++) {
                    // 1. Tính tọa độ x cho từng cột
                    int x = margin + i * (barWidth + spacing);

                    // 2. Tính chiều cao cột dựa trên dữ liệu mẫu
                    int barHeight = (int) ((hours[i] / maxHours) * maxHeight);
                    int y = baseLineY - barHeight;

                    // 3. Vẽ cột (Đổ màu Gradient cho chuyên nghiệp)
                    GradientPaint gp = new GradientPaint(x, y, COLOR_PRIMARY, x, baseLineY, new Color(100, 180, 255));
                    g2.setPaint(gp);
                    g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8); // Bo góc 8px

                    // 4. Vẽ tên Thứ bên dưới
                    g2.setColor(Color.GRAY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = x + (barWidth - fm.stringWidth(days[i])) / 2;
                    g2.drawString(days[i], textX, baseLineY + 25);

                    // 5. Vẽ số giờ trên đầu cột
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    String hourText = hours[i] + "h";
                    int hourX = x + (barWidth - g2.getFontMetrics().stringWidth(hourText)) / 2;
                    g2.drawString(hourText, hourX, y - 5);
                }
            }
        };
        setupChartStyle(pnlBarChart, "Thời gian tập trung các ngày trong tuần");

        pnlPieChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = Math.min(getWidth(), getHeight()) - 60; // Chỉnh size
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2 + 10;

                // Vẽ mẫu các phần của hình tròn (Ví dụ: Xong 70%, Đang làm 20%, Trễ 10%)
                g2.setColor(new Color(40, 167, 69)); // Xanh lá - Done
                g2.fillArc(x, y, size, size, 0, 250);

                g2.setColor(new Color(255, 193, 7)); // Vàng - In Progress
                g2.fillArc(x, y, size, size, 250, 70);

                g2.setColor(new Color(220, 53, 69)); // Đỏ - Overdue
                g2.fillArc(x, y, size, size, 320, 40);

                // Vẽ một hình tròn trắng nhỏ ở giữa để tạo thành biểu đồ Donut
                g2.setColor(Color.WHITE);
                int innerSize = size / 2;
                int innerX = (getWidth() - innerSize) / 2;
                int innerY = (getHeight() - innerSize) / 2 + 10;
                g2.fillOval(innerX, innerY, innerSize, innerSize);
            }
        };
        setupChartStyle(pnlPieChart, "Tỉ lệ hoàn thành công việc");

        chartPanel.add(pnlBarChart);
        chartPanel.add(pnlPieChart);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // 3. Bottom: Bảng danh sách
        tableModel = new DefaultTableModel(new String[]{"Tên bài tập", "Hạn chót", "Mức độ"}, 0);
        tblUpcomingTasks = new JTable(tableModel);
        tblUpcomingTasks.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tblUpcomingTasks);
        scrollPane.setPreferredSize(new Dimension(0, 150)); // Giảm nhẹ chiều cao bảng để nhường chỗ cho biểu đồ
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bài tập sắp đến hạn"));

        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        return mainPanel;
    }

    /**
     * Hàm phụ trợ để tránh lặp code setup style
     */
    private void setupChartStyle(JPanel panel, String title) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)), title),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }
    private JPanel createStatCard(String title, JLabel valueLabel, String icon, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);

        // Tạo viền bo góc và đổ bóng nhẹ bằng LineBorder màu nhạt
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 238, 242), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Tiêu đề nhỏ bên trên
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(120, 130, 140));

        // Giá trị số lớn ở giữa - Sử dụng màu accent Color để làm nổi bật
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);

        // Biểu tượng icon bên phải
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 35));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

}