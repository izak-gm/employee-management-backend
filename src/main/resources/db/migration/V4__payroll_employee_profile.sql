CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS payroll (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    employee_id UUID NOT NULL,

    payroll_month INT NOT NULL,
    payroll_year INT NOT NULL,
    payroll_date DATE NOT NULL,

    gross_pay NUMERIC(19,2) NOT NULL,
    taxable_pay NUMERIC(19,2) NOT NULL,
    total_earnings NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_deductions NUMERIC(19,2) NOT NULL,
    net_pay NUMERIC(19,2) NOT NULL,
    paye NUMERIC(19,2) NOT NULL,
    income_tax NUMERIC(19,2) NOT NULL,
    personal_relief NUMERIC(19,2) NOT NULL,

    statutory_deductions NUMERIC(19,2),
    pay_after_statutory_deductions NUMERIC(19,2),

    shif NUMERIC(19,2) NOT NULL,
    nssf NUMERIC(19,2) NOT NULL,
    housing_levy NUMERIC(19,2) NOT NULL,

    employer_nssf NUMERIC(19,2) NOT NULL,
    employer_shif NUMERIC(19,2) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED',

    generated_by UUID,
    approved_by UUID,
    reversed_by UUID,

    approved_at TIMESTAMP,
    payment_date DATE,
    payment_reference VARCHAR(100),

    payroll_number VARCHAR(100) NOT NULL UNIQUE,

    remarks VARCHAR(500),
    reversal_reason VARCHAR(255),
    reversed_at TIMESTAMP,

    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_payroll_employee_month
        UNIQUE (employee_id, payroll_month, payroll_year),

    CONSTRAINT fk_payroll_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(id),

    CONSTRAINT fk_payroll_generated_by
        FOREIGN KEY (generated_by)
        REFERENCES employee(id),

    CONSTRAINT fk_payroll_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES employee(id),

    CONSTRAINT fk_payroll_reversed_by
        FOREIGN KEY (reversed_by)
        REFERENCES employee(id),

    CONSTRAINT chk_payroll_status
        CHECK (
            status IN (
                'DRAFT',
                'GENERATED',
                'APPROVED',
                'PAID',
                'REVERSED'
            )
        )
);

CREATE TABLE IF NOT EXISTS employee_payroll_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    employee_id UUID NOT NULL UNIQUE,

    basic_salary NUMERIC(18,2) NOT NULL,

    house_allowance NUMERIC(18,2) DEFAULT 0,
    transport_allowance NUMERIC(18,2) DEFAULT 0,
    medical_allowance NUMERIC(18,2) DEFAULT 0,
    other_allowance NUMERIC(18,2) DEFAULT 0,

    bank_name VARCHAR(100) NOT NULL,
    bank_branch VARCHAR(100),

    account_number VARCHAR(100) NOT NULL UNIQUE,

    kra_pin VARCHAR(50) NOT NULL UNIQUE,

    shif_number VARCHAR(50) NOT NULL UNIQUE,

    nssf_number VARCHAR(50) NOT NULL UNIQUE,

    pension_contribution NUMERIC(18,2) DEFAULT 0,

    effective_from DATE,

    active BOOLEAN NOT NULL DEFAULT TRUE,


    CONSTRAINT fk_employee_payroll_profile_employee
        FOREIGN KEY (employee_id)
        REFERENCES employee(id)
        ON DELETE CASCADE
);


CREATE INDEX IF NOT EXISTS idx_employee_payroll_profile_employee
ON employee_payroll_profile(employee_id);


CREATE INDEX IF NOT EXISTS idx_employee_payroll_profile_active
ON employee_payroll_profile(active);