package com.swiftwheelshub.expense.model;

import com.swiftwheelshub.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "failed_booking_rollback", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FailedBookingRollback extends BaseEntity {

    private Long bookingId;

}
