package view;

import model.entity.User;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class ProfilePopupView extends JWindow {

    // ── Màu sắc ───────────────────────────────────────────────────────────────
    private static final Color C_BG        = Color.WHITE;
    private static final Color C_BORDER    = new Color(209, 213, 219);
    private static final Color C_NAME      = new Color( 17,  24,  39);
    private static final Color C_EMAIL     = new Color(107, 114, 128);
    private static final Color C_DIVIDER   = new Color(229, 231, 235);
    private static final Color C_LOGOUT_FG = new Color( 17,  24,  39);
    private static final Color C_LOGOUT_HV = new Color(243, 244, 246);

    // ── Font ─────────────────────────────────────────────────────────────────
    private static final Font F_NAME   = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_EMAIL  = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font F_LOGOUT = new Font("Segoe UI", Font.PLAIN, 15);

    // ── Label cập nhật runtime ────────────────────────────────────────────────
    private JLabel lblName;
    private JLabel lblEmail;

    // ── Callback → ProfileController ─────────────────────────────────────────
    private Runnable onLogoutClicked;

    // ── Listener tự đóng khi click ra ngoài ──────────────────────────────────
    private AWTEventListener autoCloseListener;

    // ─────────────────────────────────────────────────────────────────────────
    public ProfilePopupView(Window owner) {
        super(owner);
        setBackground(new Color(0, 0, 0, 0)); // trong suốt để bo góc
        buildUI();
        registerAutoClose();
    }

    //  XÂY DỰNG UI
    private void buildUI() {
        // Root panel vẽ bóng + bo góc
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), sh = 6;

                // Đổ bóng
                for (int i = sh; i >= 1; i--) {
                    g2.setColor(new Color(0f, 0f, 0f, 0.04f * (sh - i + 1)));
                    g2.fill(new RoundRectangle2D.Float(
                            i, i + 2, w - i * 2f, h - i * 2f, 10, 10));
                }
                // Nền trắng
                g2.setColor(C_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, w - sh, h - sh, 10, 10));
                // Viền
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        w - sh - 1f, h - sh - 1f, 10, 10));
                g2.dispose();
            }
        };
        root.setMinimumSize(new Dimension(220,130));

        root.setOpaque(false);
        root.setBorder(new EmptyBorder(0, 0, 6, 6)); // padding cho bóng

        // Dùng BorderLayout thay vì BoxLayout
        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(4, 0, 4, 0));

        // Nửa trên: Tên + Email
        inner.add(buildInfoSection(), BorderLayout.NORTH);

        // Nửa dưới: Đường kẻ ngang + Nút Đăng xuất
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(buildDivider(), BorderLayout.NORTH);
        bottomPanel.add(buildLogoutRow(), BorderLayout.SOUTH);

        inner.add(bottomPanel, BorderLayout.SOUTH);

        root.add(inner, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ─── Tên + email ─────────────────────────────────────────────────────────

    private JPanel buildInfoSection() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 1, 0, 3));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 15, 15, 30));

        lblName = new JLabel(" ");
        lblName.setFont(F_NAME);
        lblName.setForeground(C_NAME);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEmail = new JLabel(" ");
        lblEmail.setFont(F_EMAIL);
        lblEmail.setForeground(C_EMAIL);
        lblEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lblName);
        p.add(lblEmail);
        return p;
    }

    // ─── Đường kẻ ngang ──────────────────────────────────────────────────────
    private JPanel buildDivider() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        JSeparator sep = new JSeparator();
        sep.setForeground(C_DIVIDER);
        sep.setBackground(C_DIVIDER);
        p.add(sep);
        return p;
    }

    // ─── Hàng Đăng xuất ──────────────────────────────────────────────────────
    private JPanel buildLogoutRow() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 25, 10, 25));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel("→ ");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        icon.setForeground(new Color(107, 114, 128));
        icon.setBorder(new EmptyBorder(0, 6, 0, 4));

        JLabel text = new JLabel("Đăng xuất   ");
        text.setFont(F_LOGOUT);
        text.setForeground(C_LOGOUT_FG);

        row.add(icon);
        row.add(text);
        row.add(Box.createHorizontalGlue());    // Keo dán ép nút dài ra hết cỡ

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                row.setOpaque(true);
                row.setBackground(C_LOGOUT_HV);
                row.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                row.setOpaque(false);
                row.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                setVisible(false);
                UIManager.put("OptionPane.yesButtonText", "Có");
                UIManager.put("OptionPane.noButtonText", "Không");
                int confirm = JOptionPane.showConfirmDialog(
                        ProfilePopupView.this,
                        "Bạn có chắc chắn muốn đăng xuất không?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                // 3. Nếu người dùng chọn "Có" (YES_OPTION)
                if (confirm == JOptionPane.YES_OPTION) {
                    if (onLogoutClicked != null) {
                        onLogoutClicked.run(); // Chạy callback để xử lý đăng xuất

                    }
                }
            }
        });
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 12, 0));
        wrapper.add(row, BorderLayout.CENTER);

        return wrapper;    }

    // ═════════════════════════════════════════════════════════════════════════
    //  API CÔNG KHAI
    // ═════════════════════════════════════════════════════════════════════════

    /** Điền tên + email từ User model vào labels */
    public void fillUser(User user) {
        if (user == null) return;
        lblName.setText(user.getFullName()+"   ");
        lblEmail.setText(user.getEmail()+"   ");
        ((JComponent)getContentPane()).setPreferredSize(null);
        revalidate();
        repaint();    }

    /**
     * showNextTo — hiện popup ngay bên phải của anchor component.
     * Nếu popup đang hiện → ẩn đi (toggle).
     *
     * @param anchor  Nút "Hồ sơ" trên sidebar
     */
    public void showNextTo(Component anchor) {
        if (isVisible()) { setVisible(false); return; }
        pack();
        Point pt = anchor.getLocationOnScreen();
        setLocation(pt.x + anchor.getWidth() + 4, pt.y);
        setVisible(true);
        toFront();
    }

    /** Controller đăng ký callback khi người dùng nhấn Đăng xuất */
    public void setOnLogoutClicked(Runnable r) { this.onLogoutClicked = r; }

    // ─── Tự đóng khi click ra ngoài ──────────────────────────────────────────
    private void registerAutoClose() {
        autoCloseListener = e -> {
            if (!isVisible()) return;
            if (e instanceof MouseEvent me && me.getID() == MouseEvent.MOUSE_PRESSED)
                if (!getBounds().contains(me.getLocationOnScreen()))
                    setVisible(false);
        };
        Toolkit.getDefaultToolkit()
                .addAWTEventListener(autoCloseListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    public void dispose() {
        Toolkit.getDefaultToolkit().removeAWTEventListener(autoCloseListener);
        super.dispose();
    }
}