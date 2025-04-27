import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC"); // Optional in modern Java, but fine to include
            return DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db");
        } catch (Exception e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }
}
