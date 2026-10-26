import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * Handles JDBC connection to the MySQL database.
 */
public class DBConnection {

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/college_circular_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Change this to your MySQL password

    /**
     * Obtains and returns an active Connection object to MySQL.
     * @return Connection
     * @throws SQLException if database connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
