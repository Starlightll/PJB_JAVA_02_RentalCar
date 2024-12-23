package com.rentalcar.rentalcar.jobs;


import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BusinessScheduler {

    @Autowired
    private BookingService bookingService;

    @Autowired
    EmailService emailService;

    //Run every 30 minutes
    @Scheduled(cron = "0 0/30 * * * ?")
    public void cancelExpiredBooking() {
        Integer numberOfCancel = bookingService.cancelExpiredBooking();
        emailService.simpleEmail("carbookvn@gmail.com", "Cancel expired booking", "Cancelled " + numberOfCancel + " bookings");
    }

}
