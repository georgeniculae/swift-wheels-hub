package com.carrental.customer.mapper;

import com.carrental.customer.dto.CurrentUserDto;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.dto.UserDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

    CurrentUserDto mapUserToCurrentUserDto(User user);

    UserDto mapEntityToDto(User user);

    default UserDto.RoleEnum mapToRoleEnum(Role role) {
        return switch (role) {
            case ROLE_ADMIN -> UserDto.RoleEnum.ADMIN;
            case ROLE_USER -> UserDto.RoleEnum.USER;
            case ROLE_SUPPORT -> UserDto.RoleEnum.SUPPORT;
        };
    }

}
