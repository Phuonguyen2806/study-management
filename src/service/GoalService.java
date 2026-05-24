package service;

import model.entity.Goal;
import model.entity.GoalStatus;
import model.entity.GoalType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * GoalService — Business Logic Layer
 *
 * Quản lý 2 mục tiêu CỐ ĐỊNH:
 *   1. Học 3 giờ mỗi ngày   (hours,  target 3)
 *   2. Hoàn thành 5 task/ngày (tasks, target 5)
 *
 * Người dùng KHÔNG thêm/xóa. Chỉ cập nhật tiến độ qua +1 / -1.
 */
public class GoalService {

    private final List<Goal> goalList = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────────────────
    public GoalService() {
        initDefaultGoals();
    }

    // ── Khởi tạo 2 mục tiêu cố định ─────────────────────────────────────────
    private void initDefaultGoals() {
//        goalList.clear();

        // Mục tiêu 1: Học 3 giờ mỗi ngày
        Goal g1 = new Goal(
                1,
                "Học 3 giờ mỗi ngày",
                GoalType.DAILY,
                LocalDate.now(),
                LocalDate.now().plusYears(1),   // không hết hạn trong ngày
                3.0,
                "hours"
        );
        g1.setCurrentValue(0.0);
        g1.setStatus(GoalStatus.IN_PROGRESS);

        // Mục tiêu 2: Hoàn thành 5 task mỗi ngày
        Goal g2 = new Goal(
                2,
                "Hoàn thành 5 task mỗi ngày",
                GoalType.DAILY,
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                5.0,
                "tasks"
        );
        g2.setCurrentValue(0.0);
        g2.setStatus(GoalStatus.IN_PROGRESS);

        goalList.add(g1);
        goalList.add(g2);
    }

    // ── Truy xuất ────────────────────────────────────────────────────────────
    public List<Goal> getAllGoals() {
        return this.goalList;   // trả bản sao để tránh mutation ngoài
    }

    public Goal getGoalById(int goalId) {
        return goalList.stream()
                .filter(g -> g.getGoalID() == goalId)
                .findFirst()
                .orElse(null);
    }

    public int getTotalGoals() {
        return goalList.size();
    }

    public long countByStatus(GoalStatus status) {
        return goalList.stream()
                .filter(g -> g.getStatus() == status)
                .count();
    }

    // ── Cập nhật tiến độ ─────────────────────────────────────────────────────
    /**
     * Được gọi khi người dùng nhấn +1 / -1 trên GoalPanel.
     * Sequence diagram: GoalService → Goal.evaluate → updateStatus
     */
    public void updateGoalProgress(int goalId, int delta) {
        Goal goal = getGoalById(goalId);
        if (goal == null) return;
        goal.updateProgress(delta);
        goal.updateStatus();
    }

    // ── Reset hàng ngày ───────────────────────────────────────────────────────
    public void resetDailyGoals() {
        initDefaultGoals();
    }
}