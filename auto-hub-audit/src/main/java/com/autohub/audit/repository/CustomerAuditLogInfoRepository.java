package com.autohub.audit.repository;

import com.autohub.audit.entity.CustomerAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerAuditLogInfoRepository extends JpaRepository<CustomerAuditLogInfo, Long> {
}
