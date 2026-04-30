package demo;

import database.DatabaseConnection;
import model.ServiceResult;
import model.User;
import service.AuthService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the database with demo data for testing all six Phase 3 queries.
 * Run this once against a fresh database (after running sql/schema.sql).
 *
 * All users get password "password123" so you can log in as any of them during the demo.
 *
 * Demo data covers:
 *   Q1 - Most expensive rental unit for each feature
 *   Q2 - Users who posted 2 rentals on same day with feature X and feature Y
 *   Q3 - Rental units by user X where all comments are Excellent or Good
 *   Q4 - Users who posted the most rentals on a specific date (with ties)
 *   Q5 - Users who posted reviews but every review is Poor
 *   Q6 - Users whose rentals never received a Poor review
 */
public class SeedDemoData {

    public static void main(String[] args) {
        try {
            seedUsers();
            List<Integer> rentalIds = seedRentals();
            seedReviews(rentalIds);
            System.out.println("Demo data seeded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedUsers() {
        AuthService auth = new AuthService();
        String[][] users = {
            {"alice",   "Alice",   "Smith",    "alice@email.com",   "1110001111"},
            {"bob",     "Bob",     "Jones",    "bob@email.com",     "2220002222"},
            {"carol",   "Carol",   "Lee",      "carol@email.com",   "3330003333"},
            {"dan",     "Dan",     "Garcia",   "dan@email.com",     "4440004444"},
            {"eve",     "Eve",     "Taylor",   "eve@email.com",     "5550005555"},
        };

        for (String[] u : users) {
            ServiceResult r = auth.signUp(new User(u[0], "password123", "password123", u[1], u[2], u[3], u[4]));
            System.out.println("User " + u[0] + ": " + r.getMessage());
        }
    }

    // Returns list of generated rental IDs in insertion order (index 0 = first rental, etc.)
    private static List<Integer> seedRentals() throws SQLException {
        List<Integer> rentalIds = new ArrayList<>();

        // format: {username, title, description, price, post_date, features (comma-sep)}
        String[][] rentals = {
            // [0] alice posts 2 rentals on 2025-10-15: one with Wi-Fi, one with Kitchen (Q2: feature X=Wi-Fi, Y=Kitchen)
            // also gives alice 2 posts on 2025-10-15 for Q4 tie testing
            {"alice", "LA Apartment",      "Modern downtown loft",          "200", "2025-10-15", "Wi-Fi,Parking"},
            // [1]
            {"alice", "LA Condo",          "Cozy condo near the beach",     "150", "2025-10-15", "Kitchen,Pool"},

            // [2] bob also posts 2 rentals on 2025-10-15 - ties with alice for Q4
            {"bob",   "SF Studio",         "Small studio in SOMA",          "180", "2025-10-15", "Wi-Fi,Kitchen"},
            // [3]
            {"bob",   "SF Loft",           "Spacious loft with city view",  "300", "2025-10-15", "Mountainview,Parking"},

            // [4] carol posts 1 rental on 2025-10-15 - fewer than alice/bob, so not in Q4 top
            {"carol", "Seattle Room",      "Quiet room near campus",        "90",  "2025-10-15", "Wi-Fi"},

            // [5] carol posts another rental on a different date
            {"carol", "Seattle Suite",     "Suite with mountain view",      "250", "2025-11-01", "Mountainview,Kitchen"},

            // [6] dan posts rentals that will get only Excellent/Good reviews (Q3 for user=dan)
            {"dan",   "Portland Cabin",    "Cabin in the woods",            "175", "2025-10-20", "Mountainview,Wi-Fi"},
            // [7]
            {"dan",   "Portland Flat",     "Downtown flat",                 "120", "2025-10-20", "Kitchen,Parking"},

            // [8] eve posts a rental that will get a Poor review (so eve is NOT in Q6)
            {"eve",   "Denver Apt",        "Apartment near downtown",       "160", "2025-10-22", "Parking,Pool"},

            // [9] alice posts another rental with no reviews at all (still qualifies for Q6 if no Poor on others)
            {"alice", "LA Beach House",    "Right on the sand",             "350", "2025-11-05", "Pool,Wi-Fi"},
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            String insertRental = "INSERT INTO rental_unit (username, title, description, price, post_date) VALUES (?, ?, ?, ?, ?)";
            String insertFeature = "INSERT INTO rental_feature (rental_id, feature) VALUES (?, ?)";

            for (String[] r : rentals) {
                int rentalId;
                try (PreparedStatement ps = conn.prepareStatement(insertRental, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, r[0]);
                    ps.setString(2, r[1]);
                    ps.setString(3, r[2]);
                    ps.setDouble(4, Double.parseDouble(r[3]));
                    ps.setDate(5, Date.valueOf(r[4]));
                    ps.executeUpdate();

                    ResultSet keys = ps.getGeneratedKeys();
                    keys.next();
                    rentalId = keys.getInt(1);
                }

                for (String feat : r[5].split(",")) {
                    try (PreparedStatement ps = conn.prepareStatement(insertFeature)) {
                        ps.setInt(1, rentalId);
                        ps.setString(2, feat.trim());
                        ps.executeUpdate();
                    }
                }

                rentalIds.add(rentalId);
                System.out.println("Rental " + rentalId + " [index " + (rentalIds.size() - 1) + "]: " + r[1] + " (" + r[0] + ")");
            }
        }

        return rentalIds;
    }

    private static void seedReviews(List<Integer> ids) throws SQLException {
        // ids index: 0=LA Apartment, 1=LA Condo, 2=SF Studio, 3=SF Loft,
        // 4=Seattle Room, 5=Seattle Suite, 6=Portland Cabin, 7=Portland Flat,
        // 8=Denver Apt, 9=LA Beach House

        // format: {ids index, reviewer_username, score, remark, review_date}
        Object[][] reviews = {
            // Q3: dan's rentals (index 6, 7) get only Excellent/Good reviews
            {ids.get(6), "alice", "Excellent", "Amazing cabin, loved it!",           "2025-10-25"},
            {ids.get(6), "bob",   "Good",      "Nice place, a bit remote.",          "2025-10-26"},
            {ids.get(7), "carol", "Excellent", "Great downtown location!",           "2025-10-25"},
            {ids.get(7), "eve",   "Good",      "Clean and well-maintained.",         "2025-10-26"},

            // Q5: eve posts reviews but ALL of them are Poor
            {ids.get(0), "eve",   "Poor",      "Overpriced for what you get.",       "2025-10-20"},
            {ids.get(2), "eve",   "Poor",      "Too small and noisy.",              "2025-10-21"},

            // Q6 testing: give alice's rental (index 0) a non-Poor review so alice stays in Q6
            {ids.get(0), "bob",   "Good",      "Decent place overall.",             "2025-10-20"},
            {ids.get(1), "dan",   "Excellent", "Beach access was great.",           "2025-10-20"},

            // Give eve's rental (index 8) a Poor review - so eve is NOT in Q6
            {ids.get(8), "alice", "Poor",      "Not as described.",                 "2025-10-25"},

            // bob's rental (index 2) gets a Fair review - bob still in Q6 (Fair is not Poor)
            {ids.get(2), "carol", "Fair",      "It was okay.",                      "2025-10-22"},

            // carol's rental (index 4) gets no reviews (stays reviewless, fine for Q6)
            // carol's rental (index 5) gets a Good review
            {ids.get(5), "dan",   "Good",      "Mountain view was beautiful.",      "2025-11-05"},

            // Extra review on alice's rental for variety
            {ids.get(1), "carol", "Good",      "Cozy and comfortable.",             "2025-10-22"},
        };

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO rental_review (rental_id, reviewer_username, score, remark, review_date) VALUES (?, ?, ?, ?, ?)";

            for (Object[] r : reviews) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, (int) r[0]);
                    ps.setString(2, (String) r[1]);
                    ps.setString(3, (String) r[2]);
                    ps.setString(4, (String) r[3]);
                    ps.setDate(5, Date.valueOf((String) r[4]));
                    ps.executeUpdate();
                }
                System.out.println("Review: " + r[1] + " reviewed rental #" + r[0] + " as " + r[2]);
            }
        }
    }
}
