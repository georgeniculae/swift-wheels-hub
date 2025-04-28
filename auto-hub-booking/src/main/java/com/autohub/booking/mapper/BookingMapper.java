package com.autohub.booking.mapper;

import com.autohub.dto.BookingRequest;
import com.autohub.dto.BookingResponse;
import com.autohub.dto.CreatedBookingReprocessRequest;
import com.autohub.dto.UpdatedBookingReprocessRequest;
import com.autohub.entity.Booking;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingMapper {

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapEntityToDto(Booking booking);

    @Mapping(target = "actualCarId", source = "carId")
    Booking mapDtoToEntity(BookingRequest bookingRequest);

    CreatedBookingReprocessRequest mapToCreatedBookingReprocessRequest(Booking booking);

    UpdatedBookingReprocessRequest mapToUpdatedBookingReprocessRequest(Booking booking);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapReprocessRequestToBookingResponse(CreatedBookingReprocessRequest reprocessRequest);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapReprocessRequestToBookingResponse(UpdatedBookingReprocessRequest reprocessRequest);

}
