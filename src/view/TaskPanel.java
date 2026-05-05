package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TaskPanel extends JPanel {
    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_START = new Color(40, 167, 69); // Màu xanh lá cho nút Bắt đầu
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    private JComboBox<String> cbGlobalPriority;
    private JButton btnAdd,btnEdit,btnRemove;
    private JPanel listContainer;
    private ActionListener startListener = e -> {};


    public TaskPanel() {
        this.setLayout(new BorderLayout(0, 20));
        this.setBorder(new EmptyBorder(25, 25, 25, 25));
        this.setBackground(Color.WHITE);

        // --- HEADER SECTION ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Tất cả công việc");
        lblTitle.setFont(FONT_TITLE);

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlControls.setOpaque(false);

        cbGlobalPriority = new JComboBox<>(new String[]{"Ưu tiên: Tất cả", "Cao", "Trung bình", "Thấp"});
        cbGlobalPriority.setFont(FONT_REGULAR);
        cbGlobalPriority.setPreferredSize(new Dimension(150, 35));

        btnAdd = new JButton("+Thêm công việc");
        btnAdd.setBackground(COLOR_PRIMARY);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(FONT_BOLD);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(180, 35));

        pnlControls.add(cbGlobalPriority);
        pnlControls.add(btnAdd);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlControls, BorderLayout.EAST);

        // --- LIST SECTION ---
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        // Giả lập dữ liệu mẫu
        addTaskToContainer("Làm bài tập React Hooks", "cao", "in-progress", "15/05/2024");
        listContainer.add(Box.createVerticalStrut(12));
        addTaskToContainer("Học Unit 5", "trung bình", "pending",  "18/05/2024");

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null); // Xóa viền ScrollPane cho sạch
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void addTaskToContainer(String title, String priority, String status, String deadline) {
        listContainer.add(createTaskCard(title, priority, status, deadline));
    }

    public JPanel createTaskCard(String title, String priority, String status, String deadline) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // 1. Bên trái: Checkbox và Chấm màu
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        pnlLeft.setOpaque(false);
        JCheckBox chk = new JCheckBox();
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel dot = new JLabel("●");
        dot.setForeground(COLOR_PRIMARY);
        pnlLeft.add(chk);
        pnlLeft.add(dot);

        // 2. Giữa: Tiêu đề và Các Tag (Priority, Status, Subject, Deadline)
        JPanel pnlMid = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlMid.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(FONT_BOLD);

        JPanel pnlTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTag.setOpaque(false);
        pnlTag.add(createBadge(priority, new Color(255, 235, 235), Color.RED));
        pnlTag.add(createBadge(status, new Color(230, 245, 255), COLOR_PRIMARY));
        // Tag Deadline với icon đồng hồ giả lập
        pnlTag.add(createBadge("📅 " + deadline, new Color(255, 248, 225), new Color(184, 134, 11)));

        pnlMid.add(lblT);
        pnlMid.add(pnlTag);

        // 3. Phải: Nút Bắt đầu, Sửa, Xóa
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlRight.setOpaque(false);

        JButton btnStart = createActionBtn("Bắt đầu", COLOR_START);
        btnStart.addActionListener(e -> startListener.actionPerformed(e));
        JButton btnEdit = createActionBtn("Sửa", Color.GRAY);
        JButton btnRemove = createActionBtn("Xóa", new Color(220, 53, 69));

        pnlRight.add(btnStart);
        pnlRight.add(btnEdit);
        pnlRight.add(btnRemove);

        card.add(pnlLeft, BorderLayout.WEST);
        card.add(pnlMid, BorderLayout.CENTER);
        card.add(pnlRight, BorderLayout.EAST);

        return card;
    }

    // Hàm tiện ích tạo nút chức năng nhỏ
    private JButton createActionBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(85, 30));


        // Hiệu ứng hover đơn giản
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(color);
            }
        });
        return btn;
    }

    private JPanel createBadge(String txt, Color bg, Color fg) {
        JPanel b = new JPanel();
        b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8)); // Padding cho tag
        JLabel l = new JLabel(txt.toUpperCase());
        l.setFont(FONT_STATUS);
        l.setForeground(fg);
        b.add(l);
        return b;
    }
    public void setOnStartTask(ActionListener listener) {
        this.startListener = listener;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

}