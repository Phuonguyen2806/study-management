package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginForm extends JFrame {
    private JTextField tEmail;
    private JLabel lEmail;
    private JPasswordField pass;
    private JLabel lPass;
    private JButton btnLogin;
    private JButton btnRegister;

    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public LoginForm() {
        setTitle("Đăng nhập");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setPreferredSize(new Dimension(400, 150));
        panel.setBorder(BorderFactory.createTitledBorder("ĐĂNG NHẬP"));

        // dòng email
        lEmail = new JLabel("Email:");
        panel.add(lEmail);
        tEmail = new JTextField(15);
        panel.add(tEmail);

        // dòng password
        lPass = new JLabel("Mật khẩu:");
        panel.add(lPass);
        pass = new JPasswordField(15);
        panel.add(pass);

        // dòng button
        btnLogin = createMButton("Đăng nhập");
        panel.add(btnLogin);
        btnRegister = createMButton("Tạo tài khoản");
        panel.add(btnRegister);

        JPanel mainWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        mainWrapper.add(panel);
        add(mainWrapper, BorderLayout.CENTER);
    }

    private JButton createMButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(FONT_BOLD);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    public String getEmailInput() {
        return tEmail.getText();
    }

    public String getPasswordInput() {
        return new String(pass.getPassword());
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

}
