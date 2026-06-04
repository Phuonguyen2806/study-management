package service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EmailService {
    private static final String USER_FILE_PATH = "data/users.txt";

    // 1. Logic lấy email (vốn dĩ từ UserService)
    public String getEmailByUserId(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals(userId)) {
                    return parts[2]; // Trả về email
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file users.txt: " + e.getMessage());
        }
        return null;
    }

    // 2. Logic gửi email (vốn dĩ từ EmailService)
    public boolean sendReminderEmail(String userId, String subject, String content) {
        // Bước 1: Lấy email từ chính lớp này
        String recipient = getEmailByUserId(userId);
        if (recipient == null) {
            System.err.println("Không tìm thấy email cho user: " + userId);
            return false;
        }

        // Bước 2: Cấu hình và gửi email
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your-email@gmail.com", "your-app-password");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your-email@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email nhắc nhở đã gửi tới: " + recipient);
            return true;
        } catch (MessagingException e) {
            System.err.println("Lỗi gửi mail: " + e.getMessage());
            return false;
        }
    }
}