import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class StockTrackingPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private TableRowSorter<TableModel> sorter;

    public StockTrackingPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();

        // === ✅ Tab 1: Current Stock ===
        JPanel currentStockPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        // Table and model
        String[] columns = {"Item Code", "Item Name", "Supplier Code", "Quantity (Boxes)", "Last Received Date"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        // Enable sorting
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Highlight low stock rows
        TableColumn quantityColumn = table.getColumnModel().getColumn(3);
        quantityColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    int quantity = Integer.parseInt(value.toString());
                    if (quantity < 25) {
                        c.setBackground(Color.PINK);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Load stock data
        loadDataFromDatabase();

        currentStockPanel.add(topPanel, BorderLayout.NORTH);
        currentStockPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tabbedPane.addTab("Current Stock", currentStockPanel);

        // === 📄 Tab 2: Transaction History ===
        tabbedPane.addTab("Transaction History", new TransactionHistoryPanel());

        // Only tabbed pane is shown now
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refresh() {
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        model.setRowCount(0);
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

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("item_code"),
                        rs.getString("item_name"),
                        rs.getString("supplier_code"),
                        rs.getInt("quantity_in_boxes"),
                        rs.getString("last_received") != null ? rs.getString("last_received") : "—"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage());
        }
    }

    // === 📄 Transaction History Tab ===
    class TransactionHistoryPanel extends JPanel {
        private DefaultTableModel transModel;

        public TransactionHistoryPanel() {
            setLayout(new BorderLayout());
            String[] columns = {"Transaction ID", "Item Code", "Type", "Quantity", "Source/Destination", "Date"};
            transModel = new DefaultTableModel(columns, 0);
            JTable transTable = new JTable(transModel);
            transTable.setRowHeight(28);
            loadTransactions();
            add(new JScrollPane(transTable), BorderLayout.CENTER);
        }

        private void loadTransactions() {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM ppe_transactions ORDER BY transaction_date DESC")) {

                transModel.setRowCount(0);
                while (rs.next()) {
                    transModel.addRow(new Object[]{
                            rs.getInt("transaction_id"),
                            rs.getString("item_code"),
                            rs.getString("transaction_type"),
                            rs.getInt("quantity"),
                            rs.getString("source_destination"),
                            rs.getString("transaction_date")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
            }
        }
    }
}
