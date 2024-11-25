package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.CalculateNumberOfDays;
import com.rentalcar.rentalcar.common.Constants;
import com.rentalcar.rentalcar.common.UserStatus;
import com.rentalcar.rentalcar.dto.BookingDto;
import com.rentalcar.rentalcar.dto.MyBookingDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Car;
import com.rentalcar.rentalcar.entity.DriverDetail;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.rentalcar.rentalcar.common.Constants.FINE_COST;
import static org.apache.commons.io.FilenameUtils.getExtension;

@Service
public class ViewEditBookingServiceImpl implements ViewEditBookingService{
    @Autowired
    RentalCarRepository rentalCarRepository;
    @Autowired
    private DriverDetailRepository driverDetailRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private PhoneNumberStandardService phoneNumberStandardService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepo userRepository;


    @Override
    public MyBookingDto getBookingDetail(Integer bookingId, Integer carId, HttpSession session, Integer userId) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        Object[] obj = rentalCarRepository.findBookingDetail(Long.valueOf(userId),carId,bookingId);
        if(obj == null || obj.length == 0){
            throw new RuntimeException("booking detail not found");
        }
        Object[] result = (Object[]) obj[0];

        LocalDateTime startDate = ((Timestamp) result[2]).toLocalDateTime();
        LocalDateTime actualEndDate = ((Timestamp) result[5]).toLocalDateTime();
        LocalDateTime endDate = ((Timestamp) result[3]).toLocalDateTime();
        double deposit = ((BigDecimal) result[10]).doubleValue();
        String bookingStatus = (String) result[11];
        double totalPrice = ((BigDecimal) result[6]).doubleValue(); // total pric
        double basprice = ((BigDecimal) result[9]).doubleValue();
        double hourlyRate = basprice / 24;
        String lateTime = null; //muộn bao nhiêu ngày
        double fineLateTime = 0; //phí phạt
        double totalMoney = 0; //tiền phải thánh toán
        double returnDeposit = 0; //tiền phải hoàn trả
        double fineLateTimePerDay = (basprice * FINE_COST) / 100; //tiền phạt trên ngày
        double fineLateTimePerHour = fineLateTimePerDay / 24; //tiền phat trên giờ

        Map<String, Long> map_numberOfDays = CalculateNumberOfDays.calculateNumberOfDays(startDate, endDate);
        //Lấy dữ liệu trong db
        if(bookingStatus.equalsIgnoreCase("Cancelled") || bookingStatus.equalsIgnoreCase("Completed") ||
                bookingStatus.equalsIgnoreCase("Pending payment") || bookingStatus.equalsIgnoreCase("Pending cancel")) {
            Map<String, Long> numberOfDaysFine = CalculateNumberOfDays.calculateNumberOfDays(endDate, actualEndDate);
        //  tính late date
            if(CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate) != null) { // Lấy từ db
                lateTime = CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate);
                fineLateTime = CalculateNumberOfDays.calculateRentalFee(numberOfDaysFine, fineLateTimePerDay,fineLateTimePerHour);
            }
        }
        else if(LocalDateTime.now().isAfter(endDate)) { //Lấy động dữ liệu theo thời gian thực khi bị phạt

            Map<String, Long> numberOfDayActual = CalculateNumberOfDays.calculateNumberOfDays(startDate, LocalDateTime.now());// tổng số ngày thực
            totalPrice = CalculateNumberOfDays.calculateRentalFee(numberOfDayActual,basprice,  hourlyRate);
            if(CalculateNumberOfDays.calculateLateTime(endDate, actualEndDate) != null) {
                lateTime = CalculateNumberOfDays.calculateLateTime(endDate, LocalDateTime.now());
                Map<String, Long> numberOfDaysFine = CalculateNumberOfDays.calculateNumberOfDays(endDate, LocalDateTime.now());// tổng số ngày quá hạn
                fineLateTime = CalculateNumberOfDays.calculateRentalFee(numberOfDaysFine, fineLateTimePerDay,fineLateTimePerHour);// tổng số tiền phạt
            }
        } else {// còn lại
            totalPrice = CalculateNumberOfDays.calculateRentalFee(map_numberOfDays,basprice,  hourlyRate);
        }
        Map<String, Double> map_amount = calculateAmountToPay(startDate, endDate, totalPrice, deposit, fineLateTime);
        totalMoney = map_amount.get("totalMoney");
        returnDeposit = map_amount.get("returnDeposit");

        String str_numberOfDays = map_numberOfDays.get("days") + " days " + map_numberOfDays.get("hours") + " h ";



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
                result[17] != null ? Long.valueOf((Integer) result[17]) : null,
                lateTime,
                fineLateTime,
                returnDeposit,
                totalMoney
        );
        return bookingDto;
    }

    @Override
    public void updateBooking(BookingDto bookingDto, MultipartFile[] files, HttpSession session) {

        User user = (User) session.getAttribute("user");
        User customer = userRepository.getUserById(user.getId());
        String normalizedPhone = phoneNumberStandardService.normalizePhoneNumber(bookingDto.getRentPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if(!Objects.equals(normalizedPhone, customer.getPhone()) && phoneNumberStandardService.isPhoneNumberExists(bookingDto.getRentPhone(), Constants.DEFAULT_REGION_CODE, Constants.DEFAULT_COUNTRY_CODE)) {
            throw new RuntimeException("Phone number already exists");
        }




        String folderName = String.format("%s", user.getId());
        Path draftFolderPath = Paths.get("uploads/Driver/" + bookingDto.getBookingId() + "/Detail/", folderName);

        try {
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                files[0].getSize();
                String storedPath = fileStorageService.storeFile(files[0], draftFolderPath, "drivingLicense." + getExtension(files[0].getOriginalFilename()));

                customer.setDrivingLicense(storedPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Lưu thông tin người thuê xe
        String fullName = normalizeFullName(bookingDto.getRentFullName());
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


        Booking booking = bookingRepository.findById(Long.valueOf(bookingDto.getBookingId())).orElseThrow(
                () -> new RuntimeException("Booking not found"));

        booking.setLastModified(new Date());
        bookingRepository.save(booking);

//
//        //Update Driver
        if (bookingDto.getIsCheck() && bookingDto.getSelectedUserId() != null) {
            // Xử lý driver khi ấn tích

            Long oldDriverId = null;
            if (booking.getDriver() != null && booking.getDriver().getId() != null) {
                oldDriverId = booking.getDriver().getId();
            }
            if(oldDriverId != null){
                User oldDriver = userRepository.getUserById(oldDriverId);
                oldDriver.setStatus(UserStatus.ACTIVATED);
                userRepository.save(oldDriver);
            }

            User driver = userRepository.getUserById(Long.valueOf(bookingDto.getSelectedUserId())); // Lấy đối tượng User của Driver
            if (driver != null) {

                booking.setDriver(driver);
                bookingRepository.save(booking);

                // THAY ĐỔI TRẠNG THÁI CHO DRIVER
                driver.setStatus(UserStatus.RENTED);
                userRepository.save(driver);
            }
        }



    }
//    public int calculateNumberOfDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
//        // Chuyển đổi LocalDateTime sang mili-giây (epoch milli)
//        long startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        long endMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//
//        // Tính chênh lệch thời gian (mili-giây)
//        long timeDiff = endMillis - startMillis;
//
//        // Chia để tính số ngày và làm tròn lên
//        return (int) Math.ceil(timeDiff / (1000.0 * 3600 * 24));
//    }

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


    public Map<String, Double> calculateAmountToPay(LocalDateTime startDate, LocalDateTime endDate,  double totalPrice, double deposit, double fineLateTime) {
        Map<String, Double> result = new HashMap<>();
        result.put("totalMoney", 0D);
        result.put("returnDeposit", 0D);

        if(LocalDateTime.now().isBefore(startDate)) {
            if(totalPrice > deposit) {
                result.put("totalMoney", totalPrice - deposit);
                return result;
            }
            result.put("returnDeposit",  deposit - totalPrice);
            return result;
        }

        if(LocalDateTime.now().isBefore(endDate)) {
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
