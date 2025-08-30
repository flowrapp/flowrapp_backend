-- Test data for delete invitation scenarios

-- Create business for delete testing (business_id = 3)
INSERT INTO flowrapp_management.business (name, address, town, city, country, owner_id, longitude, latitude, area, timezone_offset, created_at)
VALUES ('Delete Invitation Business', 'carretera 1', 'Zahora', 'Cádiz', 'España', 1, 0.0, 0.0, 100.0, 'Europe/Madrid', NOW());

-- Create user for delete invitation testing (user_id = 5)
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('deleteinvite', 'deleteinvite@test.com', '111222333', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Create invitation to be deleted
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (3, 1, 2, '85693920-d467-4c7b-8b01-ebe884003038', 'EMPLOYEE', 'PENDING');

-- Create another invitation for unauthorized delete test
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (2, 1, 1, '07070e7b-607c-4a27-af5c-87756466a60f', 'EMPLOYEE', 'PENDING');

-- Create another invitation but accepted
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES (1, 1, 2, 'b0c8f3d5-1f3c-4b0e-8d2a-5c6fb8c9f3d5', 'OWNER', 'ACCEPTED');