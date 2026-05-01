package database;

import service.RentalService;
import model.ServiceResult;

import java.util.List;

public class TestPhase3Queries {

    public static void main(String[] args) {
        RentalService service = new RentalService();

        // Phase 3, Query 1: Most expensive rental unit(s) for each feature
        System.out.println("=== Q1: Most expensive rentals per feature ===");
        List<String> q1 = service.getMostExpensiveRentalsPerFeature();
        for (String row : q1) {
            System.out.println(row);
        }
        System.out.println("(expected: Wi-Fi -> LA Beach House $350, Parking -> SF Loft $300, " +
                "Kitchen -> Seattle Suite $250, Pool -> LA Beach House $350, Mountainview -> SF Loft $300)");
        System.out.println();

        // Phase 3, Query 2: Users who posted 2 different rentals on same day with feature X and feature Y
        System.out.println("=== Q2: Two features on same day (Wi-Fi, Kitchen) ===");
        ServiceResult q2 = service.findUsersByTwoFeaturesOnSameDay("Wi-Fi", "Kitchen");
        System.out.println("Success: " + q2.isSuccess());
        System.out.println("Result: " + q2.getMessage());
        // alice: rentals 0/1 on 2025-10-15 split Wi-Fi and Kitchen
        // dan: rentals 6/7 on 2025-10-20 split Wi-Fi and Kitchen
        System.out.println("(expected: alice, dan)");
        System.out.println();

        // Phase 3, Query 2 (negative case): features that shouldn't match any user
        System.out.println("=== Q2: Two features on same day (Pool, Mountainview) ===");
        ServiceResult q2b = service.findUsersByTwoFeaturesOnSameDay("Pool", "Mountainview");
        System.out.println("Success: " + q2b.isSuccess());
        System.out.println("Result: " + q2b.getMessage());
        System.out.println("(expected: no users found)");
        System.out.println();

        // Phase 3, Query 3: Rentals by user X where all reviews are Excellent or Good
        System.out.println("=== Q3: User dan rentals with all Excellent/Good reviews ===");
        List<String> q3 = service.findRentalsByUserWithAllExcellentOrGood("dan");
        for (String row : q3) {
            System.out.println(row);
        }
        System.out.println("(expected: Portland Cabin and Portland Flat)");
        System.out.println();

        // Phase 3, Query 3 (negative case): user whose rental received a Poor review
        System.out.println("=== Q3: User eve (rental 8 has a Poor review) ===");
        List<String> q3b = service.findRentalsByUserWithAllExcellentOrGood("eve");
        for (String row : q3b) {
            System.out.println(row);
        }
        System.out.println("(expected: No rentals found for user eve.)");
        System.out.println();

        // Phase 3, Query 4: Users who posted the most rentals on a specific date (with ties)
        System.out.println("=== Q4: Top posters on 2025-10-15 ===");
        List<String> q4 = service.getTopPostingUsersByDate("2025-10-15");
        for (String row : q4) {
            System.out.println(row);
        }
        System.out.println("(expected: alice and bob, both with 2)");
        System.out.println();

        // Phase 3, Query 4 (single winner case)
        System.out.println("=== Q4: Top posters on 2025-10-20 ===");
        List<String> q4b = service.getTopPostingUsersByDate("2025-10-20");
        for (String row : q4b) {
            System.out.println(row);
        }
        System.out.println("(expected: dan only, with 2)");
        System.out.println();

        // Phase 3, Query 5: Users who posted some reviews and every one of them is Poor
        System.out.println("=== Q5: Users with all Poor reviews ===");
        ServiceResult q5 = service.findUsersWithAllPoorReviews();
        System.out.println("Success: " + q5.isSuccess());
        System.out.println("Result: " + q5.getMessage());
        // eve gives Poor on rentals 0 and 2 and has no other reviews.
        // alice has an Excellent on rental 6 in addition to her Poor on rental 8, so she does not qualify.
        System.out.println("(expected: eve)");
        System.out.println();

        // Phase 3, Query 6: Users whose rentals never received a Poor review (or have no reviews)
        System.out.println("=== Q6: Users with no Poor reviews on their rentals ===");
        ServiceResult q6 = service.findUsersWithNoPoorReviews();
        System.out.println("Success: " + q6.isSuccess());
        System.out.println("Result: " + q6.getMessage());
        System.out.println("(expected: carol and dan)");
    }
}
