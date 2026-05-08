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
    private JLabel lblDailyStudyTime, lblPomodoroCount, lblPendingTasks, lblInProgressTasks, lblDoneTasks, lblOverdueTasks, lblTotalTasks;
    private JTable overdueTable, upcomingTable;
    private DefaultTableModel overdueModel, upcomingModel;
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
        // 1. Tạo Panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // --- PHẦN TRÊN: 4 Ô THỐNG KÊ (Nằm ở NORTH) ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setOpaque(false);

        // Khởi tạo các Label và gán vào Card
        statsPanel.add(createStatCard("Tổng tập trung", lblDailyStudyTime = new JLabel("0.0"), "", new Color(0, 102, 204)));
        statsPanel.add(createStatCard("Số Pomodoro", lblPomodoroCount = new JLabel("0"), "", new Color(255, 87, 34)));
        statsPanel.add(createStatCard("Chưa hoàn thành", lblTotalTasks = new JLabel("0"), "", new Color(0, 150, 136)));
        statsPanel.add(createStatCard("Đã hoàn thành", lblDoneTasks = new JLabel("0"), "", new Color(40, 167, 69)));
        // --- PHẦN DƯỚI: 2 DANH SÁCH BẢNG (Nằm ở CENTER) ---
        // Sử dụng GridLayout(2, 1) để hai bảng xếp chồng lên nhau và chiếm hết diện tích còn lại
        JPanel listsContainer = new JPanel(new GridLayout(2, 1, 0, 20));
        listsContainer.setOpaque(false);

        // Bảng 1: Trễ hạn
        overdueModel = new DefaultTableModel(new String[]{"Tên công việc", "Hạn chót", "Mức độ"}, 0);
        overdueTable = new JTable(overdueModel);
        listsContainer.add(createListSection("Công việc trễ hạn", overdueTable, new Color(220, 53, 69)));

        // Bảng 2: Sắp đến hạn
        upcomingModel = new DefaultTableModel(new String[]{"Tên công việc", "Hạn chót", "Mức độ"}, 0);
        upcomingTable = new JTable(upcomingModel);
        listsContainer.add(createListSection("Công việc sắp đến hạn", upcomingTable, new Color(0, 102, 204)));

        // --- GẮP CẢ 2 PHẦN VÀO MAIN PANEL ---
        mainPanel.add(statsPanel, BorderLayout.NORTH); // Đẩy 4 ô lên trên cùng
        mainPanel.add(listsContainer, BorderLayout.CENTER); // Bảng chiếm phần diện tích ở giữa/dưới

        return mainPanel;
    }

    /**
     * Hàm hỗ trợ tạo một phần danh sách có tiêu đề và bảng
     */
    private JPanel createListSection(String title, JTable table, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(titleColor);

        // Cấu hình bảng
        table.setRowHeight(25);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    // Thêm hàm này để Test hoặc gọi từ Controller
    public void updateDailyTables(List<Object[]> overdueData, List<Object[]> upcomingData) {
        overdueModel.setRowCount(0);
        for (Object[] row : overdueData) overdueModel.addRow(row);

        upcomingModel.setRowCount(0);
        for (Object[] row : upcomingData) upcomingModel.addRow(row);
    }
    private JPanel createWeeklyView() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // 1. Top Cards: Tóm tắt tuần
        JPanel topCards = new JPanel(new GridLayout(1, 2, 20, 0));
        topCards.setOpaque(false);
        topCards.add(createStatCard("Trung bình thời gian học", lblWeeklyAvgTime = new JLabel("0.0"), "", new Color(0, 102, 204)));
        topCards.add(createStatCard("Tỷ lệ hoàn thành tuần", lblWeeklyCompletionRate = new JLabel("0%"), "", new Color(0, 102, 204)));
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

                // 1. Cấu hình thông số
                this.setBackground(Color.WHITE);
                int paddingBottom = 60;
                int size = Math.min(getWidth(), getHeight() - paddingBottom) - 60;
                int x = (getWidth() - size) / 2;
                int y = 30;

                // Giả sử dữ liệu phần trăm
                int donePercent = 70;
                int progressPercent = 20;
                int overduePercent = 10;

                // Chuyển sang độ (tổng 360)
                int angleDone = (int) (donePercent * 3.6);
                int angleProgress = (int) (progressPercent * 3.6);
                int angleOverdue = 360 - angleDone - angleProgress;

                // 2. Vẽ biểu đồ Pie
                g2.setColor(new Color(40, 167, 69)); // Xanh lá
                g2.fillArc(x, y, size, size, 0, angleDone);

                g2.setColor(new Color(255, 193, 7)); // Vàng
                g2.fillArc(x, y, size, size, angleDone, angleProgress);

                g2.setColor(new Color(220, 53, 69)); // Đỏ
                g2.fillArc(x, y, size, size, angleDone + angleProgress, angleOverdue);

                // 3. Vẽ chữ % lên từng phần bánh
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));

                drawPercentText(g2, x, y, size, 0, angleDone, donePercent + "%");
                drawPercentText(g2, x, y, size, angleDone, angleProgress, progressPercent + "%");
                drawPercentText(g2, x, y, size, angleDone + angleProgress, angleOverdue, overduePercent + "%");

                // 4. Vẽ lỗ trắng ở giữa (Donut)
                g2.setColor(Color.WHITE);
                int innerSize = size / 2;
                int innerX = x + (size - innerSize) / 2;
                int innerY = y + (size - innerSize) / 2;
                g2.fillOval(innerX, innerY, innerSize, innerSize);

                // 5. Chú thích bên dưới (Legend) - Giống image_e64875.png
                drawLegend(g2, x + size / 2, y + size + 40);
            }

            // Hàm phụ để tính toán vị trí và vẽ text %
            private void drawPercentText(Graphics2D g2, int x, int y, int size, int startAngle, int arcAngle, String text) {
                if (arcAngle < 15) return; // Nếu phần bánh quá nhỏ thì không vẽ chữ để tránh lem

                double midAngle = Math.toRadians(startAngle + arcAngle / 2.0);
                double radius = size / 2.8; // Vị trí chữ (nằm giữa tâm và viền ngoài)

                int centerX = x + size / 2;
                int centerY = y + size / 2;

                int drawX = (int) (centerX + radius * Math.cos(midAngle));
                int drawY = (int) (centerY - radius * Math.sin(midAngle)); // Trục Y trong Java ngược với toán học

                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, drawX - fm.stringWidth(text) / 2, drawY + fm.getAscent() / 2);
            }

            private void drawLegend(Graphics2D g2, int centerX, int y) {
                String[] labels = {"Hoàn thành", "Đang làm", "Trễ hạn"};
                Color[] colors = {new Color(40, 167, 69), new Color(255, 193, 7), new Color(220, 53, 69)};
                int xOffset = -120;

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                for (int i = 0; i < 3; i++) {
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(centerX + xOffset, y, 12, 12, 4, 4);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(labels[i], centerX + xOffset + 18, y + 10);
                    xOffset += 90;
                }
            }
        };
        setupChartStyle(pnlPieChart, "Tỉ lệ hoàn thành công việc");

        chartPanel.add(pnlBarChart);
        chartPanel.add(pnlPieChart);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

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