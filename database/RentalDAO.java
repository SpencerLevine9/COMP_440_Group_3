package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    // Change this ONE line if your DatabaseConnection class uses a different method name.
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public boolean canPostRentalToday(String username) {
        String sql = "SELECT COUNT(*) FROM rental_unit WHERE username = ? AND post_date = CURDATE()";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) < 2;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int createRental(String username, String title, String description, double price, List<String> features) {
        if (!canPostRentalToday(username)) {
            return -1; // limit reached
        }

        String insertRentalSql =
                "INSERT INTO rental_unit (username, title, description, price, post_date) " +
                        "VALUES (?, ?, ?, ?, CURDATE())";

        String insertFeatureSql =
                "INSERT INTO rental_feature (rental_id, feature) VALUES (?, ?)";

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            int rentalId;

            try (PreparedStatement rentalPs = conn.prepareStatement(insertRentalSql, Statement.RETURN_GENERATED_KEYS)) {
                rentalPs.setString(1, username);
                rentalPs.setString(2, title);
                rentalPs.setString(3, description);
                rentalPs.setDouble(4, price);

                rentalPs.executeUpdate();

                ResultSet keys = rentalPs.getGeneratedKeys();
                if (!keys.next()) {
                    conn.rollback();
                    return -2;
                }

                rentalId = keys.getInt(1);
            }

            try (PreparedStatement featurePs = conn.prepareStatement(insertFeatureSql)) {
                for (String feature : features) {
                    String cleanFeature = feature.trim();
                    if (!cleanFeature.isEmpty()) {
                        featurePs.setInt(1, rentalId);
                        featurePs.setString(2, cleanFeature);
                        featurePs.addBatch();
                    }
                }
                featurePs.executeBatch();
            }

            conn.commit();
            return rentalId;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -2;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean canReviewToday(String username) {
        String sql = "SELECT COUNT(*) FROM rental_review WHERE reviewer_username = ? AND review_date = CURDATE()";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) < 3;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isOwnRental(String username, int rentalId) {
        String sql = "SELECT username FROM rental_unit WHERE rental_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rentalId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return username.equals(rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasAlreadyReviewed(String username, int rentalId) {
        String sql = "SELECT COUNT(*) FROM rental_review WHERE rental_id = ? AND reviewer_username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rentalId);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean createReview(int rentalId, String reviewerUsername, String score, String remark) {
        if (isOwnRental(reviewerUsername, rentalId)) {
            System.out.println("User cannot review own rental.");
            return false;
        }

        if (hasAlreadyReviewed(reviewerUsername, rentalId)) {
            System.out.println("User already reviewed this rental.");
            return false;
        }

        if (!canReviewToday(reviewerUsername)) {
            System.out.println("User already wrote 3 reviews today.");
            return false;
        }

        String sql =
                "INSERT INTO rental_review (rental_id, reviewer_username, score, remark, review_date) " +
                        "VALUES (?, ?, ?, ?, CURDATE())";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rentalId);
            ps.setString(2, reviewerUsername);
            ps.setString(3, score);
            ps.setString(4, remark);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helpful for the search page later
    public List<String> searchRentalsByFeature(String feature) {
        List<String> results = new ArrayList<>();

        String sql =
                "SELECT ru.rental_id, ru.title, ru.description, ru.price, ru.username, " +
                        "GROUP_CONCAT(rf2.feature ORDER BY rf2.feature SEPARATOR ', ') AS features " +
                        "FROM rental_unit ru " +
                        "JOIN rental_feature rf ON ru.rental_id = rf.rental_id " +
                        "JOIN rental_feature rf2 ON ru.rental_id = rf2.rental_id " +
                        "WHERE rf.feature = ? " +
                        "GROUP BY ru.rental_id, ru.title, ru.description, ru.price, ru.username";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, feature);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String row = "Rental ID: " + rs.getInt("rental_id")
                        + " | Title: " + rs.getString("title")
                        + " | Description: " + rs.getString("description")
                        + " | Price: " + rs.getDouble("price")
                        + " | Features: " + rs.getString("features")
                        + " | Owner: " + rs.getString("username");
                results.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    // New method to get the most expensive rentals for each feature
    public List<String> getMostExpensiveRentalsPerFeature() {
        List<String> results = new ArrayList<>();

        String sql =
            "SELECT rf.feature, ru.rental_id, ru.title, ru.price, ru.username, ru.post_date " +
            "FROM rental_feature rf " +
            "JOIN rental_unit ru ON rf.rental_id = ru.rental_id " +
            "JOIN ( " +
            "    SELECT rf2.feature, MAX(ru2.price) AS max_price " +
            "    FROM rental_feature rf2 " +
            "    JOIN rental_unit ru2 ON rf2.rental_id = ru2.rental_id " +
            "    GROUP BY rf2.feature " +
            ") mx ON rf.feature = mx.feature AND ru.price = mx.max_price " +
            "ORDER BY rf.feature, ru.rental_id";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String row = "Feature: " + rs.getString("feature")
                        + " | Rental ID: " + rs.getInt("rental_id")
                        + " | Title: " + rs.getString("title")
                        + " | Price: " + rs.getDouble("price")
                        + " | Owner: " + rs.getString("username")
                        + " | Post Date: " + rs.getDate("post_date");
                results.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
        // New method to get the top posting users by date
    public List<String> getTopPostingUsersByDate(Date selectedDate) {
        List<String> results = new ArrayList<>();

        String sql =
            "SELECT ru.username, COUNT(*) AS rental_count " +
            "FROM rental_unit ru " +
            "WHERE ru.post_date = ? " +
            "GROUP BY ru.username " +
            "HAVING COUNT(*) = ( " +
            "    SELECT MAX(user_count) " +
            "    FROM ( " +
            "        SELECT COUNT(*) AS user_count " +
            "        FROM rental_unit " +
            "        WHERE post_date = ? " +
            "        GROUP BY username " +
            "    ) counts " +
            ")";

        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, selectedDate);
            ps.setDate(2, selectedDate);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String row = "Username: " + rs.getString("username")
                        + " | Rentals Posted: " + rs.getInt("rental_count")
                        + " | Date: " + selectedDate;
                results.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}