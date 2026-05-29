package controller;

import javax.swing.JOptionPane;

import model.AuthManager;
import model.repository.UserRepository;

public class AuthController {
    private AuthManager authManager;
    private MainController mainController;
    private UserRepository userRepository;


    public AuthController(MainController mainController) {
        userRepository = new UserRepository();
        authManager = new AuthManager(userRepository);
        this.mainController = mainController;
    }

    public void handleRegister(String fullName, String email, String password, String confirmPW) {
        try {
            authManager.register(fullName, email, password, confirmPW);

            JOptionPane.showMessageDialog(null, "Đăng ký tài khoản thành công!");
            mainController.getRegisterForm().resetForm();
            mainController.showLoginView();
        } catch (Exception e) {
            // Ví dụ: mật khẩu không khớp, email đã tồn tại...
            JOptionPane.showMessageDialog(null, "Đăng ký thất bại: " + e.getMessage());
        }
    }

    public void handleLogin(String email, String password) {
        try {
            // 1. Gọi AuthManager để kiểm tra email và password dưới Database/Repository
            authManager.login(email, password);

            // 2. Nếu không có Exception nào bị ném ra -> Đăng nhập thành công
            JOptionPane.showMessageDialog(null, "Đăng nhập thành công!");

            // 3. Tắt form đăng nhập và mở ứng dụng chính
            mainController.getLoginForm().dispose();
            mainController.startMainApp();

        } catch (IllegalArgumentException e) {
            // Bắt các lỗi sai mật khẩu, thiếu email từ AuthManager
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Hệ thống có lỗi: " + e.getMessage());
        }
    }

}
