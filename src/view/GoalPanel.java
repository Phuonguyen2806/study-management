package view;

import model.Goal;
import model.GoalStatus;
import model.GoalType;
import controller.GoalController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GoalPanel — View (MVC)
 * Giao diện chức năng Mục tiêu học tập
 * Khớp với mockup: header + thành tích + danh sách mục tiêu + nút +1/-1
 *
 * Interface methods (GoalPanel trong class diagram):
 *   +displayGoals(goals: List<Goal>): void
 *   +displayEvaluationResults(results: List<String>): void
 *   +getSelectedGoalId(): int
 *   +showMessage(message: String): void
 */
public class GoalPanel extends JPanel {

    // ── Palette — khớp với app Pomo Focus (đen + cam) ──────────────────────
    private static final Color C_BG        = new Color(250, 250, 252);
    private static final Color C_CARD      = Color.WHITE;
    private static final Color C_BORDER    = new Color(218, 220, 228);
    private static final Color C_BLACK     = new Color(18,  18,  22);
    private static final Color C_ORANGE    = new Color(234,  88,   0);  // cam chính
    private static final Color C_ORANGE_LT = new Color(255, 237, 213);
    private static final Color C_GOLD      = new Color(202, 138,   4);
    private static final Color C_GOLD_LT   = new Color(254, 249, 195);
    private static final Color C_GREEN     = new Color( 22, 163,  74);
    private static final Color C_GREEN_LT  = new Color(220, 252, 231);
    private static final Color C_RED       = new Color(220,  38,  38);
    private static final Color C_RED_LT    = new Color(254, 226, 226);
    private static final Color C_TEXT      = new Color(17,  24,  39);
    private static final Color C_MUTED     = new Color(107, 114, 128);
    private static final Color C_PROGRESS  = new Color(31,  41,  55);  // thanh tiến trình đen

    // ── Typography ──────────────────────────────────────────────────────────
    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font F_SUB     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SECTION = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font F_CARD_T  = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BADGE   = new Font("Segoe UI", Font.BOLD,  11);
    private static final Font F_BTN     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_STAT_N  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_STAT_V  = new Font("Segoe UI", Font.BOLD,  13);

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Controller ──────────────────────────────────────────────────────────
    private GoalController controller;

    // ── State ───────────────────────────────────────────────────────────────
    private List<Goal> currentGoals  = new ArrayList<>();
    private int        selectedGoalId = -1;

    // ── Widgets tham chiếu ──────────────────────────────────────────────────
    private JPanel goalCardsPanel;           // container danh sách thẻ mục tiêu
    private JLabel lblAchieved;
    private JLabel lblInProgress;
    private JLabel lblTotal;
    private JDialog logDialog;              // dialog nhật ký đánh giá

    // ════════════════════════════════════════════════════════════════════════
    public GoalPanel() {
        setLayout(new BorderLayout());
        setBackground(C_BG);
        buildUI();
    }

    public GoalPanel(GoalController controller) {
        this();
        this.controller = controller;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  BUILD UI
    // ─────────────────────────────────────────────────────────────────────────
    private void buildUI() {
        // Toàn bộ nội dung bên trong scroll
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_BG);
        content.setBorder(new EmptyBorder(28, 32, 28, 32));

        content.add(buildPageHeader());
        content.add(vgap(24));
        content.add(buildSummaryRow());
        content.add(vgap(28));
        content.add(buildGoalSection());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(C_BG);
        scroll.getViewport().setBackground(C_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    // ─── 1. Page header ──────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        // Trái: tiêu đề + mô tả
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

        // Phải: nút + Thêm mục tiêu
        JButton btnAdd = buildBlackBtn("+ Thêm mục tiêu");
        btnAdd.addActionListener(e -> openAddGoalDialog());

        p.add(left,   BorderLayout.WEST);
        p.add(btnAdd, BorderLayout.EAST);
        return p;
    }

    // ─── 2. Summary row (Thành tích) ─────────────────────────────────────────
    private JPanel buildSummaryRow() {
        JPanel row = new JPanel(new GridLayout(1, 1));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Chỉ giữ card "Thành tích" — đã bỏ streak
        JPanel achCard = buildAchievementCard();
        row.add(achCard);
        return row;
    }

    private JPanel buildAchievementCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(C_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(20, 24, 20, 24)));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        header.setOpaque(false);
        JLabel icon = new JLabel("🏆");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel lbl = new JLabel("Thành tích");
        lbl.setFont(F_CARD_T);
        lbl.setForeground(C_TEXT);
        header.add(icon);
        header.add(lbl);

        // Số liệu dạng bảng
        JPanel stats = new JPanel(new GridLayout(3, 2, 0, 4));
        stats.setOpaque(false);

        lblAchieved   = new JLabel("0");
        lblInProgress = new JLabel("0");
        lblTotal      = new JLabel("0");

        stats.add(makeStatLabel("Hoàn thành",    F_STAT_N, C_MUTED));
        stats.add(makeStatValue(lblAchieved,      C_GREEN));
        stats.add(makeStatLabel("Đang thực hiện", F_STAT_N, C_MUTED));
        stats.add(makeStatValue(lblInProgress,    C_ORANGE));
        stats.add(makeStatLabel("Tổng cộng",      F_STAT_N, C_MUTED));
        stats.add(makeStatValue(lblTotal,         C_TEXT));

        card.add(header, BorderLayout.NORTH);
        card.add(stats,  BorderLayout.CENTER);
        return card;
    }

    private JLabel makeStatLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    private JLabel makeStatValue(JLabel lbl, Color color) {
        lbl.setFont(F_STAT_V);
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        return lbl;
    }

    // ─── 3. Section "Mục tiêu đang thực hiện" + danh sách thẻ ──────────────
    private JPanel buildGoalSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);

        // Tiêu đề section
        JLabel sectionTitle = new JLabel("Mục tiêu đang thực hiện");
        sectionTitle.setFont(F_SECTION);
        sectionTitle.setForeground(C_TEXT);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Container chứa các thẻ (2 cột grid)
        goalCardsPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        goalCardsPanel.setOpaque(false);
        goalCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(sectionTitle);
        section.add(vgap(14));
        section.add(goalCardsPanel);
        return section;
    }

    // ─── Thẻ mục tiêu (khớp hình minh hoạ) ──────────────────────────────────
    private JPanel buildGoalCard(Goal goal) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(C_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        // ── Hàng tiêu đề + icon edit/delete ─────────────────────────────
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel nameLbl = new JLabel(goal.getTitle());
        nameLbl.setFont(F_CARD_T);
        nameLbl.setForeground(C_TEXT);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);

        JButton btnEdit = buildIconBtn("✏", C_MUTED);
        JButton btnDel  = buildIconBtn("🗑", C_RED);
        btnEdit.addActionListener(e -> openEditGoalDialog(goal));
        btnDel.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Xoá mục tiêu \"" + goal.getTitle() + "\"?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION && controller != null)
                controller.handleDeleteGoal(goal.getGoalID());
        });
        actions.add(btnEdit);
        actions.add(btnDel);

        topRow.add(nameLbl,  BorderLayout.WEST);
        topRow.add(actions,  BorderLayout.EAST);

        // ── Loại mục tiêu ────────────────────────────────────────────────
        JLabel typeLbl = new JLabel(goal.getType() != null
                ? goal.getType().getDisplayName() : "—");
        typeLbl.setFont(F_SMALL);
        typeLbl.setForeground(C_MUTED);
        typeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Tiến độ ──────────────────────────────────────────────────────
        JPanel progressSection = new JPanel(new BorderLayout(0, 4));
        progressSection.setOpaque(false);
        progressSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pRow = new JPanel(new BorderLayout());
        pRow.setOpaque(false);
        JLabel pLabel = new JLabel("Tiến độ");
        pLabel.setFont(F_SMALL);
        pLabel.setForeground(C_MUTED);
        JLabel pValue = new JLabel(goal.getProgressLabel());
        pValue.setFont(F_SMALL);
        pValue.setForeground(C_TEXT);
        pRow.add(pLabel, BorderLayout.WEST);
        pRow.add(pValue, BorderLayout.EAST);

        // Thanh progress bar đen
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(goal.getProgressPercent());
        bar.setStringPainted(false);
        bar.setPreferredSize(new Dimension(0, 8));
        bar.setBackground(new Color(229, 231, 235));
        bar.setForeground(C_PROGRESS);
        bar.setBorderPainted(false);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Phần trăm bên phải
        JPanel pctRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pctRow.setOpaque(false);
        JLabel pct = new JLabel(goal.getProgressPercent() + "%");
        pct.setFont(F_SMALL);
        pct.setForeground(C_MUTED);
        pctRow.add(pct);

        progressSection.add(pRow,   BorderLayout.NORTH);
        progressSection.add(bar,    BorderLayout.CENTER);
        progressSection.add(pctRow, BorderLayout.SOUTH);

        // ── Hàng nút -1 / +1 (khớp mockup) ─────────────────────────────
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnMinus = buildOutlineBtn("-1");
        JButton btnPlus  = buildBlackBtn("+1");

        btnMinus.addActionListener(e -> {
            if (controller != null)
                controller.handleUpdateProgress(goal.getGoalID(), -1);
        });
        btnPlus.addActionListener(e -> {
            if (controller != null)
                controller.handleUpdateProgress(goal.getGoalID(), +1);
        });

        btnRow.add(btnMinus);
        btnRow.add(btnPlus);

        // ── Ghép vào card ─────────────────────────────────────────────────
        card.add(topRow);
        card.add(vgap(4));
        card.add(typeLbl);
        card.add(vgap(16));
        card.add(progressSection);
        card.add(vgap(14));
        card.add(btnRow);

        return card;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Interface methods — GoalPanel contract
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * displayGoals — cập nhật danh sách mục tiêu lên giao diện
     * Sequence diagram step 11
     */
    public void displayGoals(List<Goal> goals) {
        this.currentGoals = goals != null ? goals : new ArrayList<>();
        SwingUtilities.invokeLater(this::refreshGoalCards);
        SwingUtilities.invokeLater(this::refreshSummary);
    }

    /**
     * displayEvaluationResults — hiển thị nhật ký kết quả đánh giá
     * Sequence diagram step 11
     */
    public void displayEvaluationResults(List<String> results) {
        if (results == null || results.isEmpty()) return;
        SwingUtilities.invokeLater(() -> showEvaluationLog(results));
    }

    /** getSelectedGoalId */
    public int getSelectedGoalId() { return selectedGoalId; }

    /** showMessage */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Refresh helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void refreshGoalCards() {
        goalCardsPanel.removeAll();

        // Chỉ hiển thị mục tiêu đang thực hiện trong danh sách chính
        List<Goal> active = currentGoals.stream()
                .filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS)
                .collect(java.util.stream.Collectors.toList());

        if (active.isEmpty()) {
            JLabel empty = new JLabel("Chưa có mục tiêu nào đang thực hiện.");
            empty.setFont(F_BODY);
            empty.setForeground(C_MUTED);
            goalCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            goalCardsPanel.add(empty);
        } else {
            goalCardsPanel.setLayout(new GridLayout(0, 2, 16, 16));
            for (Goal g : active) {
                goalCardsPanel.add(buildGoalCard(g));
            }
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

    // ─────────────────────────────────────────────────────────────────────────
    //  Dialog: Nhật ký đánh giá (popup sau khi evaluateGoals xong)
    // ─────────────────────────────────────────────────────────────────────────
    private void showEvaluationLog(List<String> results) {
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Kết quả đánh giá mục tiêu", true);
        dlg.setSize(480, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setBackground(C_CARD);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(C_CARD);
        content.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("📋 Nhật ký đánh giá");
        title.setFont(F_SECTION);
        title.setForeground(C_TEXT);

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Consolas", Font.PLAIN, 12));
        log.setForeground(new Color(31, 41, 55));
        log.setBackground(new Color(249, 250, 251));
        log.setBorder(new EmptyBorder(10, 12, 10, 12));
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        StringBuilder sb = new StringBuilder();
        for (String r : results) sb.append(r).append("\n");
        log.setText(sb.toString());

        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(new LineBorder(C_BORDER, 1, true));

        JButton close = buildBlackBtn("Đóng");
        close.setAlignmentX(Component.RIGHT_ALIGNMENT);
        close.addActionListener(e -> dlg.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(close);

        content.add(title,  BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.add(footer, BorderLayout.SOUTH);

        dlg.add(content);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Dialog: Thêm mục tiêu mới
    // ─────────────────────────────────────────────────────────────────────────
    private void openAddGoalDialog() {
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm mục tiêu mới", true);
        dlg.setSize(500, 600);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_CARD);
        content.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel title = new JLabel("Tạo mục tiêu mới");
        title.setFont(F_SECTION);
        title.setForeground(C_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tfName   = new JTextField();
        JTextField tfTarget = new JTextField();
        JComboBox<GoalType> cbType = new JComboBox<>(GoalType.values());
        JComboBox<String>   cbUnit = new JComboBox<>(
                new String[]{"hours", "tasks", "sessions"});
        JTextField tfStart  = new JTextField(LocalDate.now().format(DATE_FMT));
        JTextField tfEnd    = new JTextField(LocalDate.now().plusDays(7).format(DATE_FMT));

        content.add(title);
        content.add(vgap(16));
        content.add(formRow("Tên mục tiêu",               tfName));   content.add(vgap(10));
        content.add(formRow("Loại (Hàng ngày/tuần/tháng)", cbType));  content.add(vgap(10));
        content.add(formRow("Mục tiêu (con số)",           tfTarget)); content.add(vgap(10));
        content.add(formRow("Đơn vị",                     cbUnit));   content.add(vgap(10));
        content.add(formRow("Ngày bắt đầu (dd/MM/yyyy)",  tfStart));  content.add(vgap(10));
        content.add(formRow("Ngày kết thúc (dd/MM/yyyy)", tfEnd));    content.add(vgap(20));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancel = buildOutlineBtn("Huỷ");
        JButton save   = buildBlackBtn("Lưu");

        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            try {
                String name   = tfName.getText().trim();
                double target = Double.parseDouble(tfTarget.getText().trim());
                GoalType type = (GoalType) cbType.getSelectedItem();
                String unit   = (String)   cbUnit.getSelectedItem();
                LocalDate sd  = LocalDate.parse(tfStart.getText().trim(), DATE_FMT);
                LocalDate ed  = LocalDate.parse(tfEnd.getText().trim(),   DATE_FMT);

                if (name.isEmpty()) { showMessage("Vui lòng nhập tên mục tiêu."); return; }
                if (controller != null)
                    controller.handleAddGoal(name, type, sd, ed, target, unit);
                dlg.dispose();
            } catch (Exception ex) {
                showMessage("Dữ liệu không hợp lệ: " + ex.getMessage());
            }
        });

        footer.add(cancel);
        footer.add(save);
        content.add(footer);

        dlg.add(content);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Dialog: Chỉnh sửa mục tiêu
    // ─────────────────────────────────────────────────────────────────────────
    private void openEditGoalDialog(Goal goal) {
        // Tái dụng dialog Add nhưng điền sẵn dữ liệu
        JDialog dlg = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chỉnh sửa mục tiêu", true);
        dlg.setSize(440, 400);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(C_CARD);
        content.setBorder(new EmptyBorder(22, 26, 22, 26));

        JLabel title = new JLabel("Chỉnh sửa mục tiêu");
        title.setFont(F_SECTION);
        title.setForeground(C_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tfName   = new JTextField(goal.getTitle());
        JTextField tfTarget = new JTextField(String.valueOf((int) goal.getTargetValue()));
        JComboBox<GoalType> cbType = new JComboBox<>(GoalType.values());
        cbType.setSelectedItem(goal.getType());
        JComboBox<String> cbUnit = new JComboBox<>(
                new String[]{"hours", "tasks", "sessions"});
        cbUnit.setSelectedItem(goal.getUnit());
        JTextField tfStart = new JTextField(goal.getStartDate() != null
                ? goal.getStartDate().format(DATE_FMT) : "");
        JTextField tfEnd   = new JTextField(goal.getEndDate()   != null
                ? goal.getEndDate().format(DATE_FMT)   : "");

        content.add(title);
        content.add(vgap(16));
        content.add(formRow("Tên mục tiêu",               tfName));   content.add(vgap(10));
        content.add(formRow("Loại",                       cbType));   content.add(vgap(10));
        content.add(formRow("Mục tiêu (con số)",           tfTarget)); content.add(vgap(10));
        content.add(formRow("Đơn vị",                     cbUnit));   content.add(vgap(10));
        content.add(formRow("Ngày bắt đầu (dd/MM/yyyy)",  tfStart));  content.add(vgap(10));
        content.add(formRow("Ngày kết thúc (dd/MM/yyyy)", tfEnd));    content.add(vgap(20));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancel = buildOutlineBtn("Huỷ");
        JButton save   = buildBlackBtn("Lưu thay đổi");

        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            try {
                goal.setTitle(tfName.getText().trim());
                goal.setTargetValue(Double.parseDouble(tfTarget.getText().trim()));
                goal.setType((GoalType) cbType.getSelectedItem());
                goal.setUnit((String) cbUnit.getSelectedItem());
                goal.setStartDate(LocalDate.parse(tfStart.getText().trim(), DATE_FMT));
                goal.setEndDate(LocalDate.parse(tfEnd.getText().trim(),     DATE_FMT));
                displayGoals(currentGoals);   // re-render
                dlg.dispose();
            } catch (Exception ex) {
                showMessage("Dữ liệu không hợp lệ: " + ex.getMessage());
            }
        });

        footer.add(cancel);
        footer.add(save);
        content.add(footer);

        dlg.add(content);
        dlg.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  UI helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Nút đen chính (khớp mockup) */
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
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(40, 40, 48)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(C_BLACK); }
        });
        return btn;
    }

    /** Nút viền outline */
    private JButton buildOutlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN);
        btn.setForeground(C_TEXT);
        btn.setBackground(C_CARD);
        btn.setOpaque(true);
        btn.setBorder(new CompoundBorder(
                new LineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 18, 8, 18)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(245, 246, 248)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(C_CARD); }
        });
        return btn;
    }

    /** Nút icon nhỏ (edit / delete) */
    private JButton buildIconBtn(String icon, Color color) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        btn.setForeground(color);
        btn.setBackground(C_CARD);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(30, 30));
        return btn;
    }

    /** Hàng form label + field */
    private JPanel formRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_MUTED);

        field.setFont(F_BODY);
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(new CompoundBorder(
                    new LineBorder(C_BORDER, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        }

        row.add(lbl,   BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    /** Khoảng cách dọc cố định */
    private Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }
}