CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- EMPLOYEE PAYROLL PROFILE
CREATE TABLE IF NOT EXISTS  employee_payroll_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL UNIQUE,
    basic_salary NUMERIC(15,2) NOT NULL DEFAULT 0,
    house_allowance NUMERIC(15,2) DEFAULT 0,
    transport_allowance NUMERIC(15,2) DEFAULT 0,
    medical_allowance NUMERIC(15,2) DEFAULT 0,
    other_allowance NUMERIC(15,2) DEFAULT 0,
    bank_name VARCHAR(100),
    bank_branch VARCHAR(100),
    account_number VARCHAR(100),
    kra_pin VARCHAR(50),
    shif_number VARCHAR(50),
    nssf_number VARCHAR(50),
    pension_number VARCHAR(50),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payroll_profile_employee
        FOREIGN KEY(employee_id)
        REFERENCES employee(id)
);

-- EARNING TYPE
CREATE TABLE IF NOT EXISTS  earning_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    taxable BOOLEAN NOT NULL,
    fixed BOOLEAN NOT NULL,
    default_amount NUMERIC(15,2) DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DEDUCTION TYPE
CREATE TABLE IF NOT EXISTS  deduction_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    statutory BOOLEAN DEFAULT FALSE,
    taxable BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    calculation_type VARCHAR(50),
    fixed_amount NUMERIC(10,4),
    percentage NUMERIC(10,4),
    description VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PAYROLL
CREATE TABLE IF NOT EXISTS  payroll (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL,
    payroll_month INT NOT NULL,
    payroll_year INT NOT NULL,
    payroll_date DATE NOT NULL,
    gross_salary NUMERIC(15,2) NOT NULL,
    taxable_income NUMERIC(15,2) NOT NULL,
    total_earnings NUMERIC(15,2) NOT NULL,
    total_deductions NUMERIC(15,2) NOT NULL,
    net_salary NUMERIC(15,2) NOT NULL,
    employer_nssf NUMERIC(15,2),
    employer_shif NUMERIC(15,2),
    employer_housing_levy NUMERIC(15,2),
    reversed BOOLEAN DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED',
    generated_by UUID,
    approved_by UUID,
    approved_at TIMESTAMP,
    reversed_by UUID,
    reversed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payroll_employee
        FOREIGN KEY(employee_id)
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
    ),

    CONSTRAINT uk_employee_month_year
        UNIQUE(employee_id, payroll_month, payroll_year)
);

-- PAYROLL EARNING
CREATE TABLE IF NOT EXISTS payroll_earning (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL,
    earning_type_id UUID NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    taxable BOOLEAN NOT NULL,

    CONSTRAINT fk_payroll_earning_payroll
        FOREIGN KEY(payroll_id)
        REFERENCES payroll(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_payroll_earning_type
        FOREIGN KEY(earning_type_id)
        REFERENCES earning_type(id)
);

-- PAYROLL DEDUCTION
CREATE TABLE IF NOT EXISTS  payroll_deduction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payroll_id UUID NOT NULL,
    deduction_type_id UUID NOT NULL,
    amount NUMERIC(15,2) NOT NULL,

    CONSTRAINT fk_payroll_deduction_payroll
        FOREIGN KEY(payroll_id)
        REFERENCES payroll(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_payroll_deduction_type
        FOREIGN KEY(deduction_type_id)
        REFERENCES deduction_type(id)
);

CREATE INDEX IF NOT EXISTS  idx_payroll_employee
ON payroll(employee_id);

CREATE INDEX IF NOT EXISTS  idx_payroll_month
ON payroll(payroll_month);

CREATE INDEX IF NOT EXISTS  idx_payroll_status
ON payroll(status);

CREATE INDEX IF NOT EXISTS  idx_payroll_earning
ON payroll_earning(payroll_id);

CREATE INDEX IF NOT EXISTS  idx_payroll_deduction
ON payroll_deduction(payroll_id);