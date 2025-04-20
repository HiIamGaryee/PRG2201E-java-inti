import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class StockTrackingPanel extends JPanel {

    public StockTrackingPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"Item Code", "Item Name", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(30);

        // Connect to DB and load data
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String query = "SELECT item_code, item_name, quantity FROM ppe_items ORDER BY item_code ASC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String code = rs.getString("item_code");
                String name = rs.getString("item_name");
                int quantity = rs.getInt("quantity");
                model.addRow(new Object[]{code, name, quantity});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage());
        }

        // ✅ Apply custom cell renderer only to the Quantity column
        TableColumn quantityColumn = table.getColumnModel().getColumn(2);
        quantityColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Highlight if quantity < 25
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

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
