package com.carrental.lib.mapper;

import com.carrental.dto.UserDto;
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

}
