package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class RevenueServiceImp implements RevenueService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Map<String, Double> getWeeklyRevenue(Long userId, int year, int month, int week) {
        Map<String, Double> weeklyRevenue = new LinkedHashMap<>();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int maxWeeks = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())
                .get(weekFields.weekOfMonth());

        if (week < 1 || week > maxWeeks) {
            return weeklyRevenue;
        }

        LocalDate startOfWeek = firstDayOfMonth
                .with(weekFields.weekOfMonth(), week)
                .with(weekFields.dayOfWeek(), 1);

        LocalDate endOfWeek = startOfWeek.plusDays(6);
        if (endOfWeek.getMonthValue() != month) {
            endOfWeek = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());
        }

        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            Double revenue = bookingRepository.calculateRevenueByCarOwnerAndDate(userId, startOfDay, endOfDay);
            weeklyRevenue.put(date.getDayOfWeek().name(), revenue != null ? revenue : 0.0);
        }

        return weeklyRevenue;
    }

    @Override
    public Map<String, Double> getMonthlyRevenue(Long userId, int year, int month) {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        int daysInMonth = startOfMonth.lengthOfMonth();

        // Fetch revenue per day in the month
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate day = startOfMonth.plusDays(i - 1);
            LocalDateTime startOfDay = day.atStartOfDay();
            LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
            Double revenue = bookingRepository.calculateRevenueByCarOwnerAndDate(userId, startOfDay, endOfDay);
            monthlyRevenue.put("Day " + i, revenue != null ? revenue : 0.0);
        }

        return monthlyRevenue;
    }

    @Override
    public Map<String, Double> getYearlyRevenue(Long userId, int year) {
        Map<String, Double> yearlyRevenue = new LinkedHashMap<>();

        // Fetch revenue per month in the year
        for (int i = 1; i <= 12; i++) {
            LocalDate startOfMonth = LocalDate.of(year, i, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
            LocalDateTime startOfDay = startOfMonth.atStartOfDay();
            LocalDateTime endOfDay = endOfMonth.plusDays(1).atStartOfDay();
            Double revenue = bookingRepository.calculateRevenueByCarOwnerAndDate(userId, startOfDay, endOfDay);
            yearlyRevenue.put(startOfMonth.getMonth().name(), revenue != null ? revenue : 0.0);
        }

        return yearlyRevenue;
    }
}
