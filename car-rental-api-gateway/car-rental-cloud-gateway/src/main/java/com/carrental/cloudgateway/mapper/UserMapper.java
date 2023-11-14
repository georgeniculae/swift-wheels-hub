package com.carrental.cloudgateway.mapper;

import com.carrental.cloudgateway.model.User;
import com.carrental.dto.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User mapUserDtoToUser(UserDto userDto);

}
