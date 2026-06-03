package view;

import model.entity.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class TaskPanel extends JPanel {
    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_START = new Color(40, 167, 69); // Màu xanh lá cho nút Bắt đầu
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    private JComboBox<String> cbGlobalPriority;
    private JButton btnAdd, btnEdit, btnRemove;
    private JPanel listContainer;

    //tạo sẵn các ActionListener
    private ActionListener startListener = e -> {};
    private ActionListener deleteListener = e -> {};
    private ActionListener editListener = e -> {};


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

        cbGlobalPriority = new JComboBox<>(new String[]{"Ưu tiên: ALL", "HIGH", "MEDIUM", "LOW"});
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

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        this.add(pnlHeader, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void addTaskToContainer(int taskId, String title, String priority, String status, String deadline) {
        listContainer.add(createTaskCard(taskId, title, priority, status, deadline));
        // Thêm khoảng giãn cách giữa các Card trong cấu trúc BoxLayout dọc
        listContainer.add(Box.createVerticalStrut(12));
    }

    public JPanel createTaskCard(int taskId, String title, String priority, String status, String deadline) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        // 1. Bên trái: Checkbox
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        pnlLeft.setOpaque(false);
        JCheckBox chk = new JCheckBox();
        chk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlLeft.add(chk);

        // 2. Giữa: Tiêu đề văn bản & Tập hợp thẻ nhãn (Badge)
        JPanel pnlMid = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlMid.setOpaque(false);

        JLabel lblT = new JLabel(title);
        lblT.setFont(FONT_BOLD);

        JPanel pnlTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlTag.setOpaque(false);
        pnlTag.add(createBadge(priority, new Color(255, 235, 235), Color.RED));
        pnlTag.add(createBadge(status, new Color(230, 245, 255), COLOR_PRIMARY));
        // Tag Deadline
        pnlTag.add(createBadge(deadline, new Color(255, 248, 225), new Color(184, 134, 11)));

        pnlMid.add(lblT);
        pnlMid.add(pnlTag);

        // 3. Phải: Nút Bắt đầu, Sửa, Xóa
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlRight.setOpaque(false);

        JButton btnStart = createActionBtn("Bắt đầu", COLOR_START);
        btnStart.addActionListener(e -> startListener.actionPerformed(e));
        JButton btnEdit = createActionBtn("Sửa", Color.GRAY);
        btnEdit.putClientProperty("taskId", taskId); //Lưu lại taskId để biết sửa task nào
        btnEdit.addActionListener(e -> editListener.actionPerformed(e));
        JButton btnRemove = createActionBtn("Xóa", new Color(220, 53, 69));
        btnRemove.putClientProperty("taskId", taskId);
        btnRemove.addActionListener(e -> deleteListener.actionPerformed(e));

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
        return btn;
    }

    private JPanel createBadge(String txt, Color bg, Color fg) {
        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        JLabel l = new JLabel(txt.toUpperCase(), SwingConstants.CENTER);
        l.setFont(FONT_STATUS);
        l.setForeground(fg);
        b.add(l);
        return b;
    }

    // Hàm xóa sạch các thẻ card cũ để chuẩn bị nạp lại danh sách mới
    public void clearTaskList() {
        listContainer.removeAll();
        listContainer.revalidate();
        listContainer.repaint();
    }

    // Duyệt qua danh sách Task thật nạp từ file txt và hiển thị lên giao diện
    public void renderTaskList(List<Task> tasks) {
        clearTaskList();
        java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        for (Task task : tasks) {
            String formattedDeadline = displayFormat.format(task.getDeadline());
            addTaskToContainer(task.getTaskId(), task.getTitle(), task.getPriority().name(), task.getStatus().name(), formattedDeadline);
        }
        listContainer.revalidate();
        listContainer.repaint();
    }

    public void setOnStartTask(ActionListener listener) {
        this.startListener = listener;
    }

    public void setOnDeleteTask(ActionListener listener) {
        this.deleteListener = listener;
    }

    public void setOnEditTask(ActionListener listener) {
        this.editListener = listener;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JComboBox<String> getCbGlobalPriority() {
        return cbGlobalPriority;
    }
}