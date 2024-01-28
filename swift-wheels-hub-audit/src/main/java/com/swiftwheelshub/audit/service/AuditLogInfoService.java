package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.AuditLogInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogInfoService {

    private final AuditLogInfoMapper auditLogInfoMapper;
    private final AuditLogInfoRepository auditLogInfoRepository;

    public void saveAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        AuditLogInfo auditLogInfo = auditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest);
        auditLogInfoRepository.save(auditLogInfo);
    }

}
