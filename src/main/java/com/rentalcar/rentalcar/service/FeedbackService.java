package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.FeedbackDto;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface FeedbackService {
    void createFeedback(FeedbackDto feedback);

    void showFeedback(FeedbackDto feedback);

    Double avgOfRating(HttpSession session);

    int[] getRatingCount(HttpSession session);

    List<FeedbackDto> findData(Integer rating,int page, int limit, HttpSession session);

    int getTotalPages(int rating, int limit, HttpSession session);
    long getTotalElements(int rating, HttpSession session);
}
