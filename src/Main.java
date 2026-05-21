import controller.MainController;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        MainFrame view = new MainFrame();
        MainController controller = new MainController(view);

    }

    private static void loadTestData() {
//        TaskRepositoryImpl repo = new TaskRepositoryImpl();
//        List<Task> list = repo.getTasksByUserId(1);
//
//        System.out.println("--- Danh sách Task của User 1 ---");
//        for (Task t : list) {
//            System.out.println(t);
//        }
    }
}
