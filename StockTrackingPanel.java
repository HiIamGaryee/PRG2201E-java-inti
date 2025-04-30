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

        // Search + Export Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        JButton exportButton = new JButton("Export to CSV");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(exportButton, BorderLayout.EAST);

        // Table and model
        String[] columns = {"Item Code", "Item Name", "Quantity (Boxes)"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        // Enable sorting
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Cell renderer for quantity alerts
        TableColumn quantityColumn = table.getColumnModel().getColumn(2);
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

        // Event listeners
        searchButton.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (!text.isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        resetButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        exportButton.addActionListener(e -> exportToCSV());

        // Load data into model
        loadDataFromDatabase();

        currentStockPanel.add(topPanel, BorderLayout.NORTH);
        currentStockPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tabbedPane.addTab("Current Stock", currentStockPanel);

        // === 🔍 Tab 2: Search & Filter Panel ===
        tabbedPane.addTab("Search & Filter", new SearchPanel());

        // === 📄 Tab 3: Transaction History ===
        tabbedPane.addTab("Transaction History", new TransactionHistoryPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String query = "SELECT item_code, item_name, quantity_in_boxes FROM ppe_items ORDER BY item_code ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String code = rs.getString("item_code");
                String name = rs.getString("item_name");
                int quantity = rs.getInt("quantity_in_boxes");
                model.addRow(new Object[]{code, name, quantity});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage());
        }
    }

    private void exportToCSV() {
        try (FileWriter csv = new FileWriter("stock_export.csv")) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "\n" : ","));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    csv.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "\n" : ","));
                }
            }

            JOptionPane.showMessageDialog(this, "Exported to stock_export.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage());
        }
    }

    // === 🔍 SearchPanel as inner class ===
    class SearchPanel extends JPanel {
        private DefaultTableModel searchModel;
        private JTable searchTable;

        public SearchPanel() {
            setLayout(new BorderLayout());

            JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField searchField = new JTextField(20);
            JButton searchBtn = new JButton("Search");

            searchBar.add(new JLabel("Search item:"));
            searchBar.add(searchField);
            searchBar.add(searchBtn);

            String[] cols = {"Item Code", "Item Name", "Quantity (Boxes)"};
            searchModel = new DefaultTableModel(cols, 0);
            searchTable = new JTable(searchModel);
            searchTable.setRowHeight(28);
            searchTable.setAutoCreateRowSorter(true);

            add(searchBar, BorderLayout.NORTH);
            add(new JScrollPane(searchTable), BorderLayout.CENTER);

            loadSearchResults("");

            searchBtn.addActionListener(e -> {
                String keyword = searchField.getText().trim();
                loadSearchResults(keyword);
            });
        }

        private void loadSearchResults(String keyword) {
            searchModel.setRowCount(0);
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String query = "SELECT item_code, item_name, quantity_in_boxes FROM ppe_items WHERE item_code LIKE ? OR item_name LIKE ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                String pattern = "%" + keyword + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    searchModel.addRow(new Object[]{
                            rs.getString("item_code"),
                            rs.getString("item_name"),
                            rs.getInt("quantity_in_boxes")
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
            }
        }
    }

    // === 📄 TransactionHistoryPanel as inner class ===
    class TransactionHistoryPanel extends JPanel {
        private DefaultTableModel transModel;

        public TransactionHistoryPanel() {
            setLayout(new BorderLayout());

            String[] columns = {"ID", "Item Code", "Type", "Quantity", "Source/Destination", "Date"};
            transModel = new DefaultTableModel(columns, 0);
            JTable transTable = new JTable(transModel);
            transTable.setRowHeight(28);

            loadTransactions();
            add(new JScrollPane(transTable), BorderLayout.CENTER);
        }

        private void loadTransactions() {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String query = "SELECT * FROM ppe_transactions ORDER BY transaction_date DESC";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

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
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
            }
        }
    }
}
