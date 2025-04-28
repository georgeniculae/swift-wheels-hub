package com.autohub.audit.repository;

import com.autohub.entity.ExpenseAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseAuditLogInfoRepository extends JpaRepository<ExpenseAuditLogInfo, Long> {
}
