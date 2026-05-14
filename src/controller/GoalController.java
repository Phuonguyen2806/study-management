package controller;

import model.Goal;
import model.GoalStatus;
import service.GoalService;
import view.GoalPanel;

import java.util.List;

/**
 * GoalController — Controller (MVC)
 *
 * Cầu nối giữa GoalPanel (View) và GoalService (Model/Service).
 *
 * Luồng khởi động đúng:
 *   1. new GoalController(goalService)
 *   2. new GoalPanel(goalController)        ← View tạo sau
 *   3. goalController.setGoalPanel(panel)   ← Kết nối 2 chiều
 *   4. goalController.loadAndDisplay()      ← Gọi để hiển thị lần đầu
 */
public class GoalController {

    private final GoalService goalService;
    private GoalPanel         goalPanel;

    // ─────────────────────────────────────────────────────────────────────────
    public GoalController() {
        this.goalService = new GoalService();
    }

    /** Được gọi sau khi GoalPanel đã được tạo */
    public void setGoalPanel(GoalPanel panel) {
        this.goalPanel = panel;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Load và hiển thị lần đầu — MainController gọi sau khi setup xong
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Sequence diagram Giai đoạn 1:
     *   MainController → handleEvaluateGoals() → GoalService.getGoals()
     *   → GoalPanel.displayGoals()
     */
    public void loadAndDisplay() {
        if (goalPanel == null) return;
        List<Goal> goals = goalService.getAllGoals();
        goalPanel.displayGoals(goals);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Xử lý +1 / -1 từ View
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Sequence diagram Giai đoạn 3:
     *   GoalPanel (View) → GoalController → GoalService.updateGoalProgress()
     *   → Goal.updateProgress() + Goal.updateStatus()
     *   → GoalPanel.displayGoals() (Giai đoạn 4)
     */
    public void handleUpdateProgress(int goalId, int delta) {
        goalService.updateGoalProgress(goalId, delta);
        if (goalPanel != null) {
            goalPanel.displayGoals(goalService.getAllGoals());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lấy danh sách (dùng cho MainController khi cần)
    // ─────────────────────────────────────────────────────────────────────────
    public List<Goal> getGoals() {
        return goalService.getAllGoals();
    }
    // hàm khởi tạo.
    public void initialize(GoalPanel panel) {
        if (panel != null) {
            this.goalPanel = panel;

            // 1. Kết nối ngược lại từ View về Controller này
            this.goalPanel.setController(this);

            // 2. Kích hoạt hiển thị dữ liệu ngay lập tức
            loadAndDisplay();
        }
    }

    // ── Thống kê cho summary card ─────────────────────────────────────────────
    public int  getTotalGoals()                     { return goalService.getTotalGoals(); }
    public long getCountByStatus(GoalStatus status) { return goalService.countByStatus(status); }
}