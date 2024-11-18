package com.rentalcar.rentalcar.mail;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.TimeFormatter;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


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


    @Async
    public void sendBookingConfirmation(User user, BookingDto bookingdto, Booking booking, Car car) {
        String recipientAddress = user.getEmail();
        Long bookingNumber = booking.getBookingId();
        String carName = car.getCarName();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String  fromDate = bookingdto.getPickUpDate().format(dateTimeFormatter);
        String  toDate = bookingdto.getReturnDate().format(dateTimeFormatter);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String  pricePerDay = currencyFormatter.format(car.getBasePrice());
        String  totalPrice = currencyFormatter.format(booking.getTotalPrice());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Booking Confirmation</h2>" +
                    "<p>Dear " + user.getUsername() + ",</p>" +
                    "<p>Thank you for booking with us! Here are your booking details:</p>" +
                    "<ul>" +
                    "<li><strong>Booking Number:</strong> " + bookingNumber + "</li>" +
                    "<li><strong>Car Name:</strong> " + carName + "</li>" +
                    "<li><strong>From:</strong> " + fromDate + "</li>" +
                    "<li><strong>To:</strong> " + toDate + "</li>" +
                    "<li><strong>Price per Day:</strong> " + pricePerDay + "</li>" +
                    "<li><strong>Total Price:</strong> " + totalPrice + "</li>" +
                    "</ul>" +
                    "<p>We look forward to serving you. If you have any questions, please contact our support team.</p>" +
                    "<p>Best regards,<br>The Support Team</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If you did not make this booking, please contact our support team immediately.</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject("Booking Confirmation - Booking No. " + bookingNumber);
            helper.setText(htmlMessage, true); // 'true' indicates that this is an HTML email

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    @Async
    public void sendBookingConfirmationWithDeposit(User user, Booking booking, Car car, double depositAmount) {
        String recipientAddress = user.getEmail();
        Long bookingNumber = booking.getBookingId();
        String carName = car.getCarName();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String bookingDate = LocalDateTime.now().format(dateTimeFormatter);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String depositFormatted = currencyFormatter.format(depositAmount);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Congratulations!</h2>" +
                    "<p style='font-size: 16px;'>Your car <strong style='color: #FF5722;'>" + carName + "</strong> has been successfully booked on <strong style='color: #2196F3;'>" + bookingDate + "</strong>.</p>" +
                    "<p style='font-size: 16px;'>Please go to your wallet to check if the deposit of <strong style='color: #FF5722;'>" + depositFormatted + "</strong> has been paid.</p>" +
                    "<p style='font-size: 16px;'>Additionally, visit your car's details page to confirm the deposit. Thank you for choosing us!</p>" +
                    "<p style='color: #757575;'>Best regards,<br><strong style='color: #4CAF50;'>The Support Team</strong></p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If you did not make this booking, please contact our support team immediately.</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject("Your car has been booked - Booking No. " + bookingNumber);
            helper.setText(htmlMessage, true); // 'true' indicates that this is an HTML email

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendReturnCarSuccessfully(User user, Booking booking, Integer carId ,String carName, double remainingMoney) {
        String recipientAddress = user.getEmail();
        Long bookingNumber = booking.getBookingId();
        String urlToWallet = "http://localhost:8080/wallet/my-wallet";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String bookingDate = LocalDateTime.now().format(dateTimeFormatter);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String remainingMoneyString = currencyFormatter.format(remainingMoney);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Updated HTML message content
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Booking Successful!</h2>" +
                    "<p style='font-size: 16px;'>Congratulations! Your car <strong style='color: #FF5722;'>" + carName + "</strong> has been returned successfully. <strong style='color: #2196F3;'>" + bookingDate + "</strong>.</p>" +
                    "<p style='font-size: 16px;'>The amount of <strong style='color: #FF5722;'>" + remainingMoneyString + "</strong> has been successfully credited to your wallet.</p>" +
                    "<a href='" + urlToWallet + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>My Wallet</a>" +

                    "<p style='font-size: 16px;'>We recommend visiting your wallet page to confirm the remaining money. Thank you for cooperate with us!</p>" +
                    "<p style='color: #757575;'>Best regards,<br><strong style='color: #4CAF50;'>The Support Team</strong></p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If you did not make this booking, please contact our support team immediately.</p>" +
                    "<p style='font-size: 12px; color: #555;'>" + urlToWallet + "</p>" +

                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject("Your car has been successfully booked - Car No. " + carId);
            helper.setText(htmlMessage, true); // 'true' indicates that this is an HTML email

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Async
    public void sendRequestConfirmPayment(User user, Booking booking, Integer carId ,String carName, double remainingMoney) {
        String recipientAddress = user.getEmail();
        String baseUrl = "http://localhost:8080/car-owner/edit-car/";
        String urlToEditCar = baseUrl + carId;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String bookingDate = LocalDateTime.now().format(dateTimeFormatter);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String remainingMoneyString = currencyFormatter.format(remainingMoney);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Updated HTML message content
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Car Returned Confirmation</h2>" +
                    "<p style='font-size: 16px;'>The customer has returned the car <strong style='color: #FF5722;'>" + carName + "</strong> on <strong style='color: #2196F3;'>" + bookingDate + "</strong>.</p>" +
                    "<p style='font-size: 16px;'>Please visit the <strong style='color: #FF5722;'>Confirm Payment</strong> section to complete the return of the remaining deposit amount of <strong style='color: #FF5722;'>" + remainingMoneyString + " VND</strong>.</p>" +
                    "<a href='" + urlToEditCar + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Confirm Payment</a>" +

                    "<p style='font-size: 16px;'>We encourage you to complete the transaction at your earliest convenience. Thank you for your cooperation!</p>" +
                    "<p style='color: #757575;'>Best regards,<br><strong style='color: #4CAF50;'>The Support Team</strong></p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If this return was made in error, please contact our support team immediately.</p>" +
                    "<p style='font-size: 12px; color: #555;'>" + urlToEditCar + "</p>" +

                    "</body>" +
                    "</html>";


            helper.setTo(recipientAddress);
            helper.setSubject("Your car has been successfully booked - Car No. " + carId);
            helper.setText(htmlMessage, true); // 'true' indicates that this is an HTML email

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Async
    public void sendPaymentConfirmation(User user, Booking booking, Integer carId ,String carName, double remainingMoney) {
        String recipientAddress = user.getEmail();
        Long bookingNumber = booking.getBookingId();
        String urlToWallet = "http://localhost:8080/wallet/my-wallet";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String bookingDate = LocalDateTime.now().format(dateTimeFormatter);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String remainingMoneyString = currencyFormatter.format(remainingMoney);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Updated HTML message content
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #4CAF50;'>Payment Successfully Credited!</h2>" +
                    "<p style='font-size: 16px;'>Dear <strong style='color: #2196F3;'>" + user.getFullName() + "</strong>,</p>" +
                    "<p style='font-size: 16px;'>We are pleased to inform you that the remaining amount of <strong style='color: #FF5722;'>" + remainingMoneyString + "</strong> has been successfully credited to your wallet after returning the car <strong style='color: #FF5722;'>" + carName + "</strong>.</p>" +
                    "<p style='font-size: 16px;'>Please visit the link below to check your wallet balance:</p>" +
                    "<a href='" + urlToWallet + "' style='display: inline-block; background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Go to My Wallet</a>" +
                    "<p style='font-size: 16px;'>Thank you for renting our car. We hope you had a great experience! Please don't hesitate to reach out if you need any further assistance.</p>" +
                    "<p style='color: #757575;'>Best regards,<br><strong style='color: #4CAF50;'>The Support Team</strong></p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #555;'>If you did not make this booking, please contact our support team immediately.</p>" +
                    "<p style='font-size: 12px; color: #555;'>" + urlToWallet + "</p>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientAddress);
            helper.setSubject("Your Payment has been Successfully Credited - Booking No. " + carId);
            helper.setText(htmlMessage, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



}
