package service;

import database.RentalDAO;
import model.ServiceResult;

import java.util.ArrayList;
import java.util.List;


public class RentalService {

    private final RentalDAO rentalDAO;

    public RentalService() {
        this.rentalDAO = new RentalDAO();
    }

    public RentalService(RentalDAO rentalDAO) {
        this.rentalDAO = rentalDAO;
    }

    public ServiceResult postRental(String username,
                                    String title,
                                    String description,
                                    String priceText,
                                    String featuresText) {
        if (isBlank(username)) {
            return new ServiceResult(false, "No logged-in user was found");
        }
        if (isBlank(title) || isBlank(description) || isBlank(priceText) || isBlank(featuresText)) {
            return new ServiceResult(false, "All rental fields are required.");
        }

        double price;
        try {
            price = Double.parseDouble(priceText.trim());
        } catch (NumberFormatException e) {
            return new ServiceResult(false, "Price must be a valid number.");
        }
        if (price < 0) {
            return new ServiceResult(false, "Price cannot be negative.");
        }

        List<String> features = parseFeatures(featuresText);
        if (features.isEmpty()) {
            return new ServiceResult(false, "Please enter at least one feature.");
        }

        int result = rentalDAO.createRental(username, title.trim(), description.trim(), price, features);
        if (result > 0) {
            return new ServiceResult(true, "Rental posted successfully. New rental ID: " + result);
        }
        if (result == -1) {
            return new ServiceResult(false, "You can post at most 2 rentals per day.");
        }
        return new ServiceResult(false, "Could not post rental due to a database or system error.");
    }

    public List<String> searchByFeature(String feature) {
        if (isBlank(feature)) {
            return new ArrayList<>();
        }
        return rentalDAO.searchRentalsByFeature(feature.trim());
    }

    public ServiceResult submitReview(String rentalIDText,
                                      String reviewerUsername,
                                      String score,
                                      String remark) {
        if (isBlank(reviewerUsername)) {
            return new ServiceResult(false, "No logged-in user was found.");
        }
        if (isBlank(rentalIDText) || isBlank(score) || isBlank(remark)) {
            return new ServiceResult(false, "Rental ID, score, and remark are required.");
        }

        int rentalID;
        try {
            rentalID = Integer.parseInt(rentalIDText.trim());
        } catch (NumberFormatException e) {
            return new ServiceResult(false, "Rental ID must be a valid integer.");
        }

        String cleanScore = score.trim();
        if (!isValidScore(cleanScore)) {
            return new ServiceResult(false, "Score must be Excellent, Good, Fair, or Poor.");
        }

        if (rentalDAO.isOwnRental(reviewerUsername, rentalID)) {
            return new ServiceResult(false, "You cannot review your own rental.");
        }
        if (rentalDAO.hasAlreadyReviewed(reviewerUsername, rentalID)) {
            return new ServiceResult(false, "You have already reviewed this rental.");
        }
        if (!rentalDAO.canReviewToday(reviewerUsername)) {
            return new ServiceResult(false, "You can write at most 3 reviews per day.");
        }

        boolean success = rentalDAO.createReview(rentalID, reviewerUsername, cleanScore, remark.trim());
        if (success) {
            return new ServiceResult(true, "Review submitted successfully.");
        }
        return new ServiceResult(false, "Could not submit review.");
    }

    public List<String> getMostExpensiveRentalsPerFeature() {
        return rentalDAO.getMostExpensiveRentalsPerFeature();
    }

    
    public List<String> getTopPostingUsersByDate(String dateText) {
        List<String> results = new ArrayList<>();

        if (isBlank(dateText)) {
            results.add("Please enter a date.");
            return results;
        }

        try {
            java.sql.Date selectedDate = java.sql.Date.valueOf(dateText.trim());
            return rentalDAO.getTopPostingUsersByDate(selectedDate);
        } catch (IllegalArgumentException e) {
            results.add("Invalid date format. Use YYYY-MM-DD.");
            return results;
        }
    }


    private List<String> parseFeatures(String featuresText) {
        List<String> features = new ArrayList<>();

        String[] parts = featuresText.split(",");

        for (String part : parts) {
            String clean = part.trim();
            if (!clean.isEmpty()) {
                features.add(clean);
            }
        }
        return features;
    }

    private boolean isValidScore(String score) {
        return score.equals("Excellent")
                || score.equals("Good")
                || score.equals("Fair")
                || score.equals("Poor");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}