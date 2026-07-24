ALTER TABLE payroll
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_payrolls_deleted ON payroll (deleted);