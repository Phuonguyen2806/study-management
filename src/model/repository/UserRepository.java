package model.repository;
import model.entity.User;

import java.io.*;
import java.util.*;


public class UserRepository  implements IUserRepository {
    private final String FILE_PATH = "study-management/data/users.txt";

    public UserRepository() {
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return users;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 5) {
                    int id = Integer.parseInt(data[0]);
                    String fullName = data[1];
                    String email = data[2];
                    String password = data[3];
                    boolean isLogin = Boolean.parseBoolean(data[4]);
                    User user = new User(id, fullName, email, password, isLogin);
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

        // Phương thức lưu người dùng mới (có trong sơ đồ)
        @Override
    public void save(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            int newID = getNextID();
            user.setUserID(newID);
            bw.write(user.toString());
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateLoginStatus(String email, boolean isLogin) {
        List<User> users = getAllUsers();
        boolean isUpdated = false;
        for (User user : users) {
            if (user.checkEmail(email)) {
                user.setLogin(isLogin);
                isUpdated = true;
                break;
            }
        }
        if (isUpdated) {
            saveAllUsers(users); // Gọi hàm ghi đè toàn bộ
        }
    }

    public void saveAllUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) { // 'false' để ghi đè
            for (User u : users) {
                bw.write(u.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findUserByEmail(String email) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.checkEmail(email)) {
                return user;
            }
        }
        return null;
    }

    public int getNextID() {
        List<User> users = getAllUsers();
        int maxID = 0;
        for (User user : users) {
            if (user.hasIdGreaterThan(maxID)) {
                maxID = user.getUserID();
            }
        }
        return maxID + 1;
    }

    // Hàm lấy ID của User hiện đang đăng nhập (isLogin = true)
    public int getLoggedInUserId() {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.isLogin()) { // Kiểm tra thuộc tính isLogin đang là true
                return user.getUserID(); // Trả về ID của người dùng này
            }
        }
        return -1; // Trả về -1 nếu không tìm thấy ai đang đăng nhập
    }

    public User getUserById(int userId) {
        for (User user : getAllUsers()) {
            if (user.checkUserID(userId)) {
                return user;
            }
        }
        return null;
    }

}
