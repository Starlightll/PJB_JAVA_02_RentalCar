package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.BookingStatus;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.BookingStatusRepository;
import com.rentalcar.rentalcar.repository.RentalCarRepository;
import com.rentalcar.rentalcar.repository.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
                Long.valueOf((Integer) result[13])
        );

        Double totalPrice = calculateTotalPrice(bookingId);
        if (totalPrice > bookingDto.getDeposit()) {
            return String.format("Please confirm to return the car. The remaining amount of %.2f VND will be deducted from your wallet.", totalPrice - bookingDto.getDeposit());
        } else {
            return String.format("Please confirm to return the car. The exceeding amount of %.2f VND will be returned to your wallet..", bookingDto.getDeposit() - totalPrice);
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
                Long.valueOf((Integer) result[13])
        );

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();


            LocalDateTime currentDate = LocalDateTime.now();

            long numberOfDaysOverdue = ChronoUnit.DAYS.between(booking.getEndDate(), currentDate);
            if (currentDate.isAfter(booking.getEndDate())) {
                return booking.getTotalPrice();
            } else {
                return numberOfDaysOverdue * bookingDto.getBasePrice() * FINE_COST / 100 + booking.getTotalPrice();
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
        // Lấy thông tin người dùng từ session và các chi tiết liên quan đến booking
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
                Long.valueOf((Integer) result[13])
        );

        BigDecimal deposit = BigDecimal.valueOf(bookingDto.getDeposit());
        BigDecimal myWallet = userdb.getWallet();
        BigDecimal totalPrice = BigDecimal.valueOf(calculateTotalPrice(bookingId));


        if (totalPrice.compareTo(deposit) > 0) {
            // Kiểm tra ví người dùng
            if (totalPrice.compareTo(myWallet) > 0) {
                return -1; // Tiền trong ví không đủ
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

                    // Kiểm tra xem booking có phải của người dùng và đang ở trạng thái "In-Progress"
                    if (booking.getUser().getId().equals(user.getId()) &&
                            booking.getBookingStatus().getName().equals("In-Progress")) {

                        Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findByName("Completed");
                        if (completedStatusOptional.isPresent()) {
                            BookingStatus completedStatus = completedStatusOptional.get();
                            booking.setBookingStatus(completedStatus); // Cập nhật trạng thái booking
                            rentalCarRepository.save(booking);
                            return 1; // Booking hoàn thành
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
            // Trường hợp tiền thanh toán nhỏ hơn deposit => Kiểm tra ví của chủ xe
            if (bookingDto.getCarOwnerWallet().compareTo(totalPrice) < 0) {
                return -2; // Chủ xe không đủ tiền để trả lại cho khách hàng
            } else {
                // Cập nhật ví của chủ xe và khách hàng
                BigDecimal updatedCarOwnerWallet = bookingDto.getCarOwnerWallet().subtract(deposit.subtract(totalPrice));
                User carOwner = userRepository.getUserById(bookingDto.getCarOwnerId());
                carOwner.setWallet(updatedCarOwnerWallet);
                userRepository.save(carOwner);

                BigDecimal updatedCustomerWallet = user.getWallet().add(deposit.subtract(totalPrice));
                user.setWallet(updatedCustomerWallet);
                userRepository.save(user);
                session.setAttribute("user", user);

                // Cập nhật trạng thái booking
                if (bookingOptional.isPresent()) {
                    Booking booking = bookingOptional.get();
                    if (booking.getUser().getId().equals(user.getId()) &&
                            booking.getBookingStatus().getName().equals("In-Progress")) {

                        Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findByName("Completed");
                        if (completedStatusOptional.isPresent()) {
                            BookingStatus completedStatus = completedStatusOptional.get();
                            booking.setBookingStatus(completedStatus);
                            rentalCarRepository.save(booking);
                            System.out.println("Booking with ID " + bookingId + " has been completed!");
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
                    (booking.getBookingStatus().getName().equals("In-Progress"))) {

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> pendingPaymentStatus = bookingStatusRepository.findByName("Pending Payment");

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
}
