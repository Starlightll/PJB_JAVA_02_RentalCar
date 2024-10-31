package com.rentalcar.rentalcar.mail;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.TimeFormatter;
import com.rentalcar.rentalcar.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(User user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Account Activation";
        String confirmationUrl = "http://localhost:8080/register/confirm?token=" + token;
        String timeActive = TimeFormatter.formatTime(Constants.EXPIRATION);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Activate Your Account</h2>" +
                    "<p>Dear " + user.getUsername() + ",</p>" +
                    "<p>Thank you for registering with us. Please click the button below to activate your account. This link will expire in <strong>"+ timeActive +"</strong>.</p>" +
                    "<p style='text-align: center; margin: 20px;'>" +
                    "<a href='" + confirmationUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Activate Account</a>" +
                    "</p>" +
                    "<p>If you did not create this account, please ignore this email. The activation link will automatically expire after "+ timeActive +".</p>" +
                    "<p>Best regards,<br>The Support Team</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If the button above does not work, copy and paste the following link into your browser:</p>" +
                    "<p style='font-size: 12px; color: #555;'>" + confirmationUrl + "</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);  // 'true' indicates that this is an HTML email

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Error sending verification email: " + e.getMessage());
        }
    }


    @Async
    public void sendResetPassword(User user, String token)  {
        String recipientAddress = user.getEmail();
        String confirmationUrl = "http://localhost:8080/reset-password?token=" + token + "&email=" + user.getEmail();
        String timeResetPass = TimeFormatter.formatTime(Constants.EXPIRATION_FORGOTPASS);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Reset Your Password</h2>" +
                    "<p>Dear "+ user.getUsername() +",</p>" +
                    "<p>We received a request to reset your password. Please click the button below to reset your password. For security reasons, this link will expire in <strong>"+ timeResetPass +"</strong>.</p>" +
                    "<p style='text-align: center; margin: 20px;'>" +
                    "<a href='" + confirmationUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                    "</p>" +
                    "<p>If you did not request a password reset, please ignore this email. The link will automatically expire after "+ timeResetPass +".</p>" +
                    "<p>Best regards,<br>The Support Team</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If the button above does not work, copy and paste the following link into your browser:</p>" +
                    "<p style='font-size: 12px; color: #555;'>" + confirmationUrl + "</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject("Reset Your Password");
            helper.setText(htmlMessage, true); // 'true' indicates that this is an HTML email

            mailSender.send(message);
        }catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
