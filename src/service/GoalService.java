package service;


import model.entity.Goal;
import model.entity.GoalStatus;
import model.entity.TaskStatus;
import model.repository.IGoalRepository;
import model.repository.GoalRepositoryImpl;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class GoalService {
    private final List<Goal> goalList = new ArrayList<>();
    private String currentUserId = "1";
    private final IGoalRepository goalRepository;
    public GoalService(IGoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public void setCurrentUser(String userId) {
        // Nếu ID truyền vào trùng với ID hiện tại đang chạy thì bỏ qua, không nạp lại
        if (userId != null && userId.equals(this.currentUserId) && !this.goalList.isEmpty()) {
            return;
        }
        this.currentUserId = userId;
        init();
    }

    public List<Goal> syncAndGetActiveGoals(LocalDate date, StatisticsService statisticsService) {
        if (date.equals(LocalDate.now()) && statisticsService != null) {

            // Tầng Service tự đi lấy giờ học thô
            double hoursToday = statisticsService.getTodayFocusTime();

            // Tầng Service tự đi lấy số task đã xong
            java.util.Map<model.entity.TaskStatus, Integer> taskStats =
                    statisticsService.getTodayTaskStatusStatistics();
            int tasksDoneToday = taskStats.getOrDefault(model.entity.TaskStatus.DONE, 0);

            // Tầng Service tự gọi hàm đồng bộ nội bộ của nó
            this.syncGoalsWithStatistics(date, hoursToday, tasksDoneToday);
        }

        // 2. Trả về kết quả sau khi đã xử lý nghiệp vụ xong xuôi
        return this.getActiveGoalsByDate(date);
    }

    public void init() {

        goalList.clear();

        goalList.addAll(
                goalRepository.loadGoalsByUserId(
                        currentUserId
                )
        );

        LocalDate today =
                LocalDate.now();

        boolean hasGoalsToday =
                goalList.stream()
                        .anyMatch(
                                g -> g.getTargetDate()
                                        .equals(today)
                        );

        if (!hasGoalsToday) {

            generateDefaultGoalsForDate(today);

            saveToFile();
        }
    }
    private void generateDefaultGoalsForDate(LocalDate date) {
        goalList.add(new Goal(1, "Học 30 phút mỗi ngày", date, 0.5, "hours"));
        goalList.add(new Goal(2, "Hoàn thành 1 task mỗi ngày", date, 1.0, "tasks"));
        goalList.add(new Goal(3, "Học 1 giờ mỗi ngày", date, 1.0, "hours"));
        goalList.add(new Goal(4, "Hoàn thành 3 task mỗi ngày", date, 3.0, "tasks"));
        goalList.add(new Goal(5, "Học 3 giờ mỗi ngày", date, 3.0, "hours"));
        goalList.add(new Goal(6, "Hoàn thành 5 task mỗi ngày", date, 5.0, "tasks"));
    }


    // --- SAVE TO FILE: Bảo vệ dữ liệu tuyệt đối của User khác ---
    public void saveToFile() {

        for (Goal g : goalList) {

            if (g.getStatus() != GoalStatus.ACHIEVED
                    && g.getStatus() != GoalStatus.FAILED) {

                g.updateStatus();
            }
        }

        goalRepository.saveGoalsByUserId(
                currentUserId,
                goalList
        );
    }




    public List<Goal> getActiveGoalsByDate(LocalDate date) {
        List<Goal> activeGoals = new ArrayList<>();

        // 1. Cập nhật trạng thái một loạt trước
        for (Goal g : goalList) {
            g.updateStatus();
        }

        // 2. Gom tất cả các mục tiêu đã HOÀN THÀNH hoặc THẤT BẠI của ngày đó lên trước
        for (Goal g : goalList) {
            if (g.getTargetDate().equals(date) &&
                    (g.getStatus() == GoalStatus.ACHIEVED || g.getStatus() == GoalStatus.FAILED)) {
                activeGoals.add(g);
            }
        }

        // 3. XỬ LÝ PHÂN LUỒNG CUỐN CHIẾU RIÊNG BIỆT CHO "IN_PROGRESS"

        // Lọc riêng danh sách các mục tiêu đang dở dang (IN_PROGRESS) của ngày được chọn
        List<Goal> inProgressGoals = goalList.stream()
                .filter(g -> g.getTargetDate().equals(date) && g.getStatus() == GoalStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        // Nhánh 1: Tìm mục tiêu "hours"  ở cấp độ thấp nhất đang cần làm
        Goal nextHoursGoal = inProgressGoals.stream()
                .filter(g -> g.getUnit().equalsIgnoreCase("hours") || g.getTitle().toLowerCase().contains("học"))
                .min(Comparator.comparingInt(Goal::getGoalID)) // Lấy ID nhỏ nhất (cấp thấp nhất)
                .orElse(null);

        // Nhánh 2: Tìm mục tiêu "tasks"  ở cấp độ thấp nhất đang cần làm
        Goal nextTasksGoal = inProgressGoals.stream()
                .filter(g -> g.getUnit().equalsIgnoreCase("tasks") || g.getTitle().toLowerCase().contains("task"))
                .min(Comparator.comparingInt(Goal::getGoalID)) // Lấy ID nhỏ nhất (cấp thấp nhất)
                .orElse(null);

        // Đẩy mục tiêu hours tiếp theo vào danh sách hiển thị (nếu có)
        if (nextHoursGoal != null) {
            activeGoals.add(nextHoursGoal);
        }

        // Đẩy mục tiêu tasks tiếp theo vào danh sách hiển thị (nếu có)
        if (nextTasksGoal != null) {
            activeGoals.add(nextTasksGoal);
        }

        return activeGoals;
    }


    public List<Goal> getGoalsByDate(LocalDate date) {
        return goalList.stream()
                .filter(g -> g.getTargetDate().equals(date))
                .collect(Collectors.toList());
    }


    public long countByStatusAndDate(GoalStatus status, LocalDate date) {
        return getGoalsByDate(date).stream()
                .filter(g -> g.getStatus() == status)
                .count();
    }


    public void syncGoalsWithStatistics(LocalDate date, double totalHoursToday, int totalTasksDoneToday) {
        boolean isChanged = false;
        for (Goal g : goalList) {
            if (g.getTargetDate().equals(date)) {
                String titleLower = g.getTitle().toLowerCase();
                if (titleLower.contains("học") || titleLower.contains("giờ") || titleLower.contains("phút") || g.getUnit().equalsIgnoreCase("hours")) {
                    g.evaluate(totalHoursToday);
                    isChanged = true;
                } else if (titleLower.contains("task") || titleLower.contains("bài tập") || titleLower.contains("việc") || g.getUnit().equalsIgnoreCase("tasks")) {
                    g.evaluate(totalTasksDoneToday);
                    isChanged = true;
                }
            }
        }
        if (isChanged) {
            saveToFile();
        }


    }
    // Thêm hàm này vào GoalService.java
    public List<Goal> getSortedActiveGoals(LocalDate date, StatisticsService statisticsService) {
        // 1. Lấy danh sách mục tiêu hoạt động đã đồng bộ (logic cũ của bạn)
        List<Goal> activeGoals = syncAndGetActiveGoals(date, statisticsService);

        // 2. Tiến hành sắp xếp phân loại ngay tại tầng Service (Cắt từ View sang)
        List<Goal> sortedGoals = new ArrayList<>(activeGoals);
        Collections.sort(sortedGoals, new Comparator<Goal>() {
            @Override
            public int compare(Goal g1, Goal g2) {
                // Giữ nguyên logic so sánh thông minh của bạn ở đây
                boolean isG1Hours = g1.getUnit().equalsIgnoreCase("hours") || g1.getTitle().toLowerCase().contains("học") || g1.getTitle().toLowerCase().contains("giờ");
                boolean isG2Hours = g2.getUnit().equalsIgnoreCase("hours") || g2.getTitle().toLowerCase().contains("học") || g2.getTitle().toLowerCase().contains("giờ");

                if (isG1Hours && !isG2Hours) return -1;
                if (!isG1Hours && isG2Hours) return 1;

                if (g1.getStatus() != GoalStatus.ACHIEVED && g2.getStatus() == GoalStatus.ACHIEVED) return -1;
                if (g1.getStatus() == GoalStatus.ACHIEVED && g2.getStatus() != GoalStatus.ACHIEVED) return 1;

                return Integer.compare(g1.getGoalID(), g2.getGoalID());
            }
        });

        return sortedGoals;
    }
}



