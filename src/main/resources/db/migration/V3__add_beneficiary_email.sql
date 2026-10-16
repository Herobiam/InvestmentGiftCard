-- V3: Add beneficiary email column to gift_cards table
ALTER TABLE gift_cards ADD COLUMN IF NOT EXISTS beneficiary_email VARCHAR(100);

-- Add comment for documentation
COMMENT ON COLUMN gift_cards.beneficiary_email IS 'Email address of the beneficiary for sending gift card notifications';
