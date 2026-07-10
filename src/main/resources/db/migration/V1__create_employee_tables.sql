CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS employee (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS invite_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    employee_id UUID NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_invite_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS employee_leave (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    cover_employee_id UUID,
    approved_by UUID,
    leave_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_leave_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_leave_cover_employee
        FOREIGN KEY (cover_employee_id)
        REFERENCES employee(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_leave_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES employee(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_employee_email
    ON employee(email);

CREATE INDEX IF NOT EXISTS idx_leave_employee
    ON employee_leave(employee_id);

CREATE INDEX IF NOT EXISTS idx_leave_cover_employee
    ON employee_leave(cover_employee_id);

CREATE INDEX IF NOT EXISTS idx_leave_approved_by
    ON employee_leave(approved_by);

CREATE INDEX IF NOT EXISTS idx_leave_status
    ON employee_leave(status);

CREATE INDEX IF NOT EXISTS idx_leave_type
    ON employee_leave(leave_type);

CREATE INDEX IF NOT EXISTS idx_invite_token
    ON invite_token(token);