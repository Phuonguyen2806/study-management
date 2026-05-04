package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class TaskPanel extends JPanel {
    private DefaultTableModel tableModel;

    // Sử dụng bộ constants từ MainFrame
    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    // Thành phần cần cập nhật động
    private JPanel pnlDailyCardsContainer;
    private JLabel lblDailyTitle;
    private JComboBox<String> cbGlobalPriority;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnRemove;

    public TaskPanel() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(new EmptyBorder(15, 15, 15, 15));
        this.setBackground(Color.WHITE);

        // --- PHẦN TRÊN: LỊCH (2 HÀNG) & CHI TIẾT THEO THỨ ---
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlTop.setOpaque(false);

        pnlTop.add(createWeeklyCalendarGrid()); // Khung 1
        pnlTop.add(createDailyTaskSection());   // Khung 2

        // --- PHẦN DƯỚI: TẤT CẢ TASK & BỘ LỌC ƯU TIÊN ---
        JPanel pnlBottom = createGlobalTaskSection(); // Khung 3

        this.add(pnlTop, BorderLayout.NORTH);
        this.add(pnlBottom, BorderLayout.CENTER);
    }

    // KHUNG 1: LỊCH 2 HÀNG - CÓ THỂ CHỌN THỨ
    private JPanel createWeeklyCalendarGrid() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("Lịch học");
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Grid 2 hàng, 4 cột
        JPanel grid = new JPanel(new GridLayout(2, 4, 15, 15));
        grid.setOpaque(false);

        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN", ""};

        for (String day : days) {
            if (day.isEmpty()) {
                grid.add(new JLabel(""));
                continue;
            }
            // Gọi hàm tạo ô vuông
            grid.add(createDayBox(day, grid));
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createDayBox(String day, JPanel parentGrid) {
        JPanel dayBox = new JPanel();
        // Xếp các thành phần theo cột (từ trên xuống dưới)
        dayBox.setLayout(new BoxLayout(dayBox, BoxLayout.Y_AXIS));
        dayBox.setOpaque(false);
        dayBox.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
        dayBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ép ô thành hình vuông to
        dayBox.setPreferredSize(new Dimension(100, 100));

        // 1. Khoảng trống phía trên để đẩy chữ xuống giữa
        dayBox.add(Box.createVerticalGlue());

        // 2. Nhãn tên Thứ
        JLabel lblDay = new JLabel(day);
        lblDay.setFont(FONT_BOLD);
        lblDay.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa chữ
        dayBox.add(lblDay);

        // 3. Khoảng cách nhỏ giữa chữ và thanh màu (giống hình mockup)
        dayBox.add(Box.createVerticalStrut(8));

        // 4. Panel chứa các thanh màu nằm ngang
        JPanel pnlIndicators = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        pnlIndicators.setOpaque(false);
        pnlIndicators.setMaximumSize(new Dimension(100, 5)); // Khống chế chiều cao thanh màu

        // Ví dụ: Hiển thị thanh màu cho Thứ 4 như trong hình
        if (day.equals("T4")) {
            pnlIndicators.add(createHorizontalIndicator(COLOR_PRIMARY));
            pnlIndicators.add(createHorizontalIndicator(new Color(40, 167, 69)));
        }
        dayBox.add(pnlIndicators);

        // 5. Khoảng trống phía dưới để cân bằng với phía trên
        dayBox.add(Box.createVerticalGlue());

        // Sự kiện click
        dayBox.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                updateDailyView(day);
                resetCalendarBorders(parentGrid);
                dayBox.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2, true));
                lblDay.setForeground(COLOR_PRIMARY);
            }
        });

        return dayBox;
    }

    // KHUNG 2: CHI TIẾT TASK THEO THỨ (Card có MatteBorder bên trái)
    private JPanel createDailyTaskSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        lblDailyTitle = new JLabel("Task - Thứ 2"); // Mặc định
        lblDailyTitle.setFont(FONT_BOLD);
        panel.add(lblDailyTitle, BorderLayout.NORTH);

        pnlDailyCardsContainer = new JPanel();
        pnlDailyCardsContainer.setLayout(new BoxLayout(pnlDailyCardsContainer, BoxLayout.Y_AXIS));
        pnlDailyCardsContainer.setOpaque(false);

        // Load mặc định 1 vài card
        updateDailyView("Thứ 2");

        panel.add(new JScrollPane(pnlDailyCardsContainer), BorderLayout.CENTER);
        return panel;
    }

    // KHUNG 3: TẤT CẢ TASK & BỘ LỌC ƯU TIÊN
    private JPanel createGlobalTaskSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Thanh điều hướng Khung 3
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Tất cả Task");
        lblTitle.setFont(FONT_TITLE);

        cbGlobalPriority = new JComboBox<>(new String[]{"Ưu tiên: Tất cả", "High", "Medium", "Low"});
        cbGlobalPriority.setFont(FONT_REGULAR);

        btnAdd = new JButton("+ Thêm Task");
        btnAdd.setBackground(COLOR_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(FONT_BOLD);

        pnlHeader.add(lblTitle);
        pnlHeader.add(cbGlobalPriority); // Bộ lọc ưu tiên
        pnlHeader.add(btnAdd);

        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        // Task mẫu dạng card ngang dài (Hình 2 bạn gửi)
        listContainer.add(createLongTaskCard("Làm bài tập React Hooks", "high", "in-progress", "Lập trình Web"));
        listContainer.add(Box.createVerticalStrut(10));
        listContainer.add(createLongTaskCard("Học Unit 5", "medium", "pending", "Tiếng Anh"));

        panel.add(pnlHeader, BorderLayout.NORTH);
        panel.add(new JScrollPane(listContainer), BorderLayout.CENTER);
        return panel;
    }

    // --- HÀM CẬP NHẬT GIAO DIỆN KHI CHỌN THỨ ---
    private void updateDailyView(String dayName) {
        lblDailyTitle.setText("Task - " + dayName);
        pnlDailyCardsContainer.removeAll();

        // Giả lập load task theo thứ
        pnlDailyCardsContainer.add(createDailyMiniCard("Làm bài tập React", "in-progress", "Lập trình Web", COLOR_PRIMARY));
        pnlDailyCardsContainer.add(Box.createVerticalStrut(10));

        pnlDailyCardsContainer.revalidate();
        pnlDailyCardsContainer.repaint();
    }

    // --- HÀM TẠO CARD CHO KHUNG 2 (Hình 1 bạn gửi) ---
    private JPanel createDailyMiniCard(String title, String status, String subject, Color accent) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        // Đường kẻ xanh dọc bên trái như mockup
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel("<html>" + title + "</html>");
        lblTitle.setFont(FONT_BOLD);

        JPanel pnlInfo = new JPanel(new GridLayout(2, 1));
        pnlInfo.setOpaque(false);
        JLabel lblSub = new JLabel(subject);
        lblSub.setFont(FONT_REGULAR);
        lblSub.setForeground(Color.GRAY);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(255, 245, 200)); // Màu vàng nhạt
        lblStatus.setFont(FONT_STATUS);

        pnlInfo.add(lblSub);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(pnlInfo, BorderLayout.CENTER);
        card.add(lblStatus, BorderLayout.EAST); // Badge in-progress

        return card;
    }

    // --- HÀM TẠO CARD CHO KHUNG 3 (Hình 2 bạn gửi) ---
    private JPanel createLongTaskCard(String title, String priority, String status, String subject) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Trái: Checkbox + Dot
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);
        pnlLeft.add(new JCheckBox());
        JLabel dot = new JLabel("●"); dot.setForeground(COLOR_PRIMARY);
        pnlLeft.add(dot);

        // Giữa: Title + tag
        JPanel pnlMid = new JPanel(new GridLayout(2, 1));
        pnlMid.setOpaque(false);
        JLabel lblT = new JLabel(title); lblT.setFont(FONT_BOLD);

        JPanel pnlTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTag.setOpaque(false);
        pnlTag.add(createBadge(priority, new Color(255, 230, 230), Color.RED));
        pnlTag.add(createBadge(status, new Color(230, 240, 255), COLOR_PRIMARY));
        pnlTag.add(createBadge(subject, new Color(245, 245, 245), Color.DARK_GRAY));

        pnlMid.add(lblT); pnlMid.add(pnlTag);

        // Phải: Sửa/Xóa
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        pnlRight.setOpaque(false);
        btnEdit = new JButton("Sửa");
        pnlRight.add(btnEdit);
        btnRemove = new JButton("Xóa");
        pnlRight.add(btnRemove);

        card.add(pnlLeft, BorderLayout.WEST);
        card.add(pnlMid, BorderLayout.CENTER);
        card.add(pnlRight, BorderLayout.EAST);
        return card;
    }
    /**
     * Tạo thanh ngang màu đại diện cho Subject.color trong Class Diagram
     */
    private JPanel createHorizontalIndicator(Color color) {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(35, 4)); // Thanh ngang dài và mỏng
        indicator.setBackground(color);
        // Bo góc cho thanh màu
        indicator.setBorder(BorderFactory.createLineBorder(color, 1, true));
        return indicator;
    }
    /**
     * Reset viền của tất cả các ô trong lịch về trạng thái mặc định
     */
    private void resetCalendarBorders(JPanel grid) {
        for (Component c : grid.getComponents()) {
            if (c instanceof JPanel) {
                JPanel box = (JPanel) c;
                box.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
                for (Component sub : box.getComponents()) {
                    if (sub instanceof JLabel) sub.setForeground(Color.BLACK);
                }
            }
        }
    }

    private JPanel createBadge(String txt, Color bg, Color fg) {
        JPanel b = new JPanel(); b.setBackground(bg);
        JLabel l = new JLabel(txt); l.setFont(FONT_STATUS);
        l.setForeground(fg);
        b.add(l); return b;
    }

    public JButton getBtnAdd(){
        return btnAdd;
    }
    public JButton getBtnEdit(){
        return btnEdit;
    }
    public JButton getBtnRemove(){
        return btnRemove;
    }
    public void addAddTaskListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }




}

