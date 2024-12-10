package com.rentalcar.rentalcar.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface RevenueService {
    Map<String, Double> getWeeklyRevenue(Long userId, int year, int month, int week);
    Map<String, Double> getMonthlyRevenue(Long userId, int year, int month);
    Map<String, Double> getYearlyRevenue(Long userId, int year);
}
