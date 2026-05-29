package model.repository;

import model.entity.Task;
import model.entity.TaskStatus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TaskRepositoryImpl implements ITaskRepository{
    private final String FILE_PATH = "study-management/data/tasks.txt";
public class TaskRepositoryImpl implements ITaskRepository {
    private String filePath;
    private List<Task> taskList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tasks;
        }
        // Định dạng thời gian trùng khớp với file text của bạn
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    public TaskRepositoryImpl() {
        this.taskList = new ArrayList<>();
    }

    // 1. ĐỌC DỮ LIỆU TỪ FILE TXT VÀO BỘ NHỚ
    @Override
    public void init(String filePath) {
        this.filePath = filePath;
        this.taskList.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Format chuẩn của file data:
                // taskId|title|description|deadline|priority|estPomo|compPomo|status|userId
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    Task task = new Task(
                            Integer.parseInt(parts[0]),     // taskId
                            parts[1],                       // title
                            parts[2],                       // description
                            dateFormat.parse(parts[3]),     // deadline
                            parts[4],                       // priority
                            Integer.parseInt(parts[5]),     // estPomo
                            Integer.parseInt(parts[6]),     // compPomo
                            TaskStatus.valueOf(parts[7].toUpperCase()), // status
                            Integer.parseInt(parts[8])      // userId
                    );
                    taskList.add(task);
                String[] data = line.split("\\|");
                if (data.length >= 9) {
                    int taskId = Integer.parseInt(data[0]);
                    String title = data[1];
                    String description = data[2];
                    Date deadline = sdf.parse(data[3]);
                    String priority = data[4];
                    int estPomo = Integer.parseInt(data[5]);
                    int compPomo = Integer.parseInt(data[6]);
                    String state = data[7];
                    int userId = Integer.parseInt(data[8]);
                    Task task = new Task(taskId, title, description, deadline, priority, estPomo, compPomo, state, userId);
                    tasks.add(task);
                }
            }
            System.out.println("Init thành công: Đã load " + taskList.size() + " tasks từ file.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private int getNextID() {
        List<Task> tasks = getAllTasks();
        int maxID = 0;
        for (Task t : tasks) {
            if (t.getTaskId() > maxID) {
                maxID = t.getTaskId();
            }
        }
        return maxID + 1;
    }
            System.err.println("Lỗi đọc file tasks.txt: " + e.getMessage());
        }
    }

    // 2. TÌM KIẾM CÔNG VIỆC THEO TRẠNG THÁI
    @Override
    public List<Task> findTasksByStatus(String status) {
        List<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.getStatus().name().equalsIgnoreCase(status)) {
                result.add(task);
    public boolean save(Task task, int userId) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Mở file ở chế độ ghi tiếp (true) để không làm mất dữ liệu cũ
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            int newID = getNextID(); // Lấy ID tự động tăng kế tiếp

            // Xây dựng chuỗi dòng dữ liệu phân tách bằng dấu | theo khuôn mẫu
            String line = newID + "|" +
                    task.getTitle() + "|" +
                    task.getDescription() + "|" +
                    sdf.format(task.getDeadline()) + "|" +
                    task.getPriority() + "|" +
                    task.getEstPomo() + "|" +
                    task.getCompPomo() + "|" +
                    task.getState() + "|" +
                    userId;

            bw.write(line);
            bw.newLine(); // Xuống dòng cho task kế tiếp
            return true;  // Trả về true báo hiệu lưu file thành công

        } catch (IOException e) {
            e.printStackTrace();
            return false; // Trả về false nếu gặp sự cố I/O (Lỗi lưu trữ dữ liệu - Variation #2)
        }
    }

    public boolean delete(int taskId) {
        // 1. Lấy toàn bộ danh sách task hiện có trong file lên RAM
        List<Task> allTasks = getAllTasks();
        // 2. Tìm và xóa task có trùng taskId
        boolean removed = allTasks.removeIf(task -> task.getTaskId() == taskId);
        // Nếu không tìm thấy task nào để xóa, trả về false luôn
        if (!removed) return false;
        // 3. Ghi đè lại toàn bộ danh sách đã xóa xuống file txt
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Lưu ý: FileWriter ở đây tham số thứ 2 là FALSE để ghi đè (overwrite) xóa sạch file cũ
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Task task : allTasks) {
                String line = task.getTaskId() + "|" +
                        task.getTitle() + "|" +
                        task.getDescription() + "|" +
                        sdf.format(task.getDeadline()) + "|" +
                        task.getPriority() + "|" +
                        task.getEstPomo() + "|" +
                        task.getCompPomo() + "|" +
                        task.getState() + "|" +
                        task.getUserId();
                bw.write(line);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return result;
    }

    // 3. CẬP NHẬT CÔNG VIỆC VÀ GHI LẠI VÀO FILE
    @Override
    public void updateTask(Task updatedTask) {
        // Cập nhật trong bộ nhớ
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getTaskId() == updatedTask.getTaskId()) {
                taskList.set(i, updatedTask);
    public boolean update(Task updatedTask) {
        // 1. Lấy toàn bộ danh sách hiện có trên RAM
        List<Task> allTasks = getAllTasks();
        boolean found = false;
        // 2. Tìm task trùng ID và thay thế bằng dữ liệu mới
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getTaskId() == updatedTask.getTaskId()) {
                allTasks.set(i, updatedTask);
                found = true;
                break;
            }
        }
        // Ghi lại toàn bộ xuống file
        saveToFile();
    }

    // 4. THÊM CÔNG VIỆC MỚI
    @Override
    public void addTask(Task newTask) {
        taskList.add(newTask);
        saveToFile();
    }

    // HÀM HỖ TRỢ: GHI ĐÈ BỘ NHỚ XUỐNG FILE TXT
    private void saveToFile() {
        if (filePath == null || filePath.isEmpty()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : taskList) {
                // Ráp lại thành chuỗi format có dấu |
                String line = String.format("%d|%s|%s|%s|%s|%d|%d|%s|%d",
                        task.getTaskId(),
                        task.getTitle(),
                        task.getDescription(),
                        dateFormat.format(task.getDeadline()),
                        task.getPriority(),
                        task.getEstPomo(),
                        task.getCompPomo(),
                        task.getStatus().name(),
                        task.getUserId()
                );
        if (!found) return false;
        // 3. Ghi đè toàn bộ danh sách mới xuống file text
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) { // false để ghi đè
            for (Task task : allTasks) {
                String line = task.getTaskId() + "|" +
                        task.getTitle() + "|" +
                        task.getDescription() + "|" +
                        sdf.format(task.getDeadline()) + "|" +
                        task.getPriority() + "|" +
                        task.getEstPomo() + "|" +
                        task.getCompPomo() + "|" +
                        task.getState() + "|" +
                        task.getUserId();
                bw.write(line);
                bw.newLine();
            }
            return true;
            System.out.println("Đã cập nhật dữ liệu xuống file " + filePath + " thành công!");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
            System.err.println("Lỗi ghi file tasks.txt: " + e.getMessage());
        }
    }
}
