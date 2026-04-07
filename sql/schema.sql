-- This is where the database setup script should go.
-- this is just a permanent copy of the database setup
-- Database setup script for COMP 440 Group 3
-- Run this script in MySQL Workbench to create the database and user table

CREATE DATABASE IF NOT EXISTS comp440_project;

USE comp440_project;

-- Phase 1
CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE
);

-- Phase 2
CREATE TABLE IF NOT EXISTS rental_unit (
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    post_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (username) REFERENCES user(username)
);

CREATE TABLE IF NOT EXISTS rental_feature (
    rental_id INT NOT NULL,
    feature VARCHAR(100) NOT NULL,
    PRIMARY KEY (rental_id, feature),
    FOREIGN KEY (rental_id) REFERENCES rental_unit(rental_id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rental_review (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    rental_id INT NOT NULL,
    reviewer_username VARCHAR(50) NOT NULL,
    score ENUM('Excellent', 'Good', 'Fair', 'Poor') NOT NULL,
    remark TEXT,
    review_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    FOREIGN KEY (rental_id) REFERENCES rental_unit(rental_id)
        ON DELETE CASCADE,
    FOREIGN KEY (reviewer_username) REFERENCES user(username),
    UNIQUE (rental_id, reviewer_username)
);