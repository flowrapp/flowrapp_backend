-- Test data for expired invitation scenarios

-- Create user for expired invitation testing (user_id will be auto-generated)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('expired', 'expired@test.com', '123000456', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Create expired invitation for accept testing
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status, created_at, expires_at)
VALUES ((SELECT id FROM flowrapp_management.users WHERE mail = 'expired@test.com'),
        1, 1, 'bdaf548b-ce82-41e0-a6c4-c7d3f78f6ea4', 'EMPLOYEE', 'PENDING',
        NOW() - INTERVAL '10 days', NOW() - INTERVAL '3 days');