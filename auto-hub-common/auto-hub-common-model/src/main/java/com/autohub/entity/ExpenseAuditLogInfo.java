package com.autohub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "expense_audit_log_info", schema = "public")
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ExpenseAuditLogInfo extends BaseEntity {

    @NotEmpty(message = "Method name cannot be empty")
    private String methodName;

    private String username;

    private LocalDateTime timestamp;

    @Builder.Default
    private List<String> parametersValues = new ArrayList<>();

    public ExpenseAuditLogInfo(String methodName, String username, LocalDateTime timestamp, List<String> parametersValues) {
        this.methodName = methodName;
        this.username = username;
        this.timestamp = timestamp;
        this.parametersValues = Objects.requireNonNullElseGet(parametersValues, ArrayList::new);
    }

}
