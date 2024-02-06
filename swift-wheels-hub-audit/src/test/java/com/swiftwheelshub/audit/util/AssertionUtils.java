package com.swiftwheelshub.audit.util;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.BookingAuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest, BookingAuditLogInfo bookingAuditLogInfo) {
        assertEquals(auditLogInfoRequest.username(), bookingAuditLogInfo.getUsername());
        assertEquals(auditLogInfoRequest.methodName(), bookingAuditLogInfo.getMethodName());
        assertEquals(auditLogInfoRequest.parametersValues(), bookingAuditLogInfo.getParametersValues());
    }

}
