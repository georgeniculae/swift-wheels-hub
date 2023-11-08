package com.carrental.audit.service;

import com.carrental.audit.mapper.AuditLogInfoMapper;
import com.carrental.audit.repository.AuditLogInfoRepository;
import com.carrental.dto.AuditLogInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogInfoService {

    private final AuditLogInfoMapper auditLogInfoMapper;
    private final AuditLogInfoRepository auditLogInfoRepository;

    public void saveAuditLogInfo(AuditLogInfoDto auditLogInfoDto) {
        auditLogInfoRepository.save(auditLogInfoMapper.mapDtoToEntity(auditLogInfoDto));
    }

}
