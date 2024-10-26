package com.rentalcar.rentalcar.mail;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(User user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Account Activation";
        String confirmationUrl = "http://localhost:8080/register/confirm?token=" + token;
        int hour = Constants.EXPIRATION / 60;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Activate Your Account</h2>" +
                    "<p>Dear " + user.getUsername() + ",</p>" +
                    "<p>Thank you for registering with us. Please click the button below to activate your account. This link will expire in <strong>"+ hour +" hours</strong>.</p>" +
                    "<p style='text-align: center; margin: 20px;'>" +
                    "<a href='" + confirmationUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Activate Account</a>" +
                    "</p>" +
                    "<p>If you did not create this account, please ignore this email. The activation link will automatically expire after 24 hours.</p>" +
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

    public void sendResetPassword(User user, String token)  {
        String recipientAddress = user.getEmail();
        String confirmationUrl = "http://localhost:8080/reset-password?token=" + token + "&email=" + user.getEmail();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Reset Your Password</h2>" +
                    "<p>Dear "+ user.getUsername() +",</p>" +
                    "<p>We received a request to reset your password. Please click the button below to reset your password. For security reasons, this link will expire in <strong>"+ Constants.EXPIRATIONFORGOTPASS+" minutes</strong>.</p>" +
                    "<p style='text-align: center; margin: 20px;'>" +
                    "<a href='" + confirmationUrl + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                    "</p>" +
                    "<p>If you did not request a password reset, please ignore this email. The link will automatically expire after 30 minutes.</p>" +
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
