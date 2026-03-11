package database;

public class TestLogin {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        boolean validLogin = userDAO.validateLogin("spencer", "password123");
        boolean invalidLogin = userDAO.validateLogin("spencer", "wrongpassword");

        System.out.println("Correct password: " + validLogin);
        System.out.println("Wrong password: " + invalidLogin);
    }
}