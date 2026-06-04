package controller;

import model.entity.Priority;
import model.entity.Task;
import model.entity.TaskStatus;
import model.repository.ITaskRepository;
import model.repository.TaskRepositoryImpl;
import model.repository.UserRepository;
import view.TaskForm;
import view.TaskPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class TaskController {

    private TaskPanel view;
    private ITaskRepository repository;
    private UserRepository userRepository;
    private Frame owner;

    public TaskController(TaskPanel view, Frame owner) {
        this.view = view;
        this.owner = owner;
        this.repository = new TaskRepositoryImpl();
        repository.init("study-management/data/tasks.txt");
        this.userRepository = new UserRepository();
        initEvents();
        refreshView();
    }
    private void initEvents() {
        view.getBtnAdd().addActionListener(e -> showTaskForm());
        view.setOnDeleteTask(e -> {
            JButton btnClicked = (JButton) e.getSource();
            int taskId = (int) btnClicked.getClientProperty("taskId");
            handleDeleteTask(taskId);
        });
        view.setOnEditTask(e -> {
            JButton btnClicked = (JButton) e.getSource();
            int taskId = (int) btnClicked.getClientProperty("taskId");
            handleEditTask(taskId);
        });
        view.getCbGlobalPriority().addActionListener(e -> {
            refreshView(); // Mỗi lần đổi lựa chọn, nạp lại danh sách theo bộ lọc mới
        });
    }

    public void addStartListener(ActionListener listener) {
        // Gọi hàm set listener chung trong view
        view.setOnStartTask(listener);
    }

    private void showTaskForm() {
        TaskForm form = new TaskForm(owner);
        form.getBtnAdd().addActionListener(e -> {
            boolean success = handleAddTask(form);
            if (success) {
                form.dispose(); // Đóng biểu mẫu nhập liệu
                JOptionPane.showMessageDialog(owner, "Thêm công việc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                refreshView(); // Cập nhật lại toàn bộ danh sách hiển thị mới nhất
            }
        });
        form.setVisible(true);
    }

    private void handleDeleteTask(int taskId) {
        int confirm = JOptionPane.showConfirmDialog(
                owner,
                "Bạn có chắc chắn muốn xóa công việc này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = repository.delete(taskId);
            if (success) {
                JOptionPane.showMessageDialog(owner, "Xóa công việc thành công!");
                refreshView(); // Nạp lại dữ liệu hiển thị mới nhất lên màn hình
            } else {
                JOptionPane.showMessageDialog(owner, "Lỗi hệ thống: Không thể xóa công việc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public boolean handleAddTask(TaskForm form) {
        // --- BƯỚC 1: TRUY VẾT USER ID ĐANG ĐĂNG NHẬP ---
        int currentUserId = userRepository.getLoggedInUserId();
        if (currentUserId == -1) {
            JOptionPane.showMessageDialog(form, "Không tìm thấy phiên đăng nhập! Vui lòng thử lại.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // --- BƯỚC 2: TRÍCH XUẤT DỮ LIỆU TỪ FORM ---
        String title = form.getTitleInput();
        String deadlineStr = form.getDeadlineInput();
        String priority = form.getPriorityInput();
        String status = form.getStatusInput();
        String desc = form.getDescriptionInput();
        // --- BƯỚC 3: KIỂM TRA TÍNH HỢP LỆ (LUỒNG RẼ NHÁNH #1) ---
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(form, "Tiêu đề không được để trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false; // Giữ nguyên form để người dùng sửa đổi tiếp
        }
        if (!form.validateDate()) {
            return false; // Trả về false nếu cấu trúc chuỗi ngày nhập không đạt chuẩn dd/mm/yyyy
        }
        // Chuyển đổi định dạng văn bản sang kiểu Date
        Date deadlineDate = parseStringToDate(deadlineStr);
        if (deadlineDate == null) {
            JOptionPane.showMessageDialog(form, "Ngày tháng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Kiểm tra thời điểm hạn chót không nằm trong quá khứ
        if (deadlineDate.before(new Date())) {
            JOptionPane.showMessageDialog(form, "Hạn chót không hợp lệ! Không thể chọn thời gian trong quá khứ.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        // --- BƯỚC 4: KHỞI TẠO ĐỐI TƯỢNG TASK MỚI ---
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase().replace(" ", "_"));
        Priority priorityTask = Priority.valueOf(priority);
        Task newTask = new Task(title, desc, deadlineDate, priorityTask, 0, 0, taskStatus);
        // --- BƯỚC 5: GỌI REPOSITORY LƯU TRỮ XUỐNG FILE (LUỒNG RẼ NHÁNH #2) ---
        boolean isSaved = repository.save(newTask, currentUserId);
        if (!isSaved) {
            JOptionPane.showMessageDialog(form, "Lỗi hệ thống: Không thể lưu bài tập lúc này. Vui lòng thử lại sau.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return false; // Kết thúc tiến trình rẽ nhánh lỗi lưu trữ, giữ nguyên form nhập liệu
        }
        return true;
    }

    private Date parseStringToDate(String dateStr) {
        java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        inputFormat.setLenient(false);
        try {
            Date parsedDate = inputFormat.parse(dateStr);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(parsedDate);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            cal.set(java.util.Calendar.SECOND, 0);
            return cal.getTime();
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public void refreshView() {
        // 1. Lấy ID của user đang đăng nhập (isLogin = true)
        int currentUserId = userRepository.getLoggedInUserId();
        if (currentUserId != -1) {
            // 2. Lấy toàn bộ task từ file txt lên thông qua Repository
            List<Task> allTasks = repository.getAllTasks();
            // 3. Lấy giá trị độ ưu tiên đang được chọn từ ComboBox
            String selectedPriority = (String) view.getCbGlobalPriority().getSelectedItem();
            // 4. Tạo một danh sách mới để chứa các task thỏa mãn điều kiện lọc
            List<Task> filteredTasks = new ArrayList<>();
            for (Task task : allTasks) {
                // Kiểm tra xem task có thuộc về User hiện tại hay không
                if (task.getUserId() == currentUserId) {
                    // Kiểm tra điều kiện lọc Độ Ưu Tiên
                    if (selectedPriority.equals("Ưu tiên: ALL")|| selectedPriority.isEmpty()) {
                        // Nếu chọn "Tất cả", nạp toàn bộ task của user này
                        filteredTasks.add(task);
                    } else {
                        // Lưu ý: So sánh không phân biệt hoa thường hoặc chuẩn hóa chuỗi nếu cần
                        // Ví dụ: "Cao", "Trung bình", "Thấp"
                        if (task.getPriority().name().equalsIgnoreCase(selectedPriority)) {
                            filteredTasks.add(task);
                        }
                    }
                }
            }
            // 5. Gọi View render lại danh sách đã lọc
            view.renderTaskList(filteredTasks);
        } else {
            // Nếu không có ai đăng nhập, xóa sạch màn hình danh sách task
            view.clearTaskList();
        }
    }
    private void handleEditTask(int taskId) {
        // Tìm đối tượng Task cũ trong cơ sở dữ liệu dựa vào taskId
        List<Task> allTasks = repository.getAllTasks();
        Task targetTask = null;
        for (Task t : allTasks) {
            if (t.getTaskId() == taskId) {
                targetTask = t;
                break;
            }
        }
        if (targetTask == null) {
            JOptionPane.showMessageDialog(owner, "Không tìm thấy công việc cần sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Khởi tạo Form và đổ dữ liệu cũ lên các ô nhập liệu
        TaskForm form = new TaskForm(owner);
        // Bạn hãy kiểm tra xem TaskForm của bạn đã có các hàm set dữ liệu này chưa nhé:
        form.setTitleInput(targetTask.getTitle());
        form.setDescriptionInput(targetTask.getDescription());
        form.setPriorityInput(targetTask.getPriority().name());
        form.setStatusInput( targetTask.getStatus().name());
        java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        form.setDeadlineInput(displayFormat.format(targetTask.getDeadline()));
        // Thay đổi text của nút bấm trên Form thành "Cập nhật" thay vì "Thêm" (nếu cần thiết)
        form.getBtnAdd().setText("Cập nhật");
        // Lắng nghe sự kiện khi người dùng bấm nút xác nhận trên Form
        final Task finalTargetTask = targetTask; // Biến final để dùng trong Lambda
        form.getBtnAdd().addActionListener(evt -> {
            // Trích xuất thông tin mới từ Form sau khi người dùng chỉnh sửa
            String title = form.getTitleInput();
            String deadlineStr = form.getDeadlineInput();
            String priority = form.getPriorityInput();
            String status = form.getStatusInput();
            String desc = form.getDescriptionInput();

            // Kiểm tra tính hợp lệ dữ liệu (Validate tương tự như lúc Thêm)
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(form, "Tiêu đề không được để trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!form.validateDate()) return;

            Date deadlineDate = parseStringToDate(deadlineStr);
            if (deadlineDate == null) {
                JOptionPane.showMessageDialog(form, "Ngày tháng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase().replace(" ", "_"));
            Priority priorityTask = Priority.valueOf(priority);
            // Gán các giá trị mới cập nhật vào đối tượng task cũ
            finalTargetTask.setTitle(title);
            finalTargetTask.setDescription(desc);
            finalTargetTask.setDeadline(deadlineDate);
            finalTargetTask.setPriority(priorityTask);
            finalTargetTask.setStatus(taskStatus);

            // Gọi Repository cập nhật xuống file txt
            boolean success = repository.update(finalTargetTask);
            if (success) {
                form.dispose(); // Đóng form
                JOptionPane.showMessageDialog(owner, "Cập nhật công việc thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                refreshView(); // Nạp lại giao diện mới nhất
            } else {
                JOptionPane.showMessageDialog(form, "Lỗi hệ thống: Không thể cập nhật công việc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        form.setVisible(true);
    }
}
