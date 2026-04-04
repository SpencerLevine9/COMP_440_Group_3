package loginpage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rentalui.MainApp;
import service.UserService;
import service.ValidationService;
import model.User;
import model.ServiceResult;

public class LoginPage extends Application {

    private Stage window;
    private final UserService userService = new UserService();
    private final ValidationService validationService = new ValidationService();

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

            ServiceResult validationResult = validationService.validateLoginFields(user, pass);
            if (!validationResult.isSuccess()) {
                message.setText(validationResult.getMessage());
                return;
            }

            boolean success = userService.validateLoginCredentials(user, pass);
            if (success) {
                new MainApp(window, user);
            } else {
                message.setText("Invalid username or password.");
            }
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
        password.setPromptText("Password");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm Password");

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
        Button backBtn = new Button("Back to Login");

        registerBtn.setOnAction(e -> {
            User newUser = new User(
                    username.getText(),
                    password.getText(),
                    confirm.getText(),
                    firstName.getText(),
                    lastName.getText(),
                    email.getText(),
                    phone.getText());

            ServiceResult validationResult = validationService.validateSignupFields(newUser);
            if (!validationResult.isSuccess()) {
                message.setText(validationResult.getMessage());
                return;
            }

            ServiceResult duplicateCheck = userService.checkDuplicates(newUser);
            if (!duplicateCheck.isSuccess()) {
                message.setText(duplicateCheck.getMessage());
                return;
            }

            ServiceResult result = userService.registerUser(newUser);
            message.setText(result.getMessage());

            if (result.isSuccess()) {
                showLoginPage();
            }
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

    public static void main(String[] args) {
        launch(args);
    }
}