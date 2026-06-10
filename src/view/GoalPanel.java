package view;

import model.entity.Goal;
import model.entity.GoalStatus;
import controller.GoalController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GoalPanel extends JPanel {
    private GoalController controller;
    private JPanel pnlCardsContainer;

    // Nhãn số liệu thống kê tổng quan
    private JLabel lblAchievedCount;
    private JLabel lblInProgressCount;
    private JLabel lblTotalCount;

    public GoalPanel() {
        initComponents();
    }

    public void setController(GoalController controller) {
        this.controller = controller;
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Nền sáng hiện đại
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. TIÊU ĐỀ MODULE
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 0, 5));
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Mục tiêu học tập", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(33, 37, 41));

        JLabel lblSubTitle = new JLabel("Theo dõi tiến độ và duy trì động lực học tập mỗi ngày", SwingConstants.LEFT);
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubTitle.setForeground(new Color(108, 117, 125));

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblSubTitle);

        // 2. BẢNG THỐNG KÊ LỊCH SỬ THÀNH TÍCH
        JPanel pnlSummaryCard = new JPanel(new BorderLayout());
        pnlSummaryCard.setBackground(Color.WHITE);
        pnlSummaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblSummaryTitle = new JLabel("Lịch sử thành tích hệ thống", SwingConstants.LEFT);
        lblSummaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSummaryTitle.setForeground(new Color(33, 37, 41));
        lblSummaryTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlSummaryCard.add(lblSummaryTitle, BorderLayout.NORTH);

        JPanel pnlSummaryGrid = new JPanel(new GridLayout(3, 2, 0, 8));
        pnlSummaryGrid.setOpaque(false);

        JLabel lblAchievedText = new JLabel("• Hoàn thành", SwingConstants.LEFT);
        lblAchievedText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAchievedText.setForeground(new Color(40, 167, 69));
        lblAchievedCount = new JLabel("0", SwingConstants.RIGHT);
        lblAchievedCount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAchievedCount.setForeground(new Color(40, 167, 69));

        JLabel lblInProgressText = new JLabel("• Đang thực hiện", SwingConstants.LEFT);
        lblInProgressText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInProgressText.setForeground(new Color(255, 152, 0));
        lblInProgressCount = new JLabel("0", SwingConstants.RIGHT);
        lblInProgressCount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblInProgressCount.setForeground(new Color(255, 152, 0));

        JLabel lblTotalText = new JLabel("• Tổng số mục tiêu", SwingConstants.LEFT);
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalText.setForeground(new Color(0, 123, 255));
        lblTotalCount = new JLabel("0", SwingConstants.RIGHT);
        lblTotalCount.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotalCount.setForeground(new Color(0, 123, 255));

        pnlSummaryGrid.add(lblAchievedText);
        pnlSummaryGrid.add(lblAchievedCount);
        pnlSummaryGrid.add(lblInProgressText);
        pnlSummaryGrid.add(lblInProgressCount);
        pnlSummaryGrid.add(lblTotalText);
        pnlSummaryGrid.add(lblTotalCount);
        pnlSummaryCard.add(pnlSummaryGrid, BorderLayout.CENTER);

        JPanel pnlNorthWrapper = new JPanel(new BorderLayout(0, 15));
        pnlNorthWrapper.setOpaque(false);
        pnlNorthWrapper.add(pnlHeader, BorderLayout.NORTH);
        pnlNorthWrapper.add(pnlSummaryCard, BorderLayout.CENTER);

        // 3. KHU VỰC HIỂN THỊ CÁC CARD CHI TIẾT (BỌC CUỘN)
        JPanel pnlCenterWrapper = new JPanel(new BorderLayout(0, 10));
        pnlCenterWrapper.setOpaque(false);
        pnlCenterWrapper.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblDetailTitle = new JLabel("Trạng thái mục tiêu chi tiết", SwingConstants.LEFT);
        lblDetailTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblDetailTitle.setForeground(new Color(33, 37, 41));
        pnlCenterWrapper.add(lblDetailTitle, BorderLayout.NORTH);

        pnlCardsContainer = new ScrollablePanel(new WrapLayout(FlowLayout.LEFT, 15, 15));
        pnlCardsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(pnlCardsContainer);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        pnlCenterWrapper.add(scrollPane, BorderLayout.CENTER);

        add(pnlNorthWrapper, BorderLayout.NORTH);
        add(pnlCenterWrapper, BorderLayout.CENTER);
    }

    public void displayGoals(List<Goal> activeGoals) {
        pnlCardsContainer.removeAll();

        // Nạp số liệu thống kê lên bảng trên
        if (controller != null) {
            lblTotalCount.setText(String.valueOf(controller.getTotalGoals()));
            lblAchievedCount.setText(String.valueOf(controller.getCountByStatus(GoalStatus.ACHIEVED)));
            lblInProgressCount.setText(String.valueOf(controller.getCountByStatus(GoalStatus.IN_PROGRESS)));
        }

        if (activeGoals == null || activeGoals.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có mục tiêu nào được thiết lập cho ngày này.", SwingConstants.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblEmpty.setForeground(Color.GRAY);
            pnlCardsContainer.setLayout(new BorderLayout());
            pnlCardsContainer.add(lblEmpty, BorderLayout.CENTER);
        } else {
            pnlCardsContainer.setLayout(new WrapLayout(FlowLayout.LEFT, 15, 15));

            List<Goal> sortedGoals = new ArrayList<>(activeGoals);

            // THỰC HIỆN SẮP XẾP PHÂN LOẠI THEO NHÓM (GIỜ TRƯỚC - TASK SAU) VÀ CUỐN CHIẾU TRONG TỪNG NHÓM
            Collections.sort(sortedGoals, new Comparator<Goal>() {
                @Override
                public int compare(Goal g1, Goal g2) {
                    // 1. Phân biệt loại mục tiêu: Đẩy nhóm "Hours" lên trước nhóm "Tasks"
                    boolean isG1Hours = g1.getUnit().equalsIgnoreCase("hours") || g1.getTitle().toLowerCase().contains("học") || g1.getTitle().toLowerCase().contains("giờ");
                    boolean isG2Hours = g2.getUnit().equalsIgnoreCase("hours") || g2.getTitle().toLowerCase().contains("học") || g2.getTitle().toLowerCase().contains("giờ");

                    if (isG1Hours && !isG2Hours) return -1; // g1 là Giờ học -> lên trước
                    if (!isG1Hours && isG2Hours) return 1;  // g2 là Giờ học -> lên trước

                    // 2. Nếu CÙNG LOẠI mục tiêu (Cùng là Giờ học hoặc cùng là Task), xét trạng thái HOÀN THÀNH
                    // Mục tiêu nào CHƯA HOÀN THÀNH (IN_PROGRESS) phải đứng trước để cuốn chiếu lên
                    if (g1.getStatus() != GoalStatus.ACHIEVED && g2.getStatus() == GoalStatus.ACHIEVED) {
                        return -1;
                    }
                    if (g1.getStatus() == GoalStatus.ACHIEVED && g2.getStatus() != GoalStatus.ACHIEVED) {
                        return 1;
                    }

                    // 3. Nếu cùng loại và cùng trạng thái, sắp xếp theo ID tăng dần (Nhỏ làm trước, lớn làm sau)
                    return Integer.compare(g1.getGoalID(), g2.getGoalID());
                }
            });

            // Tiến hành render danh sách đã sắp xếp đúng nhóm và cuốn chiếu
            for (Goal goal : sortedGoals) {
                JPanel card = createGoalCard(goal);
                pnlCardsContainer.add(card);
            }
        }

        pnlCardsContainer.revalidate();
        pnlCardsContainer.repaint();
    }

    /**
     * Tạo Card hình chữ nhật nằm ngang chuẩn tỉ lệ UI, thu hẹp chiều cao tối đa
     */
    private JPanel createGoalCard(Goal goal) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setPreferredSize(new Dimension(260, 140));
        card.setMinimumSize(new Dimension(260, 140));
        card.setMaximumSize(new Dimension(260, 140));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 235), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        // 1. Tiêu đề mục tiêu
        JLabel lblGoalTitle = new JLabel(goal.getTitle());
        lblGoalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGoalTitle.setForeground(new Color(33, 37, 41));
        lblGoalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 2. Dòng hiển thị số tiến độ và phần trăm chữ nằm ngang hàng
        JPanel pnlProgressText = new JPanel(new BorderLayout());
        pnlProgressText.setOpaque(false);
        pnlProgressText.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlProgressText.setBorder(new EmptyBorder(8, 0, 4, 0));

        String progressStr = String.format("Tiến độ: %.1f/%.1f %s",
                goal.getCurrentValue(), goal.getTargetValue(), goal.getUnit());
        JLabel lblProgressStr = new JLabel(progressStr);
        lblProgressStr.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblProgressStr.setForeground(new Color(108, 117, 125));

        int percentage = 0;
        boolean isAchieved = goal.getStatus() == GoalStatus.ACHIEVED;

        if (isAchieved || goal.getTargetValue() <= 0) {
            percentage = 100;
        } else {
            percentage = (int) Math.min(100, (goal.getCurrentValue() / goal.getTargetValue()) * 100);
        }

        JLabel lblPercent = new JLabel(percentage + "%");
        lblPercent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPercent.setForeground(new Color(0, 123, 255));

        pnlProgressText.add(lblProgressStr, BorderLayout.WEST);
        pnlProgressText.add(lblPercent, BorderLayout.EAST);

        // 3. Thanh Progress Bar thanh thoát tinh chỉnh mỏng lại (Cao 6px)
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setPreferredSize(new Dimension(230, 6));
        progressBar.setMaximumSize(new Dimension(230, 6));
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(0, 123, 255));
        progressBar.setBackground(new Color(235, 240, 250));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 4. Nhãn trạng thái chữ nằm sát lề dưới gọn gàng
        JLabel lblStatus = new JLabel(isAchieved ? "Đã hoàn thành" : "Đang thực hiện");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(isAchieved ? new Color(40, 167, 69) : new Color(255, 152, 0));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        card.add(lblGoalTitle);
        card.add(pnlProgressText);
        card.add(progressBar);
        card.add(lblStatus);

        return card;
    }

    // NHÓM CLASS NỘI BỘ (STATIC NESTED CLASSES)

    private static class ScrollablePanel extends JPanel implements Scrollable {
        public ScrollablePanel(LayoutManager layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 50;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxwidth = targetWidth - (insets.left + insets.right + hgap * 2);
                int nmembers = target.getComponentCount();
                int x = 0;
                int y = insets.top + vgap;
                int rowHeight = 0;

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if ((x == 0) || (x + d.width <= maxwidth)) {
                            if (x > 0) x += hgap;
                            x += d.width;
                            rowHeight = Math.max(rowHeight, d.height);
                        } else {
                            x = d.width;
                            y += vgap + rowHeight;
                            rowHeight = d.height;
                        }
                    }
                }
                return new Dimension(targetWidth, y + rowHeight + vgap + insets.bottom);
            }
        }
    }


}