insert into employee (id, employee_number, first_name, middle_name, last_name, email, phone_number, gender, date_of_birth, national_id,
profile_photo, password, role, status, hire_date, confirmation_date, exit_date, employment_type, department_id, position_id,
supervisor_id, created_at, updated_at)
values (gen_random_uuid(), 'RBK-0001', 'Izak', null, 'Admin', 'izak@gmail.com', '+254700000000', 'MALE', '1995-01-01', '12345678',
null, '$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti', 'SUPERADMIN', 'ACTIVE',
current_date, current_date, null, 'FULL_TIME', null, null,
null, current_timestamp, current_timestamp)
ON CONFLICT (email) DO NOTHING;

insert into department (
    id,    name,    code,    active)
values (
    gen_random_uuid(),    'Administration',    'ADMIN',    true
)
ON CONFLICT (name) DO NOTHING;

insert into position (
    id,    name,    code,    department_id,    active
)
values (
    gen_random_uuid(),    'System Administrator',    'SYS_ADMIN',
    (select id from department where name = 'Administration'),
    true
)
ON CONFLICT (code) DO NOTHING;