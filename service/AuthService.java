package service;

import model.ServiceResult;
import model.User;

public class AuthService {
    private final ValidationService validationService;
    private final UserService userService;
    public AuthService() {
        this.validationService = new ValidationService();
        this.userService = new UserService();
    }
    public AuthService(ValidationService validationService, UserService userService) {
        this.validationService = validationService;
        this.userService = userService;
    }

    public ServiceResult signUp(User user) {
        ServiceResult validationResult = validationService.validateSignupFields(user);
        if (!validationResult.isSuccess()) {
            return validationResult;
        }

        ServiceResult duplicateResult = userService.checkDuplicates(user);
        if(!duplicateResult.isSuccess()) {
            return duplicateResult;
        }

        return userService.registerUser(user);
    }

    public ServiceResult login(String username, String password) {
        ServiceResult validationResult = validationService.validateLoginFields(username, password);
        if(!validationResult.isSuccess()) {
            return validationResult;
        }

        boolean valid = userService.validateLoginCredentials(username, password);
        if (valid) {
            return ServiceResult.success("Login successful.");
        }

        return ServiceResult.failure("Invalid username or password.");
    }
}