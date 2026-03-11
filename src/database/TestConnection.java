package database;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn != null) {
                System.out.println("Connected to MySQL successfully!");
            }

        } catch (Exception e) {
            System.out.println("Connection failed.");
            e.printStackTrace();
        }

    }

}