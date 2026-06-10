package model.repository;

import model.entity.Priority;
import model.entity.Task;
import model.entity.TaskStatus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskRepositoryImpl implements ITaskRepository {
    private final String FILE_PATH = "data/tasks.txt";
    private String filePath;
    private final List<Task> taskList = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TaskRepositoryImpl() {
        init(FILE_PATH);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(this.taskList);
    }

    // 1. ĐỌC DỮ LIỆU TỪ FILE TXT VÀO BỘ NHỚ
    @Override
    public void init(String filePath) {
        this.filePath = filePath;
        this.taskList.clear();
        File file = new File(filePath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    Task task = new Task(
                            Integer.parseInt(parts[0].trim()),
                            parts[1].trim(),
                            parts[2].trim(),
                            dateFormat.parse(parts[3].trim()),
                            Priority.valueOf(parts[4].trim()),
                            Integer.parseInt(parts[5].trim()),
                            Integer.parseInt(parts[6].trim()),
                            TaskStatus.valueOf(parts[7].trim()),
                            Integer.parseInt(parts[8].trim())
                    );
                    taskList.add(task);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc file: " + e.getMessage());
        }
    }

    @Override
    public List<Task> findTasksByStatus(String status, int userID) {
        List<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isStatus(status)&& task.isUserTask(userID)) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> findTasksByUserId(int userId) {
        List<Task> result = new ArrayList<>();
        for (Task task : taskList) {
            if (task.isUserTask(userId)) {
                result.add(task);
            }
        }
        return result;
    }

    public Task findTaskById(int taskId,int userId) {
        for (Task task : taskList) {
            if (task.isTaskID(taskId) && task.isUserTask(userId)) {
                return task;
            }
        }
        return null;
    }

    // hàm dùng để thêm task và lưu lại trong file
    public boolean save(Task task, int userId) {
        taskList.add(task);
        return saveToFile();
    }

    public boolean delete(int taskId, int userId) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).isTaskID(taskId) && taskList.get(i).isUserTask(userId)) {
                taskList.remove(i);
                return saveToFile();
            }
        }
        return false;
    }

    // 3. CẬP NHẬT CÔNG VIỆC VÀ GHI LẠI VÀO FILE
    public boolean update(Task updatedTask) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).checkIDTask(updatedTask) && taskList.get(i).checkUserTask(updatedTask)) {
                taskList.set(i, updatedTask);
                return saveToFile();
            }
        }
        return false;
    }

    // HÀM HỖ TRỢ: GHI ĐÈ BỘ NHỚ XUỐNG FILE TXT
    public boolean saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Task task : taskList) {
                bw.write(
                        task.getTaskId() + "|" +
                                task.getTitle() + "|" +
                                task.getDescription() + "|" +
                                dateFormat.format(task.getDeadline()) + "|" +
                                task.getPriority().name() + "|" +
                                task.getEstPomo() + "|" +
                                task.getCompPomo() + "|" +
                                task.getStatus().name() + "|" +
                                task.getUserId()
                );
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void refresh() {
        init(FILE_PATH);
    }

}
