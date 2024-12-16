package yourstyle.com.shope.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import yourstyle.com.shope.model.Product;
import yourstyle.com.shope.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendVoucherEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // `true` để kích hoạt chế độ HTML

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // Xử lý lỗi
        }
    }

    @Override
    public void sendProductShareEmail(String senderEmail, String recipientEmail, Product product) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Sử dụng email cố định của server
            helper.setFrom(new InternetAddress("thaithithaolinh2004@gmail.com", "YourStyle"));
            helper.setTo(recipientEmail);
            helper.setSubject("Có người gửi bất ngờ cho bạn nè!!!");

            // Nội dung email có thông tin người gửi
            String emailContent = String.format(
                    "<p>Xin chào,</p>" +
                            "<p><b>%s</b> đã chia sẻ với bạn một sản phẩm mà họ quan tâm:</p>" +
                            "<p><b>Sản phẩm:</b> %s</p>" +
                            "<p><b>Mô tả:</b> %s</p>" +
                            "<p><a href='http://localhost:8080/yourstyle/product/detail/%d'>Xem chi tiết sản phẩm</a></p>" +
                            "<p>Trân trọng,</p>" +
                            "<p>YourStyle Team</p>",
                    senderEmail, // Email người gửi
                    product.getName(),
                    product.getDescription(),
                    product.getProductId()
            );

            helper.setText(emailContent, true); // Gửi email dạng HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }


    //reset password
    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Thiết lập body email ở dạng HTML
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi nếu có
        }
    }
}


