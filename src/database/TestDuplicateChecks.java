package database;

public class TestDuplicateChecks {

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        System.out.println("Username exists: " + userDAO.usernameExists("spencer"));
        System.out.println("Email exists: " + userDAO.emailExists("spencer@email.com"));
        System.out.println("Phone exists: " + userDAO.phoneExists("1234567890"));
    }
}