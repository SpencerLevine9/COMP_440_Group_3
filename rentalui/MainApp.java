package rentalui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class MainApp {

    private Stage window;
    private List<String> sampleRentals = Arrays.asList(
            "Cabin - Wi-Fi, Kitchen, Mountain View - $100",
            "Apartment - Wi-Fi, Parking - $80",
            "Villa - Pool, Kitchen, Wi-Fi - $200",
            "Studio - Wi-Fi, Balcony, Pet Friendly - $600",
            "House - Kitchen, Garden, Parking, Air Conditioning - $150",
            "Condo - Elevator, Free Gym, Security System - $120",
            "Beach House - Kitchen, Pool, Balcony - $250");

    public MainApp(Stage stage) {
        this.window = stage;
        window.setTitle("Rental System");
        showMainMenu();
    }

    // MAIN MENU
    private void showMainMenu() {

        Button postBtn = new Button("Post A Rental");
        Button searchBtn = new Button("Search Available Rentals");
        Button reviewBtn = new Button("Write A Review");

        postBtn.setMaxWidth(Double.MAX_VALUE);
        searchBtn.setMaxWidth(Double.MAX_VALUE);
        reviewBtn.setMaxWidth(Double.MAX_VALUE);

        postBtn.setOnAction(e -> showPostRental());
        searchBtn.setOnAction(e -> showSearch());
        reviewBtn.setOnAction(e -> showReview());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                new Label("Hello, welcome to our Rental Management System!"),
                new Label("What would you like to do?"),
                postBtn,
                searchBtn,
                reviewBtn);

        window.setScene(new Scene(layout, 800, 400));
    }

    // POST RENTAL PAGE
    private void showPostRental() {

        TextField title = new TextField();
        title.setPromptText("Rental Title");

        TextField desc = new TextField();
        desc.setPromptText("Rental Description");

        TextField feature = new TextField();
        feature.setPromptText("Rental Features");

        TextField price = new TextField();
        price.setPromptText("Rental Price");

        Label message = new Label();

        Button submit = new Button("Submit Rental Listing");
        Button back = new Button("Back");

        submit.setOnAction(e -> message.setText("Rental submitted successfully."));
        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                new Label("Post Rental Listing"),
                title,
                desc,
                feature,
                price,
                submit,
                back,
                message);

        window.setScene(new Scene(layout, 420, 420));
    }

    // SEARCH RENTALS PAGE
    private void showSearch() {

        TextField featureSearch = new TextField();
        featureSearch.setPromptText("Search by features (e.g. Wi-Fi, Pool)");

        TextField featureX = new TextField();
        featureX.setPromptText("Feature X");

        TextField featureY = new TextField();
        featureY.setPromptText("Feature Y");

        TextField dateField = new TextField();
        dateField.setPromptText("Enter Date (YYYY-MM-DD)");

        ListView<String> results = new ListView<>();

        Button normalSearch = new Button("Search");
        Button expensiveBtn = new Button("View Most Expensive Listings By Feature");
        Button comboBtn = new Button("Find Hosts With Features Posted on the Same Day");
        Button topHostsBtn = new Button("Top Hosts By Listing Date");
        Button trustedHostsBtn = new Button("Top Rated Hosts");
        Button newestBtn = new Button("Sort by Newest Listings First");
        Button oldestBtn = new Button("Sort by Oldest Listings First");
        Button back = new Button("Back");

        normalSearch.setOnAction(e -> {

            results.getItems().clear();

            String input = featureSearch.getText().toLowerCase();

            for (String rental : sampleRentals) {
                if (rental.toLowerCase().contains(input)) {
                    results.getItems().add(rental);
                }
            }
        });

        expensiveBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(
                    "Wi-Fi -> Studio - $600",
                    "Kitchen -> Beach House - $250",
                    "Pool -> Beach House - $250",
                    "Parking -> House - $150");
        });

        comboBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(
                    "Hosts who posted rentals same day with:",
                    featureX.getText() + " and " + featureY.getText(),
                    "JohnDoe",
                    "AliceSmith");
        });

        topHostsBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(
                    "Top hosts on " + dateField.getText(),
                    "JeffPowers",
                    "AliceSmith");
        });

        newestBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(sampleRentals);
        });

        oldestBtn.setOnAction(e -> {
            results.getItems().clear();
            for (int i = sampleRentals.size() - 1; i >= 0; i--) {
                results.getItems().add(sampleRentals.get(i));
            }
        });

        trustedHostsBtn.setOnAction(e -> {
            results.getItems().clear();
            results.getItems().addAll(
                    "Top Rated Hosts:",
                    "CleanHost1",
                    "HelpfulStay2");
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                new Label("Search Available Rentals"),
                featureSearch,
                normalSearch,
                newestBtn,
                oldestBtn,

                new Label("Advanced Search & Reports"),
                expensiveBtn,

                featureX,
                featureY,
                comboBtn,

                dateField,
                topHostsBtn,

                trustedHostsBtn,

                results,
                back);

        window.setScene(new Scene(layout, 540, 720));
    }

    // WRITE REVIEW PAGE
    private void showReview() {

        ComboBox<String> rating = new ComboBox<>();
        rating.getItems().addAll("Excellent", "Good", "Fair", "Poor");

        TextArea remark = new TextArea();
        remark.setPromptText("Write your review here...");

        ComboBox<String> reviewFilter = new ComboBox<>();
        reviewFilter.getItems().addAll("Excellent", "Good", "Fair", "Poor");
        reviewFilter.setPromptText("Please Select Review Type");

        ComboBox<String> posterFilter = new ComboBox<>();
        posterFilter.getItems().addAll("Excellent", "Good", "Fair", "Poor");
        posterFilter.setPromptText("Rental Posters Received Only...");

        Label userResults = new Label();
        Label posterResults = new Label();

        Button submit = new Button("Submit Review");
        Button showUsers = new Button("Display Users Who Posted Only This Type of Review");
        Button showPosters = new Button("Display Rental Posters Who Only Received This Type");
        Button back = new Button("Back");

        submit.setOnAction(e -> {
            userResults.setText("Review submitted successfully.");
            posterResults.setText("");
        });

        // review insights button adjusts to selected review type
        reviewFilter.setOnAction(e -> {
            String selected = reviewFilter.getValue();
            if (selected != null) {
                showUsers.setText("Display Users Who Posted Only " + selected + " Reviews");
            }
        });

        // poster reputation button adjusts to selected review type
        posterFilter.setOnAction(e -> {
            String selected = posterFilter.getValue();
            if (selected != null) {
                showPosters.setText("Display Rental Posters Who Only Received " + selected + " Reviews");
            }
        });

        showUsers.setOnAction(e -> {

            String selected = reviewFilter.getValue();

            if (selected == null) {
                userResults.setText("Please select a review type first.");
                return;
            }

            userResults.setText(
                    "Users who posted only " + selected + " reviews:\n\n" +
                            "SampleUser1\n" +
                            "SampleUser2");
        });

        showPosters.setOnAction(e -> {

            String selected = posterFilter.getValue();

            if (selected == null) {
                posterResults.setText("Select a rental poster filter first.");
                return;
            }

            posterResults.setText(
                    "Rental posters whose units only received " + selected + " reviews:\n\n" +
                            "TrustedHost1\n" +
                            "TrustedHost2");
        });

        back.setOnAction(e -> showMainMenu());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                new Label("Write Review"),
                rating,
                remark,
                submit,

                new Label("Review Insights"),
                reviewFilter,
                showUsers,
                userResults,

                new Label("Rental Poster Reputation"),
                posterFilter,
                showPosters,
                posterResults,

                back);
        window.setScene(new Scene(layout, 500, 760));
    }
}