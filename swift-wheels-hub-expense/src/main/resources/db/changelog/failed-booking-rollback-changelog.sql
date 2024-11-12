-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.failed_booking_rollback
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    booking_id BIGINT                                  NOT NULL,
    CONSTRAINT pk_failed_booking_rollback PRIMARY KEY (id)
);