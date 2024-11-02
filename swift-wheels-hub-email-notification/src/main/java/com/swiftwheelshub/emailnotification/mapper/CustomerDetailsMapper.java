package com.swiftwheelshub.emailnotification.mapper;

import com.swiftwheelshub.dto.CustomerInfo;
import com.swiftwheelshub.emailnotification.model.CustomerDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CustomerDetailsMapper {

    CustomerDetails mapToCustomerDetails(CustomerInfo customerInfo);

}
