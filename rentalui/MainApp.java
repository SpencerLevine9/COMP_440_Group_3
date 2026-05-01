package rentalui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ServiceResult;
import service.RentalService;

import java.util.List;

public class MainApp {

    private final Stage window;
    private final RentalService rentalService;
    private final String currentUsername;

    // Temporary overload so this still compiles even before LoginPage is updated.
    public MainApp(Stage stage) {
        this(stage, "demoUser");
    }

    public MainApp(Stage stage, String currentUsername) {
        this.window = stage;
        this.currentUsername = currentUsername;
        this.rentalService = new RentalService();

        window.setTitle("Rental System");
        showMainMenu();
    }

    // MAIN MENU
    private void showMainMenu() {
        Label welcome = new Label("Hello, welcome to our Rental Management System!");
        Label userLabel = new Label("Logged in as: " + currentUsername);
        Label prompt = new Label("What would you like to do?");

        Button postBtn = new Button("Post A Rental");
        Button searchBtn = new Button("Search Available Rentals");
        Button reviewBtn = new Button("Write A Review");

        postBtn.setMaxWidth(Double.MAX_VALUE);
        searchBtn.setMaxWidth(Double.MAX_VALUE);
        reviewBtn.setMaxWidth(Double.MAX_VALUE);

        postBtn.setOnAction(e -> showPostRental());
        searchBtn.setOnAction(e -> showSearch());
        reviewBtn.setOnAction(e -> showReview(""));

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                welcome,
                userLabel,
                prompt,
                postBtn,
                searchBtn,
                reviewBtn);

        window.setScene(new Scene(layout, 480, 320));
    }

    // POST RENTAL PAGE
    private void showPostRental() {
        TextField title = new TextField();
        title.setPromptText("Rental Title");

        TextField desc = new TextField();
        desc.setPromptText("Rental Description");

        TextField features = new TextField();
        features.setPromptText("Features (comma-separated)");

        TextField price = new TextField();
        price.setPromptText("Rental Price");

        Label message = new Label();

        Button submit = new Button("Submit Rental Listing");
        Button back = new Button("Back");

        submit.setOnAction(e -> {
            ServiceResult result = rentalService.postRental(
                    currentUsername,
                    title.getText(),
                    desc.getText(),
                    price.getText(),
                    features.getText()
            );

            message.setText(result.getMessage());

            if (result.isSuccess()) {
                title.clear();
                desc.clear();
                features.clear();
                price.clear();
            }
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Post Rental Listing"),
                title,
                desc,
                features,
                price,
                submit,
                back,
                message);

        window.setScene(new Scene(layout, 420, 420));
    }

    // SEARCH RENTALS PAGE
    private void showSearch() {
        TextField featureSearch = new TextField();
        featureSearch.setPromptText("Search by feature (e.g. Wi-Fi)");

        ComboBox<String> featureDropdown = new ComboBox<>();
        featureDropdown.getItems().addAll(
                "Wi-Fi",
                "Kitchen",
                "Mountainview",
                "Parking",
                "Pool",
                "Air Conditioning",
                "Pets Allowed",
                "Washer",
                "Dryer",
                "Free Gym",
                "Security System",
                "Balcony",
                "Garden",
                "Wheelchair Accessible",
                "Elevator",
                "Smoking Area"
        );
        featureDropdown.setPromptText("Or pick a feature from the list");

        featureDropdown.setOnAction(e -> {
            String selected = featureDropdown.getValue();
            if (selected != null) {
                featureSearch.setText(selected);
            }
        });

        TextField featureX = new TextField();
        featureX.setPromptText("Feature X");

        TextField featureY = new TextField();
        featureY.setPromptText("Feature Y");

        TextField userXField = new TextField();
        userXField.setPromptText("Username (for Excellent/Good report)");

        TextField dateField = new TextField();
        dateField.setPromptText("Enter Date (YYYY-MM-DD)");

        ListView<String> results = new ListView<>();
        results.setPrefHeight(180);

        Label message = new Label();

        Button normalSearch = new Button("Search");
        Button newestBtn = new Button("Sort By Newest Listings First");
        Button oldestBtn = new Button("Sort By Oldest Listings First");
        Button expensiveBtn = new Button("View Most Expensive Listings By Feature");
        Button comboBtn = new Button("Find Hosts With Features Posted On The Same Day");
        Button excellentGoodBtn = new Button("Show User's Rentals With Only Excellent/Good Reviews");
        Button topHostsBtn = new Button("Top Hosts By Listing Date");
        Button trustedHostsBtn = new Button("Top Rated Hosts");
        Button reviewSelectedBtn = new Button("Review Selected Rental");
        Button back = new Button("Back");

        normalSearch.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            String feature = featureSearch.getText().trim();
            if (feature.isEmpty()) {
                message.setText("Please enter a feature.");
                return;
            }

            List<String> matches = rentalService.searchByFeature(feature);
            if (matches.isEmpty()) {
                results.getItems().add("No rentals found.");
            } else {
                results.getItems().addAll(matches);
            }
        });

        newestBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            List<String> matches = rentalService.getAllRentalsSortedByDate(false);
            if (matches.isEmpty()) {
                results.getItems().add("No rentals found.");
            } else {
                results.getItems().addAll(matches);
            }
        });

        oldestBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            List<String> matches = rentalService.getAllRentalsSortedByDate(true);
            if (matches.isEmpty()) {
                results.getItems().add("No rentals found.");
            } else {
                results.getItems().addAll(matches);
            }
        });

        // Phase 3 Query 1
        expensiveBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");
            results.getItems().addAll(rentalService.getMostExpensiveRentalsPerFeature());
        });

        // Phase 3 Query 2
        comboBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            ServiceResult sr = rentalService.findUsersByTwoFeaturesOnSameDay(
                    featureX.getText(), featureY.getText());

            if (sr.isSuccess()) {
                for (String name : sr.getMessage().split(",\\s*")) {
                    results.getItems().add(name);
                }
            } else {
                message.setText(sr.getMessage());
            }
        });

        // Phase 3 Query 3
        excellentGoodBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            List<String> matches = rentalService.findRentalsByUserWithAllExcellentOrGood(userXField.getText());
            results.getItems().addAll(matches);
        });

        // Phase 3 Query 4
        topHostsBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            String dateText = dateField.getText().trim();
            if (dateText.isEmpty()) {
                message.setText("Please enter a date.");
                return;
            }

            results.getItems().addAll(rentalService.getTopPostingUsersByDate(dateText));
        });

        // Phase 3 Query 6
        trustedHostsBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            ServiceResult sr = rentalService.findUsersWithNoPoorReviews();

            if (sr.isSuccess()) {
                for (String name : sr.getMessage().split(",\\s*")) {
                    results.getItems().add(name);
                }
            } else {
                message.setText(sr.getMessage());
            }
        });

        reviewSelectedBtn.setOnAction(e -> {
            String selected = results.getSelectionModel().getSelectedItem();

            if (selected == null || selected.equals("No rentals found.")) {
                message.setText("Please select a rental from the results first.");
                return;
            }

            String rentalId = extractRentalId(selected);
            if (rentalId == null) {
                message.setText("Could not determine the rental ID from the selected row.");
                return;
            }

            showReview(rentalId);
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Search Available Rentals"),
                featureSearch,
                featureDropdown,
                normalSearch,
                newestBtn,
                oldestBtn,

                new Label("Advanced Search and Reports"),
                expensiveBtn,

                featureX,
                featureY,
                comboBtn,

                userXField,
                excellentGoodBtn,

                dateField,
                topHostsBtn,

                trustedHostsBtn,

                results,
                reviewSelectedBtn,
                back,
                message);

        window.setScene(new Scene(layout, 560, 760));
    }

    // WRITE REVIEW PAGE
    private void showReview(String prefilledRentalId) {
        TextField rentalIdField = new TextField();
        rentalIdField.setPromptText("Rental ID");
        rentalIdField.setText(prefilledRentalId);

        ComboBox<String> rating = new ComboBox<>();
        rating.getItems().addAll("Excellent", "Good", "Fair", "Poor");
        rating.setPromptText("Select a rating");

        TextArea remark = new TextArea();
        remark.setPromptText("Write your review here...");
        remark.setPrefRowCount(4);

        Label message = new Label();

        ComboBox<String> reviewFilter = new ComboBox<>();
        reviewFilter.getItems().addAll("Excellent", "Good", "Fair", "Poor");
        reviewFilter.setPromptText("Please Select Review Type");

        Label userResults = new Label();
        Label posterResults = new Label();

        Button submit = new Button("Submit Review");
        Button showUsers = new Button("Display Users Who Posted Only This Type Of Review");
        Button showPosters = new Button("Display Hosts Who Have Never Received A Poor Review");
        Button back = new Button("Back");

        submit.setOnAction(e -> {
            ServiceResult result = rentalService.submitReview(
                    rentalIdField.getText(),
                    currentUsername,
                    rating.getValue(),
                    remark.getText()
            );

            message.setText(result.getMessage());

            if (result.isSuccess()) {
                rentalIdField.clear();
                rating.setValue(null);
                remark.clear();
            }
        });

        reviewFilter.setOnAction(e -> {
            String selected = reviewFilter.getValue();
            if (selected != null) {
                showUsers.setText("Display Users Who Posted Only " + selected + " Reviews");
            }
        });

        // Phase 3 Query 6 (alternate entry point on the review page)
        showPosters.setOnAction(e -> {
            posterResults.setText("");

            ServiceResult sr = rentalService.findUsersWithNoPoorReviews();

            if (sr.isSuccess()) {
                posterResults.setText("Hosts with no Poor reviews:\n\n" + sr.getMessage());
            } else {
                posterResults.setText(sr.getMessage());
            }
        });

        // Phase 3 Query 5: fixed on "Poor".
        showUsers.setOnAction(e -> {
            String selected = reviewFilter.getValue();

            if (selected == null) {
                userResults.setText("Please select a review type first.");
                return;
            }

            if (!"Poor".equals(selected)) {
                userResults.setText("This report is only defined for Poor reviews. Please select Poor.");
                return;
            }

            ServiceResult sr = rentalService.findUsersWithAllPoorReviews();

            if (sr.isSuccess()) {
                userResults.setText("Users who posted only Poor reviews:\n\n" + sr.getMessage());
            } else {
                userResults.setText(sr.getMessage());
            }
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Write Review"),
                rentalIdField,
                rating,
                remark,
                submit,
                message,

                new Label("Review Insights"),
                reviewFilter,
                showUsers,
                userResults,

                new Label("Host Reputation"),
                showPosters,
                posterResults,

                back);

        window.setScene(new Scene(layout, 520, 800));
    }

    private String extractRentalId(String row) {
        String prefix = "Rental ID: ";
        int start = row.indexOf(prefix);

        if (start < 0) {
            return null;
        }

        start += prefix.length();
        int end = row.indexOf(" |", start);

        if (end < 0) {
            return null;
        }

        return row.substring(start, end).trim();
    }
}
