package model;

import model.entity.User;
import model.repository.IUserRepository;
import model.repository.UserRepository;

public class AuthManager {
    private IUserRepository iUserRepository;

    public AuthManager(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    // Phương thức xử lý Đăng nhập
    public User login(String email, String password) {
        User user = iUserRepository.findUserByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại!");
        }
        if (!user.checkPassword(password)) {
            throw new IllegalArgumentException("Sai mật khẩu!");
        }
        // Cập nhật trạng thái isLogin = true trong file quản lý users.txt
        iUserRepository.updateLoginStatus(email, true);
        return user;
    }

    // Phương thức xử lý Đăng ký
    public void register(String fullName, String email, String password, String confirmPw) {
        if (fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPw == null || confirmPw.trim().isEmpty()) {
            throw new IllegalArgumentException("Chưa điền đầy đủ thông tin đăng ký");
        }
        // 1. Kiểm tra mật khẩu và confirmPw có khớp không
        if (!password.equals(confirmPw)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp!");
        }
        // 2. Kiểm tra email đã tồn tại chưa
        if (iUserRepository.findUserByEmail(email) != null) {
            throw new IllegalArgumentException("Email này đã được đăng ký!");
        }
        // 3. Nếu ổn, tạo User mới và lưu vào Repository
        User newUser = new User(fullName, email, password);
        iUserRepository.save(newUser);
    }

    public void logout() {
        int loggedInId =  iUserRepository.getLoggedInUserId();
        if (loggedInId != -1) {
            User currentUser = null;
            for (User user : iUserRepository.getAllUsers()) {
                if (user.getUserID() == loggedInId) {
                    currentUser = user;
                    break;
                }
            }
            if (currentUser != null) {
                iUserRepository.updateLoginStatus(currentUser.getEmail(), false);
            }
        } else {
            throw new IllegalStateException("Không có người dùng nào đang đăng nhập!");
        }
    }

}
