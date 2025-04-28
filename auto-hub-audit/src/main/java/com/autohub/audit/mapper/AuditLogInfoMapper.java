package com.autohub.audit.mapper;

import com.autohub.dto.AuditLogInfoRequest;
import com.autohub.entity.BookingAuditLogInfo;
import com.autohub.entity.CustomerAuditLogInfo;
import com.autohub.entity.ExpenseAuditLogInfo;
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
