package controller;

import model.entity.Goal;
import model.entity.GoalStatus;
import model.entity.TaskStatus;
import model.entity.User;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import service.GoalService;
import service.StatisticsService;
import view.GoalPanel;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public class GoalController {
    private final GoalService goalService;
    private final StatisticsService statisticsService; // Service cung cấp số liệu thô
    private GoalPanel goalPanel;
    private LocalDate selectedDate;
    private User currentUser;


    // Truyền thẳng các Service cần thiết vào từ hàm main/bộ khởi chạy ứng dụng
    public GoalController(GoalService goalService, StatisticsService statisticsService) {
        this.goalService = goalService;
        this.statisticsService = statisticsService;
        this.selectedDate = LocalDate.now();
    }

    public void setGoalPanel(GoalPanel panel) {
        this.goalPanel = panel;
    }


    /**
     * Khởi tạo Module mục tiêu với giao diện Panel và đối tượng User hiện tại
     */
    public void initialize(GoalPanel panel, User user) {
        if (panel != null && user != null) {
            this.goalPanel = panel;
            this.goalPanel.setController(this);
            this.currentUser = user; // Gán user vào biến toàn cục để tái sử dụng
            // Thiết lập ID người dùng cho GoalService để lọc chính xác file text goals.txt
            this.goalService.setCurrentUser(String.valueOf(user.getUserID()));
            loadAndDisplay();
        }
    }


    /**
     * Hàm điều phối chính: Lấy số đếm từ Stat, đẩy sang Goal so sánh và cập nhật giao diện
     */
    public void loadAndDisplay() {
        if (goalPanel == null || currentUser == null) return;

        // Lấy danh sách đã ĐỒNG BỘ và SẮP XẾP TỪNG NHÓM từ Service
        List<Goal> sortedActiveGoals = goalService.getSortedActiveGoals(selectedDate, statisticsService);

        // Tính toán trước các số liệu thống kê
        int totalCount = goalService.getGoalsByDate(selectedDate).size();
        long achievedCount = goalService.countByStatusAndDate(GoalStatus.ACHIEVED, selectedDate);
        long inProgressCount = goalService.countByStatusAndDate(GoalStatus.IN_PROGRESS, selectedDate);

        goalPanel.displayGoals(sortedActiveGoals, totalCount, achievedCount, inProgressCount);
    }

    public void refreshView(model.entity.User currentUser) {
        if (currentUser == null || this.goalPanel == null) {
            return;
        }
        this.currentUser = currentUser;
        this.loadAndDisplay();
    }
}



