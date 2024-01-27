package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.repository.AuditLogInfoRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogInfoServiceTest {

    @InjectMocks
    private AuditLogInfoService auditLogInfoService;

    @Mock
    private AuditLogInfoMapper auditLogInfoMapper;

    @Mock
    private AuditLogInfoRepository auditLogInfoRepository;

}
