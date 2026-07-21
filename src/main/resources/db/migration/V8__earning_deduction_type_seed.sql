insert into earning_type
(name, taxable, fixed, description, active, display_order)
values
('Basic Salary', true, true, 'Employee basic salary', true, 1),
('House Allowance', TRUE, TRUE, 'Monthly house allowance', TRUE, 2),
('Transport Allowance', TRUE, TRUE, 'Monthly transport allowance', TRUE, 3),
('Medical Allowance', TRUE, TRUE, 'Medical allowance', TRUE, 4),
('Other Allowance', TRUE, TRUE, 'Other taxable allowance', TRUE, 5),
('Overtime', TRUE, FALSE, 'Overtime earnings', TRUE, 6),
('Bonus', TRUE, FALSE, 'Performance bonus', TRUE, 7),
('Commission', TRUE, FALSE, 'Sales commission', TRUE, 8),
('Leave Allowance', TRUE, FALSE, 'Leave allowance', TRUE, 9),
('Non-Taxable Benefit', FALSE, FALSE, 'Non-taxable benefit', TRUE, 10)
ON CONFLICT (name) DO NOTHING;

insert into deduction_type
(name, statutory, taxable, calculation_type,
 fixed_amount, percentage, description, active, display_order)
values
('PAYE', true, false, 'FORMULA', null, null, 'Pay As You Earn tax', true, 1),

('SHIF', TRUE, FALSE, 'FORMULA', NULL, NULL, 'Social Health Insurance Fund', TRUE, 2),

('NSSF', TRUE, FALSE, 'FORMULA', NULL, NULL, 'National Social Security Fund', TRUE, 3),

('Housing Levy', TRUE, FALSE, 'PERCENTAGE', NULL, 1.50, 'Housing Levy contribution', TRUE, 4),

('Loan Repayment', FALSE, FALSE, 'FIXED', 0.00, NULL, 'Employee loan deduction', TRUE, 5),

('Salary Advance', FALSE, FALSE, 'FIXED', 0.00, NULL, 'Salary advance recovery', TRUE, 6),

('Insurance', FALSE, FALSE, 'FIXED', 0.00, NULL, 'Private insurance deduction', TRUE, 7),

('Pension Contribution', FALSE, FALSE, 'PERCENTAGE', NULL, 5.00, 'Voluntary pension contribution', TRUE, 8),

('Union Dues', FALSE, FALSE, 'FIXED', 0.00, NULL, 'Trade union deduction', TRUE, 9),

('Other Deduction', FALSE, FALSE, 'FIXED', 0.00, NULL, 'Miscellaneous deduction', TRUE, 10)
ON CONFLICT (name) DO NOTHING;