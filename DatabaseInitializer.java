import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:ppe_inventory.db"; // Path to your database file

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Database connected or created successfully!");
                
                // Optionally, create tables here
                Statement stmt = conn.createStatement();
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "user_id TEXT PRIMARY KEY, "
                        + "name TEXT NOT NULL, "
                        + "password TEXT NOT NULL, "
                        + "user_type TEXT NOT NULL);";
                
                stmt.execute(createUsersTable);
                System.out.println("Users table created (or already exists).");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
