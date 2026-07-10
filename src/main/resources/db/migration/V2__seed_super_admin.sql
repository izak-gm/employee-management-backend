insert into employee (id,first_name,last_name,email,phone_number,password, role, status)
values (    gen_random_uuid(),    'Izak','Admin','izak@gmail.com', '+254700000000','$2y$10$BMlmePEe/O7yFmreQAT5feIvLtIlu9aRwRpfbaZNr8kb.RlyvO8ti','SUPERADMIN','ACTIVE')
ON CONFLICT (email) DO NOTHING;