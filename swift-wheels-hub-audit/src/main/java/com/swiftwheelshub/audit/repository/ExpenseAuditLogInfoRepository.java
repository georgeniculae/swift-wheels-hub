package com.swiftwheelshub.audit.repository;

import com.swiftwheelshub.entity.ExpenseAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseAuditLogInfoRepository extends JpaRepository<ExpenseAuditLogInfo, Long> {
}
