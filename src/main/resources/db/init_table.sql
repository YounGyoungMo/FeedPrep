DROP TABLE IF EXISTS notification;

CREATE TABLE notification (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              type VARCHAR(50),
                              sender_id BIGINT,
                              receiver_id BIGINT,
                              content VARCHAR(255),
                              url VARCHAR(255),
                              is_read BOOLEAN,
                              is_stale BOOLEAN,
                              created_at TIMESTAMP,
                              modified_at TIMESTAMP
);
