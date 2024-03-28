package com.swiftwheelshub.audit.repository;

import com.swiftwheelshub.entity.BookingAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingAuditLogInfoRepository extends JpaRepository<BookingAuditLogInfo, Long> {
}
