package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.CarDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
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
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @Autowired
    PhoneNumberStandardService phoneNumberStandardService;

    @Autowired
    DriverDetailRepository driverDetailRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepo userRepository;

    @Autowired
    private CarStatusRepository carStatusRepository;

    @Autowired
    private ReturnCarService returnCarService;

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
            int numberOfDays = calculateNumberOfDays(startDate, actualEndDate);

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
        Integer carId = bookingDto.getCarId();
        Car car = carRepository.findById(carId).orElse(null);

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
                    // Update status xe thành "Available"
                    Optional<CarStatus> availableStatusOptional = carStatusRepository.findById(1);
                    if (availableStatusOptional.isEmpty()) {
                        System.out.println("Car status 'Available' not found.");
                        return false;
                    }
                    CarStatus availableStatus = availableStatusOptional.get();
                    car.setCarStatus(availableStatus);
                    carRepository.save(car);
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
            int numberOfDays = calculateNumberOfDays(bookingDto.getPickUpDate(), bookingDto.getReturnDate());
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
            CarStatus notAvailableStatus = carStatusRepository.findById(14)
                    .orElseThrow(() -> new RuntimeException("Status not found"));
            car.setCarStatus(notAvailableStatus);
            carRepository.save(car);

        } catch (Exception e) {
            e.printStackTrace();
        }



        //MAIL TO CUSTOMER
        emailService.sendBookingConfirmation(customer, bookingDto, booking, car);
        //MAIL TO CAR OWNER

        emailService.sendBookingConfirmationWithDeposit(carOwner, booking, car, Double.parseDouble(bookingDto.getDeposit()));

        return booking;
    }

    @Override
    public boolean confirmDepositCar(Long carId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Check if the user is logged in
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to confirm Car with ID: " + carId);

        // Fetch the car and booking details with "Pending deposit" status
        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 1);

        // Check if the result is not empty
        if (nestedArray == null || nestedArray.length == 0) {
            System.out.println("Car with ID " + carId + " not found.");
            return false;
        }

        // Extract the relevant data from the nested array and populate the CarDto
        Object[] result = (Object[]) nestedArray[0];
        CarDto carDto = new CarDto(
                Long.valueOf((Integer) result[0]), // booking Id
                (String) result[1],   // name
                (String) result[2],   // licensePlate
                (String) result[3],   // model
                (String) result[4],   // color
                (Integer) result[5],  // seatNo
                (Integer) result[6],  // productionYear
                (String) result[7],   // transmission
                (String) result[8],   // fuel
                ((BigDecimal) result[9]).doubleValue(),  // mileage
                ((BigDecimal) result[10]).doubleValue(),  // fuelConsumption
                ((BigDecimal) result[11]).doubleValue(),  // basePrice
                ((BigDecimal) result[12]).doubleValue(),  // deposit
                (String) result[13],  // description
                (String) result[14],  // termOfUse
                ((BigDecimal) result[15]).doubleValue(),  // carPrice
                (String) result[16],  // front
                (String) result[17],  // back
                (String) result[18],  // left
                (String) result[19],  // right
                (String) result[20],  // registration
                (String) result[21],  // certificate
                (String) result[22],  // insurance
                (Date) result[23],    // lastModified
                (Integer) result[24], // userId
                (Integer) result[25], // brandId
                (Integer) result[26], // statusId
                (Integer) result[27], // bookingStatusId
                (String) result[28],  // bookingStatusName
                Long.valueOf((Integer) result[29]) // booking Id
                // bookingId
        );

        if (carDto == null) {
            return false;
        }

        // Check the booking status and booking ID
        String bookingStatusName = carDto.getBookingStatusName();
        if (!"Pending deposit".equals(bookingStatusName)) {
            System.out.println("Booking status is not 'Pending deposit'.");
            return false; // Return false if the status is not "Pending deposit"
        }

        Long bookingId = carDto.getBookingId();
        if (bookingId == null) {
            System.out.println("Booking ID not found.");
            return false; // Return false if no booking ID found
        }

        // Retrieve and update the booking
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (!bookingOptional.isPresent()) {
            System.out.println("Booking with ID " + bookingId + " not found.");
            return false; // If booking not found, return false
        }

        Booking booking = bookingOptional.get();


        // Update the booking status to "Confirmed"
        Optional<BookingStatus> confirmedStatusOptional = bookingStatusRepository.findByName("Confirmed");
        if (confirmedStatusOptional.isPresent()) {
            BookingStatus confirmedStatus = confirmedStatusOptional.get();
            booking.setBookingStatus(confirmedStatus);
            bookingRepository.save(booking);
        } else {
            System.out.println("Confirmed status not found.");
            return false; // If the "Confirmed" status is not found, return false
        }

        // Update the car status to "BOOKED"
        Car car = carRepository.getCarByCarId(carDto.getCarId().intValue());
        Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(2);
        if (bookedStatusOptional.isPresent()) {
            CarStatus bookedStatus = bookedStatusOptional.get();
            car.setCarStatus(bookedStatus);
            carRepository.save(car);
        } else {
            System.out.println("Booked status not found.");
            return false; // If the "Booked" status is not found, return false
        }

        System.out.println("Car and Booking successfully updated for booking ID: " + bookingId);
        return true;
    }


    @Override
    public Map<String, String> checkPaymentCar(Long carId, HttpSession session) {

        User user = (User) session.getAttribute("user");

        // Check if the user is logged in
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to confirm Car with ID: " + carId);

        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 4);

        if (nestedArray == null || nestedArray.length == 0) {
            System.out.println("Car with ID " + carId + " not found.");
            return Map.of("status", "error", "message", "Car with ID " + carId + " not found.");
        }

        Object[] result = (Object[]) nestedArray[0];
        CarDto carDto = new CarDto(
                Long.valueOf((Integer) result[0]), // booking Id
                (String) result[1],   // name
                (String) result[2],   // licensePlate
                (String) result[3],   // model
                (String) result[4],   // color
                (Integer) result[5],  // seatNo
                (Integer) result[6],  // productionYear
                (String) result[7],   // transmission
                (String) result[8],   // fuel
                ((BigDecimal) result[9]).doubleValue(),  // mileage
                ((BigDecimal) result[10]).doubleValue(),  // fuelConsumption
                ((BigDecimal) result[11]).doubleValue(),  // basePrice
                ((BigDecimal) result[12]).doubleValue(),  // deposit
                (String) result[13],  // description
                (String) result[14],  // termOfUse
                ((BigDecimal) result[15]).doubleValue(),  // carPrice
                (String) result[16],  // front
                (String) result[17],  // back
                (String) result[18],  // left
                (String) result[19],  // right
                (String) result[20],  // registration
                (String) result[21],  // certificate
                (String) result[22],  // insurance
                (Date) result[23],    // lastModified
                (Integer) result[24], // userId
                (Integer) result[25], // brandId
                (Integer) result[26], // statusId
                (Integer) result[27], // bookingStatusId
                (String) result[28],  // bookingStatusName
                Long.valueOf((Integer) result[29]) // booking Id
        );
        double totalPrice = returnCarService.calculateTotalPrice(carDto.getBookingId());

        User carOwner = userRepository.findById(Long.valueOf(carDto.getUserId())).get();

        double remainingMoney = carDto.getDeposit() - totalPrice;
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        String formattedMoney = formatter.format(remainingMoney);
        Optional<BookingStatus> confirmedStatusOptional = bookingStatusRepository.findById(1L);
        if (confirmedStatusOptional.isPresent()) {
            if (remainingMoney <= carOwner.getWallet().doubleValue()) {
                return Map.of(
                        "status", "success",
                        "message", "Please confirm to complete the booking. You need to return " + formattedMoney +
                                " VND of the remaining deposit. The amount will be deducted from your wallet."
                );
            } else {
                return Map.of("status", "error", "message", "Your wallet does not have enough balance for this payment.");
            }
        } else {
            System.out.println("Confirmed status not found.");
            return Map.of("status", "error", "message", "Car with Booking Status: Pending deposit is not found!");
        }

    }

    @Override
    public int confirmPaymentCar(Long carId, HttpSession session) {
        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 4);

        Object[] result = (Object[]) nestedArray[0];
        CarDto carDto = new CarDto(
                Long.valueOf((Integer) result[0]), // booking Id
                (String) result[1],   // name
                (String) result[2],   // licensePlate
                (String) result[3],   // model
                (String) result[4],   // color
                (Integer) result[5],  // seatNo
                (Integer) result[6],  // productionYear
                (String) result[7],   // transmission
                (String) result[8],   // fuel
                ((BigDecimal) result[9]).doubleValue(),  // mileage
                ((BigDecimal) result[10]).doubleValue(),  // fuelConsumption
                ((BigDecimal) result[11]).doubleValue(),  // basePrice
                ((BigDecimal) result[12]).doubleValue(),  // deposit
                (String) result[13],  // description
                (String) result[14],  // termOfUse
                ((BigDecimal) result[15]).doubleValue(),  // carPrice
                (String) result[16],  // front
                (String) result[17],  // back
                (String) result[18],  // left
                (String) result[19],  // right
                (String) result[20],  // registration
                (String) result[21],  // certificate
                (String) result[22],  // insurance
                (Date) result[23],    // lastModified
                (Integer) result[24], // userId
                (Integer) result[25], // brandId
                (Integer) result[26], // statusId
                (Integer) result[27], // bookingStatusId
                (String) result[28],  // bookingStatusName
                Long.valueOf((Integer) result[29]) // booking Id
        );
        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("User not logged in.");
            return 0;
        }

        // Lấy thông tin xe
        Optional<Car> carOptional = carRepository.findById(carId.intValue());
        if (carOptional.isEmpty()) {
            System.out.println("Car with ID " + carId + " not found.");
            return 0;
        }
        Car car = carOptional.get();

        // Kiểm tra trạng thái xe
        if (!car.getCarStatus().getStatusId().equals(11)) {
            System.out.println("Car is not in 'Booked' status.");
            return 0;
        }

        // Lấy booking liên quan
        Optional<BookingStatus> bookingStatusOptional = bookingStatusRepository.findById(4L);
        if (bookingStatusOptional.isEmpty()) {
            System.out.println("No booking with 'Pending payment' status for car ID " + carId + ".");
            return 0;
        }
        Optional<Booking> bookingOptional = rentalCarRepository.findById(carDto.getBookingId());

        Booking booking = bookingOptional.get();

        // Lấy thông tin carOwner và customer
        User carOwner = userRepository.getUserById(Long.valueOf(carDto.getUserId()));
        User customer = booking.getUser();

        // Cập nhật ví của carOwner và customer
        Double totalPrice = returnCarService.calculateTotalPrice(booking.getBookingId());
        Double deposit = car.getDeposit();
        Double remainingAmount = deposit - totalPrice;
        BigDecimal remainingMoney = BigDecimal.valueOf(remainingAmount);

        // Trừ tiền từ ví car owner
        BigDecimal updatedCustomerWallet = customer.getWallet().add(remainingMoney);
        customer.setWallet(updatedCustomerWallet);
        userRepository.save(customer);

        // Cộng tiền vào customer
        BigDecimal updatedCarOwnerWallet = carOwner.getWallet().subtract(remainingMoney);
        carOwner.setWallet(updatedCarOwnerWallet);
        userRepository.save(carOwner);

        // Update status xe thành "Available"
        Optional<CarStatus> availableStatusOptional = carStatusRepository.findById(1);
        if (availableStatusOptional.isEmpty()) {
            System.out.println("Car status 'Available' not found.");
            return 0;
        }
        CarStatus availableStatus = availableStatusOptional.get();
        car.setCarStatus(availableStatus);
        carRepository.save(car);

        Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findByName("Completed");
        if (completedStatusOptional.isEmpty()) {
            System.out.println("Booking status 'Completed' not found.");
            return 0;
        }
        BookingStatus completedStatus = completedStatusOptional.get();
        booking.setBookingStatus(completedStatus);
        bookingRepository.save(booking);

        // Gửi email thông báo
        emailService.sendPaymentConfirmation(
                customer,
                booking,
                carDto.getCarId().intValue(),
                car.getCarName(),
                remainingAmount
        );

        System.out.println("Payment confirmed and booking completed.");
        return 1; // Thành công
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



