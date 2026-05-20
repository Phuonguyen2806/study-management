package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskForm extends JDialog {
    private final Color COLOR_BG = new Color(245, 245, 245);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private final Font FONT_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    private JTextField txtTitle;
    private JTextField txtDeadline;
    private JComboBox<String>  cbPriority, cbStatus;
    private JTextArea txtDescription;
    private JButton btnAdd, btnCancel;


    public TaskForm(Frame owner) {
        super(owner, "Thêm công việc mới", true);
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
        JLabel lblHeader = new JLabel("Thêm công việc mới");
        lblHeader.setFont(FONT_TITLE);
        pnlContent.add(lblHeader);

        pnlContent.add(Box.createVerticalStrut(5)); // Khoảng cách nhỏ

        JLabel lblSubHeader = new JLabel("Điền thông tin công việc và nhấn lưu");
        lblSubHeader.setFont(FONT_STATUS);
        lblSubHeader.setForeground(Color.GRAY);
        pnlContent.add(lblSubHeader);

        pnlContent.add(Box.createVerticalStrut(25)); // Khoảng cách lớn trước khi nhập liệu

        // 1. Tiêu đề công việc
        addLabelSimple("Tiêu đề *", pnlContent);
        txtTitle = new JTextField();
        addInputSimple(txtTitle, pnlContent);

        // 2. Deadline
        addLabelSimple("Hạn chót (dd/mm/yyyy) *", pnlContent);
        txtDeadline = new JTextField();
        txtDeadline.setText("31/12/2026"); // Đặt mặc định hoặc để trống
        txtDeadline.setForeground(Color.GRAY); // Màu chữ mờ cho giống gợi ý

// Thêm sự kiện để khi click vào thì tự xóa gợi ý (Optional)
        txtDeadline.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtDeadline.getText().equals("31/12/2026")) {
                    txtDeadline.setText("");
                    txtDeadline.setForeground(Color.BLACK);
                }
            }
        });

        addInputSimple(txtDeadline, pnlContent);

        // 3. Ưu tiên & Trạng thái
        pnlContent.add(createFieldGroup("Mức độ ưu tiên", cbPriority = new JComboBox<>(new String[]{"Cao", "Trung bình", "Thấp"})));
        pnlContent.add(createFieldGroup("Trạng thái", cbStatus = new JComboBox<>(new String[]{"Đang chờ", "Đang thực hiện", "Hoàn thành"})));
        //khoảng cách giữa các thành phần
        pnlContent.add(Box.createVerticalStrut(15));

        // 4. Mô tả
        addLabelSimple("Mô tả ", pnlContent);
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
//        btnAdd.addActionListener(e -> {
//            if (validateDate()) {
//                // Thực hiện lưu dữ liệu...
//                System.out.println("Ngày hợp lệ, đang lưu...");
//            }
//        });

        pnlButtons.add(btnCancel);
        pnlButtons.add(btnAdd);

        // Thêm vào Dialog
        add(pnlContent, BorderLayout.CENTER);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    // Hàm phụ
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
        comp.setPreferredSize(new Dimension(0, 40));
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
        cb.setPreferredSize(new Dimension(0, 40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT); // Ép combo về trái

        p.add(l);
        p.add(Box.createVerticalStrut(5)); // Khoảng cách giữa nhãn và combo
        p.add(cb);
        p.add(Box.createVerticalStrut(15));
        return p;
    }

    //kiểm tra format deadline
    public boolean validateDate() {
        String dateStr = txtDeadline.getText().trim();
        // Kiểm tra định dạng bằng Regex (dd/mm/yyyy)
        if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Ngày tháng phải đúng định dạng dd/mm/yyyy (Ví dụ: 25/12/2026)");
            return false;
        }

        // Thử ép kiểu sang Date để xem ngày đó có tồn tại không (Ví dụ tránh ngày 32/01)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Không cho phép ngày sai (như 31/02)
        try {
            sdf.parse(dateStr);
            return true;
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! Vui lòng kiểm tra lại.");
            return false;
        }
    }
    public JButton getBtnAdd(){
        return btnAdd;
    }
    public JButton getBtnCancel(){
        return btnCancel;
    }


}