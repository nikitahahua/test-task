create TABLE IF NOT EXISTS versions (
    id SERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT NOW()
);

create TABLE IF NOT EXISTS card_ranges (
    id SERIAL PRIMARY KEY,
    bin INT NOT NULL,
    min_range BIGINT NOT NULL,
    max_range BIGINT NOT NULL,
    alpha_code VARCHAR(255),
    bank_name VARCHAR(255),
    version_id INT NOT NULL,
    CONSTRAINT fk_version
      FOREIGN KEY (version_id)
      REFERENCES versions(id)
);
