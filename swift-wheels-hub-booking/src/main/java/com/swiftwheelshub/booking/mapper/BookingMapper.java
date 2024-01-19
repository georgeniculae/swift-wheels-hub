package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.entity.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    BookingDto mapEntityToDto(Booking booking);

    Booking mapDtoToEntity(BookingDto bookingDto);

}
