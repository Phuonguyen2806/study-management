package controller;

import view.MainFrame;

public class MainController {

    private MainFrame mainFrame;

    public MainController(MainFrame view) {
        this.mainFrame = view;
        initEventListeners();
        openFocusView();
    }

    private void initEventListeners() {
        mainFrame.getBtnTapTrung().addActionListener(e -> openFocusView());
        mainFrame.getBtnQuanLyBaiTap().addActionListener(e -> openTaskManagementView());
        mainFrame.getBtnMucTieu().addActionListener(e -> openGoalTrackingView());
        mainFrame.getBtnThongKe().addActionListener(e -> openStatisticTrackingView());
        mainFrame.getBtnHoSo().addActionListener(e -> openProfileTrackingView());
    }

    public void openFocusView() {
        mainFrame.switchCard("TapTrung");
    }

    public void openTaskManagementView() {
        mainFrame.switchCard("QuanLyBaiTap");
    }

    public void openGoalTrackingView() {
        mainFrame.switchCard("MucTieu");
    }

    public void openStatisticTrackingView() {
        mainFrame.switchCard("ThongKe");
    }

    public void openProfileTrackingView() {
        mainFrame.switchCard("HoSo");
    }
}