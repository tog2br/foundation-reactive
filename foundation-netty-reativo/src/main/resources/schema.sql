CREATE TABLE IF NOT EXISTS films (
    id UUID PRIMARY KEY,
    characters TEXT,
    created VARCHAR(255),
    director VARCHAR(255),
    edited VARCHAR(255),
    episode_id INTEGER,
    opening_crawl TEXT,
    planets TEXT,
    producer VARCHAR(255),
    release_date VARCHAR(255),
    species TEXT,
    starships TEXT,
    title VARCHAR(255),
    url VARCHAR(255),
    vehicles TEXT,
    created_at TIMESTAMP
);
