CREATE EXTENSION IF NOT EXISTS "pgcrypto";

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
        ON DELETE SET NULL,

    CONSTRAINT chk_leave_status
        CHECK (
            status IN (
                'PENDING_COVER',
                'PENDING_APPROVAL',
                'APPROVED',
                'REJECTED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_leave_type
        CHECK (
            leave_type IN (
                'ANNUAL',
                'SICK',
                'MATERNITY',
                'PATERNITY',
                'COMPASSIONATE'
            )
        )
);

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