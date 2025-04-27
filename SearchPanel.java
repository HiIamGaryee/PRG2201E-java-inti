import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SearchPanel extends JPanel {

    private JTextField itemCodeField;
    private JTextArea resultArea;

    public SearchPanel() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Item Code:"));
        itemCodeField = new JTextField(10);
        inputPanel.add(itemCodeField);

        JButton searchBtn = new JButton("Search");
        inputPanel.add(searchBtn);
        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Search button action
        searchBtn.addActionListener(e -> searchItem());
    }

    private void searchItem() {
        String itemCode = itemCodeField.getText().trim().toUpperCase();

        if (itemCode.isEmpty()) {
            resultArea.setText("Please enter an item code.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {

            // Get total in stock
            String stockQuery = "SELECT quantity FROM ppe_items WHERE item_code = ?";
            PreparedStatement ps1 = conn.prepareStatement(stockQuery);
            ps1.setString(1, itemCode);
            ResultSet rs1 = ps1.executeQuery();

            int currentStock = rs1.next() ? rs1.getInt("quantity") : -1;
            if (currentStock == -1) {
                resultArea.setText("Item not found.");
                return;
            }

            // Get total received
            String receivedQuery = "SELECT SUM(quantity) AS total_received FROM transactions WHERE item_code = ? AND type = 'received'";
            PreparedStatement ps2 = conn.prepareStatement(receivedQuery);
            ps2.setString(1, itemCode);
            ResultSet rs2 = ps2.executeQuery();
            int received = rs2.next() ? rs2.getInt("total_received") : 0;

            // Get total distributed
            String distQuery = "SELECT SUM(quantity) AS total_distributed FROM transactions WHERE item_code = ? AND type = 'distributed'";
            PreparedStatement ps3 = conn.prepareStatement(distQuery);
            ps3.setString(1, itemCode);
            ResultSet rs3 = ps3.executeQuery();
            int distributed = rs3.next() ? rs3.getInt("total_distributed") : 0;

            // Display results
            resultArea.setText("Item Code: " + itemCode + "\n");
            resultArea.append("Current Stock: " + currentStock + " boxes\n");
            resultArea.append("Total Received: " + received + " boxes\n");
            resultArea.append("Total Distributed: " + distributed + " boxes\n");

        } catch (SQLException e) {
            resultArea.setText("Database error: " + e.getMessage());
        }
    }
}
