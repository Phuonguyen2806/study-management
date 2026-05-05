package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.AuthController;

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
    private AuthController authController;
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

        // họ và tên
        lFullName = new JLabel("Họ và tên:");
        panel.add(lFullName);
        tFullName = new JTextField();
        panel.add(tFullName);

        // email
        lEmail = new JLabel("Email:");
        panel.add(lEmail);
        tEmail = new JTextField();
        panel.add(tEmail);

        // mật khẩu
        lPass = new JLabel("Mật khẩu:");
        panel.add(lPass);
        pass = new JPasswordField();
        panel.add(pass);

        // xác nhận lại mật khẩu
        lconfiPW = new JLabel("Xác nhận lại mật khẩu:");
        panel.add(lconfiPW);
        confiPW = new JPasswordField();
        panel.add(confiPW);

        // nút
        btnLogin = createButton("Quay lại đăng nhập");
        panel.add(btnLogin);
        btnRegister = createButton("Đăng ký");
        panel.add(btnRegister);
        btnRegister.addActionListener(e -> {
            String name = tFullName.getText();
            String email = tEmail.getText();
            String p1 = new String(pass.getPassword());
            String p2 = new String(confiPW.getPassword());

            // Gọi Controller xử lý
            authController.handleRegister(name, email, p1, p2);
        });

        JPanel mainWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        mainWrapper.add(panel);
        add(mainWrapper, BorderLayout.CENTER);

    }
    private JButton createButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(FONT_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }
    public JButton getBtnLogin() {
        return btnLogin;
    }
    public JButton getBtnRegister() {
        return btnRegister;
    }




}
