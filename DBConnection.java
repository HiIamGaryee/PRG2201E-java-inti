import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("ppe_inventory.db");
        } catch (Exception e) {
            System.out.println("DB Connection Error: " + e.getMessage());
            return null;
        }
    }
}
