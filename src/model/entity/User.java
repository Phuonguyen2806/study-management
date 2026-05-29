package model.entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String fullName;
    private String userName; // Trong sơ đồ có userName
    private String passwd;
    private String email;
    private int userID;
    private boolean isLogin;

    // tạo user
    public User(String fullName, String email, String passwd) {
        this.fullName = fullName;
        this.email = email;
        this.passwd = passwd;
        this.userName = email;
        this.isLogin = false;
    }

    // đọc từ file
    public User(String fullName, String email, String passwd, boolean isLogin) {
        this.fullName = fullName;
        this.email = email;
        this.passwd = passwd;
        this.userName = email;
        this.isLogin = isLogin;

    }

    public boolean checkPassword(String pass) {

        return this.passwd.equals(pass);
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
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
