-- Database schema for CrediYa Authentication Service
-- This script creates the necessary tables for user management

-- Create users table with all required fields for user registration
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    base_salary DECIMAL(15,2) NOT NULL CHECK (base_salary >= 0 AND base_salary <= 15000000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create index on email for faster lookups during user validation
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Create index on phone for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);

-- Create index on created_at for reporting purposes
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
