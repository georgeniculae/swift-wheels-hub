package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.BookingAuditLogInfo;
import com.swiftwheelshub.entity.CustomerAuditLogInfo;
import com.swiftwheelshub.entity.ExpenseAuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AuditLogInfoMapper {

    BookingAuditLogInfo mapDtoToBookingEntity(AuditLogInfoRequest auditLogInfoRequest);

    CustomerAuditLogInfo mapDtoToCustomerEntity(AuditLogInfoRequest auditLogInfoRequest);

    ExpenseAuditLogInfo mapDtoToExpenseEntity(AuditLogInfoRequest auditLogInfoRequest);

}
