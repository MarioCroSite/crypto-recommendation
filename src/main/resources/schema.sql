CREATE TABLE IF NOT EXISTS symbol_price_summary (
    symbol VARCHAR(20) NOT NULL,
    period_start TIMESTAMP(6) NOT NULL,
    period_end TIMESTAMP(6) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('CONFLICT', 'INCOMPLETE', 'COMPLETE')),
    min_price DECIMAL(18, 6) NOT NULL,
    max_price DECIMAL(18, 6) NOT NULL,
    oldest_price DECIMAL(18, 6) NOT NULL,
    newest_price DECIMAL(18, 6) NOT NULL,
    normalized_range DECIMAL(18, 6) NOT NULL,
    PRIMARY KEY (symbol, period_start)
);

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

