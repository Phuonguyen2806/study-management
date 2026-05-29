package controller;

import view.LoginForm;
import view.MainFrame;
import view.RegisterForm;

import javax.swing.*;
import java.awt.*;

public class MainController {

    private MainFrame mainFrame;
    private GoalController goalController;
    private TaskController taskController;

    private LoginForm loginForm;
    private RegisterForm registerForm;

    private AuthController authController;

    public MainController(MainFrame view) {
        this.mainFrame = view;
        authController = new AuthController(this);
        registerForm = new RegisterForm();
        loginForm = new LoginForm();
        initAuthEvents();
        showLoginView();
    }

    private void initAuthEvents() {
        // Sự kiện trên LoginForm
        loginForm.getBtnLogin().addActionListener(e -> handleLoginAction());
        loginForm.getBtnRegister().addActionListener(e -> showRegisterView());

        // Sự kiện trên RegisterForm
        registerForm.getBtnLogin().addActionListener(e -> showLoginView());
        registerForm.getBtnRegister().addActionListener(e -> {handleRegisterAction();});
    }

    public void showLoginView() {
        registerForm.setVisible(false);
        loginForm.setVisible(true);
    }

    public void showRegisterView() {
        loginForm.setVisible(false);
        registerForm.setVisible(true);
    }

    public void handleLoginAction() {
        String email = loginForm.getEmailInput();
        String password = loginForm.getPasswordInput();
        authController.handleLogin(email, password);
    }

    public void handleRegisterAction() {
        String fullName = registerForm.getFullNameInput();
        String email = registerForm.getEmailInput();
        String password = registerForm.getPasswordInput();
        String confirmPW =registerForm.getConfirmPasswordInput();
        authController.handleRegister(fullName, email, password, confirmPW);
    }

    public void startMainApp() {
        this.mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.taskController = new TaskController(mainFrame.getTaskPanel(), mainFrame);
        this.goalController = new GoalController();
        this.goalController.initialize(mainFrame.getGoalPanel());
        openFocusView();
        initEventListeners();
    }

    private void initEventListeners() {
        mainFrame.getBtnTapTrung().addActionListener(e -> openFocusView());
        mainFrame.getBtnQuanLyBaiTap().addActionListener(e -> openTaskManagementView());
        mainFrame.getBtnMucTieu().addActionListener(e -> openGoalTrackingView());
        mainFrame.getBtnThongKe().addActionListener(e -> openStatisticTrackingView());
        mainFrame.getBtnHoSo().addActionListener(e -> openProfileTrackingView());
        taskController.addStartListener(e ->openFocusView());
        registerForm.getBtnRegister().addActionListener(e -> {handleRegisterAction();});
    }

    public void openFocusView() {
        mainFrame.switchCard("TapTrung");
        mainFrame.setActiveButton(mainFrame.getBtnTapTrung());

    }

    public void openTaskManagementView() {
        mainFrame.switchCard("QuanLyBaiTap");
        mainFrame.setActiveButton(mainFrame.getBtnQuanLyBaiTap());
    }

    public void openGoalTrackingView() {
        mainFrame.switchCard("MucTieu");
        mainFrame.setActiveButton(mainFrame.getBtnMucTieu());
    }

    public void openStatisticTrackingView() {
        mainFrame.switchCard("ThongKe");
        mainFrame.setActiveButton(mainFrame.getBtnThongKe());
    }

    public void openProfileTrackingView() {
        mainFrame.setActiveButton(mainFrame.getBtnHoSo());

    }

    public RegisterForm getRegisterForm() {
        return registerForm;
    }

    public LoginForm getLoginForm() {
        return loginForm;
    }
}