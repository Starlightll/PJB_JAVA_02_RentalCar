package com.rentalcar.rentalcar.jobs;


import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.service.RentalCarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired RentalCarServiceImpl rentalCarService;

    @Autowired EmailService emailService;

    @Autowired
    UserRepo userRepo;

    @Scheduled(cron = "0 0 9 * * ?") // Chạy hàng ngày lúc 9h sáng
    public void sendReminderEmails() {
        List<MyBookingDto> bookings = rentalCarService.getRentalsNearEndDate();

        for (MyBookingDto booking : bookings) {
            User user = userRepo.getUserById(booking.getUserId());
            emailService.sendReminderEmail(user,booking, booking.getCarId(), booking.getCarname(), booking.getEndDate(), booking.getBasePrice());
        }
    }

}
