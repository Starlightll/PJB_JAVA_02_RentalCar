package com.rentalcar.rentalcar.service;

import com.rentalcar.rentalcar.dto.FeedbackDto;
import com.rentalcar.rentalcar.entity.Booking;
import com.rentalcar.rentalcar.entity.Feedback;
import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.repository.BookingRepository;
import com.rentalcar.rentalcar.repository.RatingStarRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {


    @Autowired
    RatingStarRepository ratingStarRepo;

    @Autowired
    BookingRepository bookingRepository;

    @Override
    public void createFeedback(FeedbackDto feedbackDto) {
        Feedback feedback = new Feedback();

        feedback.setRating(feedbackDto.getRating());
        feedback.setContent(feedbackDto.getContent());
        feedback.setDateTime(LocalDateTime.now());

        Booking booking = bookingRepository.findById(feedbackDto.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        feedback.setBooking(booking);

        Feedback savedFeedback = ratingStarRepo.save(feedback);
        if (savedFeedback.getId() != null) {
            System.out.println("Feedback saved successfully with ID: " + savedFeedback.getId());
        } else {
           throw new RuntimeException("Failed to save feedback.");
        }
    }

    @Override
    public void showFeedback(FeedbackDto feedback) {

    }

    @Override
    public Double avgOfRating(HttpSession session) {

        User user = (User) session.getAttribute("user");
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        Double avg = ratingStarRepo.getAvgRatingByUserId(user.getId());
        return avg;
    }

    @Override
    public int[] getRatingCount(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        List<Object[]> ratingCounts = ratingStarRepo.getRatingCountByUserId(user.getId());

        int[] counts = new int[5];

        for(Object[] ratingCount : ratingCounts) {
            int rating = (int) ratingCount[0];
            int count = (int) ratingCount[1];
            counts[rating - 1] = count;
        }
        return counts;
    }

    @Override
    public List<FeedbackDto> findData(Integer rating,int page, int limit, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, limit); // ho tro phan trang
        Page<Object[]> resultsPage;

        if (rating != 0) {
            resultsPage = ratingStarRepo.findByRatingWithPagination(user.getId(), rating, pageable);
        } else {
            resultsPage = ratingStarRepo.findAllWithPagination(user.getId(), pageable);
        }


            // Chuyển đổi Object[] thành FeedbackDto
            for (Object[] result : resultsPage.getContent()) {
                FeedbackDto feedbackDto = new FeedbackDto(
                        (String) result[0],    // content
                        ((Timestamp) result[1]).toLocalDateTime(),  // dateTime
                        (Integer) result[2],   // rating
                        Long.valueOf((Integer) result[3]),      // bookingId
                        ((Timestamp) result[4]).toLocalDateTime(),
                        ((Timestamp) result[5]).toLocalDateTime(),
                        Long.valueOf((Integer) result[6]),      // userId
                        (String) result[7],    // frontImage
                        (String) result[8],   // carName
                        (String) result[9]
                );
                feedbackDtos.add(feedbackDto);
            }

        return feedbackDtos;

    }



    public int getTotalPages(int rating, int limit, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        long totalElements = (rating != 0)
                ? ratingStarRepo.countByRating(user.getId(), rating)
                : ratingStarRepo.countByUser(user.getId());
        return (int) Math.ceil((double) totalElements / limit);
    }

    public long getTotalElements(int rating, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return (rating != 0)
                ? ratingStarRepo.countByRating(user.getId(), rating)
                : ratingStarRepo.countByUser(user.getId());
    }

}
