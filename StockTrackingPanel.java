import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class StockTrackingPanel extends JPanel {
    private static final String DB_URL = "jdbc:sqlite:new_ppe_inventory.db";
    private static final String[] STOCK_COLUMNS = {
        "Item Code", "Item Name", "Supplier Code", "Quantity (Boxes)", "Last Received Date"
    };
    private static final String[] TRANS_COLUMNS = {
        "Transaction ID", "Item Code", "Type", "Quantity", "Source/Destination", "Date"
    };

    private DefaultTableModel stockModel;
    private JTable stockTable;
    private TableRowSorter<TableModel> sorter;
    private TransactionHistoryPanel transactionHistoryPanel;

    public StockTrackingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Current Stock
        tabbedPane.addTab("Current Stock", createCurrentStockPanel());

        // Tab 2: Transaction History
        transactionHistoryPanel = new TransactionHistoryPanel();
        tabbedPane.addTab("Transaction History", transactionHistoryPanel);

        // Refresh transaction table when switching tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                transactionHistoryPanel.refresh();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refresh() {
        loadStockData(); // stock tab
        transactionHistoryPanel.refresh(); // transaction tab
    }



    private JPanel createCurrentStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        stockModel = new DefaultTableModel(STOCK_COLUMNS, 0);
        stockTable = new JTable(stockModel);
        stockTable.setRowHeight(30);

        sorter = new TableRowSorter<>(stockModel);
        stockTable.setRowSorter(sorter);

        setLowStockRenderer(stockTable, 3);
        loadStockData();

        panel.add(new JScrollPane(stockTable), BorderLayout.CENTER);
        return panel;
    }

    private void setLowStockRenderer(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    int quantity = Integer.parseInt(value.toString());
                    c.setBackground(quantity < 25 ? Color.PINK : Color.WHITE);
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
    }

    private void loadStockData() {
        stockModel.setRowCount(0);
        String query = """
            SELECT 
                i.item_code,
                i.item_name,
                i.supplier_code,
                i.quantity_in_boxes,
                (
                    SELECT MAX(transaction_date)
                    FROM ppe_transactions t
                    WHERE t.item_code = i.item_code AND t.transaction_type = 'RECEIVE'
                ) AS last_received
            FROM ppe_items i
            ORDER BY i.item_code ASC
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stockModel.addRow(new Object[] {
                    rs.getString("item_code"),
                    rs.getString("item_name"),
                    rs.getString("supplier_code"),
                    rs.getInt("quantity_in_boxes"),
                    rs.getString("last_received") != null ? rs.getString("last_received") : "—"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load stock data:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // === Inner class for Transaction History tab ===
    private class TransactionHistoryPanel extends JPanel {
        private DefaultTableModel transModel;
        private JTable transTable;

        public TransactionHistoryPanel() {
            setLayout(new BorderLayout());

            transModel = new DefaultTableModel(TRANS_COLUMNS, 0);
            transTable = new JTable(transModel);
            transTable.setRowHeight(28);

            add(new JScrollPane(transTable), BorderLayout.CENTER);
            loadTransactionData(); // initial load
        }

        public void refresh() {
            loadTransactionData();
        }

        public void loadTransactionData() {
            transModel.setRowCount(0);

            String query = "SELECT * FROM ppe_transactions ORDER BY transaction_id ASC";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    transModel.addRow(new Object[] {
                        rs.getInt("transaction_id"),
                        rs.getString("item_code"),
                        rs.getString("transaction_type"),
                        rs.getInt("quantity"),
                        rs.getString("source_destination"),
                        rs.getString("transaction_date")
                    });
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Failed to load transactions:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
