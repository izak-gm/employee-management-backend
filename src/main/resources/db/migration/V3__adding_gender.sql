ALTER TABLE employee
ADD COLUMN gender VARCHAR(10);

ALTER TABLE employee
ADD CONSTRAINT chk_employee_gender CHECK (gender IN ('MALE', 'FEMALE'));