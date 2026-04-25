package model;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    // =========================================================
    // VÙNG 1: KHAI BÁO UI COMPONENTS (Chỉ chứa giao diện)
    // =========================================================
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Lưu trữ các nút Menu để Controller lấy ra dùng
    private JPanel btnTapTrung;
    private JPanel btnQuanLyBaiTap;
    private JPanel btnMucTieu;
    private JPanel btnThongKe;
    private JPanel btnHoSo;

    // Lưu trữ Label của Menu để đổi màu khi click
    private Map<JPanel, JLabel> menuLabels = new HashMap<>();

    // Định nghĩa Style (Màu sắc, Font)
    private final Color primaryColor = new Color(0, 102, 204);
    private final Color selectedMenuBgColor = new Color(204, 229, 255);
    private final Font menuFont = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font menuSelectedFont = new Font("Segoe UI", Font.BOLD, 15);

    // =========================================================
    // VÙNG 2: KHỞI TẠO GIAO DIỆN (Lắp ráp các thành phần)
    // =========================================================
    public MainFrame() {
        setTitle("Pomo Focus - Hệ thống học tập cá nhân");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Khởi tạo Sidebar (Menu trái)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Thêm Logo (Giữ nguyên cấu trúc cũ của bạn)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel appTitleLabel = new JLabel("Pomo Focus");
        appTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitleLabel.setForeground(primaryColor);
        logoPanel.add(appTitleLabel);
        sidebarPanel.add(logoPanel);

        // Khởi tạo các nút Menu (Giao diện)
        btnTapTrung = createMenuButton("Tập trung");
        btnQuanLyBaiTap = createMenuButton("Quản lý bài tập");
        btnMucTieu = createMenuButton("Mục tiêu");
        btnThongKe = createMenuButton("Thống kê");
        btnHoSo = createMenuButton("Hồ sơ");

        sidebarPanel.add(btnTapTrung);
        sidebarPanel.add(btnQuanLyBaiTap);
        sidebarPanel.add(btnMucTieu);
        sidebarPanel.add(btnThongKe);
        sidebarPanel.add(btnHoSo);
        sidebarPanel.add(Box.createVerticalGlue());

        add(sidebarPanel, BorderLayout.WEST);

        // 2. Khởi tạo Vùng nội dung (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        contentPanel.setBackground(Color.WHITE);

        // Thêm các trang rỗng vào CardLayout
        contentPanel.add(createPlaceholderPanel("Màn hình Tập trung Pomodoro"), "TapTrung");
        contentPanel.add(createPlaceholderPanel("Màn hình Quản lý bài tập"), "QuanLyBaiTap");
        contentPanel.add(createPlaceholderPanel("Màn hình Mục tiêu"), "MucTieu");
        contentPanel.add(createPlaceholderPanel("Màn hình Thống kê"), "ThongKe");
        contentPanel.add(createPlaceholderPanel("Màn hình Hồ sơ"), "HoSo");

        add(contentPanel, BorderLayout.CENTER);
    }

    // Hàm tiện ích tạo nút Menu
    private JPanel createMenuButton(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel label = new JLabel(title);
        label.setFont(menuFont);
        panel.add(label);

        menuLabels.put(panel, label); // Lưu lại để sau này Controller gọi hàm đổi màu
        return panel;
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // VÙNG 3: CÁC HÀM "MỞ CỬA" CHO CONTROLLER SỬ DỤNG
    // Giải thích cho cô: "View đóng kín, chỉ mở các hàm public này để Controller gọi"
    // =========================================================

    // Mở cửa để Controller lấy nút Menu gắn sự kiện Click
    public JPanel getBtnTapTrung() {
        return btnTapTrung;
    }

    public JPanel getBtnQuanLyBaiTap() {
        return btnQuanLyBaiTap;
    }

    public JPanel getBtnMucTieu() {
        return btnMucTieu;
    }

    public JPanel getBtnThongKe() {
        return btnThongKe;
    }

    public JPanel getBtnHoSo() {
        return btnHoSo;
    }

    // Mở cửa để Controller ra lệnh đổi trang
    public void switchCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    // Mở cửa để Controller ra lệnh làm nổi bật Menu được chọn
    public void setActiveMenuUI(JPanel activePanel) {
        // Reset toàn bộ menu về màu trắng
        for (JPanel p : menuLabels.keySet()) {
            p.setBackground(Color.WHITE);
            menuLabels.get(p).setFont(menuFont);
            menuLabels.get(p).setForeground(Color.BLACK);
        }
        // Tô màu xanh cho menu được chọn
        activePanel.setBackground(selectedMenuBgColor);
        menuLabels.get(activePanel).setFont(menuSelectedFont);
        menuLabels.get(activePanel).setForeground(primaryColor);
    }
}
