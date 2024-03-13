package ch.so.agi.datahub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Value("${spring.mail.username}")
    private String mailUsername;

    private JavaMailSender emailSender;
    
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }
    
    public void send(String toAddress, String subject, String content) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom(mailUsername);
        message.setTo(toAddress); 
        message.setSubject(subject); 
        message.setText(content);
        emailSender.send(message);            
    }
}
