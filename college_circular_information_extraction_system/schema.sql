-- schema.sql
-- Database Schema for Smart College Circular Information Extraction System

CREATE DATABASE IF NOT EXISTS college_circular_db;
USE college_circular_db;

CREATE TABLE IF NOT EXISTS circulars (
    circular_id INT AUTO_INCREMENT PRIMARY KEY,
    department VARCHAR(100),
    event_name VARCHAR(150),
    event_date VARCHAR(50),
    deadline VARCHAR(50),
    venue VARCHAR(150),
    target_students VARCHAR(150),
    circular_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
