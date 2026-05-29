package model.repository;

import model.entity.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TaskRepositoryImpl implements ITaskRepository{
    private final String FILE_PATH = "study-management/data/tasks.txt";

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tasks;
        }
        // Định dạng thời gian trùng khớp với file text của bạn
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
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

    @Override
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
    }
    @Override
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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
