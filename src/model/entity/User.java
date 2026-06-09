package model.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private boolean isLogin;

    // tạo user

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public User(int userId, String fullName, String email, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public User(int userId, String fullName, String email, String password, boolean isLogin) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.isLogin = isLogin;
    }

    public int getUserID() {
        return userId;
    }

    public boolean checkPassword(String pass) {
        return this.password.equals(pass);
    }

    public boolean checkEmail(String email) {
        return this.email.equals(email);
    }
    public boolean checkUserID(int userId) {
        return this.userId == userId;
    }

    public boolean hasIdGreaterThan(int otherId) {
        return this.userId > otherId;
    }

    public String getFullName() {
        return fullName;
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

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public String toString() {
        return userId + "|" + fullName + "|" + email + "|" + password+ "|" + isLogin;}

    public void setUserID(int newID) {
        this.userId = newID;
    }
}


