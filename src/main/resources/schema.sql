-- Create extension for vector operations (if using pgvector)
CREATE EXTENSION IF NOT EXISTS vector;

-- Drop tables if they exist (in correct order to handle foreign key constraints)
DROP TABLE IF EXISTS chat_messages CASCADE;
DROP TABLE IF EXISTS chat_sessions CASCADE;
-- Let Spring AI handle its own vector store tables
-- DROP TABLE IF EXISTS drink_embeddings CASCADE;
DROP TABLE IF EXISTS drink_items CASCADE;

-- Create drink_items table
CREATE TABLE drink_items (
    id BIGSERIAL PRIMARY KEY,
    beverage_category VARCHAR(255),
    beverage VARCHAR(255),
    beverage_prep VARCHAR(255),
    calories INTEGER,
    total_fat DECIMAL(5,2),
    trans_fat DECIMAL(5,2),
    saturated_fat DECIMAL(5,2),
    sodium INTEGER,
    total_carbohydrates INTEGER,
    cholesterol INTEGER,
    dietary_fibre INTEGER,
    sugars INTEGER,
    protein DECIMAL(5,2),
    vitamin_a VARCHAR(10),
    vitamin_c VARCHAR(10),
    calcium VARCHAR(10),
    iron VARCHAR(10),
    caffeine INTEGER
);

-- Spring AI will create its own vector store tables automatically

-- Create chat_sessions table
CREATE TABLE chat_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create chat_messages table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT REFERENCES chat_sessions(id) ON DELETE CASCADE,
    message_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_drink_items_category ON drink_items(beverage_category);
CREATE INDEX idx_drink_items_beverage ON drink_items(beverage);
CREATE INDEX idx_drink_items_calories ON drink_items(calories);
CREATE INDEX idx_drink_items_caffeine ON drink_items(caffeine);
CREATE INDEX idx_chat_sessions_session_id ON chat_sessions(session_id);
CREATE INDEX idx_chat_messages_session_id ON chat_messages(session_id);
CREATE INDEX idx_chat_messages_timestamp ON chat_messages(timestamp);

-- Spring AI will create its own vector indexes automatically