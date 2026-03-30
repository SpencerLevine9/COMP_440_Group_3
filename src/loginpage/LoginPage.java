package loginpage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage extends Application {

    private Stage window;

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("System Login Page");

        showLoginPage();

        window.show();
    }

    private void showLoginPage() {

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Label message = new Label();

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Sign Up");

        loginBtn.setOnAction(e -> {

            String user = username.getText();
            String pass = password.getText();

            if (user.length() < 8) {
                message.setText("Username must be at least 8 characters.");
                return;
            }

            if (!validPassword(pass)) {
                message.setText(
                        "Password must have a minimum of 8 characters with one capital letter, one number, and one symbol.");
                return;
            }

            message.setText("Login Successful!");
        });

        signupBtn.setOnAction(e -> showSignupPage());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Login"),
                username,
                password,
                loginBtn,
                signupBtn,
                message);

        Scene scene = new Scene(layout, 300, 250);
        window.setScene(scene);
    }

    private void showSignupPage() {

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password:");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm Password:");

        TextField firstName = new TextField();
        firstName.setPromptText("First Name");

        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");

        TextField email = new TextField();
        email.setPromptText("Email Address");

        TextField phone = new TextField();
        phone.setPromptText("Phone Number");

        Label message = new Label();

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Go back to the Login Page");

        registerBtn.setOnAction(e -> {

            String user = username.getText();
            String pass = password.getText();
            String conf = confirm.getText();
            String em = email.getText();
            String ph = phone.getText();

            if (user.length() < 8) {
                message.setText("Username must be at least 8 characters.");
                return;
            }

            if (!validPassword(pass)) {
                message.setText(
                        "Password must have a minimum of 8 characters, with one capital letter, one number, and one symbol.");
                return;
            }

            if (!pass.equals(conf)) {
                message.setText("Passwords do not match.");
                return;
            }

            if (!validEmail(em)) {
                message.setText("Invalid email format.");
                return;
            }

            if (!ph.matches("\\d{10}")) {
                message.setText("Phone number must be exactly 10 digits.");
                return;
            }

            message.setText("Registration Successful!");
        });

        backBtn.setOnAction(e -> showLoginPage());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        layout.getChildren().addAll(
                new Label("Sign Up"),
                username,
                password,
                confirm,
                firstName,
                lastName,
                email,
                phone,
                registerBtn,
                backBtn,
                message);

        Scene scene = new Scene(layout, 350, 450);
        window.setScene(scene);
    }

    private boolean validPassword(String pass) {
        return pass.length() >= 8 &&
                pass.matches(".*[A-Z].*") &&
                pass.matches(".*[0-9].*") &&
                pass.matches(".*[^a-zA-Z0-9].*");
    }

    private boolean validEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(gmail|yahoo|outlook|hotmail)\\.com$");
    }

    public static void main(String[] args) {
        launch(args);
    }
}