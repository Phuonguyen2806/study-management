package service;

import model.entity.ReminderLog;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.*;
import model.strategy.EmailStrategy;
import model.strategy.NotificationStrategy;
import model.strategy.PushStrategy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReminderService {
    private final MotivationService motivationService;
    private final IUserRepository userRepository;
    private final ITaskRepository taskRepository;
    private final IReminderRepository reminderRepository;
    private static final long REMINDER_THRESHOLD_MINUTES = 120;
    private static final Set<Integer> notifiedTaskIds = new HashSet<>();
    private final java.util.concurrent.ExecutorService emailExecutor = java.util.concurrent.Executors.newCachedThreadPool();

    public ReminderService(MotivationService motivationService,IUserRepository userRepository,
                           ITaskRepository taskRepository, IReminderRepository reminderRepository) {
        this.motivationService = motivationService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.reminderRepository = reminderRepository;
        // TỰ ĐỘNG LOAD LẠI CÁC TASK ĐÃ NHẮC NHỞ KHI KHỞI TẠO
        loadNotifiedTasks();
    }

    public void executeReminderWorkflow(boolean isMainAppActive) {
        // Lấy TẤT CẢ task từ file
        List<Task> allTasks = taskRepository.getAllTasks();

        // Quét tất cả task
        runReminderCheck(allTasks, isMainAppActive);
    }

private void runReminderCheck(List<Task> allTasks, boolean isMainAppActive) {
    for (Task task : allTasks) {
        // 1. Kiểm tra trạng thái DONE
        if (task.getStatus().equals(TaskStatus.DONE)) continue;

        // 2. Kiểm tra nếu đã gửi thành công (isSent = 1) -> Bỏ qua
        if (reminderRepository.isTaskSuccessfullySent(task.getTaskId())) continue;

        // 3. Cơ chế chặn spam (Thử lại sau 1 phút)
        ReminderLog lastLog = reminderRepository.getLastLogForTask(task.getTaskId());
        if (lastLog != null && lastLog.getIsSent() == 0) {
//            long minutesSinceLastAttempt = ChronoUnit.MINUTES.between(lastLog.getTime(), LocalDateTime.now());
            long minutesSinceLastAttempt = java.time.temporal.ChronoUnit.MINUTES.between(
                    lastLog.getTime(),
                    LocalDateTime.now()
            );
            if (minutesSinceLastAttempt < 1) {
                System.out.println("DEBUG: Đang trong thời gian chờ thử lại (p1) cho task: " + task.getTitle());
                continue; // Bỏ qua task này, chờ lần quét sau
            }
        }

        // 4. Kiểm tra thời hạn
        long diff = calculateTimeDiff(task.getDeadline());

        if (diff < 0) {
            // Task quá hạn
            System.out.println("DEBUG: Task " + task.getTitle() + " đã quá hạn. Bỏ qua.");
        } else if (diff <= REMINDER_THRESHOLD_MINUTES) {
            // Task trong ngưỡng [0, 120] phút
            boolean isOwnerActive = checkOwnerActiveStatus(task.getUserId(), isMainAppActive);
            processAndSendReminder(task, isOwnerActive);
        }
    }
}
    // Logic xác định xem chủ sở hữu task có đang dùng app không
    private boolean checkOwnerActiveStatus(int userId, boolean isMainAppActive) {
        // Nếu user ID 1 đang đăng nhập trên máy này và app đang focus
        // thì return true, ngược lại return false để gửi email
        int currentLoginId = userRepository.getLoggedInUserId();
        boolean isSameUser = (currentLoginId == userId);

        System.out.println("DEBUG: Check User " + userId + " | AppActive: " + isMainAppActive + " | SameUser: " + isSameUser);

        return isMainAppActive && isSameUser;
    }

    private void processAndSendReminder(Task task, boolean isUserActive) {
        // 2. Lấy quote
        String rawQuote = motivationService.getRandomQuote();
        String cleanQuote = rawQuote.contains("|") ? rawQuote.split("\\|")[1] : rawQuote;
        String message = "Nhắc nhở: " + task.getTitle() + "\n" + cleanQuote;

        NotificationStrategy strategy = isUserActive ? new PushStrategy() : new EmailStrategy(userRepository);
        // 3. Thực hiện gửi ngầm nếu là Email
            if (!isUserActive) {
                emailExecutor.submit(() -> {
                    boolean success = strategy.send(message, task);
                    System.out.println("Kết quả gửi email ngầm: " + success);
                    saveLog(task.getTaskId(), "EMAIL", success ? 1 : 0);
                });
            } else {
                strategy.send(message, task);
                saveLog(task.getTaskId(), "PUSH", 1);
            }
        }
        private void saveLog(int taskId, String method, int isSent) {
            ReminderLog log = new ReminderLog(
                    (int) (System.currentTimeMillis() / 1000),
                    new Date(),
                    method,
                    isSent, // Bây giờ giá trị này sẽ là 0 nếu thất bại
                    taskId
        );
        reminderRepository.saveReminder(log);
    }

    private long calculateTimeDiff(Date deadline) {
        if (deadline == null) return -999;
        return ChronoUnit.MINUTES.between(
                LocalDateTime.now(),
                deadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private void loadNotifiedTasks() {
        List<ReminderLog> history = reminderRepository.getAllLogs();
        System.out.println("DEBUG: Đã load " + history.size() + " log cũ.");
        for (ReminderLog log : history) {
            notifiedTaskIds.add(log.getTaskId());
            System.out.println("DEBUG: Đã thêm task " + log.getTaskId() + " vào danh sách đã nhắc.");
        }
    }
}