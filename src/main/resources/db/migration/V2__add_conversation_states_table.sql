CREATE TABLE conversation_states (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     phone_number VARCHAR(255) NOT NULL,
                                     current_step VARCHAR(255),
                                     context_data JSON,
                                     status VARCHAR(30) NOT NULL,
                                     created_at DATETIME(6) NOT NULL,
                                     updated_at DATETIME(6) NOT NULL,
                                     CONSTRAINT uk_conversation_states_phone_number UNIQUE (phone_number)
);