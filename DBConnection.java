import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:new_ppe_inventory.db";
    private static Connection conn = null;

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("🚫 JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL);
                System.out.println("✅ Connected to database: " + DB_URL);
            }
            return conn;
        } catch (SQLException e) {
            System.err.println("🚫 DB Connection Error: " + e.getMessage());
            return null;
        }
    }

    // Optional: close connection
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("🚫 Error closing connection: " + e.getMessage());
        }
    }
}
