CREATE TABLE IF NOT EXISTS symbol_price (
    symbol VARCHAR(20) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    price DECIMAL(18, 6) NOT NULL,
    PRIMARY KEY (symbol, created_at)
);

CREATE TABLE IF NOT EXISTS symbol_config (
    symbol VARCHAR(20) PRIMARY KEY,
    time_frame VARCHAR(20) NOT NULL CHECK (time_frame IN ('MONTHLY', 'HALF_YEARLY', 'YEARLY')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS symbol_lock (
    symbol VARCHAR(20) PRIMARY KEY,
    locked BOOLEAN NOT NULL,
    locked_at TIMESTAMP(6),
);

