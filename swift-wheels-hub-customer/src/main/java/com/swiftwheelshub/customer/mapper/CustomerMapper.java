package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

    CurrentUserDto mapUserToCurrentUserDto(User user);

    UserDto mapEntityToDto(User user);

}
