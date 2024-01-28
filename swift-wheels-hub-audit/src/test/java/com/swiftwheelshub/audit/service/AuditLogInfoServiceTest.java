package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.mapper.AuditLogInfoMapperImpl;
import com.swiftwheelshub.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshub.audit.util.TestUtils;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.AuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogInfoServiceTest {

    @InjectMocks
    private AuditLogInfoService auditLogInfoService;

    @Mock
    private AuditLogInfoRepository auditLogInfoRepository;

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void saveAuditLogInfoTest_success() {
        AuditLogInfo auditLogInfo =
                TestUtils.getResourceAsJson("/data/AuditLogInfo.json", AuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtils.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(auditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(auditLogInfo);

        assertDoesNotThrow(() -> auditLogInfoService.saveAuditLogInfo(auditLogInfoRequest));

        verify(auditLogInfoMapper).mapDtoToEntity(any(AuditLogInfoRequest.class));
    }

}
