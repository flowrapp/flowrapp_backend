-- Populate push_tokens table for integration tests

INSERT INTO flowrapp_management.push_tokens (user_id, token, device_id, platform, created_at)
VALUES 
(1, gen_random_uuid(), 'device123', 'ANDROID', NOW()),
(2, gen_random_uuid(), 'device456', 'IOS', NOW());
