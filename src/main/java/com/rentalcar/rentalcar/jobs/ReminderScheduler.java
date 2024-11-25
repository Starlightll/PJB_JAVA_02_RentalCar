package com.rentalcar.rentalcar.jobs;


import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.service.RentalCarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired RentalCarServiceImpl rentalCarService;

    @Autowired EmailService emailService;

    @Scheduled(cron = "0 0 2 * * ?") // Chạy hàng ngày lúc 9h sáng
    public void sendReminderEmails() {
        List<Booking> bookings = rentalCarService.getRentalsNearEndDate();

        for (Booking booking : bookings) {
            emailService.sendReminderEmail(booking.getUser(),booking, 1, "Mes", booking.getEndDate(), 100000);
        }
    }

}
