package com.swiftwheelshub.lib.service;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.lib.mapper.UserMapper;
import com.swiftwheelshub.lib.repository.UserRepository;
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
        boolean existsByUsername = userRepository.existsByUsername(userDto.username());

        if (existsByUsername) {
            User newUser = userMapper.mapDtoToEntity(userDto);
            userRepository.saveAndFlush(newUser);
        }

        throw new SwiftWheelsHubNotFoundException("User with username " + userDto.username() + " not found");
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    }

}
