package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

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
    @Override
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

        Double totalPrice = calculateTotalPrice(bookingId);
        LocalDateTime currentTime = LocalDateTime.now();
        Double driverSalary = (double) Constants.DRIVER_SALARY * calculateNumberOfDays(bookingDto.getStartDate(), currentTime);

        // Sử dụng NumberFormat để định dạng tiền tệ
        NumberFormat currencyFormatter = NumberFormat.getInstance(Locale.US);

        if (bookingDto.getDriverId() == null) {
            if (totalPrice.equals(bookingDto.getDeposit())) {
                return "Please confirm to return the car. The deposit you pay is equal to the rental fee.";
            } else if (totalPrice > bookingDto.getDeposit()) {
                return String.format(
                        "Please confirm to return the car. The remaining amount of %s VND will be deducted from your wallet.",
                        currencyFormatter.format(totalPrice - bookingDto.getDeposit())
                );
            } else {
                return String.format(
                        "Please confirm to return the car. The exceeding amount of %s VND will be returned to your wallet.",
                        currencyFormatter.format(bookingDto.getDeposit() - totalPrice)
                );
            }
        } else {
            if (totalPrice.equals(bookingDto.getDeposit())) {
                return String.format(
                        "Please confirm to return the car. The deposit you pay is equal to the rental fee. Driver rental payment: %s VND",
                        currencyFormatter.format(driverSalary)
                );
            } else if (totalPrice > bookingDto.getDeposit()) {
                return String.format(
                        "Please confirm to return the car. The remaining amount of %s VND will be deducted from your wallet. Driver rental payment: %s VND",
                        currencyFormatter.format(totalPrice - bookingDto.getDeposit()),
                        currencyFormatter.format(driverSalary)
                );
            } else {
                return String.format(
                        "Please confirm to return the car. The exceeding amount of %s VND will be returned to your wallet. Driver rental payment: %s VND",
                        currencyFormatter.format(bookingDto.getDeposit() - totalPrice),
                        currencyFormatter.format(driverSalary)
                );
            }
        }
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

            long numberOfDaysOverdue = ChronoUnit.DAYS.between(booking.getEndDate(), currentDate);
            long numberOfDaysNotOverdue = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());

            if (currentDate.isBefore(booking.getEndDate())) {
                return booking.getTotalPrice();
            } else {
                return numberOfDaysOverdue * bookingDto.getBasePrice() * FINE_COST / 100 + numberOfDaysNotOverdue * bookingDto.getBasePrice();
            }

        }
        return 0.0;
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
        User carowner = userRepository.getUserById(bookingDto.getCarOwnerId());

        BigDecimal deposit = BigDecimal.valueOf(bookingDto.getDeposit());
        BigDecimal myWallet = userdb.getWallet();
        BigDecimal totalPrice = BigDecimal.valueOf(calculateTotalPrice(bookingId));

        // lay tien thue tai xe
        LocalDateTime currentTime = LocalDateTime.now();
        double driverSalary = (double) Constants.DRIVER_SALARY * calculateNumberOfDays(bookingDto.getStartDate(), currentTime);
        BigDecimal driverSalaryBig =  BigDecimal.valueOf(driverSalary);


        if (totalPrice.compareTo(deposit) >= 0) {
            // Kiểm tra ví người dùng
            if ((totalPrice.subtract(deposit)).compareTo(myWallet) > 0) {
                return -1;
            } else {
                // Nếu ví người dùng đủ tiền, trừ tiền vào ví của khách hàng và chuyển cho chủ xe
                BigDecimal updatedUserWallet = myWallet.subtract(totalPrice.subtract(deposit));
                user.setWallet(updatedUserWallet);
                userRepository.save(user);
                session.setAttribute("user", user);

                // Cập nhật ví của chủ xe
                BigDecimal carOwnerWallet = bookingDto.getCarOwnerWallet() != null ? bookingDto.getCarOwnerWallet() : BigDecimal.ZERO;
                BigDecimal updatedCarOwnerWallet = carOwnerWallet.add(totalPrice.subtract(deposit));
                User carOwner = userRepository.getUserById(bookingDto.getCarOwnerId());
                carOwner.setWallet(updatedCarOwnerWallet);
                userRepository.save(carOwner);


                // Cập nhật trạng thái booking
                if (bookingOptional.isPresent()) {
                    Booking booking = bookingOptional.get();
                    //======================Cộng tiền vào ví chủ xe===========================
                    transactionService.saveTransaction(carOwner, totalPrice.subtract(deposit), TransactionType.OFFSET_FINAL_PAYMENT_CAR_OWNER, booking);
                    //=================================================

                    // ======================Trừ tiền vào ví customer===========================
                    transactionService.saveTransaction(user, totalPrice.subtract(deposit), TransactionType.OFFSET_FINAL_PAYMENT_CUSTOMER, booking);
                    //=================================================
                    // Kiểm tra xem booking có phải của người dùng và đang ở trạng thái "In-Progress"
                    if (booking.getUser().getId().equals(user.getId()) &&
                            booking.getBookingStatus().getBookingStatusId() == 3) {

                        Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(5L);
                        if (completedStatusOptional.isPresent()) {
                            BookingStatus completedStatus = completedStatusOptional.get();
                            booking.setBookingStatus(completedStatus); // Cập nhật trạng thái booking
                            booking.setLastModified(new Date());
                            rentalCarRepository.save(booking);

                            //================Thay đổi trạng thái tài xế======================
                            Long oldDriverId = null;
                            if (booking.getDriver() != null && booking.getDriver().getId() != null) {
                                oldDriverId = booking.getDriver().getId();
                            }
                            if (oldDriverId != null) {
                                User oldDriver = userRepository.getUserById(oldDriverId);
                                // =====cong tien vao vi driver ===
                                BigDecimal updatedWallet = oldDriver.getWallet().add(driverSalaryBig);
                                oldDriver.setWallet(updatedWallet);
                                //==== tru tien thue nguoi lai vao vi customer
                                BigDecimal updatedUserWalletDriver = myWallet.subtract(driverSalaryBig);
                                user.setWallet(updatedUserWalletDriver);
                                userRepository.save(user);
                                session.setAttribute("user", user);

                                // update vao transaction
                                if(!driverSalaryBig.equals(BigDecimal.ZERO)) {
                                    transactionService.saveTransaction(user, driverSalaryBig, TransactionType.OFFSET_FINAL_PAYMENT_RENTAL_DRIVER, booking);
                                }

                                oldDriver.setStatus(UserStatus.ACTIVATED);
                                userRepository.save(oldDriver);
                            }
                            //==========================================


                            Optional<Car> carOptional = carRepository.findById(bookingDto.getCarId());
                            if (carOptional.isPresent()) {
                                Car car = carOptional.get();
                                Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(1);
                                if (bookedStatusOptional.isPresent()) {
                                    CarStatus bookedStatus = bookedStatusOptional.get();
                                    car.setCarStatus(bookedStatus); // Cập nhật trạng thái xe
                                    carRepository.save(car);
                                } else {
                                    System.out.println("Car status 'Available' not found.");
                                    return 0; // Trạng thái "Booked" không tồn tại
                                }
                            } else {
                                System.out.println("Car with ID " + bookingDto.getCarId() + " not found.");
                                return 0; // Xe không tồn tại
                            }

                            emailService.sendReturnCarSuccessfully(carowner, booking, bookingDto.getCarId(), bookingDto.getCarname(), totalPrice.subtract(deposit).doubleValue());
                            return 1;
                        } else {
                            System.out.println("Completed status not found.");
                            return 0; // Trạng thái "Completed" không tồn tại
                        }
                    } else {
                        System.out.println("Booking does not belong to the user or is not in an 'In-Progress' state.");
                        return 0; // Booking không thuộc về người dùng hoặc không ở trạng thái "In-Progress"
                    }
                } else {
                    System.out.println("Booking with ID " + bookingId + " not found.");
                    return 0; // Booking không tồn tại
                }
            }
        } else {
            // Cập nhật trạng thái booking
            if (bookingOptional.isPresent()) {
                Booking booking = bookingOptional.get();
                if (booking.getUser().getId().equals(user.getId()) &&
                        booking.getBookingStatus().getBookingStatusId() == 3) {

                    Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(4L);
                    Optional<Car> carOptional = carRepository.findById(bookingDto.getCarId());
                    // cap nhat trang thai xe thanh Pending payment
                    if (completedStatusOptional.isPresent()) {
                        BookingStatus completedStatus = completedStatusOptional.get();
                        booking.setBookingStatus(completedStatus);
                        LocalDateTime currentDate = LocalDateTime.now();

                        booking.setActualEndDate(currentDate);
                        rentalCarRepository.save(booking);

                        //================Thay đổi trạng thái tài xế======================
                        Long oldDriverId = null;
                        if (booking.getDriver() != null && booking.getDriver().getId() != null) {
                            oldDriverId = booking.getDriver().getId();
                        }
                        if (oldDriverId != null) {
                            User oldDriver = userRepository.getUserById(oldDriverId);
                            oldDriver.setStatus(UserStatus.ACTIVATED);
                            userRepository.save(oldDriver);
                        }
                        //==========================================

                        //==== tru tien thue nguoi lai vao vi customer
                        BigDecimal updatedUserWalletDriver = myWallet.subtract(driverSalaryBig);
                        user.setWallet(updatedUserWalletDriver);
                        userRepository.save(user);
                        session.setAttribute("user", user);

                        // update vao transaction
                        if(!driverSalaryBig.equals(BigDecimal.ZERO)) {
                            transactionService.saveTransaction(user, driverSalaryBig, TransactionType.OFFSET_FINAL_PAYMENT_RENTAL_DRIVER, booking);
                        }



                        if (carOptional.isPresent()) {
                            Car car = carOptional.get();
                            Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(11);
                            if (bookedStatusOptional.isPresent()) {
                                CarStatus bookedStatus = bookedStatusOptional.get();
                                car.setCarStatus(bookedStatus);
                                carRepository.save(car);
                            } else {
                                System.out.println("Car status 'Pending payment' not found.");
                                return 0;
                            }
                        } else {
                            System.out.println("Car with ID " + bookingDto.getCarId() + " not found.");
                            return 0; // Xe không tồn tại
                        }
                        emailService.sendRequestConfirmPayment(carowner, booking, bookingDto.getCarId(), bookingDto.getCarname(), deposit.subtract(totalPrice).doubleValue());
                        return 2;
                    } else {
                        System.out.println("Completed status not found.");
                        return 0;
                    }
                } else {
                    System.out.println("Booking does not belong to the user or is not in a cancellable state.");
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

            long numberOfDaysOverdue = ChronoUnit.DAYS.between(booking.getEndDate(), booking.getActualEndDate()); // lấy ngày thuê trong hợp đồng
            long numberOfDaysNotOverdue = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()); // lấy ngày thuê quá hạn

            if (currentDate.isBefore(booking.getEndDate())) { // check xem có quá hạn không
                return booking.getTotalPrice(); // nếu không quá hạn thì vẫn giá cũ kể cả trả trước hay sau
            } else {
                return numberOfDaysOverdue * bookingDto.getBasePrice() * FINE_COST / 100 + numberOfDaysNotOverdue * bookingDto.getBasePrice(); // số tiền theo hợp đồng  +  số ngày quá hạn * giá thuê * phạt 20%
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



