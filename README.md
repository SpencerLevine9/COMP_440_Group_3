# COMP 440 Group 3

## Project Setup

1. Copy the example database config file: 
    - cp config/db.properties.example config/db.properties
    - (or manually duplicate the file)

2. Open `config/db.properties` and replace the credentials with your local MySQL login:

    - db.user=root
    - db.password=YOUR_MYSQL_PASSWORD

3. Run the database setup script in MySQL Workbench:

    sql/schema.sql

4. Run the connection test:

    - TestConnection.java

If successful, you should see:

Connected to MySQL successfully!


## Backend Features

- MySQL database connection using JDBC
- User registration system
- Duplicate checks for username, email, and phone
- Java's built-in password hashing with PBKDF2 and a secure random salt. 
- Login authentication with password verification
- SQL injection protection using PreparedStatement


## Database Setup

Run the SQL script located in:

sql/schema.sql

This will create the database and the required user table.

## Demo Videos

Phase 1 Demo: https://www.youtube.com/watch?v=QBybrf2hZLA

Phase 2 Demo:

Phase 3 Demo:

## Contributions to Phase 3

- Spencer: Queries 1 & 4, connect results to backend methods.
- Grace: Build the GUI for Phase 3 queries. Queries 3 & 5.
- Ashton: Queries 2 & 6. Testing & full integration, demo prep.
