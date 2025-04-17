package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshub.entity.Booking;
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
