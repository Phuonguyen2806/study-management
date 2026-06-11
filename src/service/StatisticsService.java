package service;

import config.AppConstants;
import model.dto.DailyStats;
import model.dto.WeeklyStats;
import model.entity.*;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {
    private final String SESSION_FILE_PATH = AppConstants.FILE_STUDY_SESSIONS;
    private final IUserRepository userRepository;
    private final ITaskRepository taskRepository;

    public StatisticsService(ITaskRepository taskRepository, IUserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

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
    // trạng thái Done, Pending, Overdue, progress
    public Map<TaskStatus, Integer> getTodayTaskStatusStatistics() {
        Map<TaskStatus, Integer> stats = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) stats.put(status, 0);

        int targetUserId = getLoggedInId();
        List<Task> tasks = getCurrentUserTasks();
        LocalDate today = LocalDate.now();

        for (Task task : tasks) {
            // Kiểm tra cơ bản
            if (task == null || task.getUserId() != targetUserId) continue;
            LocalDate taskDate = (task.getDeadline() != null) ?
                    task.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
            // 1. Kiểm tra xem task này đã có phiên làm việc hoàn thành trong hôm nay chưa
            boolean isDoneToday = isTaskCompletedToday(task.getTaskId());

//        / 2. Xác định trạng thái hiệu dụng
            TaskStatus effectiveStatus = task.getStatus();

            if (isDoneToday) {
                effectiveStatus = TaskStatus.DONE;
            } else if (taskDate != null && taskDate.isBefore(today) && effectiveStatus != TaskStatus.DONE) {
                effectiveStatus = TaskStatus.OVERDUE;
            }
            // 2. BỘ LỌC CHẶT CHẼ HƠN:
            // - Chỉ lấy task đúng deadline hôm nay
            // - HOẶC task đã quá hạn NHƯNG vẫn chưa xong (để hiển thị trong danh sách OVERDUE)
            // - HOẶC task đã hoàn thành HÔM NAY (dù deadline là ngày nào)
            boolean isTaskForToday = (taskDate != null && taskDate.equals(today));
            // Logic mới cho biến isRelevantOverdue
            boolean isRelevantOverdue = (taskDate != null &&
                    taskDate.isBefore(today) &&
                    !isDoneToday &&
                    (effectiveStatus == TaskStatus.PENDING || effectiveStatus == TaskStatus.IN_PROGRESS));

            if (isTaskForToday || isRelevantOverdue || isDoneToday) {
                stats.put(effectiveStatus, stats.getOrDefault(effectiveStatus, 0) + 1);
            }
        }
        return stats;
    }

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

    public double calculateTaskCompletionRate() {
        // 1. Tái sử dụng logic lấy dữ liệu tuần
        Map<TaskStatus, Integer> stats = getTaskStatusCounts();

        // 2. Tính tổng số task trong Map
        int done = stats.getOrDefault(TaskStatus.DONE, 0);
        int total = stats.values().stream().mapToInt(Integer::intValue).sum();

        // 3. Tính tỷ lệ (Tránh lỗi chia 0)
        return (total == 0) ? 0.0 : ((double) done / total) * 100;
    }
    private boolean isTaskCompletedToday(int taskId) {
        int loggedInId = userRepository.getLoggedInUserId();
        Task task = taskRepository.findTaskById(taskId, loggedInId);

        if (task == null || !task.isDone()) {
            return false;
        }

        LocalDate today = LocalDate.now();

        // Sử dụng anyMatch với điều kiện chặt chẽ hơn để không bị lặp
        return getStudySessionsByUser().stream()
                .filter(s -> taskId == s.getTaskId())
                .filter(s -> {
                    LocalDate sessionDate = s.getStartTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    return sessionDate.equals(today);
                })
                // Chỉ cần 1 bản ghi hợp lệ là đủ, không cần gom thành List
                .anyMatch(s -> s.getStatus() == SessionStatus.COMPLETED
                        || s.getStatus() == SessionStatus.STOPPED_EARLY);
    }
    public Map<TaskStatus, Integer> getTaskStatusCounts() {
        Map<TaskStatus, Integer> counts = new EnumMap<>(TaskStatus.class);
        for (TaskStatus status : TaskStatus.values()) counts.put(status, 0);

        int targetUserId = getLoggedInId();
        List<Task> tasks = getCurrentUserTasks();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        for (Task task : tasks) {
            if (task == null || task.getUserId() != targetUserId) continue;
            LocalDate taskDate = task.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            boolean isDoneToday = isTaskCompletedToday(task.getTaskId());
            TaskStatus effectiveStatus = task.getStatus();
            if (isDoneToday) {
                effectiveStatus = TaskStatus.DONE;
            } else if (taskDate.isBefore(today) && effectiveStatus != TaskStatus.DONE) {
                effectiveStatus = TaskStatus.OVERDUE;
            }
            // 2. Định nghĩa các điều kiện để task được tính vào thống kê tuần
            // - Task có deadline nằm trong tuần hiện tại
            // - HOẶC task đã quá hạn trước tuần này nhưng chưa hoàn thành (nếu bạn muốn hiển thị Overdue tồn đọng)
            // - HOẶC task hoàn thành trong hôm nay (dù deadline là ngày nào)
            boolean isTaskInWeek = (!taskDate.isBefore(monday) && !taskDate.isAfter(sunday));
            boolean isRelevantOverdue = (taskDate.isBefore(monday) && !isDoneToday &&
                    (effectiveStatus == TaskStatus.PENDING || effectiveStatus == TaskStatus.IN_PROGRESS));

            if (isTaskInWeek || isRelevantOverdue || isDoneToday) {
                counts.put(effectiveStatus, counts.getOrDefault(effectiveStatus, 0) + 1);
            }
        }
        return counts;
    }
}