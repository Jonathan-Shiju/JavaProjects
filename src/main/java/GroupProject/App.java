package GroupProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/inventorymanagement"; // Replace with your database name
        String user = "root"; // Replace with your MySQL username
        String password = "Bcnof0912$"; // Replace with your MySQL password

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database successfully!");
            if (connection == null || connection.isClosed()) {
                System.err.println("Connection is closed!");
                return; // Exit or handle the error accordingly
            }            
            Loginpage obj = new Loginpage(); // Pass the connection
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
