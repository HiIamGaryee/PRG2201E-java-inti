import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TransactionHistoryPanel extends JPanel {
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public TransactionHistoryPanel() {
        setLayout(new BorderLayout());
        String[] columnNames = { "Transaction ID", "Item Code", "Quantity (Boxes)", "Type", "Source/Destination", "Date" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        loadTransactions();
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);
    }
    
    public void refresh() {
        loadTransactions();
    }

    private void loadTransactions() {
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM ppe_transactions ORDER BY transaction_date DESC")) {
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("transaction_id"),
                        rs.getString("item_code"),
                        rs.getInt("quantity"),
                        rs.getString("transaction_type"),
                        rs.getString("source_destination"),
                        rs.getString("transaction_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}