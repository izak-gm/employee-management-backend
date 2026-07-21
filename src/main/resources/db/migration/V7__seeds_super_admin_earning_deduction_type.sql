INSERT INTO position (id, name, description, active)
VALUES
(gen_random_uuid(), 'Software Engineer', 'Software development role', TRUE);

INSERT INTO employee (
    id,
    employee_number,
    first_name,
    last_name,
    email,
    phone_number,
    gender,
    password,
    role,
    status,
    employment_type,
    hire_date,
    position_id
)
VALUES
(
    gen_random_uuid(),
    'EMP001',
    'System',
    'Administrator',
    'admin@riverbank.com',
    '+254700000001',
    'MALE',
    '$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti',
    'SUPERADMIN',
    'ACTIVE',
    'PERMANENT',
    CURRENT_DATE,
    NULL
),

(
    gen_random_uuid(),
    'EMP002',
    'John',
    'Kamau',
    'i@riverbank.com',
    '+254700000002',
    'MALE',
    '$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti',
    'EMPLOYEE',
    'ACTIVE',
    'PERMANENT',
    CURRENT_DATE,
    (SELECT id FROM position WHERE name = 'Software Engineer')
),

(
    gen_random_uuid(),
    'EMP003',
    'Mary',
    'Wanjiku',
    'm@riverbank.com',
    '+254700000003',
    'FEMALE',
    '$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti',
    'EMPLOYEE',
    'ACTIVE',
    'PERMANENT',
    CURRENT_DATE,
    (SELECT id FROM position WHERE name = 'Software Engineer')
),

(
    gen_random_uuid(),
    'EMP004',
    'David',
    'Otieno',
    'd@riverbank.com',
    '+254700000004',
    'MALE',
    '$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti',
    'EMPLOYEE',
    'ACTIVE',
    'PERMANENT',
    CURRENT_DATE,
    (SELECT id FROM position WHERE name = 'Software Engineer')
)

ON CONFLICT (email) DO NOTHING;