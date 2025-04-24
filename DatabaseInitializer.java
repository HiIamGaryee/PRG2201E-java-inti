import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create PPE Items table
            String createPPEItemsTable = "CREATE TABLE IF NOT EXISTS ppe_items (" +
                    "item_code TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "description TEXT," +
                    "quantity INTEGER NOT NULL," +
                    "min_stock_level INTEGER NOT NULL," +
                    "unit TEXT NOT NULL," +
                    "category TEXT NOT NULL" +
                    ")";
            
            // Create PPE Transactions table
            String createPPETransactionsTable = "CREATE TABLE IF NOT EXISTS ppe_transactions (" +
                    "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "item_code TEXT NOT NULL," +
                    "quantity INTEGER NOT NULL," +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "transaction_type TEXT NOT NULL," +
                    "source_destination TEXT NOT NULL," +
                    "notes TEXT," +
                    "FOREIGN KEY (item_code) REFERENCES ppe_items(item_code)" +
                    ")";
            
            stmt.execute(createPPEItemsTable);
            stmt.execute(createPPETransactionsTable);
            
            System.out.println("Database tables created successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
        }
    }
}
