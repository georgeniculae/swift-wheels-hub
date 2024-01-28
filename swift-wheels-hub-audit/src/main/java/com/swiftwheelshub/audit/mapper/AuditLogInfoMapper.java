package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.AuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuditLogInfoMapper {

    AuditLogInfo mapDtoToEntity(AuditLogInfoRequest auditLogInfoRequest);

}
