package com.swiftwheelshub.audit.util;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.AuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest, AuditLogInfo auditLogInfo) {
        assertEquals(auditLogInfoRequest.username(), auditLogInfo.getUsername());
        assertEquals(auditLogInfoRequest.methodName(), auditLogInfo.getMethodName());
        assertEquals(auditLogInfoRequest.parametersValues(), auditLogInfo.getParametersValues());
    }

}
