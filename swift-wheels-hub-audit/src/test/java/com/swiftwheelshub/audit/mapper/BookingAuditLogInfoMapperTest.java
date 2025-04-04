package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.audit.util.AssertionUtil;
import com.swiftwheelshub.audit.util.TestUtil;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.BookingAuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingAuditLogInfoMapperTest {

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void mapDtoToEntityTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        BookingAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToBookingEntity(auditLogInfoRequest);

        AssertionUtil.assertAuditLogInfo(auditLogInfoRequest, bookingAuditLogInfo);
    }

}
