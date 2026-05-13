package controller;

import javax.swing.JOptionPane;

import model.AuthManager;

public class AuthController {
    private AuthManager authManager;
    private MainController mainController;


    public AuthController(MainController mainController) {
        this.mainController = mainController;
    }

    public void handleRegister(String fullName, String email, String password, String confirmPW) {
        try {
            // Gọi xuống Manager để xử lý đăng ký
            authManager.register(fullName, email, password, confirmPW);

            // Thông báo và có thể điều hướng người dùng quay lại trang Login
            JOptionPane.showMessageDialog(null, "Đăng ký tài khoản thành công!");
            mainController.showLoginView();
        } catch (Exception e) {
            // Ví dụ: mật khẩu không khớp, email đã tồn tại...
            JOptionPane.showMessageDialog(null, "Đăng ký thất bại: " + e.getMessage());
        }
    }

    public void handleLogin(String email, String password) {
        try {
            // Gọi xuống Manager để kiểm tra logic
            authManager.login(email, password);

            // Nếu không có lỗi, thông báo thành công và chuyển màn hình
            System.out.println("Đăng nhập thành công!");
            // Gọi MainController để chuyển sang màn hình chính (Home)
            mainController.openFocusView();
        } catch (Exception e) {
            // Hiển thị lỗi ra View nếu đăng nhập thất bại
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

}
