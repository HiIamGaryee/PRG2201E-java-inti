import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class SearchFilterPanel extends JPanel {
    private JTextField itemCodeField;
    private JTabbedPane tabbedPane;
    private DefaultTableModel receiveModel, distributeModel;

    public SearchFilterPanel() {
        setLayout(new BorderLayout());

        // Top search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCodeField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        topPanel.add(new JLabel("Search Item Code:"));
        topPanel.add(itemCodeField);
        topPanel.add(searchBtn);

        // Tabs
        tabbedPane = new JTabbedPane();

        // Received tab
        receiveModel = new DefaultTableModel(new String[]{"Source", "Total Quantity", "Last Received Date"}, 0);
        JTable receiveTable = new JTable(receiveModel);
        JButton exportReceive = new JButton("Export Receive CSV");
        JPanel receivePanel = new JPanel(new BorderLayout());
        receivePanel.add(new JScrollPane(receiveTable), BorderLayout.CENTER);
        receivePanel.add(exportReceive, BorderLayout.SOUTH);
        tabbedPane.addTab("Received", receivePanel);

        // Distributed tab
        distributeModel = new DefaultTableModel(new String[]{"Destination", "Total Quantity", "Last Distributed Date"}, 0);
        JTable distributeTable = new JTable(distributeModel);
        JButton exportDistribute = new JButton("Export Distribute CSV");
        JPanel distributePanel = new JPanel(new BorderLayout());
        distributePanel.add(new JScrollPane(distributeTable), BorderLayout.CENTER);
        distributePanel.add(exportDistribute, BorderLayout.SOUTH);
        tabbedPane.addTab("Distributed", distributePanel);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Action listeners
        searchBtn.addActionListener(e -> searchTransactions());
        exportReceive.addActionListener(e -> exportTableToCSV(receiveModel, "received_summary.csv"));
        exportDistribute.addActionListener(e -> exportTableToCSV(distributeModel, "distributed_summary.csv"));
    }

    private void searchTransactions() {
        String keyword = itemCodeField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item code or part of it.");
            return;
        }
        loadSummaryTable("RECEIVE", keyword, receiveModel);
        loadSummaryTable("DISTRIBUTE", keyword, distributeModel);
    }

    private void loadSummaryTable(String type, String keyword, DefaultTableModel model) {
        model.setRowCount(0);
        String query = "SELECT source_destination, SUM(quantity) as total_quantity, MAX(transaction_date) as latest_date "
                     + "FROM ppe_transactions "
                     + "WHERE transaction_type = ? AND item_code LIKE ? "
                     + "GROUP BY source_destination "
                     + "ORDER BY latest_date DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type);
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("source_destination"),
                        rs.getInt("total_quantity"),
                        rs.getString("latest_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
        }
    }

    private void exportTableToCSV(DefaultTableModel model, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.write(",");
            }
            writer.write("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    writer.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
            }

            JOptionPane.showMessageDialog(this, "Exported to " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage());
        }
    }
}
