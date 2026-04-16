-- ============================================================================
-- Investment Gift Card - PostgreSQL Database Schema
-- Version: 1.0
-- Description: Initial schema creation for all tables
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- 1. ROLES TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0
);

-- Index for role lookups
CREATE INDEX IF NOT EXISTS idx_roles_name ON roles(name);
CREATE INDEX IF NOT EXISTS idx_roles_is_deleted ON roles(is_deleted);

-- ============================================================================
-- 2. USERS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    -- Constraints
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- Indexes for user queries
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_is_deleted ON users(is_deleted);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

-- ============================================================================
-- 3. USER_ROLES JOIN TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    
    PRIMARY KEY (user_id, role_id),
    
    CONSTRAINT fk_user_roles_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_user_roles_role 
        FOREIGN KEY (role_id) 
        REFERENCES roles(id) 
        ON DELETE CASCADE
);

-- Index for role lookups by user
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);

-- ============================================================================
-- 4. GIFT_CARDS TABLE (Vouchers)
-- ============================================================================
CREATE TABLE IF NOT EXISTS gift_cards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Reference identifiers
    reference_number VARCHAR(50) NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    
    -- Buyer Information (Section A)
    buyer_full_name VARCHAR(200) NOT NULL,
    buyer_id_passport VARCHAR(50) NOT NULL,
    buyer_mobile VARCHAR(20) NOT NULL,
    buyer_address TEXT NOT NULL,
    
    -- Beneficiary Information (Section B)
    beneficiary_full_name VARCHAR(200) NOT NULL,
    beneficiary_dob DATE NOT NULL,
    beneficiary_mobile VARCHAR(20) NOT NULL,
    relationship VARCHAR(50) NOT NULL,
    contact_method VARCHAR(20) NOT NULL,
    
    -- Voucher Details (Section C)
    total_value DECIMAL(15, 2) NOT NULL,
    expiry_date DATE NOT NULL,
    
    -- Payment Details (Section D)
    payment_method VARCHAR(30) NOT NULL,
    fnb_account_number VARCHAR(30),
    branch VARCHAR(100),
    
    -- Status
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_KYC',
    
    -- Foreign key to user who created this gift card
    created_by_user_id UUID,
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    -- Constraints
    CONSTRAINT uk_gift_cards_reference_number UNIQUE (reference_number),
    CONSTRAINT uk_gift_cards_batch_number UNIQUE (batch_number),
    
    CONSTRAINT fk_gift_cards_created_by_user 
        FOREIGN KEY (created_by_user_id) 
        REFERENCES users(id) 
        ON DELETE SET NULL,
    
    -- Check constraints for valid status
    CONSTRAINT chk_gift_cards_status 
        CHECK (status IN ('PENDING_KYC', 'CARD_ISSUED', 'REDEEMED', 'CANCELLED', 'EXPIRED')),
    
    -- Check constraint for positive value
    CONSTRAINT chk_gift_cards_value_positive 
        CHECK (total_value > 0)
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_gift_cards_reference_number ON gift_cards(reference_number);
CREATE INDEX IF NOT EXISTS idx_gift_cards_batch_number ON gift_cards(batch_number);
CREATE INDEX IF NOT EXISTS idx_gift_cards_status ON gift_cards(status);
CREATE INDEX IF NOT EXISTS idx_gift_cards_is_deleted ON gift_cards(is_deleted);
CREATE INDEX IF NOT EXISTS idx_gift_cards_created_by_user_id ON gift_cards(created_by_user_id);
CREATE INDEX IF NOT EXISTS idx_gift_cards_expiry_date ON gift_cards(expiry_date);
CREATE INDEX IF NOT EXISTS idx_gift_cards_buyer_id_passport ON gift_cards(buyer_id_passport);
CREATE INDEX IF NOT EXISTS idx_gift_cards_beneficiary_mobile ON gift_cards(beneficiary_mobile);
CREATE INDEX IF NOT EXISTS idx_gift_cards_created_at ON gift_cards(created_at DESC);

-- ============================================================================
-- 5. REDEMPTIONS TABLE
-- ============================================================================
CREATE TABLE IF NOT EXISTS redemptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Foreign key to gift card (one-to-one)
    gift_card_id UUID NOT NULL,
    
    -- Buyer verification (Section E)
    provided_buyer_name VARCHAR(200) NOT NULL,
    provided_buyer_phone VARCHAR(20) NOT NULL,
    
    -- Verification status
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    verification_call_time TIMESTAMP,
    verification_notes TEXT,
    verified_by VARCHAR(100),
    
    -- Product Selection (Section F)
    selected_product VARCHAR(30) NOT NULL,
    other_product_description VARCHAR(200),
    account_name VARCHAR(200) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    
    -- Completion tracking
    redemption_date TIMESTAMP,
    beneficiary_signature_captured BOOLEAN DEFAULT FALSE,
    
    -- Foreign key to user who processed this redemption
    processed_by_user_id UUID,
    
    -- Audit columns
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100),
    version BIGINT DEFAULT 0,
    
    -- Constraints
    CONSTRAINT uk_redemptions_gift_card_id UNIQUE (gift_card_id),
    
    CONSTRAINT fk_redemptions_gift_card 
        FOREIGN KEY (gift_card_id) 
        REFERENCES gift_cards(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_redemptions_processed_by_user 
        FOREIGN KEY (processed_by_user_id) 
        REFERENCES users(id) 
        ON DELETE SET NULL,
    
    -- Check constraints for valid values
    CONSTRAINT chk_redemptions_verification_status 
        CHECK (verification_status IN ('PENDING', 'CALL_INITIATED', 'VERIFIED', 'FAILED', 'BUYER_UNREACHABLE')),
    
    CONSTRAINT chk_redemptions_selected_product 
        CHECK (selected_product IN ('CALL_ACCOUNT', 'NOTICE_ACCOUNT', 'MONEY_MAXIMISER', 'OTHER'))
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_redemptions_gift_card_id ON redemptions(gift_card_id);
CREATE INDEX IF NOT EXISTS idx_redemptions_verification_status ON redemptions(verification_status);
CREATE INDEX IF NOT EXISTS idx_redemptions_is_deleted ON redemptions(is_deleted);
CREATE INDEX IF NOT EXISTS idx_redemptions_processed_by_user_id ON redemptions(processed_by_user_id);
CREATE INDEX IF NOT EXISTS idx_redemptions_created_at ON redemptions(created_at DESC);

-- ============================================================================
-- COMMENTS FOR DOCUMENTATION
-- ============================================================================
COMMENT ON TABLE roles IS 'User roles for authorization (e.g., ROLE_USER, ROLE_ADMIN)';
COMMENT ON TABLE users IS 'User accounts for authentication';
COMMENT ON TABLE user_roles IS 'Many-to-many relationship between users and roles';
COMMENT ON TABLE gift_cards IS 'Investment gift card vouchers with buyer and beneficiary information';
COMMENT ON TABLE redemptions IS 'Gift card redemption records with verification workflow';

COMMENT ON COLUMN gift_cards.status IS 'PENDING_KYC: Awaiting verification, CARD_ISSUED: Ready for redemption, REDEEMED: Already redeemed, CANCELLED: Cancelled, EXPIRED: Past expiry date';
COMMENT ON COLUMN redemptions.verification_status IS 'PENDING: Awaiting call, CALL_INITIATED: Call in progress, VERIFIED: Buyer confirmed, FAILED: Verification failed, BUYER_UNREACHABLE: Could not reach buyer';
