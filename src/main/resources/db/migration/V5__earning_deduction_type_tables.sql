CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS earning_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    taxable BOOLEAN NOT NULL,
    fixed BOOLEAN NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_earning_type_active
ON earning_type(active);

CREATE TABLE IF NOT EXISTS deduction_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    statutory BOOLEAN NOT NULL,
    taxable BOOLEAN NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    calculation_type VARCHAR(50) NOT NULL,
    fixed_amount NUMERIC(19,2),
    percentage NUMERIC(5,2),
    description VARCHAR(255),
    display_order INT DEFAULT 0,

    CONSTRAINT chk_deduction_calculation_type
        CHECK (
            calculation_type IN (
                'FIXED',
                'PERCENTAGE',
                'FORMULA'
            )
        )
);

CREATE INDEX IF NOT EXISTS idx_deduction_type_active
ON deduction_type(active);