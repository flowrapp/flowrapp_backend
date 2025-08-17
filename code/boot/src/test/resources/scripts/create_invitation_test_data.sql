-- Test data for create invitation scenarios

-- Create a new business for testing (business_id = 2)
INSERT INTO flowrapp_management.business (name, owner_id, longitude, latitude, area, created_at)
VALUES ('Create Invitation Business', 1, 0.0, 0.0, 100.0, NOW());

-- Create a third user for testing (user_id = 3)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('newuser', 'newuser@test.com', '987654321', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Create a fourth user for testing duplicate scenarios (user_id = 4)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('duplicate', 'duplicate@test.com', '555123456', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Add user_id = 4 as member of business_id = 2 to test "already member" scenario
INSERT INTO flowrapp_management.users_roles (user_id, business_id, role, invited_by, joined_at)
VALUES (4, 2, 'EMPLOYEE', 1, NOW());

-- Create existing pending invitation for duplicate scenario
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (2, 1, 2, '45ce70ab-ca18-4f04-b717-9afb6fd3070e', 'EMPLOYEE', 'PENDING');
