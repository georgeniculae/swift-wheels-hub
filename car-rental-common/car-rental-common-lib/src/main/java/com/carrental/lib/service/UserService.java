package com.carrental.lib.service;

import com.carrental.dto.UserDto;
import com.carrental.entity.User;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.mapper.UserMapper;
import com.carrental.lib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring", name = "jpa")
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void saveUser(UserDto userDto) {
        User user = userMapper.mapDtoToEntity(userDto);
        userRepository.saveAndFlush(user);
    }

    public void updateUser(UserDto userDto) {
        boolean existsByUsername = userRepository.existsByUsername(userDto.getUsername());

        if (existsByUsername) {
            User newUser = userMapper.mapDtoToEntity(userDto);
            userRepository.saveAndFlush(newUser);
        }

        throw new CarRentalNotFoundException("User with username " + userDto.getUsername() + " not found");
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    }

}
