-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.expense_audit_log_info (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    method_name VARCHAR(255),
    username VARCHAR(255),
    parameters_values TEXT[],
    CONSTRAINT pk_expense_audit_log_info PRIMARY KEY (id)
);
