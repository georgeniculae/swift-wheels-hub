package com.swiftwheelshub.audit.repository;

import com.swiftwheelshub.entity.AuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogInfoRepository extends JpaRepository<AuditLogInfo, Long> {
}
