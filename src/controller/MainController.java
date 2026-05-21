package controller;

import view.LoginForm;
import view.MainFrame;
import view.RegisterForm;

import javax.swing.*;

public class MainController {

    private MainFrame mainFrame;
    private GoalController goalController;
    private TaskController taskController;

    private LoginForm loginForm;
    private RegisterForm registerForm;


    public MainController(MainFrame view) {
        this.mainFrame = view;
        this.goalController = new GoalController();
        this.goalController.initialize(mainFrame.getGoalPanel());
        initEventListeners();
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
        registerForm.getBtnRegister().addActionListener(e -> {
            // Sau khi đăng ký xong thì quay lại login
            handleRegisterAction();
        });
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
        //logic khac

        loginForm.dispose();
        startMainApp();
    }

    public void handleRegisterAction() {
// logic khac
        showLoginView();
    }

    private void startMainApp() {
        this.mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        this.taskController = new TaskController(mainFrame.getTaskPanel(), mainFrame);
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
        mainFrame.switchCard("HoSo");
        mainFrame.setActiveButton(mainFrame.getBtnHoSo());

    }
}