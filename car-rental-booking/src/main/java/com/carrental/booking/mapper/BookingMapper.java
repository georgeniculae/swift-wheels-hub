package com.carrental.booking.mapper;

import com.carrental.dto.BookingDto;
import com.carrental.entity.Booking;
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
