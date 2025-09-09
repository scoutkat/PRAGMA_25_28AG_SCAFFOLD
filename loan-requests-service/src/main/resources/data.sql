-- Initial data for CrediYa Loan Requests Service
-- This script inserts sample loan types for testing and development

-- Insert sample loan types
INSERT INTO loan_types (name, description, min_amount, max_amount, min_term_months, max_term_months, interest_rate, is_active, created_at, updated_at)
VALUES 
    ('Personal Loan', 'General purpose personal loan for various needs', 100000, 10000000, 6, 60, 12.5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Home Loan', 'Loan for purchasing or renovating a home', 5000000, 50000000, 60, 360, 8.75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Car Loan', 'Loan for purchasing a new or used vehicle', 500000, 50000000, 12, 84, 10.25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Education Loan', 'Loan for educational expenses and tuition fees', 200000, 20000000, 12, 120, 9.5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Business Loan', 'Loan for business expansion and working capital', 1000000, 100000000, 12, 120, 11.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Emergency Loan', 'Quick loan for emergency situations', 50000, 2000000, 3, 24, 15.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;