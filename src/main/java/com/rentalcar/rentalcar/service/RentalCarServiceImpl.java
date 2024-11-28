package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.CalculateNumberOfDays;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.rentalcar.rentalcar.common.Constants.FINE_COST;
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

        boolean isCustomer = user.getRoles().stream()
                .anyMatch(role -> "Customer".equals(role.getRoleName()));

        List<MyBookingDto> bookingDtos = new ArrayList<>();
        // Kiểm tra nếu sortBy là "lastModified", ánh xạ nó thành "b.lastModified"
        String mappedSortBy = sortBy.equalsIgnoreCase("lastModified") ? "b.lastModified" : sortBy;
        Sort.Direction sorDirection = order.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(sorDirection, mappedSortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Object[]> resultsPage;
        resultsPage = isCustomer ? rentalCarRepository.findAllWithPagination(user.getId(), pageable)
                : rentalCarRepository.findAllWithPaginationOfCarOwner(user.getId(), pageable);


        for (Object[] result : resultsPage.getContent()) {
            LocalDateTime startDate = ((Timestamp) result[2]).toLocalDateTime();
            LocalDateTime actualEndDate = ((Timestamp) result[5]).toLocalDateTime();
            LocalDateTime endDate = ((Timestamp) result[3]).toLocalDateTime();
            String bookingStatus = (String) result[11];
            double deposit = ((BigDecimal) result[10]).doubleValue();
            double totalPrice = ((BigDecimal) result[6]).doubleValue(); // total pric
            double baserice = ((BigDecimal) result[9]).doubleValue();
            double hourlyRate = baserice / 24;
            String lateTime = null; //muộn bao nhiêu ngày
            double fineLateTime = 0; //phí phạt
            double totalMoney = 0; //tiền phải thánh toán
            double returnDeposit = 0; //tiền phải hoàn trả
            double salaryDriver = 0; // lương lái xe nếu có
            double fineLateTimePerDay =baserice +((baserice * FINE_COST) / 100); //tiền phạt trên ngày
            double fineLateTimePerHour = fineLateTimePerDay / 24; //tiền phat trên giờ

            if (LocalDateTime.now().isBefore(startDate)) {
                returnDeposit = baserice;
            }

            Map<String, Long> map_numberOfDays = CalculateNumberOfDays.calculateNumberOfDays(startDate, endDate);
            if (bookingStatus.equalsIgnoreCase("Cancelled") || bookingStatus.equalsIgnoreCase("Completed") ||
                bookingStatus.equalsIgnoreCase("Pending payment") || bookingStatus.equalsIgnoreCase("Pending cancel")) {
                if(actualEndDate.isBefore(endDate)) { //kiểm tra xem actual date có nhỏ hơn enđate hay không
                    map_numberOfDays = CalculateNumberOfDays.calculateNumberOfDays(startDate, actualEndDate);
                }

//            tính late date
                Map<String, Long> numberOfDaysFine = CalculateNumberOfDays.calculateNumberOfDays(endDate, actualEndDate);
                if (CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate) != null) {
                    lateTime = CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate);
                    fineLateTime = CalculateNumberOfDays.calculateRentalFee(numberOfDaysFine, fineLateTimePerDay, fineLateTimePerHour);
                }
            } else if (LocalDateTime.now().isAfter(endDate)) {

                totalPrice = CalculateNumberOfDays.calculateRentalFee(map_numberOfDays, baserice, hourlyRate);
                if (CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate) != null) {
                    lateTime = CalculateNumberOfDays.calculateLateTime(endDate, LocalDateTime.now());
                    Map<String, Long> numberOfDaysFine = CalculateNumberOfDays.calculateNumberOfDays(endDate, LocalDateTime.now());// tổng số ngày quá hạn
                    fineLateTime = CalculateNumberOfDays.calculateRentalFee(numberOfDaysFine, fineLateTimePerDay,fineLateTimePerHour);// tổng số tiền phạt
                }
            }else if( bookingStatus.equalsIgnoreCase("In-Progress") ||
                    bookingStatus.equalsIgnoreCase("Pending return")){//Lấy động dữ liệu theo thời gian thực khi đang trong quá trình dùng xe
                map_numberOfDays = CalculateNumberOfDays.calculateNumberOfDays(startDate, LocalDateTime.now());
                Map<String, Long> numberOfDayActual = CalculateNumberOfDays.calculateNumberOfDays(startDate, LocalDateTime.now());// tổng số ngày thực
                totalPrice = CalculateNumberOfDays.calculateRentalFee(numberOfDayActual,baserice,  hourlyRate);

            } else {
                totalPrice = CalculateNumberOfDays.calculateRentalFee(map_numberOfDays, baserice, hourlyRate);
            }

            Map<String, Double> map_amount = calculateAmountToPay(startDate, endDate, totalPrice, deposit, fineLateTime);
            totalMoney = map_amount.get("totalMoney");
            returnDeposit = map_amount.get("returnDeposit");
            String str_numberOfDays = map_numberOfDays.get("days") + " d " + map_numberOfDays.get("hours") + " h ";

            MyBookingDto bookingDto = new MyBookingDto(
                    Long.valueOf((Integer) result[0]),
                    (String) result[1],
                    ((Timestamp) result[2]).toLocalDateTime(), //start date
                    ((Timestamp) result[3]).toLocalDateTime(), //end date
                    (String) result[4], // driverInfo
                    ((Timestamp) result[5]).toLocalDateTime(),//actualEndDate
                    totalPrice, // total price
                    Long.valueOf((Integer) result[7]), //userId
                    str_numberOfDays, //numberOfDays
                    (Integer) result[8], //paymentMethod
                    ((BigDecimal) result[9]).doubleValue(), // basePrice
                    ((BigDecimal) result[10]).doubleValue(), // deposit
                    (String) result[11], //bookingStatus
                    (String) result[12],
                    (String) result[13],
                    (String) result[14],
                    (String) result[15],
                    (Integer) result[16],
                    lateTime,
                    fineLateTime,
                    returnDeposit,
                    totalMoney
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
                Optional<BookingStatus> cancelledStatusOptional = bookingStatusRepository.findById(7L);
                double totalPriceCanceled = calculateTotalCancel(booking.getStartDate(), booking.getEndDate(), booking.getTotalPrice());
                if (cancelledStatusOptional.isPresent()) {
                    BookingStatus cancelledStatus = cancelledStatusOptional.get();
                    booking.setBookingStatus(cancelledStatus); // Update the status of the booking
                    booking.setLastModified(new Date());
                    booking.setTotalPrice(totalPriceCanceled);
                    booking.setActualEndDate(LocalDateTime.now());

                    // Save the updated booking
                    rentalCarRepository.save(booking);

                    //================Thay đổi trạng thái tài xế======================
                    Long DriverId = null;
                    if (booking.getDriver() != null && booking.getDriver().getId() != null) {
                        DriverId = booking.getDriver().getId();
                    }
                    if (DriverId != null) {
                        User Driver = userRepository.getUserById(DriverId);
                        Driver.setStatus(UserStatus.ACTIVATED);
                        userRepository.save(Driver);
                    }
                    //==========================================

                    // Update status xe thành "Pending cancel"
                    Optional<CarStatus> availableStatusOptional = carStatusRepository.findById(15);
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
                    booking.setLastModified(new Date());
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
        Car car = carRepository.getCarByCarId(bookingDto.getCarID());
        User user = (User) session.getAttribute("user");
        User customer = userRepository.getUserById(user.getId());
        String normalizedPhone = phoneNumberStandardService.normalizePhoneNumber(bookingDto.getRentPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);

//        if(userRepository.getUserByEmail(customer.getEmail()) != null) {
//            throw new RuntimeException("Email already exists");
//        }

        if (!Objects.equals(normalizedPhone, customer.getPhone()) && phoneNumberStandardService.isPhoneNumberExists(bookingDto.getRentPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE)) {
            throw new RuntimeException("Phone number already exists");
        }

        //============CHỌN VÍ ĐỂ TRẢ CỌC============================
        BigDecimal myWallet = customer.getWallet() != null ? customer.getWallet() : BigDecimal.ZERO; // VÍ CỦA CUSTOMER
        User carOwner = userRepository.getUserById(car.getUser().getId());
        BigDecimal deposit = new BigDecimal(bookingDto.getDeposit());

        if (bookingDto.getSelectedPaymentMethod() != 1) {// CHỌN PHƯƠNG THỨC THANH TOÁN KHÁC
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
                customer.setDrivingLicense(storedPath);
            } else {
                customer.setDrivingLicense(user.getDrivingLicense());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Lưu vô db
        try {

            //Lưu booking
            Map<String, Long> rentalInfo = CalculateNumberOfDays.calculateNumberOfDays(bookingDto.getPickUpDate(), bookingDto.getReturnDate());
            double dailyRate = car.getBasePrice();  // Giá thuê xe 1 ngày
            double hourlyRate = dailyRate / 24;  // Giá thuê xe 1 giờ
            Double totalPrice = CalculateNumberOfDays.calculateRentalFee(rentalInfo, dailyRate, hourlyRate);
            String fullName = normalizeFullName(bookingDto.getRentFullName());

            booking.setStartDate(bookingDto.getPickUpDate());
            booking.setEndDate(bookingDto.getReturnDate());
            booking.setDriverInfo(fullName);
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
            booking.setLastModified(new Date());
            bookingRepository.save(booking);

            //Lưu booking car
            BookingCar bookingCar = new BookingCar();
            bookingCar.setBookingId(booking.getBookingId());
            bookingCar.setCarId(Long.valueOf(bookingDto.getCarID()));
            bookingCarRepository.save(bookingCar);

            calculateAndDeductDeposit(booking, customer, carOwner, myWallet, deposit, session);  //XỬ LÝ TIỀN TRONG CỌC


            //Lưu thông tin người thuê xe
            customer.setFullName(fullName);
            customer.setEmail(bookingDto.getRentMail());
            customer.setPhone(normalizedPhone);
            customer.setNationalId(bookingDto.getRentNationalId());
            customer.setDob(bookingDto.getRentBookPickDate());
            customer.setCity(bookingDto.getRentProvince().trim());
            customer.setDistrict(bookingDto.getRentDistrict().trim());
            customer.setStreet(bookingDto.getRentStreet().trim());
            customer.setWard(bookingDto.getRentWard().trim());
            userRepository.save(customer);

            //NẾU NGƯỜI DÙNG THUÊ LÁI XE
            if (bookingDto.getIsCheck() && bookingDto.getSelectedUserId() != null) {
                // Xử lý driver khi ấn tích
                User driver = userRepository.getUserById(Long.valueOf(bookingDto.getSelectedUserId())); // Lấy đối tượng User của Driver
                if (driver != null) {
                    booking.setDriver(driver);
                    bookingRepository.save(booking);

                    // THAY ĐỔI TRẠNG THÁI CHO DRIVER
                    driver.setStatus(UserStatus.RENTED);
                    userRepository.save(driver);
                    //MAIL TO DRIVER
                    emailService.sendBookingNotificationToDriver(driver, booking, car.getCarId(), car.getCarName(), booking.getStartDate(), booking.getEndDate(), customer);
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
            booking.setLastModified(new Date());
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
        // Gửi email thông báo
        emailService.sentNotiConfirmedBooking(
                booking.getUser(),
                booking,
                car
        );

        System.out.println("Car and Booking successfully updated for booking ID: " + bookingId);
        return true;
    }


    @Override
    public Map<String, String> checkReturnCar(Long carId, HttpSession session) {

        User user = (User) session.getAttribute("user");

        // Check if the user is logged in
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to confirm Car with ID: " + carId);

        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 8);

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


        //============================TÍNH TOTAL PRICE =================================================================
        double totalPrice = returnCarService.calculateTotalPriceForActualEnddateCarOwner(carDto.getBookingId());

        //==============================================================================================================

        User carOwner = userRepository.findById(Long.valueOf(carDto.getUserId())).get();

        double remainingMoney = Math.abs(carDto.getDeposit() - totalPrice);
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        String formattedMoney = formatter.format(remainingMoney);
        Optional<BookingStatus> confirmedStatusOptional = bookingStatusRepository.findById(1L);
        if (confirmedStatusOptional.isPresent()) {
            if (totalPrice > carDto.getDeposit()) {
                return Map.of(
                        "status", "success",
                        "message", "The customer will pay an amount of " + formattedMoney +
                                " VND to complete this booking. \nPress OK and wait for customer to pay!"
                );
            } else if (totalPrice == carDto.getDeposit()) {
                return Map.of(
                        "status", "success",
                        "message", "The deposit amount equals the amount the customer needs to pay. \nPlease press OK to complete this booking."
                );
            } else {

                if (remainingMoney <= carOwner.getWallet().doubleValue()) {
                    return Map.of(
                            "status", "success",
                            "message", "Please confirm to complete the booking. You need to return " + formattedMoney +
                                    " VND of the remaining deposit. The amount will be deducted from your wallet."
                    );
                } else {
                    return Map.of("status", "error", "message", "Your wallet does not have enough balance for this payment.");
                }
            }
        } else {
            System.out.println("Pending return status not found.");
            return Map.of("status", "error", "message", "Car with Booking Status: Pending return not found!");
        }

    }

    @Override
    public int confirmReturnCar(Long carId, HttpSession session) {
        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 8);

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

        // check car status
        if (!car.getCarStatus().getStatusId().equals(16)) {
            System.out.println("Car is not in 'Pending return' status.");
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
        User driver = booking.getDriver();

        Double totalPrice = returnCarService.calculateTotalPriceForActualEnddateCarOwner(booking.getBookingId());
        Double deposit = car.getDeposit();
        double remainingAmount = Math.abs(deposit - totalPrice);
        BigDecimal remainingMoney = BigDecimal.valueOf(remainingAmount);
        LocalDateTime currentTime = LocalDateTime.now();


        if (totalPrice < carDto.getDeposit()) {

            // Cộng tiền vào customer
            BigDecimal updatedCustomerWallet = customer.getWallet().add(remainingMoney);
            customer.setWallet(updatedCustomerWallet);
            userRepository.save(customer);
            transactionService.saveTransaction(customer, remainingMoney, TransactionType.OFFSET_FINAL_BACK_REMAIN_DEPOSIT, booking);

            // Trừ tiền từ ví car owner
            BigDecimal updatedCarOwnerWallet = carOwner.getWallet().subtract(remainingMoney);
            carOwner.setWallet(updatedCarOwnerWallet);
            userRepository.save(carOwner);
            transactionService.saveTransaction(carOwner, remainingMoney, TransactionType.OFFSET_FINAL_PAYMENT_BACK_DEPOSIT, booking);

            // Update status xe thành "Available"
            Optional<CarStatus> availableStatusOptional = carStatusRepository.findById(1);
            if (availableStatusOptional.isEmpty()) {
                System.out.println("Car status 'Available' not found.");
                return 0;
            }
            CarStatus availableStatus = availableStatusOptional.get();
            car.setCarStatus(availableStatus);
            carRepository.save(car);

            Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(5L);
            if (completedStatusOptional.isEmpty()) {
                System.out.println("Booking status 'Completed' not found.");
                return 0;
            }
            BookingStatus completedStatus = completedStatusOptional.get();
            booking.setBookingStatus(completedStatus);
            booking.setLastModified(new Date());
            booking.setActualEndDate(currentTime);
            booking.setTotalPrice(totalPrice);
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
            return 1;
        } else {
            // Update status xe thành "Pending payment"
            Optional<CarStatus> availableStatusOptional = carStatusRepository.findById(1);
            if (availableStatusOptional.isEmpty()) {
                System.out.println("Car status 'Available' not found.");
                return 0;
            }
            CarStatus availableStatus = availableStatusOptional.get();
            car.setCarStatus(availableStatus);
            carRepository.save(car);

            Optional<BookingStatus> completedStatusOptional = bookingStatusRepository.findById(4L);
            if (completedStatusOptional.isEmpty()) {
                System.out.println("Booking status 'Pending payment' not found.");
                return 0;
            }
            BookingStatus completedStatus = completedStatusOptional.get();
            booking.setBookingStatus(completedStatus);
            booking.setLastModified(new Date());
            booking.setActualEndDate(currentTime);
            booking.setTotalPrice(totalPrice);
            bookingRepository.save(booking);

            emailService.sendPaymentConfirmation(
                    customer,
                    booking,
                    carDto.getCarId().intValue(),
                    car.getCarName(),
                    remainingAmount
            );

            System.out.println("Pending customer confirm payment!");
            return 2;
        }

    }

    @Override
    public boolean confirmCancelBookingCar(Long carId, HttpSession session) {
        User user = (User) session.getAttribute("user");

        // Check if the user is logged in
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        System.out.println("Attempting to confirm cancel Car with ID: " + carId);

        // Fetch the car and booking details with "Pending deposit" status
        Object[] nestedArray = carRepository.findCarAndBookingByCarId(carId, 7);

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

        // Check the booking status and booking ID
        String bookingStatusName = carDto.getBookingStatusName();
        if (!"Pending cancel".equals(bookingStatusName)) {
            System.out.println("Booking status is not 'Pending cancel'.");
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


        // Update the booking status to "Cancelled"
        Optional<BookingStatus> confirmedStatusOptional = bookingStatusRepository.findById(6L);
        if (confirmedStatusOptional.isPresent()) {
            BookingStatus confirmedStatus = confirmedStatusOptional.get();
            booking.setBookingStatus(confirmedStatus);
            booking.setLastModified(new Date());
            bookingRepository.save(booking);
        } else {
            System.out.println("Cancelled status not found.");
            return false; // If the "Confirmed" status is not found, return false
        }

        // Update the car status to "Available"
        Car car = carRepository.getCarByCarId(carDto.getCarId().intValue());
        Optional<CarStatus> bookedStatusOptional = carStatusRepository.findById(1);
        if (bookedStatusOptional.isPresent()) {
            CarStatus bookedStatus = bookedStatusOptional.get();
            car.setCarStatus(bookedStatus);
            carRepository.save(car);
        } else {
            System.out.println("Booked status not found.");
            return false; // If the "Booked" status is not found, return false
        }

        // =============== Cập nhật ví của carOwner và customer ===============
        Double deposit = car.getDeposit();
        BigDecimal depositAmount = BigDecimal.valueOf(deposit);
        // trừ tiền vào car owner
        BigDecimal updatedCustomerWallet = user.getWallet().subtract(depositAmount);
        user.setWallet(updatedCustomerWallet);
        userRepository.save(user);
        transactionService.saveTransaction(user, depositAmount, TransactionType.REFUND_DEPOSIT, booking);

        // cộng tiền từ ví customer
        User customer = booking.getUser();
        BigDecimal updatedCarOwnerWallet = customer.getWallet().add(depositAmount);
        customer.setWallet(updatedCarOwnerWallet);
        userRepository.save(customer);
        transactionService.saveTransaction(customer, depositAmount, TransactionType.RECEIVE_DEPOSIT, booking);
        System.out.println("Booking with ID " + bookingId + " has been successfully cancelled.");

        // Gửi email thông báo
        emailService.sendConfirmCancelBooking(
                customer,
                booking,
                car
        );

        System.out.println("Car and Booking successfully updated for booking ID: " + bookingId);
        return true;
    }

    @Override
    public List<MyBookingDto> getRentalsNearEndDate() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(3); // Nhắc trước 3 ngày

        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
        List<MyBookingDto> bookingDtos = new ArrayList<>();
        List<Object[]> results  = bookingRepository.findByEndDateBetween(startOfDay,endOfDay);
        if(results  == null || results.isEmpty()){
            throw new RuntimeException("No bookings found within the given date range.");
        }

        for (Object[] result : results) {

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
            bookingDtos.add(bookingDto);
        }
        return bookingDtos;
    }


    private void calculateAndDeductDeposit(Booking booking, User customer, User carOwner,
                                           BigDecimal myWallet, BigDecimal deposit, HttpSession session) {
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


    public String normalizeFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name is required");
        }

        String[] words = fullName.trim().split("\\s+");
        StringBuilder normalized = new StringBuilder();

        for (String word : words) {
            normalized.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        return normalized.toString().trim();
    }


    public double calculateTotalCancel(LocalDateTime start, LocalDateTime end, double currentTotal) {
        LocalDateTime timeNow = LocalDateTime.now();
        if (timeNow.isBefore(start)) {
            return 0;
        }

        if (timeNow.isBefore(end)) {
            return currentTotal;
        }

        return 0;

    }

    public Map<String, Double> calculateAmountToPay(LocalDateTime startDate, LocalDateTime endDate,  double totalPrice, double deposit, double fineLateTime) {
        Map<String, Double> result = new HashMap<>();
        result.put("totalMoney", 0D);
        result.put("returnDeposit", 0D);

        if(LocalDateTime.now().isBefore(startDate) || LocalDateTime.now().isBefore(endDate)) {
            if(totalPrice > deposit) {
                result.put("totalMoney", totalPrice - deposit);
                return result;
            }
            result.put("returnDeposit",  deposit - totalPrice);
            return result;
        }
        if(LocalDateTime.now().isAfter(endDate)) {
            if(totalPrice > deposit) {
                result.put("totalMoney", (totalPrice - deposit) + fineLateTime);
                return result;
            }

            double total =  totalPrice + fineLateTime; // số tiền khi tiền phạt mà cộng với total
            if(total > deposit) {
                result.put("totalMoney", total - deposit);
                return result;
            }
            result.put("returnDeposit",  deposit - total);
            return result;
        }

        return result;
    }
}



