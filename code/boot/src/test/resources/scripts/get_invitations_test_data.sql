-- Test data for get invitations scenarios

-- Create business for get invitations testing (business_id = 4)
INSERT INTO flowrapp_management.business (name, owner_id, longitude, latitude, area, created_at)
VALUES ('Get Invitations Business', 1, 0.0, 0.0, 100.0, NOW());

-- Create users for get invitations testing
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('getinvite1', 'getinvite1@test.com', '444555666', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('getinvite2', 'getinvite2@test.com', '777888999', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq');

-- Create multiple invitations with different statuses
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status, created_at, expires_at)
VALUES ((SELECT id FROM flowrapp_management.users WHERE mail = 'getinvite1@test.com'), 1, 2, '0241398c-7bcc-4bca-9f3c-00b45f867de2', 'EMPLOYEE', 'PENDING', NOW(), NOW() + INTERVAL '7 days');

INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status, created_at, expires_at)
VALUES ((SELECT id FROM flowrapp_management.users WHERE mail = 'getinvite2@test.com'), 1, 2, '4d1be6d0-004e-4b6a-af56-d47a15249535', 'EMPLOYEE', 'PENDING', NOW(), NOW() + INTERVAL '7 days');

-- Create business for empty results testing (business_id = 5)
INSERT INTO flowrapp_management.business (name, owner_id, longitude, latitude, area, created_at)
VALUES ('Empty Invitations Business', 1, 0.0, 0.0, 100.0, NOW());
