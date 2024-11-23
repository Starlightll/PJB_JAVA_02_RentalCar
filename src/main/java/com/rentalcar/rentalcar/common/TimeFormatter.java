package com.rentalcar.rentalcar.common;

public class TimeFormatter {

    private TimeFormatter() {
    }

    public static String formatTime(int minutes) {
        int days = minutes / (24 * 60);         // Calculate number of days
        int hours = (minutes % (24 * 60)) / 60; // Calculate number of hours
        int remainingMinutes = minutes % 60;    // Calculate number of minutes

        StringBuilder formattedTime = new StringBuilder();
        if (days > 0) {
            formattedTime.append(days).append(" day").append(days > 1 ? "s " : " ");
        }
        if (hours > 0) {
            formattedTime.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        }
        if (remainingMinutes > 0 || formattedTime.isEmpty()) {
            formattedTime.append(remainingMinutes).append(" minute").append(remainingMinutes > 1 ? "s" : "");
        }

        return formattedTime.toString().trim();
    }
}
