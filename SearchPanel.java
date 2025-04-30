import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchPanel extends JPanel {
    private JTextField itemCodeField;
    private DefaultTableModel tableModel;

    public SearchPanel() {
        setLayout(new BorderLayout());

        // Input panel at top
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Item Code:"));
        itemCodeField = new JTextField(10);
        inputPanel.add(itemCodeField);

        JButton searchBtn = new JButton("Search");
        inputPanel.add(searchBtn);
        add(inputPanel, BorderLayout.NORTH);

        // Result table setup
        String[] columns = { "Item Code", "Type", "Source/Destination", "Total Quantity", "Date" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing
            }
        };

        JTable resultTable = new JTable(tableModel);
        resultTable.setRowHeight(30);
        resultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Custom renderer to highlight low distributed stock
        resultTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) table.getValueAt(row, 1);
                int quantity = Integer.parseInt(value.toString());

                if (type.equalsIgnoreCase("DISTRIBUTE") && quantity < 10) {
                    c.setBackground(Color.PINK);
                } else {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> searchItem());
    }

    private void searchItem() {
        String itemCode = itemCodeField.getText().trim().toUpperCase();
        if (itemCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an item code.");
            return;
        }

        tableModel.setRowCount(0); // Clear previous results

        String query = """
            SELECT item_code, transaction_type, source_destination, 
                   SUM(quantity) as total_quantity, 
                   MAX(transaction_date) as latest_date
            FROM ppe_transactions
            WHERE item_code = ?
            GROUP BY item_code, transaction_type, source_destination
            ORDER BY transaction_type, source_destination;
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                String code = rs.getString("item_code");
                String type = rs.getString("transaction_type");
                String sourceDest = rs.getString("source_destination");
                int quantity = rs.getInt("total_quantity");
                String date = rs.getString("latest_date");

                tableModel.addRow(new Object[]{code, type, sourceDest, quantity, date});
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No transactions found for this item.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
