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

    private void showMainMenu() {
        Label welcome = new Label("Main Menu");
        Label userLabel = new Label("Logged in as: " + currentUsername);

        Button postBtn = new Button("Post Rental");
        Button searchBtn = new Button("Search Rentals");
        Button reviewBtn = new Button("Write Review");
        Button expensiveBtn = new Button("Most Expensive Rentals");
        Button topUsersBtn = new Button("Top Posters By Date");

        postBtn.setOnAction(e -> showPostRental());
        searchBtn.setOnAction(e -> showSearch());
        reviewBtn.setOnAction(e -> showReview(""));
        expensiveBtn.setOnAction(e -> showMostExpensiveRentals());
        topUsersBtn.setOnAction(e -> showTopPostingUsersByDate());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                welcome,
                userLabel,
                postBtn,
                searchBtn,
                reviewBtn,
                expensiveBtn,
                topUsersBtn
        );

        window.setScene(new Scene(layout, 320, 360));
    }

    private void showPostRental() {
        TextField title = new TextField();
        title.setPromptText("Title");

        TextField desc = new TextField();
        desc.setPromptText("Description");

        TextField features = new TextField();
        features.setPromptText("Features (comma-separated)");

        TextField price = new TextField();
        price.setPromptText("Price");

        Label message = new Label();

        Button submit = new Button("Submit");
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
                new Label("Post Rental"),
                title,
                desc,
                features,
                price,
                submit,
                back,
                message
        );

        window.setScene(new Scene(layout, 380, 320));
    }

    private void showSearch() {
        TextField featureInput = new TextField();
        featureInput.setPromptText("Enter one feature");

        Label hint = new Label("Type one feature or pick one from the dropdown.");

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
        featureDropdown.setPromptText("Select a feature");

        featureDropdown.setOnAction(e -> {
            String selected = featureDropdown.getValue();
            if (selected != null) {
                featureInput.setText(selected);
            }
        });

        ListView<String> results = new ListView<>();
        results.setPrefHeight(180);

        Label message = new Label();

        Button searchBtn = new Button("Search");
        Button reviewSelectedBtn = new Button("Review Selected Rental");
        Button back = new Button("Back");

        searchBtn.setOnAction(e -> {
            String feature = featureInput.getText().trim();

            results.getItems().clear();
            message.setText("");

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
                new Label("Search Rentals"),
                hint,
                featureDropdown,
                featureInput,
                searchBtn,
                results,
                reviewSelectedBtn,
                back,
                message
        );

        window.setScene(new Scene(layout, 540, 430));
    }

    private void showReview(String prefilledRentalId) {
        TextField rentalIdField = new TextField();
        rentalIdField.setPromptText("Rental ID");
        rentalIdField.setText(prefilledRentalId);

        ComboBox<String> rating = new ComboBox<>();
        rating.getItems().addAll("Excellent", "Good", "Fair", "Poor");
        rating.setPromptText("Select a rating");

        TextArea remark = new TextArea();
        remark.setPromptText("Write your review...");
        remark.setPrefRowCount(4);

        Label message = new Label();

        Button submit = new Button("Submit Review");
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

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Write Review"),
                rentalIdField,
                rating,
                remark,
                submit,
                back,
                message
        );

        window.setScene(new Scene(layout, 380, 340));
    }


    private void showMostExpensiveRentals() {
        ListView<String> results = new ListView<>();
        results.setPrefHeight(220);

        Button loadBtn = new Button("Load Results");
        Button back = new Button("Back");

        loadBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(rentalService.getMostExpensiveRentalsPerFeature());
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Most Expensive Rental Units For Each Feature"),
                loadBtn,
                results,
                back
        );

        window.setScene(new Scene(layout, 560, 350));
    }


    private void showTopPostingUsersByDate() {
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD");

        ListView<String> results = new ListView<>();
        results.setPrefHeight(220);

        Label message = new Label();

        Button searchBtn = new Button("Search");
        Button back = new Button("Back");

        searchBtn.setOnAction(e -> {
            results.getItems().clear();
            message.setText("");

            String dateText = dateField.getText().trim();
            if (dateText.isEmpty()) {
                message.setText("Please enter a date.");
                return;
            }

            List<String> matches = rentalService.getTopPostingUsersByDate(dateText);
            results.getItems().addAll(matches);
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Users Who Posted The Most Rentals On A Specific Date"),
                dateField,
                searchBtn,
                results,
                back,
                message
        );

        window.setScene(new Scene(layout, 560, 350));
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