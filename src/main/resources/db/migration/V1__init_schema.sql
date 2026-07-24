CREATE TABLE clients (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         phone_number VARCHAR(255) NOT NULL,
                         name VARCHAR(255),
                         created_at DATETIME(6) NOT NULL,
                         CONSTRAINT uk_clients_phone_number UNIQUE (phone_number)
);

CREATE TABLE workers (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         phone_number VARCHAR(255) NOT NULL,
                         skill VARCHAR(30) NOT NULL,
                         area VARCHAR(255) NOT NULL,
                         status VARCHAR(30) NOT NULL,
                         no_show_count INT NOT NULL,
                         created_at DATETIME(6) NOT NULL,
                         CONSTRAINT uk_workers_phone_number UNIQUE (phone_number)
);

CREATE TABLE jobs (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      client_id BIGINT NOT NULL,
                      service_type VARCHAR(30) NOT NULL,
                      area VARCHAR(255) NOT NULL,
                      preferred_date DATE,
                      budget VARCHAR(255),
                      status VARCHAR(30) NOT NULL,
                      created_at DATETIME(6) NOT NULL,
                      CONSTRAINT fk_jobs_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE job_offers (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            job_id BIGINT NOT NULL,
                            worker_id BIGINT NOT NULL,
                            response VARCHAR(30) NOT NULL,
                            offered_at DATETIME(6) NOT NULL,
                            responded_at DATETIME(6),
                            CONSTRAINT fk_job_offers_job FOREIGN KEY (job_id) REFERENCES jobs (id),
                            CONSTRAINT fk_job_offers_worker FOREIGN KEY (worker_id) REFERENCES workers (id)
);

CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          job_id BIGINT NOT NULL,
                          worker_id BIGINT NOT NULL,
                          confirmed_at DATETIME(6) NOT NULL,
                          reminder_sent BIT(1) NOT NULL,
                          outcome VARCHAR(30) NOT NULL,
                          CONSTRAINT uk_bookings_job_id UNIQUE (job_id),
                          CONSTRAINT fk_bookings_job FOREIGN KEY (job_id) REFERENCES jobs (id),
                          CONSTRAINT fk_bookings_worker FOREIGN KEY (worker_id) REFERENCES workers (id)
);