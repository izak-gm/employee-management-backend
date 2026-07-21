CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- EARNING TYPE
CREATE TABLE IF NOT EXISTS payroll_earning (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL,
    earning_type_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payroll_earning_payroll
        FOREIGN KEY (payroll_id)
        REFERENCES payroll(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_payroll_earning_type
        FOREIGN KEY (earning_type_id)
        REFERENCES earning_type(id)
        ON DELETE RESTRICT
);


CREATE INDEX IF NOT EXISTS idx_payroll_earning_payroll
ON payroll_earning(payroll_id);


CREATE INDEX IF NOT EXISTS idx_payroll_earning_type
ON payroll_earning(earning_type_id);

-- DEDUCTION TYPE
CREATE TABLE IF NOT EXISTS payroll_deduction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL,
    deduction_type_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    remarks VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payroll_deduction_payroll
        FOREIGN KEY (payroll_id)
        REFERENCES payroll(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_payroll_deduction_type
        FOREIGN KEY (deduction_type_id)
        REFERENCES deduction_type(id)
        ON DELETE RESTRICT
);


CREATE INDEX IF NOT EXISTS idx_payroll_deduction_payroll
ON payroll_deduction(payroll_id);

CREATE INDEX IF NOT EXISTS idx_payroll_deduction_type
ON payroll_deduction(deduction_type_id);