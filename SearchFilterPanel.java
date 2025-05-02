import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class SearchFilterPanel extends JPanel {

    private static final String DB_URL = "jdbc:sqlite:new_ppe_inventory.db";

    private JTextField itemCodeField;
    private DefaultTableModel receiveModel;
    private DefaultTableModel distributeModel;

    public SearchFilterPanel() {
        setLayout(new BorderLayout());

        // === Top Panel: Search Bar ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCodeField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search Item Code:"));
        topPanel.add(itemCodeField);
        topPanel.add(searchButton);

        // === Tabbed Pane for Receive and Distribute ===
        JTabbedPane tabbedPane = new JTabbedPane();

        // === Tab 1: Received Summary ===
        receiveModel = new DefaultTableModel(new String[]{"Source", "Total Quantity", "Last Received Date"}, 0);
        JTable receiveTable = new JTable(receiveModel);
        JButton exportReceiveButton = new JButton("Export Receive CSV");

        JPanel receivePanel = new JPanel(new BorderLayout());
        receivePanel.add(new JScrollPane(receiveTable), BorderLayout.CENTER);
        receivePanel.add(exportReceiveButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Received", receivePanel);

        // === Tab 2: Distributed Summary ===
        distributeModel = new DefaultTableModel(new String[]{"Destination", "Total Quantity", "Last Distributed Date"}, 0);
        JTable distributeTable = new JTable(distributeModel);
        JButton exportDistributeButton = new JButton("Export Distribute CSV");

        JPanel distributePanel = new JPanel(new BorderLayout());
        distributePanel.add(new JScrollPane(distributeTable), BorderLayout.CENTER);
        distributePanel.add(exportDistributeButton, BorderLayout.SOUTH);
        tabbedPane.addTab("Distributed", distributePanel);

        // === Add components to main panel ===
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // === Event Listeners ===
        searchButton.addActionListener(e -> performSearch());
        exportReceiveButton.addActionListener(e -> exportTableToCSV(receiveModel, "received_summary.csv"));
        exportDistributeButton.addActionListener(e -> exportTableToCSV(distributeModel, "distributed_summary.csv"));
    }

    private void performSearch() {
        String keyword = itemCodeField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an item code or part of it.");
            return;
        }
        loadSummaryData("RECEIVE", keyword, receiveModel);
        loadSummaryData("DISTRIBUTE", keyword, distributeModel);
    }

    private void loadSummaryData(String type, String keyword, DefaultTableModel model) {
        model.setRowCount(0); // Clear previous results

        String query = """
                SELECT source_destination, 
                       SUM(quantity) AS total_quantity, 
                       MAX(transaction_date) AS latest_date
                FROM ppe_transactions
                WHERE transaction_type = ? AND item_code LIKE ?
                GROUP BY source_destination
                ORDER BY latest_date DESC
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type);
            stmt.setString(2, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("source_destination"),
                            rs.getInt("total_quantity"),
                            rs.getString("latest_date")
                    });
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving data:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTableToCSV(DefaultTableModel model, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write column headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");

            // Write rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    writer.write(model.getValueAt(row, col).toString());
                    if (col < model.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(this, "Exported successfully to " + filename);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to export CSV:\n" + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
