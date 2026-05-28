package model.repository;

import model.entity.Task;
import model.entity.TaskStatus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements ITaskRepository {
    private String filePath;
    private List<Task> taskList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
                }
            }
            System.out.println("Init thành công: Đã load " + taskList.size() + " tasks từ file.");
        } catch (Exception e) {
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
            }
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
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Đã cập nhật dữ liệu xuống file " + filePath + " thành công!");
        } catch (IOException e) {
            System.err.println("Lỗi ghi file tasks.txt: " + e.getMessage());
        }
    }
}
