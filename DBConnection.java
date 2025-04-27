import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:ppe_inventory.db";
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Load the SQLite JDBC driver
                Class.forName("org.sqlite.JDBC");

                // Establish connection
                conn = DriverManager.getConnection(DB_URL);
                System.out.println("✅ Connected to database: " + DB_URL);
            } catch (ClassNotFoundException e) {
                System.err.println("🚫 JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("🚫 DB Connection Error: " + e.getMessage());
            }
        }
        return conn;
    }

    // Optional: close connection
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("🚫 Error closing DB: " + e.getMessage());
        }
    }
}
