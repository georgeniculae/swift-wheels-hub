package com.swiftwheelshub.emailnotification.repository;

import com.swiftwheelshub.emailnotification.model.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {

    @Query("""
            Select new com.swiftwheelshub.emailnotification.model.CustomerDetails(c.email)
            From CustomerDetails c
            where c.username = ?1""")
    Optional<String> findByUsername(String username);

}
