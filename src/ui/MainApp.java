package ui;

import utils.AppIcons;
import utils.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends JFrame {
    public static final String CARD_BAI_TAP = "BaiTap";
    public static final String CARD_LICH_HOC = "LichHoc";
    public static final String CARD_MON_HOC = "MonHoc";
    public static final String CARD_MUC_TIEU = "MucTieu";
    public static final String CARD_THONG_KE = "ThongKe";
    public static final String CARD_TAP_TRUNG = "TapTrung";

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private List<MenuButton> menuButtons = new ArrayList<>();

    public MainApp() {
        setTitle("StudyApp - Hệ thống học tập cá nhân");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppTheme.BG_LIGHT);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main Content
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(AppTheme.BG_WHITE);
        contentWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(AppTheme.BG_WHITE);
        cardPanel.setBorder(null);

        // Sử dụng Hằng số (Constants) ở đây
        cardPanel.add(createDummyPanel("Bài Tập", "Quản lý và nộp bài tập đúng hạn tại đây."), CARD_BAI_TAP);
        cardPanel.add(createDummyPanel("Lịch Học", "Kiểm tra lịch học và các sự kiện sắp tới trong tuần."), CARD_LICH_HOC);
        cardPanel.add(createDummyPanel("Môn Học", "Các môn học hiện tại trong học kỳ của bạn."), CARD_MON_HOC);
        cardPanel.add(createDummyPanel("Mục Tiêu", "Đặt mục tiêu học tập và theo dõi tiến độ một cách chi tiết."), CARD_MUC_TIEU);
        cardPanel.add(createDummyPanel("Thống Kê", "Phân tích và thống kê thời gian học tập qua từng tuần."), CARD_THONG_KE);
        cardPanel.add(createDummyPanel("Tập Trung", "Sử dụng đồng hồ Pomodoro để tăng năng suất học tập."), CARD_TAP_TRUNG);

        contentWrapper.add(cardPanel, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);

        if (!menuButtons.isEmpty()) {
            selectMenu(CARD_BAI_TAP);
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppTheme.BG_WHITE);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppTheme.BORDER_COLOR));

        // Logo
        JLabel logo = new JLabel("✨ StudyApp");
        logo.setFont(AppTheme.FONT_H1);
        logo.setForeground(AppTheme.PRIMARY_BLUE);
        logo.setBorder(new EmptyBorder(30, 25, 30, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // Menu Data
        String[] menus = { "Bài tập", "Lịch học", "Môn học", "Mục tiêu", "Thống kê", "Tập trung" };
        String[] iconTypes = { "assignment", "calendar", "book", "target", "chart", "timer" };
        // Sử dụng mảng Hằng số
        String[] cardNames = { CARD_BAI_TAP, CARD_LICH_HOC, CARD_MON_HOC, CARD_MUC_TIEU, CARD_THONG_KE, CARD_TAP_TRUNG };

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(AppTheme.BG_WHITE);
        menuContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < menus.length; i++) {
            MenuButton btn = new MenuButton(menus[i], iconTypes[i], cardNames[i]);
            menuButtons.add(btn);
            menuContainer.add(btn);

            if (i < menus.length - 1) {
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setForeground(AppTheme.BORDER_COLOR);
                separator.setBackground(AppTheme.BG_WHITE);
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                menuContainer.add(separator);
            }
        }

        sidebar.add(menuContainer);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void selectMenu(String cardName) {
        for (MenuButton btn : menuButtons) {
            btn.setActive(btn.getCardName().equals(cardName));
        }
        cardLayout.show(cardPanel, cardName);
    }

    private JPanel createDummyPanel(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppTheme.BG_WHITE);

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(AppTheme.BG_WHITE);
        header.setBorder(new EmptyBorder(40, 40, 20, 40));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEXT_MAIN);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(AppTheme.FONT_REGULAR);
        subLabel.setForeground(AppTheme.TEXT_MUTED);

        header.add(titleLabel);
        header.add(Box.createRigidArea(new Dimension(0, 8)));
        header.add(subLabel);

        // Separator
        JPanel separator = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(AppTheme.BORDER_COLOR);
                g.drawLine(40, 0, getWidth() - 40, 0);
            }
        };
        separator.setPreferredSize(new Dimension(0, 1));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setBackground(AppTheme.BG_WHITE);
        header.add(Box.createRigidArea(new Dimension(0, 20)));
        header.add(separator);

        p.add(header, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(AppTheme.BG_WHITE);

        JLabel contentHint = new JLabel("<html><div style='text-align: center;'><br><br>Giao diện cho nội dung <b>"
                + title.replaceAll("[^a-zA-ZÀ-ỹ ]", "").trim() + "</b><br>sẽ được hiển thị ở đây.</div></html>");
        contentHint.setFont(AppTheme.FONT_REGULAR);
        contentHint.setForeground(AppTheme.TEXT_MUTED);
        contentPanel.add(contentHint);

        p.add(contentPanel, BorderLayout.CENTER);

        return p;
    }

    // Custom Menu Button
    class MenuButton extends JPanel {
        private String cardName;
        private boolean isActive = false;
        private boolean isHovered = false;
        private JLabel textLabel;

        public MenuButton(String text, String iconType, String cardName) {
            this.cardName = cardName;
            setLayout(new BorderLayout());
            setBackground(AppTheme.BG_WHITE);
            setMaximumSize(new Dimension(280, 48));
            setPreferredSize(new Dimension(280, 48));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 25, 0, 0));

            textLabel = new JLabel(text);
            textLabel.setIcon(AppIcons.getIcon(iconType, 20));
            textLabel.setIconTextGap(15);
            textLabel.setFont(AppTheme.FONT_REGULAR);
            textLabel.setForeground(AppTheme.TEXT_MAIN);
            add(textLabel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectMenu(cardName);
                }
            });
        }

        public String getCardName() {
            return cardName;
        }

        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                textLabel.setFont(AppTheme.FONT_BOLD);
                textLabel.setForeground(AppTheme.PRIMARY_BLUE);
            } else {
                textLabel.setFont(AppTheme.FONT_REGULAR);
                textLabel.setForeground(AppTheme.TEXT_MAIN);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (isActive) {
                g2.setColor(AppTheme.MENU_SELECTED_BG);
                g2.fillRect(0, 0, width, height);
                g2.setColor(AppTheme.PRIMARY_BLUE);
                g2.fillRect(0, 0, 4, height);
            } else if (isHovered) {
                g2.setColor(AppTheme.MENU_HOVER);
                g2.fillRect(0, 0, width, height);
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải giao diện hệ thống: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}