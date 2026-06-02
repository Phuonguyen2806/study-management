package model.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String fullName;
    private String userName; // Trong sơ đồ có userName
    private String passwd;
    private String email;
    private String password;
    private int userID;
    private boolean isLogin;

    public User() {
    }
    // tạo user
    public User(String fullName, String email, String passwd) {
        this.fullName = fullName;
        this.email = email;
        this.password = passwd;
        this.userName = email;
        this.isLogin = false;
    }

    public User(int userId, String fullName, String email, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    // đọc từ file
    public User(String fullName, String email, String passwd, boolean isLogin) {
        this.fullName = fullName;
        this.email = email;
        this.password = passwd;
        this.userName = email;
        this.isLogin = isLogin;

    }

    public int getUserId() {
        return userId;
    }

    public boolean checkPassword(String pass) {
        return this.password.equals(pass);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswd() {
        return password;
    }

    public int getUserID() {
        return userID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return userId + "|" + fullName + "|" + email + "|" + password;}
    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}


