package com.xeroxx.backend.service;

import com.xeroxx.backend.entity.PrintOrder;
import com.xeroxx.backend.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(User user, String otp) {
        if (user.getEmail() != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Your Xeroxx OTP");
            message.setText("Use this OTP to log in: " + otp);
            mailSender.send(message);
        }
        log.info("OTP {} sent to user {}", otp, user.getEmail() != null ? user.getEmail() : user.getMobile());
    }

    public void notifyStatus(PrintOrder order) {
        if (order.getUser() != null && order.getUser().getEmail() != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getUser().getEmail());
            message.setSubject("Order status update");
            message.setText("Order " + order.getId() + " is now " + order.getStatus());
            mailSender.send(message);
        }
        log.info("Sent status update for order {}", order.getId());
    }
}



