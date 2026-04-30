package database;

import service.RentalService;
import model.ServiceResult;

public class TestPhase3Queries {

    public static void main(String[] args) {
        RentalService service = new RentalService();

        // Phase 3, Query 2: Users who posted 2 different rentals on same day with feature X and feature Y
        System.out.println("=== Q2: Two features on same day (Wi-Fi, Kitchen) ===");
        ServiceResult q2 = service.findUsersByTwoFeaturesOnSameDay("Wi-Fi", "Kitchen");
        System.out.println("Success: " + q2.isSuccess());
        System.out.println("Result: " + q2.getMessage());
        System.out.println();

        // Phase 3, Query 2 (negative case): features that shouldn't match any user
        System.out.println("=== Q2: Two features on same day (Pool, Mountainview) ===");
        ServiceResult q2b = service.findUsersByTwoFeaturesOnSameDay("Pool", "Mountainview");
        System.out.println("Success: " + q2b.isSuccess());
        System.out.println("Result: " + q2b.getMessage());
        System.out.println();

        // Phase 3, Query 6: Users whose rentals never received a Poor review (or have no reviews)
        System.out.println("=== Q6: Users with no Poor reviews on their rentals ===");
        ServiceResult q6 = service.findUsersWithNoPoorReviews();
        System.out.println("Success: " + q6.isSuccess());
        System.out.println("Result: " + q6.getMessage());
    }
}
