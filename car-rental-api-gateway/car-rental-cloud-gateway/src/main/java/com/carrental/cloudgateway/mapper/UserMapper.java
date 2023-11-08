package com.carrental.cloudgateway.mapper;

import com.carrental.cloudgateway.model.User;
import com.carrental.dto.UserDto;
import com.carrental.entity.Role;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User mapUserDtoToUser(UserDto userDto);

    default Role mapToUserRoleEnum(UserDto.RoleEnum role) {
        return switch (role) {
            case ADMIN -> Role.ROLE_ADMIN;
            case USER -> Role.ROLE_USER;
            case SUPPORT -> Role.ROLE_SUPPORT;
        };
    }

}
