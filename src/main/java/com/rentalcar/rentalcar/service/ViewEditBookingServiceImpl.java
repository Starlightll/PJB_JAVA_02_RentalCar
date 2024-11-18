package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.common.Constants;
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
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

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
    public MyBookingDto getBookingDetail(Integer bookingId, Integer carId, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("user not found");
        }
        Object[] obj = rentalCarRepository.findBookingDetail(user.getId(),carId,bookingId);
        if(obj == null || obj.length == 0){
            throw new RuntimeException("booking detail not found");
        }
        Object[] result = (Object[]) obj[0];

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
                (Integer) result[16],
                result[17] != null ? Long.valueOf((Integer) result[17]) : null
        );
        return bookingDto;
    }

    @Override
    public void updateBooking(BookingDto bookingDto, MultipartFile[] files, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Optional<DriverDetail> optionalDriverDetail  = driverDetailRepository.findById(bookingDto.getBookingId());
        DriverDetail driverDetail = optionalDriverDetail.orElse(null);

        if(driverDetail == null) {
            throw new RuntimeException();
        }

        String folderName = String.format("%s", user.getId());
        Path draftFolderPath = Paths.get("uploads/Driver/" + bookingDto.getBookingId() + "/Detail/", folderName);

        try {
            if (files[0] != null && !files[0].isEmpty() && files[0].getSize() > 0) {
                files[0].getSize();
                String storedPath = fileStorageService.storeFile(files[0], draftFolderPath, "drivingLicense." + getExtension(files[0].getOriginalFilename()));

                driverDetail.setDrivingLicense(storedPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        Booking booking = bookingRepository.findById(Long.valueOf(bookingDto.getBookingId())).orElseThrow(
                () -> new RuntimeException("Booking not found"));
        driverDetail.setBooking(booking);
        driverDetailRepository.save(driverDetail);

        booking.setLastModified(new Date());

//
//        //Update Driver
//        if (bookingDto.getIsCheck() && bookingDto.getSelectedUserId() != null) {
//            // Xử lý driver khi ấn tích
//            User driver = userRepository.getUserById(Long.valueOf(bookingDto.getSelectedUserId())); // Lấy đối tượng User của Driver
//            if (driver != null) {
//                booking.setDriver(driver);
//                bookingRepository.save(booking);
//
//                // THAY ĐỔI TRẠNG THÁI CHO DRIVER
//                driver.setStatusDriverId(2);
//                userRepository.save(driver);
//            }
//        }



    }
}
