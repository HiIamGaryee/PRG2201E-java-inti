import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PPETransaction {
    private String itemCode;
    private int quantity;
    private String transactionType;
    private String sourceDestination;
    private String dateTime;

    public PPETransaction(String itemCode, int quantity, String transactionType, String sourceDestination) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.sourceDestination = sourceDestination;
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public boolean processTransaction() {
        try (Connection conn = DBConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            try {
                // Check current stock
                String checkSql = "SELECT quantity_in_boxes FROM ppe_items WHERE item_code = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, itemCode);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    throw new SQLException("Item not found");
                }

                int currentQuantity = rs.getInt("quantity_in_boxes");

                // Check stock level for distribution
                if (transactionType.equals("DISTRIBUTE") && currentQuantity < quantity) {
                    throw new SQLException("Insufficient stock");
                }

                // Calculate new quantity
                int newQuantity = transactionType.equals("RECEIVE") ? currentQuantity + quantity
                        : currentQuantity - quantity;

                // Update stock
                String updateSql = "UPDATE ppe_items SET quantity_in_boxes = ? WHERE item_code = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setString(2, itemCode);
                updateStmt.executeUpdate();

                // Record transaction
                String transSql = "INSERT INTO ppe_transactions (item_code, quantity_in_boxes, transaction_type, " +
                        "source_destination, transaction_date) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement transStmt = conn.prepareStatement(transSql);
                transStmt.setString(1, itemCode);
                transStmt.setInt(2, quantity);
                transStmt.setString(3, transactionType);
                transStmt.setString(4, sourceDestination);
                transStmt.setString(5, dateTime);
                transStmt.executeUpdate();

                // Check for low stock alert (less than 5)
                if (transactionType.equals("DISTRIBUTE") && newQuantity < 5) {
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null,
                                "Warning: Stock for item '" + itemCode + "' is critically low (" + newQuantity + ")!",
                                "Low Stock Alert", javax.swing.JOptionPane.WARNING_MESSAGE);
                    });
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet getTransactionHistory(String itemCode) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM ppe_transactions WHERE item_code = ? ORDER BY transaction_date DESC";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, itemCode);
        return pstmt.executeQuery();
    }
}