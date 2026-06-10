package controller;

import config.AppConstants;
import model.TaskManager;
import model.entity.Task;
import model.repository.ITaskRepository;
import model.repository.IUserRepository;
import model.repository.TaskRepositoryImpl;
import model.repository.UserRepository;
import view.TaskForm;
import view.TaskPanel;

import javax.swing.*;
import java.awt.*;

public class TaskController {

    private TaskPanel view;
    private IUserRepository userRepository;
    private Frame owner;
    private TaskManager taskManager;

    public TaskController(TaskPanel view, Frame owner,ITaskRepository repository,
                          IUserRepository userRepository) {
        this.view = view;
        this.owner = owner;
        this.userRepository = userRepository;
        repository.init(AppConstants.FILE_TASKS);
        taskManager = new TaskManager(repository, userRepository);
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

    public void handleDeleteTask(int taskId) {
        int confirm = JOptionPane.showConfirmDialog(
                owner,
                "Bạn có chắc chắn muốn xóa công việc này không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if(confirm != JOptionPane.YES_OPTION){
            return;
        }
        try {
            taskManager.deleteTask(taskId);
            JOptionPane.showMessageDialog(owner, "Xóa công việc thành công!");
            refreshView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(owner, e.getMessage());
        }
    }


    public boolean handleAddTask(TaskForm form) {
        try {
            taskManager.addTask(form.getTitleInput(),
                    form.getDescriptionInput(),
                    form.getDeadlineInput(),
                    form.getPriorityInput(),
                    form.getStatusInput()
            );
            refreshView();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(form, e.getMessage());
            return false;
        }
    }

    public void handleEditTask(int taskId) {
        Task task = taskManager.getTaskById(taskId);
        TaskForm form = new TaskForm(owner);
        form.loadForm(task);
        form.getBtnAdd().addActionListener(e -> updateTask(form, taskId));
        form.setVisible(true);

    }

    public void updateTask(TaskForm form, int taskId) {
        try {
            taskManager.updateTask(
                    taskId,
                    form.getTitleInput(),
                    form.getDescriptionInput(),
                    form.getDeadlineInput(),
                    form.getPriorityInput(),
                    form.getStatusInput()
            );
            form.dispose();
            JOptionPane.showMessageDialog(owner, "Cập nhật công việc thành công!");
            refreshView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(form, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshView() {
        int currentUserId = userRepository.getLoggedInUserId();
        if (currentUserId == -1) {
            view.clearTaskList();
            return;
        }
        taskManager.updateOverdueTasks();
        String selectedPriority = (String) view.getCbGlobalPriority().getSelectedItem();
        view.renderTaskList(taskManager.getTasksByUserAndPriority(currentUserId, selectedPriority));
    }
}

