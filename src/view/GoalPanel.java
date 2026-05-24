package view;

import model.entity.Goal;
import model.entity.GoalStatus;
import controller.GoalController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GoalPanel — View (MVC)
 *
 * Sửa lỗi gốc:
 *   ✓ controller KHÔNG được dùng trong buildUI() → không null-pointer
 *   ✓ goalCardsPanel được khởi tạo TRƯỚC khi addGoalCards() chạy
 *   ✓ displayGoals() luôn được gọi tường minh từ GoalController.loadAndDisplay()
 *   ✓ GridLayout(0,2) cho thẻ mục tiêu 2 cột
 *
 * Hiển thị đúng theo hình minh hoạ:
 *   - Tiêu đề "Mục tiêu học tập" + subtitle
 *   - Thẻ "Thành tích" (Hoàn thành / Đang thực hiện / Tổng cộng)
 *   - Section "Mục tiêu đang thực hiện" + 2 card 2 cột
 *   - Mỗi card: tên, loại, thanh tiến độ + nhãn, % , nút -1 / +1
 */
public class GoalPanel extends JPanel {

    // ── Màu sắc ──────────────────────────────────────────────────────────────
    private static final Color C_BG       = new Color(250, 250, 252);
    private static final Color C_CARD     = Color.WHITE;
    private static final Color C_BORDER   = new Color(218, 220, 228);
    private static final Color C_BLACK    = new Color(18,  18,  22);
    private static final Color C_GREEN    = new Color(22,  163,  74);
    private static final Color C_GREEN_LT = new Color(220, 252, 231);
    private static final Color C_ORANGE   = new Color(220,  80,   0);
    private static final Color C_RED      = new Color(185,  28,  28);
    private static final Color C_TEXT     = new Color(17,  24,  39);
    private static final Color C_MUTED    = new Color(107, 114, 128);
    private static final Color C_BAR_BG   = new Color(229, 231, 235);
    private static final Color C_BAR_FG   = new Color(31,  41,  55);

    // ── Font ─────────────────────────────────────────────────────────────────
    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font F_CARD_T  = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_STAT_N  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_STAT_V  = new Font("Segoe UI", Font.BOLD,  13);

    // ── Controller ───────────────────────────────────────────────────────────
    private GoalController controller;   // được set bên ngoài, SAU khi buildUI()

    // ── State ────────────────────────────────────────────────────────────────
    private List<Goal> currentGoals = new ArrayList<>();

    // ── Widgets cần cập nhật ─────────────────────────────────────────────────
    // Khai báo ở class-level để tránh NullPointerException
    private JPanel goalCardsPanel;
    private JLabel lblAchieved;
    private JLabel lblInProgress;
    private JLabel lblTotal;

    // ═════════════════════════════════════════════════════════════════════════
    //  Constructor — KHÔNG nhận GoalController ở đây để tránh null khi buildUI
    // ═════════════════════════════════════════════════════════════════════════
    public GoalPanel() {
        setLayout(new BorderLayout());
        setBackground(C_BG);

        // ── Khởi tạo widget trước khi buildUI dùng chúng ──────────────────
        goalCardsPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        goalCardsPanel.setOpaque(false);

        lblAchieved   = makeSummaryValue("0", C_GREEN);
        lblInProgress = makeSummaryValue("0", C_ORANGE);
        lblTotal      = makeSummaryValue("0", C_TEXT);

        buildUI();
    }

    /** Gọi từ bên ngoài sau khi tạo panel */
    public void setController(GoalController controller) {
        this.controller = controller;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  XÂY DỰNG UI — không dùng controller ở đây
    // ─────────────────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 32, 28, 32));

        // 1. Tiêu đề trang
        content.add(buildPageHeader());
        content.add(vgap(20));

        // 2. Thẻ Thành tích
        content.add(buildSummaryCard());
        content.add(vgap(28));

        // 3. Section mục tiêu
        content.add(buildGoalSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getViewport().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ─── Tiêu đề trang ───────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel("Mục tiêu học tập");
        title.setFont(F_TITLE);
        title.setForeground(C_TEXT);

        JLabel sub = new JLabel("Theo dõi tiến độ và duy trì động lực học tập");
        sub.setFont(F_SUB);
        sub.setForeground(C_MUTED);

        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);

        p.add(left, BorderLayout.WEST);
        return p;
    }

    // ─── Thẻ Thành tích ──────────────────────────────────────────────────────
    private JPanel buildSummaryCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(C_CARD);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(20, 24, 20, 24)));

        // Header: icon + label
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        header.setOpaque(false);
        JLabel iconLbl = new JLabel("🏆");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel headLbl = new JLabel("Thành tích");
        headLbl.setFont(F_CARD_T);
        headLbl.setForeground(C_TEXT);
        header.add(iconLbl);
        header.add(headLbl);

        // 3 hàng số liệu (label bên trái, số bên phải)
        JPanel stats = new JPanel(new GridLayout(3, 2, 0, 6));
        stats.setOpaque(false);

        stats.add(makeSummaryLabel("Hoàn thành"));
        stats.add(lblAchieved);
        stats.add(makeSummaryLabel("Đang thực hiện"));
        stats.add(lblInProgress);
        stats.add(makeSummaryLabel("Tổng cộng"));
        stats.add(lblTotal);

        card.add(header, BorderLayout.NORTH);
        card.add(stats,  BorderLayout.CENTER);
        return card;
    }

    private JLabel makeSummaryLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_STAT_N);
        l.setForeground(C_MUTED);
        return l;
    }

    private JLabel makeSummaryValue(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(F_STAT_V);
        l.setForeground(color);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    // ─── Section danh sách mục tiêu ──────────────────────────────────────────
    private JPanel buildGoalSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sectionTitle = new JLabel("Mục tiêu đang thực hiện");
        sectionTitle.setFont(F_SECTION);
        sectionTitle.setForeground(C_TEXT);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // goalCardsPanel đã được khởi tạo trong constructor
        goalCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(sectionTitle);
        section.add(vgap(14));
        section.add(goalCardsPanel);
        return section;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  XÂY DỰNG CARD MỤC TIÊU
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildGoalCard(Goal goal) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(C_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        // ── Tên mục tiêu ──────────────────────────────────────────────────
        JLabel nameLbl = new JLabel(goal.getTitle());
        nameLbl.setFont(F_CARD_T);
        nameLbl.setForeground(C_TEXT);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Loại mục tiêu ─────────────────────────────────────────────────
        String typeName = goal.getType() != null
                ? goal.getType().getDisplayName() : "—";
        JLabel typeLbl = new JLabel(typeName);
        typeLbl.setFont(F_SMALL);
        typeLbl.setForeground(C_MUTED);
        typeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Thanh tiến độ ─────────────────────────────────────────────────
        JPanel progressBlock = buildProgressBlock(goal);
        progressBlock.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Badge hoàn thành (chỉ hiện khi ACHIEVED) ─────────────────────
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (goal.getStatus() == GoalStatus.ACHIEVED) {
            JLabel badge = new JLabel("✓ Đã hoàn thành hôm nay!");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setForeground(C_GREEN);
            badge.setOpaque(true);
            badge.setBackground(C_GREEN_LT);
            badge.setBorder(new CompoundBorder(
                    new LineBorder(C_GREEN, 1, true),
                    new EmptyBorder(3, 10, 3, 10)));
            badgeRow.add(badge);
        }

//        // ── Nút -1 / +1 ───────────────────────────────────────────────────
//        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
//        btnRow.setOpaque(false);
//        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
//        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
//
//        JButton btnMinus = buildOutlineBtn("-1");
//        JButton btnPlus  = buildBlackBtn("+1");

//        // Lưu goalId vào biến cục bộ để lambda capture
//        final int gid = goal.getGoalID();
//        btnMinus.addActionListener(e -> {
//            if (controller != null)
//                controller.handleUpdateProgress(gid, -1);
//        });
//        btnPlus.addActionListener(e -> {
//            if (controller != null)
//                controller.handleUpdateProgress(gid, +1);
//        });

//        btnRow.add(btnMinus);
//        btnRow.add(btnPlus);

        // ── Ghép card ─────────────────────────────────────────────────────
        card.add(nameLbl);
        card.add(vgap(2));
        card.add(typeLbl);
        card.add(vgap(16));
        card.add(progressBlock);
        if (goal.getStatus() == GoalStatus.ACHIEVED) {
            card.add(vgap(8));
            card.add(badgeRow);
        }
        card.add(vgap(14));
//        card.add(btnRow);

        return card;
    }

    /** Xây dựng block tiến độ: label trên, bar, % dưới */
    private JPanel buildProgressBlock(Goal goal) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);

        // Hàng "Tiến độ" ←→ "1 / 3 hours"
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel pLabel = new JLabel("Tiến độ");
        pLabel.setFont(F_SMALL);
        pLabel.setForeground(C_MUTED);

        JLabel pValue = new JLabel(goal.getProgressLabel());
        pValue.setFont(F_SMALL);
        pValue.setForeground(C_TEXT);

        topRow.add(pLabel, BorderLayout.WEST);
        topRow.add(pValue, BorderLayout.EAST);

        // Progress bar
        int pct = goal.getProgressPercent();
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(pct);
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(0, 9));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));
        bar.setBackground(C_BAR_BG);
        bar.setForeground(goal.getStatus() == GoalStatus.ACHIEVED ? C_GREEN : C_BAR_FG);
        bar.setBorderPainted(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Hàng % (căn phải)
        JPanel pctRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pctRow.setOpaque(false);
        pctRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pctRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel pctLbl = new JLabel(pct + "%");
        pctLbl.setFont(F_SMALL);
        pctLbl.setForeground(C_MUTED);
        pctRow.add(pctLbl);

        block.add(topRow);
        block.add(vgap(5));
        block.add(bar);
        block.add(pctRow);
        return block;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  API CÔNG KHAI — GoalController gọi
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * displayGoals — hiển thị danh sách mục tiêu lên giao diện.
     * Luôn chạy trên EDT (Event Dispatch Thread).
     */
    public void displayGoals(List<Goal> goals) {
        this.currentGoals = controller.getGoals();
        SwingUtilities.invokeLater(() -> {
            refreshGoalCards();
            refreshSummary();
        });
    }

    /** displayEvaluationResults — hiển thị kết quả đánh giá */
    public void displayEvaluationResults(List<String> results) {
        if (results == null || results.isEmpty()) return;
        SwingUtilities.invokeLater(() -> showResultDialog(results));
    }

    /** showMessage */
    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Làm mới giao diện (nội bộ)
    // ─────────────────────────────────────────────────────────────────────────
    private void refreshGoalCards() {
        goalCardsPanel.removeAll();

        // Lọc chỉ lấy IN_PROGRESS và ACHIEVED để hiển thị trong section chính
        for (Goal g : currentGoals) {
            if (g.getStatus() != GoalStatus.FAILED) {
                goalCardsPanel.add(buildGoalCard(g));
            }
        }

        // Nếu tất cả đều FAILED hoặc rỗng → hiện thông báo trống
        if (goalCardsPanel.getComponentCount() == 0) {
            JLabel empty = new JLabel("Không có mục tiêu nào đang thực hiện.");
            empty.setFont(F_BODY);
            empty.setForeground(C_MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            goalCardsPanel.add(empty);
        }

        goalCardsPanel.revalidate();
        goalCardsPanel.repaint();
    }

    private void refreshSummary() {
        long ach = currentGoals.stream()
                .filter(g -> g.getStatus() == GoalStatus.ACHIEVED).count();
        long ip  = currentGoals.stream()
                .filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS).count();
        lblAchieved.setText(String.valueOf(ach));
        lblInProgress.setText(String.valueOf(ip));
        lblTotal.setText(String.valueOf(currentGoals.size()));
    }

    // ─── Dialog kết quả đánh giá ─────────────────────────────────────────────
    private void showResultDialog(List<String> results) {
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Kết quả đánh giá", true);
        dlg.setSize(420, 300);
        dlg.setLocationRelativeTo(this);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(C_CARD);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));

        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setBackground(new Color(248, 250, 252));
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);

        StringBuilder sb = new StringBuilder();
        for (String r : results) sb.append(r).append("\n");
        ta.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(ta);
        scroll.setBorder(new LineBorder(C_BORDER));

        JButton close = buildBlackBtn("Đóng");
        close.addActionListener(e -> dlg.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(close);

        JLabel head = new JLabel("📋 Nhật ký đánh giá");
        head.setFont(F_CARD_T);
        head.setForeground(C_TEXT);

        p.add(head,   BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(footer, BorderLayout.SOUTH);
        dlg.add(p);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI Helpers
    // ─────────────────────────────────────────────────────────────────────────
    private JButton buildBlackBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(C_BLACK);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 20, 9, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(40, 40, 55)); }
            public void mouseExited (MouseEvent e) { btn.setBackground(C_BLACK); }
        });
        return btn;
    }

    private JButton buildOutlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN);
        btn.setForeground(C_TEXT);
        btn.setBackground(C_CARD);
        btn.setOpaque(true);
        btn.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 20, 8, 20)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(245, 246, 248)); }
            public void mouseExited (MouseEvent e) { btn.setBackground(C_CARD); }
        });
        return btn;
    }

    private Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }
}