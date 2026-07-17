CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- DEPARTMENT
CREATE TABLE IF NOT EXISTS  department (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- POSITION
CREATE TABLE  IF NOT EXISTS position (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    description TEXT,
    department_id UUID,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_position_department
        FOREIGN KEY(department_id)
        REFERENCES department(id)

);

-- EMPLOYEE
CREATE TABLE IF NOT EXISTS  employee (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_number VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(30) NOT NULL UNIQUE,
    gender VARCHAR(20) NOT NULL,
    date_of_birth DATE,
    national_id VARCHAR(30) UNIQUE,
    profile_photo VARCHAR(255),
    password TEXT NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    hire_date DATE NOT NULL,
    confirmation_date DATE,
    exit_date DATE,
    employment_type VARCHAR(50) NOT NULL,
    department_id UUID,
    position_id UUID,
    supervisor_id UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_department

        FOREIGN KEY(department_id)

        REFERENCES department(id),

    CONSTRAINT fk_employee_position

        FOREIGN KEY(position_id)

        REFERENCES position(id),

    CONSTRAINT fk_employee_supervisor

        FOREIGN KEY(supervisor_id)

        REFERENCES employee(id)

);
CREATE INDEX  IF NOT EXISTS  idx_employee_email
ON employee(email);

CREATE INDEX IF NOT EXISTS  idx_employee_number
ON employee(employee_number);

CREATE INDEX IF NOT EXISTS  idx_employee_department
ON employee(department_id);

CREATE INDEX IF NOT EXISTS  idx_employee_position
ON employee(position_id);

CREATE INDEX  IF NOT EXISTS idx_employee_status
ON employee(status);

CREATE INDEX  IF NOT EXISTS idx_employee_role
ON employee(role);

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


CREATE INDEX IF NOT EXISTS idx_invite_token
    ON invite_token(token);