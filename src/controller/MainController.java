package controller;

import model.entity.Priority;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.*;
import service.MotivationService;
import service.ReminderService;
import view.*;

import model.entity.User;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

public class MainController {
    private MainFrame mainFrame;
    private TaskController taskController;
    private IFocusController focusController;
    private GoalController goalController;
    private StatisticsController statisticsController;
    private LoginForm loginForm;
    private RegisterForm registerForm;
    private User currentUser;
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
        registerForm.getBtnRegister().addActionListener(e -> {
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
        String email = loginForm.getEmailInput();
        String password = loginForm.getPasswordInput();
        authController.handleLogin(email, password);
    }

    public void handleRegisterAction() {
        String fullName = registerForm.getFullNameInput();
        String email = registerForm.getEmailInput();
        String password = registerForm.getPasswordInput();
        String confirmPW = registerForm.getConfirmPasswordInput();
        authController.handleRegister(fullName, email, password, confirmPW);
    }

    public void startMainApp() {
        this.mainFrame = new MainFrame();
        mainFrame.setVisible(true);
        //Khởi tạo repo
        ITaskRepository taskRepo = new TaskRepositoryImpl();
        IReminderRepository reminderRepo = new ReminderRepository();
        IUserRepository userRepo = new UserRepository();
        MotivationService motivationService = new MotivationService();
        // Khởi tạo Task
        this.taskController = new TaskController(mainFrame.getTaskPanel(), mainFrame);
        // Khởi tạo Focus
        FocusPanel focusPanel = this.mainFrame.getFocusPanel();
        this.focusController = new FocusController(focusPanel);
        focusPanel.setController(this.focusController);
        // Khởi tạo Goal
        GoalPanel goalPanel = this.mainFrame.getGoalPanel();
        this.goalController = new GoalController(taskRepo,userRepo);
        this.goalController.initialize(goalPanel,this.currentUser);
        // Khởi tạo Statistic
        StatisticsPanel statisticsPanel = this.mainFrame.getStatisticsPanel();
        this.statisticsController = new StatisticsController(mainFrame.getStatisticsPanel(),taskRepo, userRepo );
        // Bơm dữ liệu User vào cho Popup Hồ sơ
        this.mainFrame.getProfilePopupView().fillUser(this.currentUser);

        // Xử lý sự kiện khi nhấn nút "Đăng xuất" trên Popup
        this.mainFrame.getProfilePopupView().setOnLogoutClicked(() -> {
            authController.handleLogout();
            mainFrame.dispose(); // Đóng màn hình chính
            showLoginView(); // Quay lại màn hình đăng nhập
        });

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                authController.handleLogout();
                mainFrame.dispose();
                System.exit(0);
            }
        });
        ReminderService reminderService = new ReminderService(motivationService,
                userRepo,
                taskRepo,
                reminderRepo);
        ReminderController reminderController = new ReminderController(taskRepo, reminderService);
//        // 2. Gọi ReminderController quét danh sách task này (isAppOpen = true)
        reminderController.startCheckingReminders(mainFrame);

        openFocusView();
        initEventListeners();
    }

    private void initEventListeners() {
        mainFrame.getBtnTapTrung().addActionListener(e -> openFocusView());
        mainFrame.getBtnQuanLyBaiTap().addActionListener(e -> openTaskManagementView());
        mainFrame.getBtnMucTieu().addActionListener(e -> openGoalTrackingView());
        mainFrame.getBtnThongKe().addActionListener(e -> openStatisticTrackingView());
        mainFrame.getBtnHoSo().addActionListener(e -> openProfileTrackingView());
    }

    public void openFocusView() {
        mainFrame.switchCard("TapTrung");
        mainFrame.setActiveButton(mainFrame.getBtnTapTrung());
    }

    public void openTaskManagementView() {
        mainFrame.switchCard("QuanLyBaiTap");
        mainFrame.setActiveButton(mainFrame.getBtnQuanLyBaiTap());

        // Tự động làm mới danh sách công việc khi người dùng click chọn tab này
        if (this.taskController != null) {
            this.taskController.refreshView();
        }
    }

    public void openGoalTrackingView() {
        mainFrame.switchCard("MucTieu");
        mainFrame.setActiveButton(mainFrame.getBtnMucTieu());
        if (this.goalController != null && this.currentUser != null) {
            this.goalController.refreshView(this.currentUser);
        }
        }

    public void openStatisticTrackingView() {
        // 1. Chuyển đổi giao diện trước
        mainFrame.switchCard("ThongKe");
        mainFrame.setActiveButton(mainFrame.getBtnThongKe());
        // 2. Gọi Controller để load dữ liệu
        if (this.statisticsController != null && this.currentUser != null) {
            this.statisticsController.loadDailyStats(this.currentUser);
            this.statisticsController.loadWeeklyStats(this.currentUser);
        }
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

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}