package com.rentalcar.rentalcar.common;

public class TimeFormatter {
    public static String formatTime(int minutes) {
        int days = minutes / (24 * 60);         // Tính số ngày
        int hours = (minutes % (24 * 60)) / 60; // Tính số giờ còn lại
        int remainingMinutes = minutes % 60;    // Tính số phút còn lại

        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) {
            formattedTime.append(days).append(" day").append(days > 1 ? "s " : " ");
        }
        if (hours > 0) {
            formattedTime.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        }
        if (remainingMinutes > 0 || formattedTime.length() == 0) {
            formattedTime.append(remainingMinutes).append(" minute").append(remainingMinutes > 1 ? "s" : "");
        }

        return formattedTime.toString().trim();
    }

    public static void main(String[] args) {
        int minutes = 10; // Ví dụ: 8900 phút
        System.out.println(formatTime(minutes));
    }
}
