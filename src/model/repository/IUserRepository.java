package model.repository;

import model.entity.User;

import java.util.List;

public interface IUserRepository {
    public List<User> getAllUsers();

    public void save(User user);

    public void updateLoginStatus(String email, boolean isLogin);

    public User findUserByEmail(String email);

    public int getNextID();

    public int getLoggedInUserId();

    public User getUserById(int userId);

    public String getEmailByUserId(int userId);
}
