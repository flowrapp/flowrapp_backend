INSERT INTO flowrapp_management.business (name, address, town, city, country, owner_id, longitude, latitude, area, timezone_offset, created_at)
VALUES ('Accept Invitation Business', 'carretera 1', 'Zahora', 'Cádiz', 'España', 1, 0.0, 0.0, 100.0, 'Europe/Madrid', NOW());

INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, status)
VALUES ((SELECT id from flowrapp_management.users where mail = 'test@test.com'),
        (SELECT id from flowrapp_management.users where mail = 'admin@admin.com'),
        (SELECT id from flowrapp_management.business where name = 'Accept Invitation Business'),
        '23a30f35-7aa2-44cf-970a-54b22bfedcfa', 'EMPLOYEE', 'PENDING');