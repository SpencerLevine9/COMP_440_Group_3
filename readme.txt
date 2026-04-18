COMP440 Team 3 - Phase 2
Group Members:
- Spencer Levine
- Grace Gallardo
- Ashton Rosales

Project Overview:
This project is a Java + JavaFX + MySQL rental system for Phase 2. The system allows a user to:
1. Post a rental unit with title, description, features, and price
2. Search rental units by feature
3. Select a rental unit from the search results and submit a review

The application integrates Java GUI interfaces with MySQL database operations.

Technologies Used:
- Java 21
- JavaFX 21
- MySQL Server 8.0.45
- MySQL Workbench 8.0 CE

Phase 2 Functionality Implemented:
1. Rental posting interface
   - Insert rental title, description, features, and price
   - Rental IDs are generated automatically using MySQL auto-increment
   - A user can post at most 2 rental units per day

2. Search interface
   - Search for all rental units that contain a given feature
   - Results are displayed in a list on the page

3. Review interface
   - Select a rental unit from the search results
   - Choose a rating from: Excellent / Good / Fair / Poor
   - Enter a remark and submit a review
   - A user can submit at most 3 reviews per day
   - A user cannot review their own rental unit
   - A user can only review a rental unit once

Group Member Contributions:
- Spencer Levine:
  - Implemented backend/database logic for rental posting
  - Implemented backend/database logic for review rules
  - Enforced:
    - max 2 rental posts per user per day
    - max 3 reviews per user per day
    - no self-review
    - one review per user per rental
  - Helped connect Java/JDBC logic with the database
  - Helped final integration and demo preparation

- Grace Gallardo:
  - Built the rental posting GUI/form
  - Built the search by feature GUI/form
  - Built the review GUI/form
  - Added user interaction flow and success/error messages

- Ashton Rosales:
  - Implemented search/result display and integration support
  - Helped build/display the results list page
  - Helped integration testing for Phase 2
  - Helped sample data/demo setup

Project Structure:
- database/
- loginpage/
- rentalui/
- service/
- model/
- sql/
- lib/
- config/
    db.properties.example

Database Setup:
1. Open MySQL Workbench
2. Run the SQL script:
   sql/schema.sql
3. Make sure the database and tables are created successfully

Configuration Setup:
1. Go to:
   config/db.properties.example
2. Copy it and create a new file named:
   config/db.properties
3. Fill in your own database information:
   - db.url
   - db.user
   - db.password

Important:
Do not submit or share a real config/db.properties file containing personal database credentials.

How to Run the Project:
Method 1: VS Code
1. Open the project folder in VS Code
2. Make sure Java 21 and JavaFX 21 are installed
3. Make sure JavaFX library paths are configured in:
   - .vscode/settings.json
   - .vscode/launch.json
4. Run the application using:
   loginpage.LoginPage

Method 2: Java command line
1. Compile/run the project with JavaFX configured on the module path
2. Launch the entry point:
   loginpage.LoginPage

Program Flow:
1. Start at the login page
2. Log in or sign up
3. Open the main menu
4. Post a rental
5. Search rentals by feature
6. Select a rental from the search results
7. Submit a review

Notes:
- Features should be entered exactly as expected for search matching
- Example features include:
  Wi-Fi, Kitchen, Mountainview, Parking
- Search is based on the stored feature value in the database

Demo Video:
YouTube Link:
https://youtu.be/DQhj2wtvF8k