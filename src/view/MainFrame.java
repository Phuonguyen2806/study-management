package view;

import model.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private FocusPanel focusPanel;
    private GoalPanel goalPanel;
    private TaskPanel taskPanel;
    private StatisticsPanel statisticsPanel;
    private ProfilePopupView profilePopupView;

    private JButton btnTapTrung;
    private JButton btnQuanLyBaiTap;
    private JButton btnMucTieu;
    private JButton btnThongKe;
    private JButton btnHoSo;
    private JButton[] allMenuButtons;
    private JButton currentActiveButton;

    private final Color COLOR_PRIMARY = new Color(0, 102, 204);
    private final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public MainFrame() {
        setTitle("Pomo Focus - Nâng cao năng suất làm việc với Bộ đếm Pomodoro");
        setSize(900, 600);
        setLocationRelativeTo(null);
        // Dùng DO_NOTHING_ON_CLOSE để khi người dùng tắt đột ngột app thì sẽ ko tắt ngay
        // mà đợi hàm logout của chạy xong để lưu file
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        // ÁP DỤNG COMPOSITE PATTERN: MainFrame chứa 2 Node lớn
        add(createSidebarPanel(), BorderLayout.WEST);   // Khối Menu Trái
        add(createContentPanel(), BorderLayout.CENTER); // Khối Nội dung Phải
        this.profilePopupView = new ProfilePopupView(this);

    }

    // --- NODE 1: Tạo cây Sidebar ---
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Node con: Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setBackground(Color.WHITE);
        JLabel appTitleLabel = new JLabel("Pomo Focus");
        appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitleLabel.setForeground(COLOR_PRIMARY);
        logoPanel.add(appTitleLabel);
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);

        // Node con: Menu Buttons
        JPanel menuButtonsPanel = new JPanel();
        menuButtonsPanel.setLayout(new BoxLayout(menuButtonsPanel, BoxLayout.Y_AXIS));
        menuButtonsPanel.setBackground(Color.WHITE);
        menuButtonsPanel.add(Box.createVerticalStrut(10));

        btnTapTrung = createMenuButton("Tập trung");
        btnQuanLyBaiTap = createMenuButton("Quản lý công việc");
        btnMucTieu = createMenuButton("Mục tiêu");
        btnThongKe = createMenuButton("Thống kê");
        btnHoSo = createMenuButton("Hồ sơ");

        allMenuButtons = new JButton[]{btnTapTrung, btnQuanLyBaiTap, btnMucTieu, btnThongKe, btnHoSo};
        for (JButton btn : allMenuButtons) {
            menuButtonsPanel.add(btn);
        }
        sidebarPanel.add(menuButtonsPanel, BorderLayout.CENTER);

        return sidebarPanel;
    }

    // --- NODE 2: Tạo cây Content (CardLayout) ---
    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(Color.WHITE);

        focusPanel = new FocusPanel();
        taskPanel = new TaskPanel();
        goalPanel = new GoalPanel();
        statisticsPanel = new StatisticsPanel();

        // Thêm các lá (Leaf Nodes) vào CardLayout Composite
        contentPanel.add(focusPanel, "TapTrung");
        contentPanel.add(taskPanel, "QuanLyBaiTap");
        contentPanel.add(goalPanel, "MucTieu");
        contentPanel.add(statisticsPanel, "ThongKe");

        add(contentPanel, BorderLayout.CENTER);
        // Nút "Hồ sơ" bật popup nhỏ bên cạnh !
        btnHoSo.addActionListener(e -> {
            profilePopupView.showNextTo(btnHoSo);
        });
        return contentPanel;
    }

    private JButton createMenuButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(FONT_REGULAR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setBackground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (btn != currentActiveButton) btn.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(MouseEvent evt) {
                if (btn != currentActiveButton) btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public void setActiveButton(JButton selectedBtn) {
        for (JButton btn : allMenuButtons) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
            btn.setFont(FONT_REGULAR);
        }
        selectedBtn.setBackground(new Color(220, 235, 252));
        selectedBtn.setForeground(COLOR_PRIMARY);
        selectedBtn.setFont(FONT_BOLD);
        currentActiveButton = selectedBtn;
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    public JButton getBtnTapTrung() {
        return btnTapTrung;
    }

    public JButton getBtnQuanLyBaiTap() {
        return btnQuanLyBaiTap;
    }

    public JButton getBtnMucTieu() {
        return btnMucTieu;
    }

    public JButton getBtnThongKe() {
        return btnThongKe;
    }

    public JButton getBtnHoSo() {
        return btnHoSo;
    }

    public void switchCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    public FocusPanel getFocusPanel() {
        return focusPanel;
    }

    public TaskPanel getTaskPanel() {
        return taskPanel;
    }

    public GoalPanel getGoalPanel() {
        return goalPanel;
    }

    public ProfilePopupView getProfilePopupView() {
        return profilePopupView;
    }

    public StatisticsPanel getStatisticsPanel() {
        return statisticsPanel;
    }
}