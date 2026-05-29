package controller;

import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.Task;
import model.entity.TaskStatus;
import model.entity.User;
import service.StatisticsService;
import view.MainFrame;
import view.StatisticsPanel;
import view.TaskPanel;

import java.util.List;
import java.util.Map;

public class StatisticsController {
	private StatisticsPanel view;
	private StatisticsService service;

	public StatisticsController(StatisticsPanel view) {
		this.view = view;
		this.service = new StatisticsService();
	}

	public void loadDailyStats(User currentUser) {
		// Gọi Service lấy đối tượng DTO hoàn chỉnh
		DailyStats stats = service.getDailyStats(currentUser);
		List<Task> overdue = service.getOverdueTasks(currentUser);
		List<Task> upcoming = service.getUpcomingTodayTasks(currentUser);

		// Cập nhật lên View (Các phương thức hiển thị)
		view.displayDailyStudyTime(stats.getTodayFocusTime());
		view.displayPomodoroCount(stats.getPomodoroCount());
		view.displayTaskStatus(stats.getTaskStatusMap());
		view.displayDailyTables(overdue, upcoming);
		// Refresh giao diện để vẽ lại kết quả
		view.refresh();
	}

	public void loadWeeklyStats(User currentUser) {
		// Gọi Service
		WeeklyStats weeklyStats = service.getWeeklyStatistics(currentUser);
		Map<TaskStatus, Integer> counts = service.getTaskStatusCounts(currentUser);
	    int total = counts.values().stream().mapToInt(Integer::intValue).sum();
	    
	    if (total > 0) {
	        int done = (counts.getOrDefault(TaskStatus.DONE, 0) * 100) / total;
	        int prog = (counts.getOrDefault(TaskStatus.IN_PROGRESS, 0) * 100) / total;
	        int over = 100 - done - prog;
	        
	        view.updatePieChartData(done, prog, over);
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