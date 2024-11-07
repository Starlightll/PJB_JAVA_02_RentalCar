package com.rentalcar.rentalcar.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDto {

    private Long bookingId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 1000, message = "The length of content not exceed 1000 character.")
    private String content;
    private String carName;
    private LocalDateTime dateTime;
    @Column(name = "front")
    private String frontImage;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime actualEndDate;
    private String username;

    public FeedbackDto(String content, LocalDateTime date, Integer rating, Long bookingId,
                       LocalDateTime actualEndDate, LocalDateTime startDate,
                       Long userId, String frontImage, String carName, String username) {
        this.content = content;
        this.dateTime = date;
        this.rating = rating;
        this.bookingId = bookingId;
        this.actualEndDate = actualEndDate;
        this.startDate = startDate;
        this.userId = userId;
        this.frontImage = frontImage;
        this.carName = carName;
        this.username = username;
    }
}
