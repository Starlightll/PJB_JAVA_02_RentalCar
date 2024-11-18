package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;

import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.entity.*;
import com.rentalcar.rentalcar.exception.UserException;
import com.rentalcar.rentalcar.mail.EmailService;
import com.rentalcar.rentalcar.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class RentalCarServiceImpl implements RentalCarService {

    @Autowired
    RentalCarRepository rentalCarRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    CarRepository carRepository;
    @Autowired
    private BookingStatusRepository bookingStatusRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingCarRepository bookingCarRepository;

    @Autowired PhoneNumberStandardService phoneNumberStandardService;

    @Autowired DriverDetailRepository driverDetailRepository;

    @Autowired
    EmailService emailService;

    @Autowired UserRepo userRepository;

    @Autowired
    private CarStatusRepository carStatusRepository;

    @Autowired
    private TransactionService transactionService;

    @Override
    public Page<MyBookingDto> getBookings(int page, int size, String sortBy, String order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<MyBookingDto> bookingDtos = new ArrayList<>();
        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Object[]> resultsPage;
        resultsPage = rentalCarRepository.findAllWithPagination(user.getId(), pageable);

        for (Object[] result : resultsPage.getContent()) {
            LocalDateTime startDate = ((Timestamp) result[2]).toLocalDateTime();
            LocalDateTime actualEndDate = ((Timestamp) result[5]).toLocalDateTime();

            // Tính toán số ngày giữa startDate và actualEndDate
            int numberOfDays = (int) ChronoUnit.DAYS.between(startDate, actualEndDate);

            MyBookingDto bookingDto = new MyBookingDto(
                    Long.valueOf((Integer) result[0]),
                    (String) result[1],
                    ((Timestamp) result[2]).toLocalDateTime(), //start date
                    ((Timestamp) result[3]).toLocalDateTime(), //end date
                    (String) result[4], // driverInfo
                    ((Timestamp) result[5]).toLocalDateTime(),//actualEndDate
                    ((BigDecimal) result[6]).doubleValue(), // total price
                    Long.valueOf((Integer) result[7]), //userId
                    numberOfDays, //numberOfDays
                    (Integer) result[8], //paymentMethod
                    ((BigDecimal) result[9]).doubleValue(), // basePrice
                    ((BigDecimal) result[10]).doubleValue(), // deposit
                    (String) result[11], //bookingStatus
                    (String) result[12],
                    (String) result[13],
                    (String) result[14],
                    (String) result[15],
                    (Integer) result[16]
            );
            bookingDtos.add(bookingDto);
        }


        return new PageImpl<>(bookingDtos, pageable, resultsPage.getTotalElements());
    }

    @Override
    public boolean cancelBooking(Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to cancel booking with ID: " + bookingId);

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            // Check if the booking belongs to the user and is in a cancellable state
            if (booking.getUser().getId().equals(user.getId()) &&
                    (booking.getBookingStatus().getName().equals("Confirmed") ||
                            booking.getBookingStatus().getName().equals("Pending deposit") ||
                            booking.getBookingStatus().getName().equals("Stopped"))) {

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> cancelledStatusOptional = bookingStatusRepository.findByName("Cancelled");

                if (cancelledStatusOptional.isPresent()) {
                    BookingStatus cancelledStatus = cancelledStatusOptional.get();
                    booking.setBookingStatus(cancelledStatus); // Update the status of the booking

                    // Save the updated booking
                    rentalCarRepository.save(booking);
                    System.out.println("Booking with ID " + bookingId + " has been successfully cancelled.");
                    return true;
                } else {
                    System.out.println("Cancelled status not found.");
                }
            } else {
                System.out.println("Booking does not belong to the user or is not in a cancellable state.");
            }
        } else {
            System.out.println("Booking with ID " + bookingId + " not found.");
        }

        return false;
    }

    @Override
    public boolean confirmPickupBooking(Long bookingId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Booking has been update to In-Progress: " + bookingId);

        Optional<Booking> bookingOptional = rentalCarRepository.findById(bookingId);

        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();

            // Check if the booking belongs to the user and is in a cancellable state
            if (booking.getUser().getId().equals(user.getId()) &&
                    (booking.getBookingStatus().getName().equals("Confirmed"))) {

                // Fetch the "Cancelled" BookingStatus from the database
                Optional<BookingStatus> cancelledStatusOptional = bookingStatusRepository.findByName("In-Progress");

                if (cancelledStatusOptional.isPresent()) {
                    BookingStatus cancelledStatus = cancelledStatusOptional.get();
                    booking.setBookingStatus(cancelledStatus); // Update the status of the booking

                    // Save the updated booking
                    rentalCarRepository.save(booking);
                    System.out.println("Booking with ID " + bookingId + " has been update to In-Progress.");
                    return true;
                } else {
                    System.out.println("In-Progress status not found.");
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
    public CarDto getCarDetails(Integer carId) {
        Object[] result = carRepository.findCarByCarId(carId);
        Object[] nestedArray = (Object[]) result[0];
        Long carid = nestedArray[0] instanceof Integer ? Long.valueOf((Integer) nestedArray[0]) : null;
        Double averageRating = nestedArray[27] != null ? (Double) nestedArray[27] : 0;


        // Ánh xạ từng giá trị từ result vào CarDto
        return new CarDto(
                carid,  // carId
                (String) nestedArray[1],   // name
                (String) nestedArray[2],   // licensePlate
                (String) nestedArray[3],   // model
                (String) nestedArray[4],   // color
                (Integer) nestedArray[5],  // seatNo
                (Integer) nestedArray[6],  // productionYear
                (String) nestedArray[7],   // transmission
                (String) nestedArray[8],   // fuel
                ((BigDecimal) nestedArray[9]).doubleValue(),  // mileage
                ((BigDecimal) nestedArray[10]).doubleValue(),  // fuelConsumption
                ((BigDecimal) nestedArray[11]).doubleValue(),  // basePrice
                ((BigDecimal) nestedArray[12]).doubleValue(),  // deposit
                (String) nestedArray[13],  // description
                (String) nestedArray[14],  // termOfUse
                ((BigDecimal) nestedArray[15]).doubleValue(),  // carPrice
                (String) nestedArray[16],  // front
                (String) nestedArray[17],  // back
                (String) nestedArray[18],  // left
                (String) nestedArray[19],  // right
                (String) nestedArray[20],  // registration
                (String) nestedArray[21],  // certificate
                (String) nestedArray[22],  // insurance
                (Date) nestedArray[23],  // lastModified
                (Integer) nestedArray[24], // userId
                (Integer) nestedArray[25], // brandId
                (Integer) nestedArray[26], // statusId
                averageRating   // averageRating
        );
    }

    @Override
    public Booking saveBooking(BookingDto bookingDto, MultipartFile[] files, HttpSession session) {
        Booking booking = new Booking();
        DriverDetail driverDetail = new DriverDetail();
        Car car = carRepository.getCarByCarId(bookingDto.getCarID());
        User user = (User) session.getAttribute("user");
        User customer = userRepository.getUserById(user.getId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

//        if(userRepository.getUserByEmail(customer.getEmail()) != null) {
//            throw new RuntimeException("Email already exists");
//        }

//        if(phoneNumberStandardService.isPhoneNumberExists(customer.getPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE)) {
//            throw new RuntimeException("Phone number already exists");
//        }

        //============CHỌN VÍ ĐỂ TRẢ CỌC============================
        BigDecimal myWallet = customer.getWallet() != null ? customer.getWallet() : BigDecimal.ZERO; // VÍ CỦA CUSTOMER
        User carOwner = userRepository.getUserById(car.getUser().getId());
        BigDecimal deposit = new BigDecimal(bookingDto.getDeposit());
        if(bookingDto.getSelectedPaymentMethod() != 1) {// CHỌN PHƯƠNG THỨC THANH TOÁN KHÁC
             throw new RuntimeException("Other Pay Method not helps now, please use your wallet");
        }

        if (myWallet.compareTo(deposit) < 0) {
            throw new RuntimeException("Your wallet must be greater than deposit");
        }

        //==========================================================

        String folderName = String.format("%s", user.getId());
        Path draftFolderPath = Paths.get("uploads/Driver/" + user.getId() + "/Detail/", folderName);

        try {
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                files[0].getSize();
                String storedPath = fileStorageService.storeFile(files[0], draftFolderPath, "drivingLicense." + getExtension(files[0].getOriginalFilename()));
                driverDetail.setDrivingLicense(storedPath);
            } else {
                driverDetail.setDrivingLicense(user.getDrivingLicense());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Lưu vô db
        try {

            //Lưu booking
            int numberOfDays = (int) ChronoUnit.DAYS.between(bookingDto.getPickUpDate(), bookingDto.getReturnDate());
            Double totalPrice = car.getBasePrice() * numberOfDays;
            booking.setStartDate(bookingDto.getPickUpDate());
            booking.setEndDate(bookingDto.getReturnDate());
            booking.setDriverInfo(bookingDto.getRentFullName());
            booking.setActualEndDate(bookingDto.getReturnDate());
            booking.setTotalPrice(totalPrice);
            booking.setUser(user);
            // Cập nhật BookingStatus
            BookingStatus bookingStatus = bookingStatusRepository.findById(1L) // 1L là ID của status mà bạn muốn
                    .orElseThrow(() -> new RuntimeException("BookingStatus not found"));
            booking.setBookingStatus(bookingStatus);
            // Cập nhật PaymentMethod
            PaymentMethod paymentMethod = paymentMethodRepository.findById((long) bookingDto.getSelectedPaymentMethod())
                    .orElseThrow(() -> new RuntimeException("PaymentMethod not found"));
            booking.setPaymentMethod(paymentMethod);
            bookingRepository.save(booking);

            //Lưu booking car
            BookingCar bookingCar = new BookingCar();
            bookingCar.setBookingId(booking.getBookingId());
            bookingCar.setCarId(Long.valueOf(bookingDto.getCarID()));
            bookingCarRepository.save(bookingCar);

            calculateAndDeductDeposit(booking, customer, carOwner,myWallet, deposit, session);  //XỬ LÝ TIỀN TRONG CỌC


            //Lưu thông tin người thuê xe
            String normalizedPhone = phoneNumberStandardService.normalizePhoneNumber(bookingDto.getRentPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);
            driverDetail.setFullName(bookingDto.getRentFullName());
            driverDetail.setEmail(bookingDto.getRentMail());
            driverDetail.setPhone(normalizedPhone);
            driverDetail.setNationalId(bookingDto.getRentNationalId());
            driverDetail.setDob(bookingDto.getRentBookPickDate());
            driverDetail.setCity(bookingDto.getRentProvince().trim());
            driverDetail.setDistrict(bookingDto.getRentDistrict().trim());
            driverDetail.setStreet(bookingDto.getRentStreet().trim());
            driverDetail.setWard(bookingDto.getRentWard().trim());
            driverDetail.setBooking(booking);
            driverDetailRepository.save(driverDetail);


            if (bookingDto.getIsCheck() && bookingDto.getSelectedUserId() != null) {
                // Xử lý driver khi ấn tích
                User driver = userRepository.getUserById(Long.valueOf(bookingDto.getSelectedUserId())); // Lấy đối tượng User của Driver
                if (driver != null) {
                    booking.setDriver(driver);
                    bookingRepository.save(booking);

                    // THAY ĐỔI TRẠNG THÁI CHO DRIVER
                    driver.setStatusDriverId(2);
                    userRepository.save(driver);
                }
            }

            //THAY ĐỔI TRẠNG THÁI CHO XE
            CarStatus notAvailableStatus = carStatusRepository.findByName("BOOKED")
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            car.setCarStatus(notAvailableStatus);
            carRepository.save(car);

        } catch (Exception e) {
            e.printStackTrace();
        }



        //MAIL TO CUSTOMER
        emailService.sendBookingConfirmation(customer, bookingDto, booking,car);
        //MAIL TO CAR OWNER

        emailService.sendBookingConfirmationWithDeposit(carOwner, booking, car,  Double.parseDouble(bookingDto.getDeposit()));

        return booking;
    }


    private void calculateAndDeductDeposit(Booking booking, User customer, User carOwner,
                                           BigDecimal myWallet,BigDecimal deposit, HttpSession session) {
        BigDecimal carOwnerWallet = carOwner.getWallet() != null ? carOwner.getWallet() : BigDecimal.ZERO; // VÍ CỦA CAR OWNER

            BigDecimal depositedMoney = myWallet.subtract(deposit);
            customer.setWallet(depositedMoney);
            userRepository.save(customer); // TRỪ TIỀN THÀNH CÔNG
            session.setAttribute("user", customer);
            transactionService.saveTransaction(customer, deposit, TransactionType.PAY_DEPOSIT, booking);
            // CỘNG TIỀN CHO CAR OWNER
            BigDecimal moneyReceive = carOwnerWallet.add(deposit);
            carOwner.setWallet(moneyReceive);
            userRepository.save(carOwner); // CỘNG TIỀN THÀNH CÔNG
            transactionService.saveTransaction(carOwner, deposit, TransactionType.RECEIVE_DEPOSIT, booking);


    }



}



