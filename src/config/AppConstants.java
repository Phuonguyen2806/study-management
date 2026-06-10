package config;

public class AppConstants {
    // ==========================================
    // ĐƯỜNG DẪN FILE DỮ LIỆU
    // ==========================================
    public static final String FILE_TASKS        = "data/tasks.txt";
    public static final String FILE_USERS        = "data/users.txt";
    public static final String FILE_STUDY_SESSIONS = "data/studysessions.txt";
    public static final String FILE_ALARM        = "data/alarm.wav";
    public static final String FILE_REMINDERS       = "data/reminders.txt";
    public static final String FILE_QUOTES       = "data/quotes.txt";

    // ==========================================
    // CẤU HÌNH THỜI GIAN (giây)
    // ==========================================
    public static final int TIME_FOCUS       = 30;
    public static final int TIME_SHORT_BREAK =  30;
    public static final int TIME_LONG_BREAK  = 15 * 60;

    // ==========================================
    // QUY TẮC NGHIỆP VỤ
    // ==========================================
    public static final int MIN_VALID_SESSION_SECONDS = 10;
    public static final int SESSIONS_BEFORE_LONG_BREAK = 2;

}
