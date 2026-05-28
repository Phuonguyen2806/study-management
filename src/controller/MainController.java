package controller;

import view.*;

import javax.swing.*;
import model.entity.User;

public class MainController {
    private MainFrame mainFrame;
    private TaskController taskController;
    private IFocusController focusController;
    private GoalController goalController;
    private LoginForm loginForm;
    private RegisterForm registerForm;
    private User currentUser;

    public MainController(MainFrame view) {
        this.mainFrame = view;
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
        // logic khac
        // Tạm thời giả lập việc đăng nhập thành công (Sau này bạn thay bằng code kiểm tra file users.txt)
        this.currentUser = new User(1, "Nguyễn Ngọc Phương Uyên", "uyen.nnp@nlu.edu.vn", "password123");
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
        // Khởi tạo Task
        this.taskController = new TaskController(mainFrame.getTaskPanel(), mainFrame);
        // Khởi tạo Focus
        FocusPanel focusPanel = this.mainFrame.getFocusPanel();
        this.focusController = new FocusController(focusPanel);
        focusPanel.setController(this.focusController);
        // Khởi tạo Goal
        GoalPanel goalPanel = this.mainFrame.getGoalPanel();
        this.goalController = new GoalController();
        this.goalController.initialize(goalPanel);

        // Bơm dữ liệu User vào cho Popup Hồ sơ
        this.mainFrame.getProfilePopupView().fillUser(this.currentUser);

        // Xử lý sự kiện khi nhấn nút "Đăng xuất" trên Popup
        this.mainFrame.getProfilePopupView().setOnLogoutClicked(() -> {
            mainFrame.dispose(); // Đóng màn hình chính
            showLoginView();     // Quay lại màn hình đăng nhập
        });

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
        mainFrame.setActiveButton(mainFrame.getBtnHoSo());
        mainFrame.getProfilePopupView().showNextTo(mainFrame.getBtnHoSo());
    }
}