package com.rentalcar.rentalcar.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CalculateNumberOfDays {
    public static void main(String[] args) {
        // Ví dụ: thời gian bắt đầu và kết thúc
//        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 24, 2, 0);  // 9:00 AM
//        LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 26, 0, 0);
        //1,916,667 || 1916666.6666666665
        LocalDateTime startDateTime = LocalDateTime.of(2024, 11, 24, 2, 0);  // 9:00 AM
        LocalDateTime endDateTime = LocalDateTime.of(2024, 11, 24, 5, 0);   // 2:30 PM
        //125.000 || 125000
        // Tính số ngày và giờ
        Map<String, Long> rentalInfo = calculateNumberOfDays(startDateTime, endDateTime);

        // Giả sử giá thuê xe theo ngày và giờ
        double dailyRate = 1000000;  // Giá thuê xe 1 ngày
        double hourlyRate = dailyRate / 24;  // Giá thuê xe 1 giờ

        // Tính phí thuê
        double totalFee = calculateRentalFee(rentalInfo, dailyRate, hourlyRate);

        System.out.println("Total rental fee: " + totalFee + " VND");
    }

    // Hàm tính số ngày và giờ
    public static Map<String, Long> calculateNumberOfDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Sử dụng Duration để tính sự khác biệt
        Duration duration = Duration.between(startDateTime, endDateTime);

        long diffInMillis = duration.toMillis();

        Map<String, Long> result = new HashMap<>();

        if (diffInMillis <= 0) {
            result.put("days", 0L);
            result.put("hours", 0L);
            return result;
        }

        // Tính số ngày và giờ
        long diffDays = duration.toDays();
        long diffHours = (duration.toHours() % 24);

        result.put("days", diffDays);
        result.put("hours", diffHours);

        return result;
    }

    // Hàm tính phí thuê
    public static double calculateRentalFee(Map<String, Long> rentalInfo, double dailyRate, double hourlyRate) {
        long days = rentalInfo.get("days");
        long hours = rentalInfo.get("hours");

        double totalFee = (days * dailyRate) + (hours * hourlyRate);


        if (days == 0 && hours > 0) {
            totalFee = hourlyRate * hours;
        } else if (days == 0 && hours == 0) {
            totalFee = 0;
        }

        return totalFee;
    }


    public static String calculateLateTime(LocalDateTime endDate, LocalDateTime lateDate) {

        if (endDate.isBefore(lateDate)) {

            Duration duration = Duration.between(endDate, lateDate);


            long lateDays = duration.toDays();

            long lateHours = duration.toHours() % 24;

            return "Late " + lateDays + " days " + lateHours + " h";
        }

        return null;
    }

}
