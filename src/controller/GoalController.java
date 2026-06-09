package controller;


import model.entity.Goal;
import model.entity.GoalStatus;
import model.entity.TaskStatus;
import model.entity.User;
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
    private User currentUser; // ĐÃ BỔ SUNG: Biến toàn cục lưu trữ User đăng nhập


    public GoalController() {
        this.goalService = new GoalService();
        this.statisticsService = new StatisticsService(); // Khởi tạo service thống kê
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
            this.goalService.setCurrentUser(String.valueOf(user.getUserId()));


            // Tiến hành quét số liệu đồng bộ và hiển thị lên giao diện
            loadAndDisplay();
        }
    }


    /**
     * Hàm điều phối chính: Lấy số đếm từ Stat, đẩy sang Goal so sánh và cập nhật giao diện
     */
    public void loadAndDisplay() {
        if (goalPanel == null || currentUser == null) return;


        // 1. Thực hiện đồng bộ số liệu THƯỚC (Nếu là ngày hôm nay)
        if (selectedDate.equals(LocalDate.now())) {
            double hoursToday = statisticsService.getTodayFocusTime(currentUser);
            Map<TaskStatus, Integer> taskStats = statisticsService.getTodayTaskStatusStatistics(currentUser);
            int tasksDoneToday = taskStats.getOrDefault(TaskStatus.DONE, 0);


            // Đẩy số liệu mới cập nhật vào RAM và ghi xuống file luôn
            goalService.syncGoalsWithStatistics(selectedDate, hoursToday, tasksDoneToday);
        }


        // 2. Sau khi đã đồng bộ dữ liệu chuẩn vào file/RAM -> Mới lấy ra để hiển thị lên UI
        List<Goal> activeGoals = goalService.getActiveGoalsByDate(selectedDate);
        goalPanel.displayGoals(activeGoals);
    }






    /**
     * Thay đổi ngày xem mục tiêu (ví dụ xem lịch sử mục tiêu các ngày trước)
     */
    public void changeSelectedDate(LocalDate newDate) {
        this.selectedDate = newDate;
        loadAndDisplay();
    }


    /**
     * Lấy ngày đang được chọn trên giao diện
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }


    /**
     * Các hàm bổ trợ để GoalPanel gọi lấy số liệu hiển thị lên "Bảng lịch sử thành tích"
     */
    public int getTotalGoals() {
        return goalService.getGoalsByDate(selectedDate).size();
    }


    public long getCountByStatus(GoalStatus status) {
        return goalService.countByStatusAndDate(status, selectedDate);
    }
}



