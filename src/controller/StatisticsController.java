package controller;

import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.Task;
import model.entity.TaskStatus;
import model.entity.User;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import service.StatisticsService;
import view.StatisticsPanel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticsController {
    private StatisticsPanel view;
    private StatisticsService service;
    private ITaskRepository taskRepository;
    private IUserRepository userRepository;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public StatisticsController(StatisticsPanel view, ITaskRepository taskRepo, IUserRepository userRepo) {
        this.view = view;
        this.taskRepository = taskRepo;
        this.userRepository = userRepo;
        this.service = new StatisticsService(this.taskRepository, this.userRepository);
		// KÍCH HOẠT SCHEDULER
        scheduler.scheduleAtFixedRate(() -> {
            // Kiểm tra nếu view vẫn tồn tại thì mới làm mới dữ liệu
            if (view != null) {
                taskRepository.refresh();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
    //Controller nhận dữ liệu từ Model gọi View cập nhật dữ liệu
    public void loadDailyStats() {
        // 1. Làm dữ liệu luôn luôn mới nhât
        taskRepository.refresh();
        // 2. Gom tất cả dữ liệu được tính toán từ Model
        DailyStats stats = service.getDailyStats();
        List<Task> overdue = service.getOverdueTasks();
        List<Task> upcoming = service.getUpcomingTodayTasks();
        Map<TaskStatus, Integer> statsMap = service.getTodayTaskStatusStatistics(); // Đảm bảo hàm này trả về dữ liệu đúng
        // 3. Gọi View để cập nhật dữ liệu
        view.displayDailyStudyTime(stats.getTodayFocusTime());
        view.displayPomodoroCount(stats.getPomodoroCount());
        view.displayDailyTables(overdue, upcoming);
        view.displayTaskStatus(statsMap);
        view.refresh();
    }


    public void loadWeeklyStats() {
        // Luôn refresh trước khi tính toán để đảm bảo số liệu mới nhất
        taskRepository.refresh();
        // Gọi Service
        WeeklyStats weeklyStats = service.getWeeklyStatistics();
        Map<TaskStatus, Integer> counts = service.getTaskStatusCounts();
        double total = counts.values().stream().mapToInt(Integer::intValue).sum();

        if (total > 0) {
            double done = (counts.getOrDefault(TaskStatus.DONE, 0) * 100) / total;
            double prog = (counts.getOrDefault(TaskStatus.IN_PROGRESS, 0) * 100) / total;
            double pend = (counts.getOrDefault(TaskStatus.PENDING, 0) * 100) / total;
            double over = Math.max(0, 100 - done - prog - pend);

            view.updatePieChartData(done, prog, over, pend);
        }
        // Giai đoạn 4: Hiển thị lên UI
        double avgTime = weeklyStats.getAverageFocusTime();
        view.displayWeeklyStudyTime(Math.round(avgTime * 100.0) / 100.0);
        view.showStudyTimeChart(weeklyStats.getStudyTimeByDay());
        double completionRate = weeklyStats.getCompletionRate();
        view.showTaskCompletionPieChart(completionRate);
        view.refresh(); // Vẽ lại giao diện
    }
}