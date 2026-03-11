package service;

import model.ServiceResult;
import model.User;

import java.util.regex.Pattern;

public class ValidationService {
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final Pattern Email_Pattern =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern Phone_Pattern =
            Pattern.compile("^\\d{10,15}$");

    public ServiceResult validateSignupFields(User user) {
        if (user == null) {
            return ServiceResult.failure("User data is missing.");
        }
        if (isBlank(user.getUsername()) ||
                isBlank(user.getPassword()) ||
                isBlank(user.getConfirmPassword()) ||
                isBlank(user.getFirstName()) ||
                isBlank(user.getLastName()) ||
                isBlank(user.getEmail()) ||
                isBlank(user.getPhone()))
        {
            return ServiceResult.failure("All fields are required.");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            return ServiceResult.failure("Password and confirm password do not match.");
        }
        if (user.getPassword().length() < 8) {
            return ServiceResult.failure("Password must be at least 8 characters long.");
        }
        if (!Phone_Pattern.matcher(user.getPhone()).matches()) {
            return ServiceResult.failure("Invalid phone number. 10 to 15 numeral digits expected.");
        }
        if (!Email_Pattern.matcher(user.getEmail()).matches()) {
            return ServiceResult.failure("Invalid email.");
        }

        return ServiceResult.success("Signup validation passed.");
    }

    public ServiceResult validateLoginFields(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return ServiceResult.failure("Both username and password are required.");
        }

        return ServiceResult.success("Login validation passed.");
    }
}

