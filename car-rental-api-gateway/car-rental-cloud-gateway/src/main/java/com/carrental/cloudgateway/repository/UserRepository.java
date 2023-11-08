package com.carrental.cloudgateway.repository;

import com.carrental.cloudgateway.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    @Query("""
            SELECT *
            FROM public.user u
            WHERE u.username = :username""")
    Mono<User> findByUsername(@Param("username") String username);

}
