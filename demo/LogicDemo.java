package demo;

import model.User;
import model.ServiceResult;
import service.AuthService;

public class LogicDemo {

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        User newUser = new User(
                "jdoe",
                "password123",
                "password123",
                "John",
                "Doe",
                "jdoe@email.com",
                "1234567890");

        ServiceResult signupResult = authService.signUp(newUser);
        System.out.println("Signup success: " + signupResult.isSuccess());
        System.out.println("Signup message: " + signupResult.getMessage());

        ServiceResult loginResult1 = authService.login("jdoe", "password123");
        System.out.println("Login #1 success: " + loginResult1.isSuccess());
        System.out.println("Login #1 message: " + loginResult1.getMessage());

        ServiceResult loginResult2 = authService.login("jdoe", "wrongpassword");
        System.out.println("Login #2 success: " + loginResult2.isSuccess());
        System.out.println("Login #2 message: " + loginResult2.getMessage());
    }
}