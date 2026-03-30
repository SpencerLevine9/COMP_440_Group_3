package database;

public class TestUserInsert {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        boolean success = userDAO.registerUser(
                "spencer",
                "password123",
                "Spencer",
                "Levine",
                "spencer@email.com",
                "1234567890"
        );

        if(success){
            System.out.println("User inserted successfully!");
        } else {
            System.out.println("User insert failed.");
        }

    }
}