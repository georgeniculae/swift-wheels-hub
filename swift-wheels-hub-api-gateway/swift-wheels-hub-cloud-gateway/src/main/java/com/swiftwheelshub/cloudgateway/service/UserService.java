package com.swiftwheelshub.cloudgateway.service;

import com.swiftwheelshub.cloudgateway.mapper.UserMapper;
import com.swiftwheelshub.cloudgateway.model.User;
import com.swiftwheelshub.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Deprecated
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String USERNAME = "username";
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final UserMapper userMapper;

    @Transactional
    public Mono<User> saveUser(UserDto userDto) {
        return Mono.just(userDto)
                .map(userMapper::mapUserDtoToUser)
                .flatMap(user -> r2dbcEntityTemplate.insert(user.setAsNew()))
                .onErrorResume(e -> {
                    log.error("Error while saving object to database: {}", e.getMessage());

                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<User> updateUser(UserDto userDto) {
        return r2dbcEntityTemplate.selectOne(getFindByIdUsernameQuery(userDto.username()), User.class)
                .map(existingUser -> updateExistingUser(userDto, existingUser))
                .flatMap(r2dbcEntityTemplate::update)
                .onErrorResume(e -> {
                    log.error("Error while saving object to database: {}", e.getMessage());

                    return Mono.empty();
                });
    }

    @Transactional
    public Mono<Long> deleteUserByUsername(String username) {
        return r2dbcEntityTemplate.delete(getDeletionQuery(username), User.class);
    }

    private Query getFindByIdUsernameQuery(String username) {
        return Query.query(Criteria.where(USERNAME).is(username));
    }

    private User updateExistingUser(UserDto userDto, User existingUser) {
        existingUser.setPassword(userDto.password());
        existingUser.setFirstName(userDto.firstName());
        existingUser.setLastName(userDto.lastName());
        existingUser.setEmail(userDto.email());
        existingUser.setDateOfBirth(userDto.dateOfBirth());
        existingUser.setAddress(userDto.address());

        return existingUser;
    }

    private Query getDeletionQuery(String username) {
        return Query.query(Criteria.where(USERNAME).is(username));

    }

}
