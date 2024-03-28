package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.repository.BookingAuditLogInfoRepository;
import com.swiftwheelshub.audit.repository.CustomerAuditLogInfoRepository;
import com.swiftwheelshub.audit.repository.ExpenseAuditLogInfoRepository;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.entity.BookingAuditLogInfo;
import com.swiftwheelshub.entity.CustomerAuditLogInfo;
import com.swiftwheelshub.entity.ExpenseAuditLogInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogInfoService {

    private final AuditLogInfoMapper auditLogInfoMapper;
    private final BookingAuditLogInfoRepository bookingAuditLogInfoRepository;
    private final CustomerAuditLogInfoRepository customerAuditLogInfoRepository;
    private final ExpenseAuditLogInfoRepository expenseAuditLogInfoRepository;

    public void saveBookingAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        BookingAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToBookingEntity(auditLogInfoRequest);
        bookingAuditLogInfoRepository.save(bookingAuditLogInfo);
    }

    public void saveCustomerAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        CustomerAuditLogInfo bookingAuditLogInfo = auditLogInfoMapper.mapDtoToCustomerEntity(auditLogInfoRequest);
        customerAuditLogInfoRepository.save(bookingAuditLogInfo);
    }

    public void saveExpenseAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        ExpenseAuditLogInfo expenseAuditLogInfo = auditLogInfoMapper.mapDtoToExpenseEntity(auditLogInfoRequest);
        expenseAuditLogInfoRepository.save(expenseAuditLogInfo);
    }

}
