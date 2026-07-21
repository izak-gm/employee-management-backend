insert into position (id, name, description, active)
values
(gen_random_uuid(), 'Software Engineer', 'Software development role', true);

insert into employee (
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
values
(
    gen_random_uuid(),
    'RBK-0001',
    'System',
    'Administrator',
    'izak@gmail.com',
    '+254700000001',
    'MALE',
    '$2y$10$0qvzyTkGZXAWCfsyi4alde9JcyVM83YsEWEj3CUleBGzVS0n7JDN2',
    'SUPERADMIN',
    'ACTIVE',
    'PERMANENT',
    current_date,
    null
)

ON CONFLICT (email) DO NOTHING;