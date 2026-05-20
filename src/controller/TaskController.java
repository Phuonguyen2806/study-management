package controller;

import view.TaskForm;
import view.TaskPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class TaskController {

    private TaskPanel view;
//    private ITaskRepository repository;
    private Frame owner;

    public TaskController(TaskPanel view, Frame owner) {
        this.view = view;
        this.owner = owner;
        // Gán sự kiện ngay khi khởi tạo
        initEvents();
    }
    private void initEvents() {
        view.getBtnAdd().addActionListener(e -> showTaskForm());
    }

    public void addStartListener(ActionListener listener) {
        // Gọi hàm set listener chung trong view
        view.setOnStartTask(listener);
    }

    private void showTaskForm() {
        TaskForm form = new TaskForm(owner);
        form.setVisible(true);
        // Khi người dùng nhấn "Lưu" trên Form
//        form.getBtnAdd().addActionListener(e -> {
//            // Chuyển dữ liệu qua TaskController xử lý (handleAddTask)
//            boolean success = handleAddTask(form);
//
//            if (success) {
//                form.dispose(); // Đóng form
//                JOptionPane.showMessageDialog(null, "Thêm thành công!");
//                // Giai đoạn 3: Cập nhật hiển thị
//                refreshView();
//            }
//        });
    }



    public boolean handleAddTask(TaskForm form) {
        // Giai đoạn 2: Lấy dữ liệu từ Form
//        String title = form.getTxtTitle().getText();
//        String subject = (String) form.getCbSubject().getSelectedItem();
//        String priority = (String) form.getCbPriority().getSelectedItem();
//        String status = (String) form.getCbStatus().getSelectedItem();
//        String desc = form.getTxtDescription().getText();

        // <<create>> taskObject
//        Task newTask = new Task(title, subject, priority, status, desc);
//
//        // Lưu trữ qua Repository
//        return repository.save(newTask); // Trả về true/false (Giai đoạn 3)
        return true;
    }

    public void refreshView() {
        // Logic để load lại list Card trên TaskPanel
        // view.updateTaskList(repository.getAll());
    }
}
