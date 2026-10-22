-- Initial sample data for Employee Management Portal

-- Remove existing employee records before inserting new sample data
DELETE FROM employees;

INSERT INTO employees (first_name, last_name, email, department, job_title, salary, hire_date, status, created_at, updated_at)
VALUES 
('Alice', 'Smith', 'alice.smith@example.com', 'Engineering', 'Lead Software Engineer', 115000.00, '2022-03-15', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bob', 'Johnson', 'bob.johnson@example.com', 'Engineering', 'DevOps Specialist', 98000.00, '2022-07-01', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carol', 'Williams', 'carol.williams@example.com', 'Human Resources', 'HR Manager', 82000.00, '2021-11-10', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('David', 'Brown', 'david.brown@example.com', 'Finance', 'Senior Financial Analyst', 89000.00, '2023-01-20', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Emma', 'Davis', 'emma.davis@example.com', 'Marketing', 'Digital Marketing Director', 105000.00, '2020-05-18', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Frank', 'Miller', 'frank.miller@example.com', 'Product', 'Principal Product Manager', 125000.00, '2021-08-25', 'ON_LEAVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Grace', 'Wilson', 'grace.wilson@example.com', 'Engineering', 'Frontend Developer', 78000.00, '2023-06-12', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Henry', 'Taylor', 'henry.taylor@example.com', 'Operations', 'Operations Coordinator', 65000.00, '2023-09-01', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Isabella', 'Anderson', 'isabella.anderson@example.com', 'Human Resources', 'Recruiter', 62000.00, '2024-02-14', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jack', 'Thomas', 'jack.thomas@example.com', 'Engineering', 'QA Automation Engineer', 84000.00, '2023-04-10', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
