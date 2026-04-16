-- ============================================================================
-- Investment Gift Card - Seed Data
-- Version: 1.0
-- Description: Insert default roles and admin user
-- ============================================================================

-- ============================================================================
-- INSERT DEFAULT ROLES
-- ============================================================================
INSERT INTO roles (id, name, description, created_at, is_active, is_deleted, version)
VALUES 
    (uuid_generate_v4(), 'ROLE_USER', 'Standard user role with basic permissions', CURRENT_TIMESTAMP, TRUE, FALSE, 0),
    (uuid_generate_v4(), 'ROLE_ADMIN', 'Administrator role with full permissions', CURRENT_TIMESTAMP, TRUE, FALSE, 0)
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- INSERT ADMIN USER
-- Password: Admin@123456 (BCrypt encoded)
-- ============================================================================
DO $$
DECLARE
    admin_role_id UUID;
    user_role_id UUID;
    admin_user_id UUID;
    regular_user_id UUID;
BEGIN
    -- Get role IDs
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ROLE_ADMIN';
    SELECT id INTO user_role_id FROM roles WHERE name = 'ROLE_USER';
    
    -- Check if admin user exists
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@example.com') THEN
        -- Create admin user
        INSERT INTO users (id, email, password, first_name, last_name, email_verified, created_at, is_active, is_deleted, version)
        VALUES (
            uuid_generate_v4(),
            'admin@example.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMye1XLhh1dQFkMnqMvhQIrPvDkEVh2RWRi', -- Admin@123456
            'System',
            'Administrator',
            TRUE,
            CURRENT_TIMESTAMP,
            TRUE,
            FALSE,
            0
        )
        RETURNING id INTO admin_user_id;
        
        -- Assign both ADMIN and USER roles to admin
        INSERT INTO user_roles (user_id, role_id) VALUES (admin_user_id, admin_role_id);
        INSERT INTO user_roles (user_id, role_id) VALUES (admin_user_id, user_role_id);
        
        RAISE NOTICE 'Admin user created: admin@example.com';
    END IF;
    
    -- Check if regular test user exists
    IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'hero@example.com') THEN
        -- Create regular test user
        INSERT INTO users (id, email, password, first_name, last_name, email_verified, created_at, is_active, is_deleted, version)
        VALUES (
            uuid_generate_v4(),
            'hero@example.com',
            '$2a$10$ZLhnHxdpHETcxmtEStgpI.5CHUNzqj7w9B6xFfuNLQi7.tz1qYTNq', -- Test@123456
            'Hero',
            'User',
            TRUE,
            CURRENT_TIMESTAMP,
            TRUE,
            FALSE,
            0
        )
        RETURNING id INTO regular_user_id;
        
        -- Assign USER role to regular user
        INSERT INTO user_roles (user_id, role_id) VALUES (regular_user_id, user_role_id);
        
        RAISE NOTICE 'Test user created: hero@example.com';
    END IF;
END $$;

-- ============================================================================
-- INSERT SAMPLE GIFT CARDS (for demonstration)
-- ============================================================================
DO $$
DECLARE
    sample_user_id UUID;
BEGIN
    -- Get a user ID for the created_by_user_id field
    SELECT id INTO sample_user_id FROM users WHERE email = 'hero@example.com' LIMIT 1;
    
    -- Only insert if no gift cards exist
    IF NOT EXISTS (SELECT 1 FROM gift_cards LIMIT 1) THEN
        -- Sample 1: Pending KYC
        INSERT INTO gift_cards (
            id, reference_number, batch_number,
            buyer_full_name, buyer_id_passport, buyer_mobile, buyer_address,
            beneficiary_full_name, beneficiary_dob, beneficiary_mobile, relationship, contact_method,
            total_value, expiry_date, payment_method, fnb_account_number, branch,
            status, created_by_user_id, created_at, is_active, is_deleted, version
        ) VALUES (
            uuid_generate_v4(),
            'GC-2026-001',
            'BATCH-001',
            'John Smith',
            'NAM123456789',
            '+264 81 123 4567',
            '123 Independence Avenue, Windhoek, Namibia',
            'Sarah Smith',
            '1995-05-15',
            '+264 81 987 6543',
            'Daughter',
            'SMS',
            5000.00,
            '2027-04-15',
            'BANK_TRANSFER',
            '62123456789',
            'Windhoek Main',
            'PENDING_KYC',
            sample_user_id,
            CURRENT_TIMESTAMP,
            TRUE,
            FALSE,
            0
        );
        
        -- Sample 2: Card Issued (ready for redemption)
        INSERT INTO gift_cards (
            id, reference_number, batch_number,
            buyer_full_name, buyer_id_passport, buyer_mobile, buyer_address,
            beneficiary_full_name, beneficiary_dob, beneficiary_mobile, relationship, contact_method,
            total_value, expiry_date, payment_method, fnb_account_number, branch,
            status, created_by_user_id, created_at, is_active, is_deleted, version
        ) VALUES (
            uuid_generate_v4(),
            'GC-2026-002',
            'BATCH-002',
            'Peter Johnson',
            'NAM987654321',
            '+264 81 555 1234',
            '456 Sam Nujoma Drive, Swakopmund, Namibia',
            'Emma Johnson',
            '1990-08-22',
            '+264 81 555 4321',
            'Spouse',
            'CALL',
            10000.00,
            '2027-06-30',
            'CASH',
            NULL,
            'Swakopmund',
            'CARD_ISSUED',
            sample_user_id,
            CURRENT_TIMESTAMP - INTERVAL '5 days',
            TRUE,
            FALSE,
            0
        );
        
        -- Sample 3: Already Redeemed
        INSERT INTO gift_cards (
            id, reference_number, batch_number,
            buyer_full_name, buyer_id_passport, buyer_mobile, buyer_address,
            beneficiary_full_name, beneficiary_dob, beneficiary_mobile, relationship, contact_method,
            total_value, expiry_date, payment_method, fnb_account_number, branch,
            status, created_by_user_id, created_at, is_active, is_deleted, version
        ) VALUES (
            uuid_generate_v4(),
            'GC-2026-003',
            'BATCH-003',
            'Maria Williams',
            'NAM456789123',
            '+264 81 777 8899',
            '789 Robert Mugabe Ave, Oshakati, Namibia',
            'James Williams',
            '1988-12-10',
            '+264 81 777 9988',
            'Brother',
            'EMAIL',
            2500.00,
            '2027-03-01',
            'BANK_TRANSFER',
            '62987654321',
            'Oshakati',
            'REDEEMED',
            sample_user_id,
            CURRENT_TIMESTAMP - INTERVAL '30 days',
            TRUE,
            FALSE,
            0
        );
        
        RAISE NOTICE 'Sample gift cards created';
    END IF;
END $$;
