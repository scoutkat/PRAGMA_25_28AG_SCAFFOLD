-- Sample data for CrediYa Loan Requests Service
-- This script inserts initial loan types for testing and development

-- Insert sample loan types
INSERT INTO loan_types (name, description, min_amount, max_amount, min_term_months, max_term_months, interest_rate, is_active, created_at, updated_at)
VALUES 
    ('Personal Loan', 'Personal loan for general purposes with competitive interest rates', 1000.00, 10000000.00, 6, 60, 12.50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Home Loan', 'Home loan for property purchase with flexible terms', 5000000.00, 50000000.00, 60, 360, 8.75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Vehicle Loan', 'Vehicle loan for car and motorcycle purchases', 2000000.00, 30000000.00, 12, 84, 10.25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Education Loan', 'Education loan for academic expenses with special rates', 500000.00, 20000000.00, 12, 120, 9.50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Business Loan', 'Business loan for entrepreneurs and small businesses', 1000000.00, 50000000.00, 12, 180, 11.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;