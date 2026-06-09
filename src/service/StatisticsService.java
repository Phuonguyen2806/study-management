package service;

import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {
	private final String SESSION_FILE_PATH = "data/studysessions.txt";
	private final String TASK_FILE_PATH = "data/tasks.txt";
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public DailyStats getDailyStats(User user) {
		// 1. Tính toán các giá trị thô
		double time = getTodayFocusTime(user);
		int pomo = countTodayPomodoroSessions(user);
		Map<TaskStatus, Integer> taskStats = getTodayTaskStatusStatistics(user);

		// 2. <<create>> đối tượng DailyStats
		DailyStats stats = new DailyStats(time, pomo, taskStats);
		// Có thể set thêm danh sách upcoming tasks nếu cần
		return stats;
	}

	// == METHOD DAILY
	// Method countTodayPomodoroSessions(user): Đếm số phiên Pomodoro đã hoàn thành
	// thành công.
	private int countTodayPomodoroSessions(User user) {
		int count = 0;
		String todayStr = LocalDate.now().toString();
		String targetUserId = String.valueOf(user.getUserId());

		try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE_PATH))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\\|");
				if (parts.length < 8)
					continue;

				String fileUserId = parts[1].trim();
				String startTime = parts[3].trim(); // Định dạng: YYYY-MM-DD HH:mm:ss
				String type = parts[6].trim(); // POMODORO
				String status = parts[7].trim(); // COMPLETED

				if (fileUserId.equals(targetUserId) && startTime.startsWith(todayStr) && type.equalsIgnoreCase("FOCUS")
						&& status.equalsIgnoreCase("COMPLETED")) {
					count++;
				}
			}
		} catch (IOException e) {
			System.err.println("Lỗi khi đếm Pomodoro: " + e.getMessage());
		}
		return count;
	}

    // Method getTodayTaskStatusStatistics(user): Lọc danh sách Task để đếm các
    // trạng thái Done, Pending, Overdue và các Task có deadline gần nhất
    // (getUpcomingTasks)
    public Map<TaskStatus, Integer> getTodayTaskStatusStatistics() {
        Map<TaskStatus, Integer> stats = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) stats.put(status, 0);

		int targetUserId = user.getUserId();
		LocalDate today = LocalDate.now(); // 2026-05-29

		try (BufferedReader br = new BufferedReader(new FileReader(TASK_FILE_PATH))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				// Tận dụng lại phương thức parseTaskFromLine để code gọn và chuẩn hơn
				Task task = parseTaskFromLine(line);

				if (task != null && task.getUserId() == targetUserId && task.getDeadline() != null) {
					// Chuyển deadline từ Date sang LocalDate để so sánh ngày
					LocalDate taskDate = task.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					// CHỈ ĐẾM NẾU TASK CÓ DEADLINE LÀ HÔM NAY
					if (taskDate.equals(today)) {
						TaskStatus status = task.getStatus();
						stats.put(status, stats.getOrDefault(status, 0) + 1);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Lỗi khi thống kê Task hôm nay: " + e.getMessage());
		}
		return stats;
	}
	// Method getTodayFocusTime(user): Truy vấn tổng thời gian từ các StudySession
	// có ngày trùng với hôm nay.

	public double getTodayFocusTime(User user) {
		double totalSeconds = 0;
		File file = new File(SESSION_FILE_PATH);

		// Tự động lấy ngày hôm nay (2026-05-29)
		String todayStr = LocalDate.now().toString();
		String targetUserId = String.valueOf(user.getUserId());

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("\\|");
				if (parts.length < 8)
					continue;

				// Mapping: ID|UserID|TaskID|Start|End|Duration|Type|Status
				String fileUserId = parts[1].trim();
				String startTime = parts[3].trim();
				int duration = Integer.parseInt(parts[5].trim());
				String type = parts[6].trim();
				String status = parts[7].trim();

				if (fileUserId.equals(targetUserId) && startTime.startsWith(todayStr) && type.equals("FOCUS")
						&& status.equals("COMPLETED")) {

					totalSeconds += duration;
				}
			}
		} catch (Exception e) {
			System.out.println("Lỗi đọc file: " + e.getMessage());
		}

		// Chuyển đổi sang giờ và làm tròn 1 chữ số thập phân
		double hours = totalSeconds / 3600.0;
		return Math.round(hours * 10.0) / 10.0;
	}

	public List<Task> getOverdueTasks(User user) {
		List<Task> overdueTasks = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now(); // Lấy thời điểm hiện tại (29/05/2026 21:24)

		try (BufferedReader br = new BufferedReader(new FileReader(TASK_FILE_PATH))) {
			String line;
			while ((line = br.readLine()) != null) {
				Task task = parseTaskFromLine(line);
				if (task != null && task.getUserId() == user.getUserId() && task.getStatus() != TaskStatus.DONE
						&& task.getDeadline() != null) {

					// Chuyển Date sang LocalDateTime
					LocalDateTime taskDateTime = task.getDeadline().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();

					// Điều kiện: Deadline thuộc ngày hôm nay VÀ giờ deadline đã qua
					if (taskDateTime.toLocalDate().equals(now.toLocalDate()) && taskDateTime.isBefore(now)) {
						overdueTasks.add(task);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return overdueTasks;
	}

	public List<Task> getUpcomingTodayTasks(User user) {
		List<Task> upcomingTodayTasks = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now(); // Lấy thời điểm hiện tại

		try (BufferedReader br = new BufferedReader(new FileReader(TASK_FILE_PATH))) {
			String line;
			while ((line = br.readLine()) != null) {
				Task task = parseTaskFromLine(line);
				if (task != null && task.getUserId() == user.getUserId() && task.getStatus() != TaskStatus.DONE
						&& task.getDeadline() != null) {

					LocalDateTime taskDateTime = task.getDeadline().toInstant().atZone(ZoneId.systemDefault())
							.toLocalDateTime();

					// Điều kiện: Deadline thuộc ngày hôm nay VÀ giờ deadline >= giờ hiện tại
					if (taskDateTime.toLocalDate().equals(now.toLocalDate()) && !taskDateTime.isBefore(now)) {
						upcomingTodayTasks.add(task);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return upcomingTodayTasks;
	}

	private Task parseTaskFromLine(String line) {
		try {
			String[] parts = line.split("\\|");
			// Giả sử định dạng file:
			// ID|Title|Desc|Deadline|Priority|EstPomo|CompPomo|Status|UserId
			int taskId = Integer.parseInt(parts[0]);
			String title = parts[1];
			String desc = parts[2];
			Date deadline = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parts[3]);
			Priority priority = Priority.valueOf(parts[4].trim().toUpperCase());
			int estPomo = Integer.parseInt(parts[5]);
			int compPomo = Integer.parseInt(parts[6]);
			TaskStatus status = TaskStatus.valueOf(parts[7]);
			int userId = Integer.parseInt(parts[8]);

			return new Task(taskId, title, desc, deadline, priority, estPomo, compPomo, status, userId);
		} catch (Exception e) {
			return null; // Bỏ qua các dòng lỗi
		}
	}

	// == METHOD WEEKLY
	// Gọi phương thức chính để lấy đối tượng báo cáo
	public WeeklyStats getWeeklyStatistics(User user) {
		Map<LocalDate, Double> studyTimeMap = getStudyTimeByDay(user);
	    
	    // Tính trung bình từ map đã có sẵn thay vì gọi lại service
	    double avgTime = studyTimeMap.values().stream()
	                                 .mapToDouble(d -> d)
	                                 .average().orElse(0.0);
	                                 
	    double completionRate = calculateTaskCompletionRate(user);

	    return new WeeklyStats(avgTime, studyTimeMap, completionRate);
	}

	// Method 1
	public Map<LocalDate, Double> getStudyTimeByDay(User user) {
		Map<LocalDate, Double> studyTimeMap = new LinkedHashMap<>();
		LocalDate today = LocalDate.now();

		for (int i = 6; i >= 0; i--) {
			LocalDate date = today.minusDays(i);
			double totalHours = 0.0;

			// Duyệt qua file để tính thời gian cho ngày này
			try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE_PATH))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] parts = line.split("\\|");
					// Giả định cột 1: UserId, cột 3: startTime, cột 5: duration, cột 7: status
					if (parts[1].trim().equals(String.valueOf(user.getUserId())) && parts[3].contains(date.toString())
							&& parts[7].trim().equalsIgnoreCase("COMPLETED")) {
						totalHours += Double.parseDouble(parts[5].trim()) / 3600.0; // Giả định duration lưu bằng giây
					}
				}
			} catch (IOException | NumberFormatException e) {
				e.printStackTrace();
			}
			studyTimeMap.put(date, totalHours);
		}
		return studyTimeMap;
	}

	public double calculateAverageFocusTime(User user) {
	    Map<LocalDate, Double> map = getStudyTimeByDay(user);
	    return map.values().stream()
	              .mapToDouble(d -> d)
	              .average()
	              .orElse(0.0);
	}

	public double calculateTaskCompletionRate(User user) {
	    int totalTasks = 0;
	    int completedTasks = 0;
	    LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

	    try (BufferedReader br = new BufferedReader(new FileReader(TASK_FILE_PATH))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            Task task = parseTaskFromLine(line); // Hàm parse đã có
	            if (task != null && task.getUserId() == user.getUserId() && task.getDeadline() != null) {
	                LocalDate taskDate = task.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	                
	                if (!taskDate.isBefore(sevenDaysAgo)) {
	                    totalTasks++;
	                    if (task.getStatus() == TaskStatus.DONE) completedTasks++;
	                }
	            }
	        }
	    } catch (IOException e) { e.printStackTrace(); }
	    
	    return (totalTasks == 0) ? 0.0 : ((double) completedTasks / totalTasks) * 100;
	}

	public Map<TaskStatus, Integer> getTaskStatusCounts(User user) {
	    Map<TaskStatus, Integer> counts = new EnumMap<>(TaskStatus.class);
	    for (TaskStatus status : TaskStatus.values()) {
	        counts.put(status, 0);
	    }

	    // Xác định khoảng thời gian: Thứ Hai đến Chủ Nhật của tuần hiện tại
	    LocalDate today = LocalDate.now();
	    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	    LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

	    try (BufferedReader br = new BufferedReader(new FileReader(TASK_FILE_PATH))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            Task task = parseTaskFromLine(line);
	            if (task != null && task.getUserId() == user.getUserId() && task.getDeadline() != null) {
	                LocalDate taskDate = task.getDeadline().toInstant()
	                                         .atZone(ZoneId.systemDefault()).toLocalDate();

	                // Kiểm tra xem ngày của task có nằm trong khoảng từ Thứ Hai đến Chủ Nhật không
	                if (!taskDate.isBefore(monday) && !taskDate.isAfter(sunday)) {
	                    TaskStatus status = task.getStatus();
	                    counts.put(status, counts.getOrDefault(status, 0) + 1);
	                }
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return counts;
	}
}