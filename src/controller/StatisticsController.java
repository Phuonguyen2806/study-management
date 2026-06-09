package controller;

import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.Task;
import model.entity.TaskStatus;
import model.entity.User;
import service.StatisticsService;
import view.StatisticsPanel;
import java.util.List;
import java.util.Map;

public class StatisticsController {
	private StatisticsPanel view;
	private StatisticsService service;

	public StatisticsController(StatisticsPanel view) {
		this.view = view;
		this.service = new StatisticsService();
	}
	// Trong StatisticsController.java
	public void loadDailyStats(User currentUser) {
		if (currentUser == null) return;

		// Sử dụng service hiện có, đảm bảo lấy lại dữ liệu mới nhất
		DailyStats stats = service.getDailyStats();
		List<Task> overdue = service.getOverdueTasks();
		List<Task> upcoming = service.getUpcomingTodayTasks();
		Map<TaskStatus, Integer> statsMap = service.getTodayTaskStatusStatistics(); // Đảm bảo hàm này trả về dữ liệu đúng

		// Cập nhật lên View
		view.displayDailyStudyTime(stats.getTodayFocusTime());
		view.displayPomodoroCount(stats.getPomodoroCount());
//		view.displayTaskStatus(stats.getTaskStatusMap());
		view.displayDailyTables(overdue, upcoming);
		view.displayTaskStatus(statsMap);

		view.refresh();
	}

	public void loadWeeklyStats(User currentUser) {
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
		// Sửa dòng bị lỗi trong StatisticsController.java thành:
		double completionRate = weeklyStats.getCompletionRate();
		view.showTaskCompletionPieChart(completionRate);
		view.refresh(); // Vẽ lại giao diện
	}
}