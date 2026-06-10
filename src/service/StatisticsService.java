package service;

import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.*;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import model.repository.TaskRepositoryImpl;
import model.repository.UserRepository;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class StatisticsService {
    private final String SESSION_FILE_PATH = "data/studysessions.txt";
    private final String TASK_FILE_PATH = "data/tasks.txt";
    private final UserRepository userRepository = new UserRepository();
    private final TaskRepositoryImpl taskRepository = new TaskRepositoryImpl();


    private int getLoggedInId() {
        return userRepository.getLoggedInUserId();
    }

    public List<Task> getCurrentUserTasks() {
        int loggedInId = userRepository.getLoggedInUserId();
        List<Task> allTasks = taskRepository.getAllTasks();
        List<Task> userTasks = new ArrayList<>();
        for (Task task : allTasks) {
            if (task.getUserId() == loggedInId) {
                userTasks.add(task);
            }
        }
        return userTasks;
    }
    private List<StudySession> getStudySessionsByUser() {
        List<StudySession> sessions = new ArrayList<>();
        String targetUserId = String.valueOf(getLoggedInId());

        try (BufferedReader br = new BufferedReader(new FileReader(SESSION_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                if (parts[1].trim().equals(targetUserId)) {
                    StudySession session = parseSessionFromLine(line);
                    if (session != null) sessions.add(session);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file sessions: " + e.getMessage());
        }
        return sessions;
    }

    private StudySession parseSessionFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
            int sessionId = Integer.parseInt(parts[0].trim());
            int userId = Integer.parseInt(parts[1].trim());
            Integer taskId = (parts[2].trim().equals("null")) ? null : Integer.parseInt(parts[2].trim());
            Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parts[3].trim());
            Date endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parts[4].trim());
            int duration = Integer.parseInt(parts[5].trim());
            SessionType type = SessionType.valueOf(parts[6].trim().toUpperCase());
            SessionStatus status = SessionStatus.valueOf(parts[7].trim().toUpperCase());
            return new StudySession(sessionId, userId, taskId, startTime, endTime, duration, type, status);
        } catch (Exception e) {
            return null;
        }
    }


    public DailyStats getDailyStats() {
        return new DailyStats(getTodayFocusTime(), countTodayPomodoroSessions(), getTodayTaskStatusStatistics());
    }

    // == METHOD DAILY
    // Method countTodayPomodoroSessions(user): Đếm số phiên Pomodoro đã hoàn thành
    // thành công.
    private int countTodayPomodoroSessions() {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (StudySession session : getStudySessionsByUser()) {
            LocalDate sessionDate = session.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (sessionDate.equals(today) && session.getSessionType() == SessionType.FOCUS && session.getStatus() == SessionStatus.COMPLETED) {
                count++;
            }
        }
        return count;
    }

    // Method getTodayTaskStatusStatistics(user): Lọc danh sách Task để đếm các
    // trạng thái Done, Pending, Overdue và các Task có deadline gần nhất
    // (getUpcomingTasks)
    public Map<TaskStatus, Integer> getTodayTaskStatusStatistics() {
        Map<TaskStatus, Integer> stats = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) stats.put(status, 0);

        // 1. Lấy danh sách task ĐÃ ĐƯỢC LỌC theo user hiện tại từ Service
        List<Task> tasks = getCurrentUserTasks();
        LocalDate today = LocalDate.now();

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));


        for (Task task : tasks) {
            if (task.getDeadline() != null) {
                LocalDate taskDate = task.getDeadline().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();


                // Logic đếm: Nếu muốn đếm task của hôm nay
                if (!taskDate.isBefore(startOfWeek) && !taskDate.isAfter(endOfWeek)) {
                    TaskStatus status = task.getStatus();
                    stats.put(status, stats.getOrDefault(status, 0) + 1);
                }
            }
        }
        return stats;

    }

    // Method getTodayFocusTime(user): Truy vấn tổng thời gian từ các StudySession
    // có ngày trùng với hôm nay.

    public double getTodayFocusTime() {
        double totalSeconds = 0;
        LocalDate today = LocalDate.now();
        for (StudySession session : getStudySessionsByUser()) {
            LocalDate sessionDate = session.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (sessionDate.equals(today) && session.getSessionType() == SessionType.FOCUS && session.getStatus() == SessionStatus.COMPLETED) {
                totalSeconds += session.getDuration();
            }
        }
        return Math.round((totalSeconds / 3600.0) * 10.0) / 10.0;
    }


    public List<Task> getOverdueTasks() {
        List<Task> tasks = getCurrentUserTasks();
        List<Task> overdueTasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            if (task.getStatus() != TaskStatus.DONE && task.getDeadline() != null) {

                // Chuyển Date sang LocalDateTime
                LocalDateTime taskDateTime = task.getDeadline().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // Kiểm tra điều kiện: Deadline là ngày hôm nay và đã qua giờ hiện tại
                if (taskDateTime.toLocalDate().equals(now.toLocalDate()) && taskDateTime.isBefore(now)) {
                    overdueTasks.add(task);
                }
            }
        }
        return overdueTasks;
    }

    public List<Task> getUpcomingTodayTasks() {
        List<Task> tasks = getCurrentUserTasks();
        List<Task> upcomingTodayTasks = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now(); // Lấy thời điểm hiện tại

        for (Task task : tasks) {
            if (task.getStatus() != TaskStatus.DONE && task.getDeadline() != null) {

                LocalDateTime taskDateTime = task.getDeadline().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // Điều kiện: Deadline thuộc ngày hôm nay VÀ chưa tới hạn (hoặc đúng thời điểm hiện tại)
                if (taskDateTime.toLocalDate().equals(now.toLocalDate()) && !taskDateTime.isBefore(now)) {
                    upcomingTodayTasks.add(task);
                }
            }
        }
        return upcomingTodayTasks;
    }

    private Task parseTaskFromLine(String line) {
        try {
            String[] parts = line.split("\\|");
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
    public WeeklyStats getWeeklyStatistics() {
        Map<LocalDate, Double> map = getStudyTimeByDay();
        double avg = map.values().stream().mapToDouble(d -> d).average().orElse(0.0);
        return new WeeklyStats(avg, map, calculateTaskCompletionRate());
    }


    // Method 1
    public Map<LocalDate, Double> getStudyTimeByDay() {
        Map<LocalDate, Double> studyTimeMap = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 0; i < 7; i++) {
            studyTimeMap.put(monday.plusDays(i), 0.0);
        }
        for (StudySession session : getStudySessionsByUser()) {
            LocalDate sessionDate = session.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (studyTimeMap.containsKey(sessionDate) && session.getStatus() == SessionStatus.COMPLETED) {
                studyTimeMap.put(sessionDate, studyTimeMap.get(sessionDate) + (session.getDuration() / 3600.0));
            }
        }
        return studyTimeMap;
    }
    public double calculateAverageFocusTime(User user) {
        Map<LocalDate, Double> map = getStudyTimeByDay();
        return map.values().stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }
public double calculateTaskCompletionRate() {
    // 1. Tái sử dụng logic lấy dữ liệu tuần
    Map<TaskStatus, Integer> stats = getTodayTaskStatusStatistics();

    // 2. Tính tổng số task trong Map
    int done = stats.getOrDefault(TaskStatus.DONE, 0);
    int total = stats.values().stream().mapToInt(Integer::intValue).sum();

    // 3. Tính tỷ lệ (Tránh lỗi chia 0)
    return (total == 0) ? 0.0 : ((double) done / total) * 100;
}
public Map<TaskStatus, Integer> getTaskStatusCounts() {
        Map<TaskStatus, Integer> counts = new EnumMap<>(TaskStatus.class);
    for (TaskStatus status : TaskStatus.values()) {
        counts.put(status, 0);
    }
    // Sử dụng getCurrentUserTasks() đã được sửa để lấy dữ liệu đúng của user hiện tại
    int targetUserId = getLoggedInId();
    List<Task> tasks = getCurrentUserTasks();
    LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

    for (Task task : tasks) {
        if (task != null && task.getUserId() == targetUserId && task.getDeadline() != null) {
            LocalDateTime taskDeadline = task.getDeadline().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Kiểm tra phạm vi thời gian (tuần này)
            LocalDate taskDate = taskDeadline.toLocalDate();
            if (!taskDate.isBefore(monday) && !taskDate.isAfter(sunday)) {

                // 1. Xác định trạng thái thực tế
                TaskStatus effectiveStatus = task.getStatus();


                // 2. Logic ưu tiên: Nếu chưa xong (NOT DONE) mà quá hạn -> OVERDUE
                if (effectiveStatus != TaskStatus.DONE && taskDeadline.isBefore(now)) {
                    effectiveStatus = TaskStatus.OVERDUE;
                }
                // 3. Cập nhật vào map (IN_PROGRESS, TODO, DONE, hoặc OVERDUE đều nằm ở đây)
                counts.put(effectiveStatus, counts.getOrDefault(effectiveStatus, 0) + 1);
            }
        }
    }

        return counts;
    }
}