CREATE DATABASE IF NOT EXISTS payments_db;

USE payments_db;

CREATE TABLE gw_request (
                            id CHAR(36) PRIMARY KEY,
                            transaction_id VARCHAR(255) NOT NULL,
                            amount DOUBLE NOT NULL,
                            mobile_number VARCHAR(15) NOT NULL,
                            status VARCHAR(50) NOT NULL
);

CREATE TABLE payment_requests (
                                  id CHAR(36) NOT NULL,
                                  transaction_id CHAR(36) NOT NULL,
                                  amount DECIMAL(10, 2) NOT NULL,
                                  mobile_number VARCHAR(15) NOT NULL,
                                  status VARCHAR(20) NOT NULL,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (id)
);

CREATE TABLE pending_requests (
                                  id CHAR(36) NOT NULL,
                                  payment_request_id CHAR(36) NOT NULL,
                                  retry_count INT DEFAULT 0,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (id),
                                  FOREIGN KEY (payment_request_id) REFERENCES payment_requests(id)
);
