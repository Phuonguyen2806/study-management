package service;


import model.entity.Goal;
import model.entity.GoalStatus;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class GoalService {
    private final String FILE_PATH = "data/goals.txt";
    private final List<Goal> goalList = new ArrayList<>();
    private String currentUserId = "1";


    public GoalService() {


    }


    public void setCurrentUser(String userId) {
        // Nếu ID truyền vào trùng với ID hiện tại đang chạy thì bỏ qua, không nạp lại
        if (userId != null && userId.equals(this.currentUserId) && !this.goalList.isEmpty()) {
            return;
        }
        this.currentUserId = userId;
        init();
    }


    // --- INIT: Đọc dữ liệu nguyên bản từ file, không tự ý ghi đè số của người dùng ---
    public void init() {
        goalList.clear();
        try {
            Path path = Paths.get(FILE_PATH);
            if (path.getParent() != null && !Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path);
                int lineNumber = 0;

                for (String line : lines) {
                    lineNumber++;
                    if (line.trim().isEmpty()) continue;

                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        String fileUserId = parts[0].trim();

                        if (fileUserId.equals(currentUserId)) {
                            try {
                                LocalDate date = LocalDate.parse(parts[1].trim());
                                int id = Integer.parseInt(parts[2].trim());
                                String title = parts[3].trim();
                                double currentValue = Double.parseDouble(parts[4].trim());
                                double targetValue = Double.parseDouble(parts[5].trim()); // Đọc chuẩn xác 0.0167
                                String unit = parts[6].trim();
                                GoalStatus status = GoalStatus.valueOf(parts[7].trim());

                                // KHÔNG CHÈN CODE ÉP BUỘC TARGETVALUE Ở ĐÂY NỮA
                                Goal goal = new Goal(id, title, date, targetValue, unit);
                                goal.setCurrentValue(currentValue);
                                goal.setStatus(status);

                                goalList.add(goal);
                            } catch (Exception e) {
                                System.err.println(">>> [Lỗi định dạng] Dòng " + lineNumber + ": " + e.getMessage());
                            }
                        }
                    }
                }
            }

            // Tự động sinh mục tiêu mặc định nếu chưa có dữ liệu ngày hôm nay
            LocalDate today = LocalDate.now();
            boolean hasGoalsToday = goalList.stream().anyMatch(g -> g.getTargetDate().equals(today));

            if (!hasGoalsToday) {
                System.out.println(">>> Phát hiện User [" + currentUserId + "] chưa có mục tiêu cho ngày " + today + ". Tự động khởi tạo 6 mục tiêu mặc định...");
                generateDefaultGoalsForDate(today);
                saveToFile();
            }

            System.out.println("Init Goal: Đã nạp " + goalList.size() + " mục tiêu của User ID [" + currentUserId + "]");
        } catch (IOException e) {
            System.err.println("Lỗi Init Goal: " + e.getMessage());
        }
    }


    // Hàm phụ trợ: Tự động sinh 6 mục tiêu cho một ngày nhất định
    private void generateDefaultGoalsForDate(LocalDate date) {
        goalList.add(new Goal(1, "Học 30 phút mỗi ngày", date, 0.0167, "hours"));
        goalList.add(new Goal(2, "Hoàn thành 1 task mỗi ngày", date, 1.0, "tasks"));
        goalList.add(new Goal(3, "Học 1 giờ mỗi ngày", date, 1.0, "hours"));
        goalList.add(new Goal(4, "Hoàn thành 3 task mỗi ngày", date, 3.0, "tasks")); // Đã sửa lại từ 2.0 thành 3.0 cho khớp tiêu đề của bạn
        goalList.add(new Goal(5, "Học 3 giờ mỗi ngày", date, 3.0, "hours"));
        goalList.add(new Goal(6, "Hoàn thành 5 task mỗi ngày", date, 5.0, "tasks"));
    }


    // --- SAVE TO FILE: Bảo vệ dữ liệu tuyệt đối của User khác ---
    public void saveToFile() {
        try {
            Path path = Paths.get(FILE_PATH);
            List<String> allLinesToSave = new ArrayList<>();


            // 1. ĐỌC FILE CŨ ĐỂ GIỮ LẠI DỮ LIỆU CỦA USER KHÁC
            if (Files.exists(path)) {
                List<String> currentFileLines = Files.readAllLines(path);
                for (String line : currentFileLines) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        String fileUserId = parts[0].trim();


                        // SỬA LỖI CHÍNH: Chỉ giữ lại dòng của USER KHÁC.
                        // Toàn bộ dữ liệu (cũ + mới) của USER HIỆN TẠI sẽ được ghi lại từ goalList ở bước 2.
                        if (!fileUserId.equals(currentUserId)) {
                            allLinesToSave.add(line);
                        }
                    }
                }
            }


            // 2. GHI TOÀN BỘ DỮ LIỆU CỦA USER HIỆN TẠI (BAO GỒM CẢ CÁC NGÀY TRƯỚC VÀ NGÀY NAY)
            for (Goal g : goalList) {
                // Đảm bảo trạng thái luôn được cập nhật trước khi ghi file
                if (g.getStatus() != GoalStatus.ACHIEVED && g.getStatus() != GoalStatus.FAILED) {
                    g.updateStatus();
                }


                String line = String.format("%s | %s | %d | %s | %s | %s | %s | %s",
                        currentUserId,
                        g.getTargetDate().toString(),
                        g.getGoalID(),
                        g.getTitle(),
                        String.valueOf(g.getCurrentValue()),
                        String.valueOf(g.getTargetValue()),
                        g.getUnit(),
                        g.getStatus().name()
                );
                allLinesToSave.add(line);
            }


            // 3. GHI ĐÈ LẠI FILE
            Files.write(path, allLinesToSave, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println(">>> Đã đồng bộ dữ liệu của User ID [" + currentUserId + "] vào file goals.txt");


        } catch (IOException e) {
            System.err.println("Lỗi lưu file Goal: " + e.getMessage());
        }
    }


    public List<Goal> getActiveGoalsByDate(LocalDate date) {
        List<Goal> activeGoals = new ArrayList<>();


        // Cập nhật trạng thái một loạt trước
        for (Goal g : goalList) {
            g.updateStatus();
        }
//        saveToFile();


        // Mục tiêu đã hoàn thành
        for (Goal g : goalList) {
            if (g.getTargetDate().equals(date) && g.getStatus() == GoalStatus.ACHIEVED) {
                activeGoals.add(g);
            }
        }
        // Mục tiêu thất bại
        for (Goal g : goalList) {
            if (g.getTargetDate().equals(date) && g.getStatus() == GoalStatus.FAILED) {
                activeGoals.add(g);
            }
        }


        // Mục tiêu dở dang (tối đa 2)
        int inProgressCount = 0;
        for (Goal g : goalList) {
            if (g.getTargetDate().equals(date) && g.getStatus() == GoalStatus.IN_PROGRESS) {
                activeGoals.add(g);
                inProgressCount++;
                if (inProgressCount == 2) {
                    break;
                }
            }
        }
        return activeGoals;
    }


    public void autoUpdateProgressFromStatistics(double totalHoursToday, double totalTasksToday) {
        LocalDate today = LocalDate.now();
        boolean isUpdated = false;


        for (Goal g : getGoalsByDate(today)) {
            String titleLower = g.getTitle().toLowerCase();
            if (titleLower.contains("học") || titleLower.contains("giờ") || titleLower.contains("phút")) {
                g.evaluate(totalHoursToday);
                isUpdated = true;
            } else if (titleLower.contains("task") || titleLower.contains("bài tập") || titleLower.contains("việc")) {
                g.evaluate(totalTasksToday);
                isUpdated = true;
            }
        }
        if (isUpdated) saveToFile();
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
                }
                else if (titleLower.contains("task") || titleLower.contains("bài tập") || titleLower.contains("việc") || g.getUnit().equalsIgnoreCase("tasks")) {
                    g.evaluate(totalTasksDoneToday);
                    isChanged = true;
                }
            }
        }


        if (isChanged) {
            saveToFile();
        }
    }
}



