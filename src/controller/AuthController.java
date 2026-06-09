package controller;

import javax.swing.JOptionPane;

import model.AuthManager;
import model.entity.User;
import model.repository.IUserRepository;
import model.repository.UserRepository;


public class AuthController {
    private AuthManager authManager;
    private MainController mainController;
    private IUserRepository iUserRepository;


    public AuthController(MainController mainController) {
        iUserRepository = new UserRepository();
        authManager = new AuthManager(iUserRepository);
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
            User user = authManager.login(email, password);
            mainController.setCurrentUser(user);
            mainController.getLoginForm().dispose();
            mainController.startMainApp();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    public void handleLogout() {
        try {
            authManager.logout();
            mainController.setCurrentUser(null);
        } catch (Exception e) {
            System.out.println("Lỗi khi đăng xuất ở Model: " + e.getMessage());
        }
    }

}
