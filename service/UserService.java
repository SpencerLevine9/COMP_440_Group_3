package service;

import model.ServiceResult;
import model.User;
import database.UserDAO;

public class UserService {
    private final UserDAO userDAO;
    public UserService() {
        this.userDAO = new UserDAO();
    }
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ServiceResult checkDuplicates(User user) {
        if (userDAO.usernameExists(user.getUsername())) {
            return ServiceResult.failure("Username already exists.");
        }
        if (userDAO.emailExists(user.getEmail())) {
            return ServiceResult.failure("Email already exists.");
        }
        if (userDAO.phoneExists(user.getPhone())) {
            return ServiceResult.failure("Phone number already exists.");
        }

        return ServiceResult.success("No duplicates found.");
    }

    public ServiceResult registerUser(User user) {
        boolean register = userDAO.registerUser(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone()
        );

        if (register) {
            return ServiceResult.success("Signup successful.");
        }

        return ServiceResult.failure("Signup failed. User could not be stored.");
    }

    public boolean validateLoginCredentials(String username, String password) {
        return userDAO.validateLogin(username, password);
    }
}