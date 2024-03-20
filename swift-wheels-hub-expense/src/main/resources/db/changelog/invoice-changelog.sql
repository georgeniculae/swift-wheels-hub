-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.invoice (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   customer_username VARCHAR(255),
   customer_email VARCHAR(255),
   car_id BIGINT NOT NULL,
   receptionist_employee_id BIGINT,
   booking_id BIGINT NOT NULL,
   car_date_of_return date,
   is_vehicle_damaged BOOLEAN,
   damage_cost DECIMAL,
   additional_payment DECIMAL,
   total_amount DECIMAL,
   comments VARCHAR(255),
   CONSTRAINT pk_invoice PRIMARY KEY (id)
);
