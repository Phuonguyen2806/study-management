package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

public class RegisterForm extends JFrame{
    private JTextField tFullName;
    private JLabel lFullName;
    private JTextField tEmail;
    private JLabel lEmail;
    private JPasswordField pass;
    private JLabel lPass;
    private JPasswordField confiPW;
    private JLabel lconfiPW;
    private JButton btnRegister;
    private JButton btnLogin;

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public RegisterForm() {
        setTitle("Đăng ký");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setPreferredSize(new Dimension(400, 250));
        panel.setBorder(BorderFactory.createTitledBorder("ĐĂNG KÝ"));

        // tieu de
        JLabel lblTitle = new JLabel("Pomo Focus", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(new Color(0,102,204));
        add(lblTitle, BorderLayout.NORTH);

        // họ và tên
        lFullName = new JLabel("Họ và tên:");
        lFullName.setFont(FONT_REGULAR);
        panel.add(lFullName);
        tFullName = new JTextField();
        panel.add(tFullName);

        // email
        lEmail = new JLabel("Email:");
        lEmail.setFont(FONT_REGULAR);
        panel.add(lEmail);
        tEmail = new JTextField();
        panel.add(tEmail);

        // mật khẩu
        lPass = new JLabel("Mật khẩu:");
        lPass.setFont(FONT_REGULAR);
        panel.add(lPass);
        pass = new JPasswordField();
        panel.add(pass);

        // xác nhận lại mật khẩu
        lconfiPW = new JLabel("Xác nhận lại mật khẩu:");
        lconfiPW.setFont(FONT_REGULAR);
        panel.add(lconfiPW);
        confiPW = new JPasswordField();
        panel.add(confiPW);

        // nút
        btnLogin = createButton("Quay lại đăng nhập");
        panel.add(btnLogin);
        btnRegister = createButton("Đăng ký");
        panel.add(btnRegister);

        JPanel mainWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        mainWrapper.add(panel);
        add(mainWrapper, BorderLayout.CENTER);

    }
    private JButton createButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(FONT_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 40));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(0, 102, 204));
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1));
        return btn;
    }

    public void resetForm() {
        tFullName.setText("");
        tEmail.setText("");
        pass.setText("");
        confiPW.setText("");
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }
    public JButton getBtnRegister() {
        return btnRegister;
    }
    public String getFullNameInput() {
        return tFullName.getText();
    }

    public String getEmailInput() {
        return tEmail.getText();
    }

    public String getPasswordInput() {
        return new String(pass.getPassword());
    }

    public String getConfirmPasswordInput() {
        return new String(confiPW.getPassword());
    }


}
