// Tested:
// 1. max 2 rentals/day
// 2. no self-review
// 3. no duplicate review
// 4. max 3 reviews/day
// used to sanity test rental/review backend logic during Phase 2

package database;

import java.util.Arrays;
import java.util.List;

public class TestRentalReview {
    public static void main(String[] args) {
        RentalDAO dao = new RentalDAO();

        // Basic rental creation sanity test
        List<String> features = Arrays.asList("Mountainview", "Kitchen", "Wi-Fi");

        int rentalId = dao.createRental(
                "spencer",
                "San Diego, California",
                "Temporary backend sanity test rental",
                120.00,
                features
        );

        if (rentalId > 0) {
            System.out.println("Rental created successfully. rental_id = " + rentalId);
        } else if (rentalId == -1) {
            System.out.println("Rental blocked: user already posted 2 rentals today.");
        } else {
            System.out.println("Rental insert failed.");
        }

        // Optional review sanity test only if rental insert succeeded
        if (rentalId > 0) {
            boolean reviewOk = dao.createReview(
                    rentalId,
                    "john",
                    "Good",
                    "Backend sanity review test"
            );

            System.out.println("Review inserted: " + reviewOk);
        } else {
            System.out.println("Skipping review test because no new rental was created.");
        }
    }
}