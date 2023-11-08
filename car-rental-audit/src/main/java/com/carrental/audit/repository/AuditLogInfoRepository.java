package com.carrental.audit.repository;

import com.carrental.entity.AuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogInfoRepository extends JpaRepository<AuditLogInfo, Long> {
}
