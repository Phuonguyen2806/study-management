import controller.MainController;
import model.database.DatabaseConnection;
import model.entity.Task;
import model.repository.TaskRepository;
import view.MainFrame;

import java.util.List;
import java.sql.*;
public class Main {
    public static void main(String[] args) {
        MainFrame view = new MainFrame();
        MainController controller = new MainController(view);
    }

    }

