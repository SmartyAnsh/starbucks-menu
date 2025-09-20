-- Test schema for H2 database
CREATE TABLE IF NOT EXISTS drink_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    beverage VARCHAR(255),
    beverage_category VARCHAR(255),
    beverage_prep VARCHAR(255),
    calories INTEGER,
    total_fat_g DECIMAL(5,2),
    trans_fat_g DECIMAL(5,2),
    saturated_fat_g DECIMAL(5,2),
    sodium_mg INTEGER,
    total_carbohydrates_g DECIMAL(5,2),
    cholesterol_mg INTEGER,
    dietary_fibre_g DECIMAL(5,2),
    sugars_g DECIMAL(5,2),
    protein_g DECIMAL(5,2),
    vitamin_a_dv DECIMAL(5,2),
    vitamin_c_dv DECIMAL(5,2),
    calcium_dv DECIMAL(5,2),
    iron_dv DECIMAL(5,2),
    caffeine_mg INTEGER
);

CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255),
    message TEXT,
    response TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id)
);