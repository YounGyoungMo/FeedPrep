INSERT INTO notifications (
    type, sender_id, receiver_id, content, url,
    is_read, is_stale, created_at, modified_at
) VALUES
      (0, 1, 2, '30일 전 알림입니다.', '/post/30', false, false, DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -30, CURRENT_TIMESTAMP)),
      (1, 3, 2, '10일 전 알림입니다.', '/post/10', false, false, DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),
      (2, 4, 2, '3일 전 알림입니다.', '/profile/4', true, false, DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP));
