package service;

import config.AppConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotivationService {
    private final String QUOTES_FILE = AppConstants.FILE_QUOTES;
    private List<String> quotes;
    private Random random;

    public MotivationService() {
        this.random = new Random();
        this.quotes = new ArrayList<>();
        loadQuotes();
    }

    // Tải dữ liệu từ file vào danh sách bộ nhớ
    private void loadQuotes() {
        try {
            this.quotes = Files.readAllLines(Paths.get(QUOTES_FILE));
        } catch (IOException e) {
            System.err.println("Không thể đọc file quotes: " + e.getMessage());
            // Dự phòng nếu lỗi file
            this.quotes.add("Hãy cố gắng hết sức mình!");
        }
    }

    // Lấy một câu ngẫu nhiên
    public String getRandomQuote() {
        if (quotes.isEmpty()) return "Hãy tiếp tục cố gắng!";
        return quotes.get(random.nextInt(quotes.size()));
    }
}