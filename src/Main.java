import controller.MainController;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        MainFrame view = new MainFrame();
        MainController controller = new MainController(view);
    }

    }

