-- Test data for create invitation scenarios

-- Create a new business for testing (business_id = 2)
INSERT INTO flowrapp_management.business (name, owner_id, longitude, latitude, area, timezone_offset, created_at)
VALUES ('Register Invitation Business', 1, 0.0, 0.0, 100.0, 'Europe/Madrid', NOW());

-- Create a third user for testing (user_id = 3)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash, enabled)
VALUES ('newuser', 'newuser@test.com', '987654321', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq', false);

-- Create a fourth user for testing duplicate scenarios (user_id = 4)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('duplicate', 'duplicate@test.com', '555123456', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Create existing pending invitation for duplicate scenario
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (3, 1, 2, '45ce70ab-ca18-4f04-b717-9afb6fd3070e', 'EMPLOYEE', 'PENDING');

-- Create existing accepted invitation for duplicate scenario
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (4, 1, 2, '582ed6f4-2524-4ad6-8fbc-8ea5fe71c9af', 'EMPLOYEE', 'ACCEPTED');