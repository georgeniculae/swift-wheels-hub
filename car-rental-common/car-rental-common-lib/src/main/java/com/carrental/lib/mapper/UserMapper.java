package com.carrental.lib.mapper;

import com.carrental.dto.UserDto;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    UserDto mapEntityToDto(User user);

    User mapDtoToEntity(UserDto userDto);

    default UserDto.RoleEnum mapToUserRoleEnum(Role role) {
        return switch (role) {
            case ROLE_ADMIN -> UserDto.RoleEnum.ADMIN;
            case ROLE_USER -> UserDto.RoleEnum.USER;
            case ROLE_SUPPORT -> UserDto.RoleEnum.SUPPORT;
        };
    }

    default Role mapToUserRoleEnum(UserDto.RoleEnum role) {
        return switch (role) {
            case ADMIN -> Role.ROLE_ADMIN;
            case USER -> Role.ROLE_USER;
            case SUPPORT -> Role.ROLE_SUPPORT;
        };
    }

}
