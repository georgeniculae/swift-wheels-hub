package com.swiftwheelshub.emailnotification.repository;

import com.swiftwheelshub.emailnotification.model.CustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {
}
