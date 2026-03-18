CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    email VARCHAR(40) NOT NULL UNIQUE,
    nickname VARCHAR(20) NOT NULL UNIQUE,
    age TINYINT UNSIGNED NOT NULL CHECK (age BETWEEN 1 AND 120),
    password VARCHAR(255) NOT NULL,
    login_attempts TINYINT UNSIGNED DEFAULT 0 CHECK (login_attempts <= 100),
    best_score_easy INT DEFAULT 0,
    best_score_normal INT DEFAULT 0,
    best_score_hard INT DEFAULT 0,
    best_score_mastermind INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    level ENUM('EASY', 'NORMAL', 'HARD', 'MASTERMIND') NOT NULL,
    code_length TINYINT NOT NULL,
    allow_duplicates BOOLEAN NOT NULL,
    secret_code VARCHAR(50) NOT NULL,
    status ENUM('IN_PROGRESS', 'WON', 'LOST', 'GAVE_UP') NOT NULL,
    attempts_used TINYINT DEFAULT 0,
    score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE guesses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    attempt_number TINYINT NOT NULL,
    guess VARCHAR(50) NOT NULL,
    correct_positions TINYINT NOT NULL,
    correct_colors TINYINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES games(id)
);