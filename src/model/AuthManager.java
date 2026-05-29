package model;

import model.entity.User;
import model.repository.UserRepository;

public class AuthManager {
    private UserRepository userRepository;

    public AuthManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Phương thức xử lý Đăng nhập
    public User login(String email, String password) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại!");
        }
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Sai mật khẩu!");
        }

        // --- KHÚC BỔ SUNG ---
        // Cập nhật trạng thái isLogin = true trong file quản lý users.txt
        userRepository.updateLoginStatus(email, true);

        return user;
    }

    // Phương thức xử lý Đăng ký
    public void register(String fullName, String email, String password, String confirmPw) {
        // 1. Kiểm tra mật khẩu và confirmPw có khớp không
        if (!password.equals(confirmPw)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }

        // 2. Kiểm tra email đã tồn tại chưa
        if (userRepository.findByUserEmail(email)) {
            throw new IllegalArgumentException("Email này đã được đăng ký!");
        }

        // 3. Nếu ổn, tạo User mới và lưu vào Repository
        User newUser = new User(fullName, email, password);
        userRepository.save(newUser);
    }

}
