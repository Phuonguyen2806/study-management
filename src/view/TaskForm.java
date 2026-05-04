package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskForm extends JDialog {
    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Color COLOR_BG = new Color(245, 245, 245);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    private JTextField txtTitle;
    private JComboBox<String> cbSubject, cbPriority, cbStatus;
    private JTextArea txtDescription;
    private JButton btnAdd, btnCancel;



    public TaskForm(Frame owner) {
        super(owner, "Thêm bài tập mới", true);
        this.setModal(true);
        setSize(500, 650);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- PANEL NỘI DUNG (CENTER) ---
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Tiêu đề form
        JLabel lblHeader = new JLabel("Thêm bài tập mới");
        lblHeader.setFont(FONT_TITLE);
        pnlContent.add(lblHeader);

        pnlContent.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ

        JLabel lblSubHeader = new JLabel("Điền thông tin bài tập và nhấn lưu");
        lblSubHeader.setFont(FONT_STATUS);
        lblSubHeader.setForeground(Color.GRAY);
        pnlContent.add(lblSubHeader);

        pnlContent.add(Box.createVerticalStrut(25)); // Khoảng cách lớn trước khi nhập liệu

        // 1. Tiêu đề bài tập
        addLabelSimple("Tiêu đề *", pnlContent);
        txtTitle = new JTextField();
        addInputSimple(txtTitle, pnlContent);

        // 2. Môn học
        addLabelSimple("Môn học *", pnlContent);
        cbSubject = new JComboBox<>(new String[]{"Lập trình Web", "Cấu trúc dữ liệu", "Tiếng Anh"});
        addInputSimple(cbSubject, pnlContent);

        // 3. Ưu tiên & Trạng thái (Dùng GridLayout cho nhanh)
        pnlContent.add(createFieldGroup("Mức độ ưu tiên", cbPriority = new JComboBox<>(new String[]{"High", "Medium", "Low"})));
        pnlContent.add(createFieldGroup("Trạng thái", cbStatus = new JComboBox<>(new String[]{"Pending", "In-progress", "Done"})));
        //khoảng cách giữa các thành phần
        pnlContent.add(Box.createVerticalStrut(15));

        // 4. Mô tả
        addLabelSimple("Mô tả (Markdown)", pnlContent);
        txtDescription = new JTextArea(8, 10);
        txtDescription.setFont(FONT_REGULAR);
        txtDescription.setLineWrap(true);
        txtDescription.setBackground(COLOR_BG);
        JScrollPane scroll = new JScrollPane(txtDescription);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pnlContent.add(scroll);

        // --- PANEL NÚT BẤM (SOUTH) ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlButtons.setBackground(Color.WHITE);

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(FONT_BOLD);
        btnCancel.addActionListener(e -> dispose());

        btnAdd = new JButton("Thêm");
        btnAdd.setFont(FONT_BOLD);
        btnAdd.setBackground(new Color(13, 15, 28));
        btnAdd.setForeground(Color.WHITE);

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnAdd);

        // Thêm vào Dialog
        add(pnlContent, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    // Hàm phụ để code sạch hơn (không dùng GridBag nữa)
    private void addLabelSimple(String text, JPanel container) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(l);
        container.add(Box.createVerticalStrut(8));
    }

    private void addInputSimple(JComponent comp, JPanel container) {
        comp.setFont(FONT_REGULAR);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        comp.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(comp);
        container.add(Box.createVerticalStrut(15));
    }

    private JPanel createFieldGroup(String label, JComboBox cb) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); // Xếp dọc trong từng ô nhỏ
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(FONT_BOLD);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); // Ép nhãn về trái

        cb.setFont(FONT_REGULAR);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT); // Ép combo về trái

        p.add(l);
        p.add(Box.createVerticalStrut(5)); // Khoảng cách giữa nhãn và combo
        p.add(cb);
        return p;
    }


    public JButton getBtnAdd(){
        return btnAdd;
    }
    public JButton getBtnCancel(){
        return btnCancel;
    }


}