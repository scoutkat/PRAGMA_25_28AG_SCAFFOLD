-- Database schema for CrediYa Loan Requests Service
-- This script creates the necessary tables for loan request management

-- Create loan types table for different loan products
CREATE TABLE IF NOT EXISTS loan_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    min_amount DECIMAL(15,2) NOT NULL CHECK (min_amount >= 0),
    max_amount DECIMAL(15,2) NOT NULL CHECK (max_amount >= 0),
    min_term_months INTEGER NOT NULL CHECK (min_term_months > 0),
    max_term_months INTEGER NOT NULL CHECK (max_term_months > 0),
    interest_rate DECIMAL(5,2) NOT NULL CHECK (interest_rate >= 0),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create loan requests table for storing loan applications
CREATE TABLE IF NOT EXISTS loan_requests (
    id BIGSERIAL PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    loan_type_id BIGINT NOT NULL REFERENCES loan_types(id),
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    term_months INTEGER NOT NULL CHECK (term_months > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_REVIEW',
    monthly_payment DECIMAL(15,2),
    total_interest DECIMAL(15,2),
    total_amount DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(255),
    rejection_reason TEXT
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_loan_requests_user_email ON loan_requests(user_email);
CREATE INDEX IF NOT EXISTS idx_loan_requests_status ON loan_requests(status);
CREATE INDEX IF NOT EXISTS idx_loan_requests_created_at ON loan_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_loan_requests_loan_type_id ON loan_requests(loan_type_id);

-- Create index on loan types for active products
CREATE INDEX IF NOT EXISTS idx_loan_types_active ON loan_types(is_active);
