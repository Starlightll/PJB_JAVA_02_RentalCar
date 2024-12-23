package com.rentalcar.rentalcar.mappers;

import com.rentalcar.rentalcar.dto.BookingDto1;
import com.rentalcar.rentalcar.entity.Booking;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)public interface BookingMapper {
    Booking toEntity(BookingDto1 bookingDto1);

    BookingDto1 toDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)Booking partialUpdate(BookingDto1 bookingDto1, @MappingTarget Booking booking);
}