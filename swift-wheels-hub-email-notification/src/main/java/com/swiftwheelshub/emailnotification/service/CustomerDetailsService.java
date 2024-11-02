package com.swiftwheelshub.emailnotification.service;

import com.swiftwheelshub.dto.CustomerInfo;
import com.swiftwheelshub.emailnotification.mapper.CustomerDetailsMapper;
import com.swiftwheelshub.emailnotification.model.CustomerDetails;
import com.swiftwheelshub.emailnotification.repository.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerDetailsService {

    private final CustomerDetailsRepository customerDetailsRepository;
    private final CustomerDetailsMapper customerDetailsMapper;

    public void saveCustomerDetails(CustomerInfo payload) {
        CustomerDetails customerDetails = customerDetailsMapper.mapToCustomerDetails(payload);
        customerDetailsRepository.save(customerDetails);
    }

}
