import java.sql.Connection;
import java.sql.DriverManager;

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
            Class.forName("org.sqlite.JDBC"); // Optional in modern Java, but fine to include
            return DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db");
        } catch (Exception e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }
}
