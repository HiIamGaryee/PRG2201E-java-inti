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

        // Column names
        String[] columns = {"Item Code", "Item Name", "Quantity (Boxes)"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        // 🔽 Enable sorting by column headers
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // ✅ Custom cell renderer for Quantity column (restock alert)
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
                        c.setBackground(Color.PINK); // Low stock
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } catch (NumberFormatException e) {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // 🔍 Search bar panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

        // 🔍 Search button action
        searchButton.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (!text.isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // case-insensitive
            }
        });

        // 🔄 Reset filter
        resetButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        // 💾 Export to CSV
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCSV());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(exportButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 📥 Load data from DB
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
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

    // 💾 Export current table to CSV
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
}
