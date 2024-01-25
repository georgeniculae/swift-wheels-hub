package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.dto.CurrentUserDetails;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    String UTC = "UTC";
    String ADDRESS = "address";

    CurrentUserDetails mapUserToCurrentUserDto(User user);

    UserDto mapEntityToDto(User user);

    @Mapping(target = "address", expression = "java(getAddress(userRepresentation))")
    @Mapping(target = "registrationDate", expression = "java(getRegistrationDate())")
    RegistrationResponse mapToRegistrationResponse(UserRepresentation userRepresentation);

    default String getAddress(UserRepresentation userRepresentation) {
        return userRepresentation.getAttributes()
                .getOrDefault(ADDRESS, List.of(StringUtils.EMPTY))
                .getFirst();
    }

    default String getRegistrationDate() {
        return ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.of(UTC))
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
