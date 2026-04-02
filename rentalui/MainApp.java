package rentalui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainApp {

    private Stage window;

    // TEMPORARY sample data (will be replaced later by DB)
    private List<String> sampleRentals = Arrays.asList(
            "Cabin - Wi-Fi, Kitchen, Mountain View - $100",
            "Apartment - Wi-Fi, Parking - $80",
            "Villa - Pool, Kitchen, Wi-Fi - $200",
            "Studio - Wi-Fi, Balcony, Pet Friendly - $600",
            "House - Kitchen, Garden, Parking, Air Conditioning - $150",
            "Condo - Elevator, Free Gym, Security System - $120",
            "One Room Apartment - Washer, Dryer, Parking, Wi-Fi - $90",
            "Beach House - Kitchen, Pool, Balcony, Smoking Area - $250",
            "Mountain Cabin - Mountain View, Fireplace, Pets Allowed, Garden - $180",
            "City Loft - Wi-Fi, Air Conditioning, Elevator, Security System, Pool - $110",
            "Suburban Home - Parking, Washer, Dryer, Free Gym, Balcony - $130",
            "Downtown Studio - Kitchen, Wi-Fi, Air Conditioning, Elevator, Security System, Wheelchair Accessible - $95");

    // Constructor receives the stage from LoginPage
    public MainApp(Stage stage) {
        this.window = stage;
        window.setTitle("Rental System");
        showMainMenu();
    }

    private void showMainMenu() {
        Button postBtn = new Button("Post Rental");
        Button searchBtn = new Button("Search Rentals");
        Button reviewBtn = new Button("Write Review");

        postBtn.setOnAction(e -> showPostRental());
        searchBtn.setOnAction(e -> showSearch());
        reviewBtn.setOnAction(e -> showReview());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Main Menu"),
                postBtn,
                searchBtn,
                reviewBtn);

        window.setScene(new Scene(layout, 300, 250));
    }

    // -- POST RENTAL --
    private void showPostRental() {
        TextField title = new TextField();
        title.setPromptText("Title");

        TextField desc = new TextField();
        desc.setPromptText("Description");

        TextField feature = new TextField();
        feature.setPromptText("Feature");

        TextField price = new TextField();
        price.setPromptText("Price");

        Label message = new Label();

        Button submit = new Button("Submit");
        Button back = new Button("Back");

        submit.setOnAction(e -> {
            try {
                String t = title.getText().trim();
                String d = desc.getText().trim();
                String f = feature.getText().trim();
                String priceText = price.getText().trim();

                if (t.isEmpty() || d.isEmpty() || f.isEmpty() || priceText.isEmpty()) {
                    message.setText("All fields are required.");
                    return;
                }

                double p = Double.parseDouble(priceText);
                // TODO: call RentalService here
                message.setText("Rental submitted (placeholder)");

            } catch (Exception ex) {
                message.setText("Invalid input.");
            }
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Post Rental"),
                title, desc, feature, price,
                submit, back,
                message);

        window.setScene(new Scene(layout, 350, 300));
    }

    // -- SEARCH WINDOW --
    private void showSearch() {
        TextField featureInput = new TextField();
        featureInput.setPromptText("Enter feature(s) separated by a comma.)");

        Label hint = new Label("Please select from dropdown or type features manually (e.g. Wi-Fi, Kitchen)");

        ComboBox<String> featureDropdown = new ComboBox<>();
        featureDropdown.getItems().addAll(
                "Wi-Fi", "Kitchen", "Mountain View", "Parking", "Pool", "Air Conditioning", "Pets Allowed", "Washer",
                "Dryer", "Free Gym",
                "Security System", "Balcony", "Garden", "Wheelchair Accessible", "Elevator", "Smoking Area");
        featureDropdown.setPromptText("Select a feature");

        featureDropdown.setOnAction(e -> {
            String selected = featureDropdown.getValue();
            if (selected != null) {
                if (featureInput.getText().isEmpty()) {
                    featureInput.setText(selected);
                } else {
                    featureInput.setText(featureInput.getText() + ", " + selected);
                }
            }
        });

        ListView<String> results = new ListView<>();

        Button searchBtn = new Button("Search");
        Button back = new Button("Back");

        searchBtn.setOnAction(e -> {
            String input = featureInput.getText().toLowerCase();

            results.getItems().clear();

            if (input.isEmpty()) {
                results.getItems().add("Please enter at least one feature.");
                return;
            }

            String[] features = input.split(",");

            List<String> matches = new ArrayList<>();

            for (String rental : sampleRentals) {
                String lowerRental = rental.toLowerCase();

                boolean allMatch = true;

                for (String f : features) {
                    String trimmed = f.trim();
                    if (!lowerRental.contains(trimmed)) {
                        allMatch = false;
                        break;
                    }
                }

                if (allMatch) {
                    matches.add(rental);
                }
            }

            if (matches.isEmpty()) {
                results.getItems().add("No rentals found.");
            } else {
                results.getItems().addAll(matches);
            }
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
                back);

        window.setScene(new Scene(layout, 400, 350));
    }

    // -- REVIEW PAGE --
    private void showReview() {
        ComboBox<String> rating = new ComboBox<>();
        rating.getItems().addAll("Excellent", "Good", "Fair", "Poor");

        TextArea remark = new TextArea();
        remark.setPromptText("Write your review...");

        Label message = new Label();

        Button submit = new Button("Submit Review");
        Button back = new Button("Back");

        submit.setOnAction(e -> {
            String r = rating.getValue();
            String text = remark.getText();

            if (r == null) {
                message.setText("Select a rating.");
                return;
            }

            // TODO: call review service
            message.setText("Review submitted (placeholder)");
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Write Review"),
                rating,
                remark,
                submit,
                back,
                message);

        window.setScene(new Scene(layout, 350, 300));
    }
}