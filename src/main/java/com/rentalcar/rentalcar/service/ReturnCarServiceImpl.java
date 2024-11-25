package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.rentalcar.rentalcar.common.Constants.FINE_COST;


@Service
public class ReturnCarServiceImpl implements ReturnCarService {
    @Autowired
    RentalCarRepository rentalCarRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    UserRepo userRepository;

    @Autowired
    CarRepository carRepository;

    @Autowired
    CarStatusRepository carStatusRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private TransactionService transactionService;

    public String returnCar(Long bookingId, HttpSession session) {
//        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), //start date
                ((Timestamp) result[2]).toLocalDateTime(), //end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), //userId
                (Integer) result[7], //bookingStatus
                (Integer) result[8], //paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, //driver
                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue(), // deposit
                (BigDecimal) result[12], //carowner wallet
                Long.valueOf((Integer) result[13]),
                (String) result[14], //car name
                (Integer) result[15] //cariD

        );

        LocalDateTime currentTime = LocalDateTime.now();
        long hours = Math.abs(calculateNumberOfDays(bookingDto.getStartDate(), currentTime));
        double driverSalary;
        User driver = userRepository.getUserById(bookingDto.getDriverId());
        if (currentTime.isBefore(bookingDto.getStartDate())) {
            driverSalary = 0.0;
        } else {
            driverSalary = driver.getSalaryDriver() * hours;
        }
        BigDecimal driverSalaryBig = BigDecimal.valueOf(driverSalary);
        NumberFormat currencyFormatter = NumberFormat.getInstance(Locale.US);

        if (bookingDto.getDriverId() != null) {
            return String.format(
                    "Please confirm to send request return the car. \nDriver rental payment: %s VND",
                    currencyFormatter.format(driverSalaryBig)
            );

        } else {
            return ("Please confirm to send request return the car.");
        }
    }

    @Override
    public int confirmReturnCar(Long bookingId, HttpSession session) {

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        User user = (User) session.getAttribute("user");

        User userdb = userRepository.getUserById(user.getId());
        User users = userRepository.getUserById(user.getId());
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), // start date
                ((Timestamp) result[2]).toLocalDateTime(), // end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(), // actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), // userId
                (Integer) result[7], // bookingStatus
                (Integer) result[8], // paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, // driver
                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue(), // deposit
                (BigDecimal) result[12], // carOwner wallet
                Long.valueOf((Integer) result[13]),
                (String) result[14], //car name
                (Integer) result[15] //cariD

        );

        User car_owner = userRepository.getUserById(bookingDto.getCarOwnerId());

        BigDecimal deposit = BigDecimal.valueOf(bookingDto.getDeposit());
        BigDecimal myWallet = userdb.getWallet();

        // calculate driver salary
        double driverSalary;
        LocalDateTime currentTime = LocalDateTime.now();
        long hours = Math.abs(calculateNumberOfDays(bookingDto.getStartDate(), currentTime));
        User driver = userRepository.getUserById(bookingDto.getDriverId());

        if (currentTime.isBefore(bookingDto.getStartDate())) {
            driverSalary = 0.0;
        } else {
            driverSalary = driver.getSalaryDriver() * hours;
        }
        BigDecimal driverSalaryBig = BigDecimal.valueOf(driverSalary);


        if (bookingDto.getDriverId() != null) {
            // check wallet user
            if (driverSalary > user.getWallet().doubleValue()) {
                return -1;
            } else {
                // Pay money for driver
                BigDecimal updatedUserWallet = myWallet.subtract(driverSalaryBig);
                user.setWallet(updatedUserWallet);
                userRepository.save(user);
                session.setAttribute("user", user);

                // Driver receive money
                BigDecimal updatedCarOwnerWallet = driver.getWallet().add(driverSalaryBig);
                driver.setWallet(updatedCarOwnerWallet);
                userRepository.save(driver);


                // update booking Status
                if (bookingOptional.isPresent()) {
                    Booking booking = bookingOptional.get();
                    //======================Cộng tiền vào ví driver===========================
                    transactionService.saveTransaction(driver, driverSalaryBig, TransactionType.OFFSET_FINAL_RECEIVE_SALARY, booking);

                    // ======================Trừ tiền vào ví customer===========================
                    transactionService.saveTransaction(user, driverSalaryBig, TransactionType.OFFSET_FINAL_PAYMENT_RENTAL_DRIVER, booking);
                    //=================================================
                    // Check booking status is In-Progress.
                    if (booking.getUser().getId().equals(user.getId()) &&
                            booking.getBookingStatus().getBookingStatusId() == 3) {

                        Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(8L);
                        if (completedStatusOptional.isPresent()) {
                            BookingStatus completedStatus = completedStatusOptional.get();
                            booking.setBookingStatus(completedStatus);
                            booking.setLastModified(new Date());
                            rentalCarRepository.save(booking);

                            //================Update driver status======================
                            Long oldDriverId = null;
                            if (booking.getDriver() != null && booking.getDriver().getId() != null) {
                                oldDriverId = booking.getDriver().getId();
                            }
                            if (oldDriverId != null) {
                                User oldDriver = userRepository.getUserById(oldDriverId);
                                userRepository.save(user);
                                session.setAttribute("user", user);
                                oldDriver.setStatus(UserStatus.ACTIVATED);
                                userRepository.save(oldDriver);
                            }

                            Optional<Car> carOptional = carRepository.findById(bookingDto.getCarId());
                            if (carOptional.isPresent()) {
                                Car car = carOptional.get();
                                Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(16);
                                if (bookedStatusOptional.isPresent()) {
                                    CarStatus bookedStatus = bookedStatusOptional.get();
                                    car.setCarStatus(bookedStatus);
                                    carRepository.save(car);
                                } else {
                                    System.out.println("Car status 'Pending confirm' not found.");
                                    return 0;
                                }
                            } else {
                                System.out.println("Car with ID " + bookingDto.getCarId() + " not found.");
                                return 0;
                            }
                            emailService.sendRequestReturnCar(car_owner, booking, bookingDto.getCarId(), bookingDto.getCarname());
                            emailService.sendPaymentDriverSalary(driver,  booking, driverSalary);
                            return 1;
                        } else {
                            System.out.println("Completed status not found.");
                            return 0;
                        }
                    } else {
                        System.out.println("Booking does not belong to the user or is not in an 'In-Progress' state.");
                        return 0;
                    }
                } else {
                    System.out.println("Booking with ID " + bookingId + " not found.");
                    return 0;
                }
            }
        } else {
            // update booking Status
            if (bookingOptional.isPresent()) {
                Booking booking = bookingOptional.get();

                // Check booking status is In-Progress.
                if (booking.getUser().getId().equals(user.getId()) &&
                        booking.getBookingStatus().getBookingStatusId() == 3) {

                    Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(8L);
                    if (completedStatusOptional.isPresent()) {
                        BookingStatus completedStatus = completedStatusOptional.get();
                        booking.setBookingStatus(completedStatus);
                        booking.setLastModified(new Date());
                        rentalCarRepository.save(booking);

                        Optional<Car> carOptional = carRepository.findById(bookingDto.getCarId());
                        if (carOptional.isPresent()) {
                            Car car = carOptional.get();
                            Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(16);
                            if (bookedStatusOptional.isPresent()) {
                                CarStatus bookedStatus = bookedStatusOptional.get();
                                car.setCarStatus(bookedStatus);
                                carRepository.save(car);
                            } else {
                                System.out.println("Car status 'Pending confirm' not found.");
                                return 0;
                            }
                        } else {
                            System.out.println("Car with ID " + bookingDto.getCarId() + " not found.");
                            return 0;
                        }
                        emailService.sendRequestReturnCar(car_owner, booking, bookingDto.getCarId(), bookingDto.getCarname());
                        return 1;
                    } else {
                        System.out.println("Completed status not found.");
                        return 0;
                    }
                } else {
                    System.out.println("Booking does not belong to the user or is not in an 'In-Progress' state.");
                    return 0;
                }
            } else {
                System.out.println("Booking with ID " + bookingId + " not found.");
                return 0;
            }

        }

    }

    @Override
    public boolean updateBookingPendingPayment(Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Booking has been update to PendingPayment: " + bookingId);

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            // Check if the booking belongs to the user and is in a in-progress state
            if (booking.getUser().getId().equals(user.getId()) &&
                    booking.getBookingStatus().getBookingStatusId() == 3) {

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> pendingPaymentStatus = bookingStatusRepository.findById(4L);

                if (pendingPaymentStatus.isPresent()) {
                    BookingStatus pendingPayment = pendingPaymentStatus.get();
                    booking.setBookingStatus(pendingPayment); // Update the status of the booking

                    // Save the updated booking
                    rentalCarRepository.save(booking);
                    System.out.println("Booking with ID " + bookingId + " has been update to Pending payment.");
                    return true;
                } else {
                    System.out.println("Pending payment status not found.");
                }
            } else {
                System.out.println("Booking does not belong to the user or is not in a update state.");
            }
        } else {
            System.out.println("Booking with ID " + bookingId + " not found.");
        }

        return false;
    }

    @Override
    public Double calculateTotalPriceForActualEnddateCarOwner(Long bookingId) {
        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), //start date
                ((Timestamp) result[2]).toLocalDateTime(), //end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), //userId
                (Integer) result[7], //bookingStatus
                (Integer) result[8], //paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, //driver

                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue(), // deposit
                (BigDecimal) result[12], //carowner wallet
                Long.valueOf((Integer) result[13]),
                (String) result[14], //car name
                (Integer) result[15] //cariD

        );

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            LocalDateTime currentDate = LocalDateTime.now();
            long hoursActualToEnd = Math.abs(Duration.between(bookingDto.getActualEndDate(), booking.getEndDate()).toHours());
            long hoursStartToEnd = Math.abs(Duration.between(booking.getStartDate(), booking.getEndDate()).toHours());
            long hoursStartToActual = Math.abs(Duration.between(booking.getStartDate(), currentDate).toHours());

            if (currentDate.isBefore(booking.getStartDate())) {
                return bookingDto.getDeposit() * 0.1;
            }

            if (currentDate.isBefore(booking.getEndDate())) {
                return hoursActualToEnd * (bookingDto.getBasePrice() / 24);
            } else {
                return hoursActualToEnd * bookingDto.getBasePrice() * FINE_COST / 100 + hoursStartToEnd * bookingDto.getBasePrice(); // số tiền theo hợp đồng  +  số ngày quá hạn * giá thuê * phạt 20%
            }

        }
        return 0.0;
    }

    @Override
    public Double calculateTotalPrice(Long bookingId) {
        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);
        Object[] nestedArray = bookingRepository.findByBookingId(bookingId);
        Object[] result = (Object[]) nestedArray[0];
        MyBookingDto bookingDto = new MyBookingDto(
                Long.valueOf((Integer) result[0]),
                ((Timestamp) result[1]).toLocalDateTime(), //start date
                ((Timestamp) result[2]).toLocalDateTime(), //end date
                (String) result[3], // driverInfo
                ((Timestamp) result[4]).toLocalDateTime(),//actualEndDate
                ((BigDecimal) result[5]).doubleValue(), // total price
                Long.valueOf((Integer) result[6]), //userId
                (Integer) result[7], //bookingStatus
                (Integer) result[8], //paymentMethod
                result[9] != null ? Long.valueOf((Integer) result[9]) : null, //driver

                ((BigDecimal) result[10]).doubleValue(), // basePrice
                ((BigDecimal) result[11]).doubleValue(), // deposit
                (BigDecimal) result[12], //carowner wallet
                Long.valueOf((Integer) result[13]),
                (String) result[14], //car name
                (Integer) result[15] //cariD

        );

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            LocalDateTime currentDate = LocalDateTime.now();

            long hours2 = Math.abs(Duration.between(booking.getStartDate(), currentDate).toHours());
            long numberOfDaysRental = (long) Math.ceil(hours2 / 24.0);


            if (currentDate.isBefore(booking.getStartDate())) {
                return bookingDto.getDeposit() * 0.1;
            }

            if (currentDate.isBefore(booking.getEndDate())) {
                return numberOfDaysRental * bookingDto.getBasePrice();
            } else {
                long hours = Math.abs(Duration.between(currentDate, booking.getEndDate()).toHours());
                long numberOfDaysOverdue = (long) Math.ceil(hours / 24.0);

                long hours1 = Math.abs(Duration.between(booking.getStartDate(), booking.getEndDate()).toHours());
                long numberOfDaysNotOverdue = (long) Math.ceil(hours1 / 24.0);

                return numberOfDaysOverdue * bookingDto.getBasePrice() * FINE_COST / 100 + numberOfDaysNotOverdue * bookingDto.getBasePrice();
            }

        }
        return 0.0;
    }

    public int calculateNumberOfDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Chuyển đổi LocalDateTime sang mili-giây (epoch milli)
        long startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        // Tính chênh lệch thời gian (mili-giây)
        long timeDiff = endMillis - startMillis;

        // Chia để tính số ngày và làm tròn lên
        return (int) Math.ceil(timeDiff / (1000.0 * 3600 * 24));
    }

}



