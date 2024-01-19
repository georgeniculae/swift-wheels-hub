package com.swiftwheelshub.cloudgateway.mapper;

import com.swiftwheelshub.cloudgateway.model.User;
import com.swiftwheelshub.dto.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Deprecated
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User mapUserDtoToUser(UserDto userDto);

}
