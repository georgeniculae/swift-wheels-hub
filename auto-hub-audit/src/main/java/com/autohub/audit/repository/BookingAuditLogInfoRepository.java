package com.autohub.audit.repository;

import com.autohub.entity.BookingAuditLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingAuditLogInfoRepository extends JpaRepository<BookingAuditLogInfo, Long> {
}
