package yourstyle.com.shope.service;

import yourstyle.com.shope.model.Product;

public interface EmailService {
    public void sendVoucherEmail(String to, String subject, String body) ;
    public void sendProductShareEmail(String senderEmail, String recipientEmail, Product product) ;
}
