package com.swiftwheelshub.audit.repository;

import com.swiftwheelshub.entity.CustomerAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAuditLogInfoRepository extends JpaRepository<CustomerAuditLogInfo, Long> {
}
