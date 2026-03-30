-- This is where the database setup script should go.
-- this is just a permanent copy of the database setup
-- Database setup script for COMP 440 Group 3
-- Run this script in MySQL Workbench to create the database and user table

CREATE DATABASE IF NOT EXISTS comp440_project;

USE comp440_project;

CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE
);