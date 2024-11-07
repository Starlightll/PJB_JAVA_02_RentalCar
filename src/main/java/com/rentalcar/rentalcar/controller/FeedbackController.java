package com.rentalcar.rentalcar.controller;


import com.rentalcar.rentalcar.dto.FeedbackDto;
import com.rentalcar.rentalcar.entity.Feedback;
import com.rentalcar.rentalcar.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/homepage-customer")
public class FeedbackController {

    @Autowired FeedbackService feedbackService;

    @GetMapping("/rating-star")
    public String ratingStar(@RequestParam("bookingId") Long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("feedbackDto", new FeedbackDto());
        return "feedback/ratingStar";
    }

    @PostMapping("/rating-star")
    @ResponseBody
    public ResponseEntity<Map<String, String>> handelRatingStar(@Valid @ModelAttribute FeedbackDto feedbackDto, BindingResult  result, HttpSession session) {
        Map<String, String> response = new HashMap<>();

        if(result.hasErrors()) {
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        try{
            feedbackService.createFeedback(feedbackDto);
            response.put("success", "Feedback successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            switch (e.getMessage()) {
                case "Booking not found": ;
                    result.rejectValue("booking", "error.booking", "Booking not found");
                    break;

            }
            result.getFieldErrors().forEach(error -> response.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

    }

    @GetMapping("/my-feedback")
    public String myFeedback(Model model, HttpSession session) {

        Double avg = feedbackService.avgOfRating(session);
        int[] counts = feedbackService.getRatingCount(session);


        model.addAttribute("counts", counts);
        model.addAttribute("avg", avg);


        return "feedback/MyFeedback";
    }


    @GetMapping("/my-feedback/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFeedbackData(
            HttpSession session,
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Integer rating) {
        List<FeedbackDto> feedbackList;
        int validLimit = Math.max(limit, 1);
        try {
            // Fetch paginated feedback data
             feedbackList = feedbackService.findData(rating != null ? rating : 0, page - 1, validLimit,session);
             Map<String, Object> response = new HashMap<>();
            response.put("feedbacks", feedbackList);
            response.put("currentPage", page);
            response.put("totalPages", feedbackService.getTotalPages(rating, validLimit, session));
            response.put("totalFeedbacks", feedbackService.getTotalElements(rating, session));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching feedback data", e);
        }

    }




}
