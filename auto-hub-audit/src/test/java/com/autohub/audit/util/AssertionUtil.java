package com.autohub.audit.util;

import com.autohub.dto.AuditLogInfoRequest;
import com.autohub.entity.BookingAuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest, BookingAuditLogInfo bookingAuditLogInfo) {
        assertEquals(auditLogInfoRequest.username(), bookingAuditLogInfo.getUsername());
        assertEquals(auditLogInfoRequest.methodName(), bookingAuditLogInfo.getMethodName());
        assertEquals(auditLogInfoRequest.parametersValues(), bookingAuditLogInfo.getParametersValues());
    }

}
