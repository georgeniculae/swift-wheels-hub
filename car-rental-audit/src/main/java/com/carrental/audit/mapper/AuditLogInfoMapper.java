package com.carrental.audit.mapper;

import com.carrental.dto.AuditLogInfoDto;
import com.carrental.entity.AuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuditLogInfoMapper {

    AuditLogInfo mapDtoToEntity(AuditLogInfoDto auditLogInfoDto);

}
