//package service;
//
//import jakarta.mail.*;
//import jakarta.mail.internet.InternetAddress;
//import jakarta.mail.internet.MimeMessage;
//import model.repository.IUserRepository;
//
//import java.util.Properties;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class EmailService {
//    private IUserRepository userRepository;
//    private final Session session;
//    // Cấu hình email gửi đi (Server) - NÊN LẤY TỪ BIẾN MÔI TRƯỜNG
//    private static final String SENDER_EMAIL = "quynhanh.30042006@gmail.com";
//    private static final String APP_PASSWORD = "ygwe vfgl jfux gmfo";
//    public EmailService(IUserRepository userRepository) {
//        this.userRepository = userRepository;
//        this.session = createSession();
//    }
//    private Session createSession() {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        return Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
//            }
//        });
//    }
///**
// * Gửi email nhắc nhở cho user sở hữu task.
// * @param userId ID của người dùng sở hữu task
// * @param subject Tiêu đề email
// * @param content Nội dung email
// * @return true nếu gửi thành công, false nếu ngược lại
// */
//    public boolean sendReminderEmail(int userId, String subject, String content) {
//        // 1. Tìm đúng email dựa trên userId của task
//        System.out.println("BẮT ĐẦU GỬI EMAIL CHO USER ID: " + userId);
//        String recipient = userRepository.getEmailByUserId(userId);
//        System.out.println("Email tìm được: " + recipient);
//        if (recipient == null) {
//            System.err.println("Không tìm thấy email cho user: " + userId);
//            return false;
//        }
//// 2. Cấu hình và gửi email
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(SENDER_EMAIL));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//            message.setSubject(subject);
//            message.setText(content);
//
//            Transport.send(message);
//            System.out.println("Email nhắc nhở đã gửi thành công tới: " + recipient);
//            return true;
//        } catch (MessagingException e) {
//            System.err.println("Lỗi hệ thống khi gửi email cho User ID " + userId + ": " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }
//}