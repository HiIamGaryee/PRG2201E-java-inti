import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class PPEManagementPanel extends JPanel {
    private JTable ppeTable;
    private DefaultTableModel tableModel;
    private JButton updateButton, deleteButton, transactionButton;
    private JTextField itemCodeField, itemNameField, quantityField, transactionQuantityField;
    private JComboBox<String> sourceComboBox, transactionTypeCombo;
    private JTextField supplierField;
    private JSpinner transactionDateSpinner;

    public PPEManagementPanel() {
        setLayout(new BorderLayout());

        // Table
        String[] columnNames = { "Item Code", "Item Name", "Supplier Code", "Quantity (Boxes)" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ppeTable = new JTable(tableModel);

        // Table selection listener
        ppeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ppeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String itemCode = tableModel.getValueAt(selectedRow, 0).toString();
                    itemCodeField.setText(itemCode);
                    itemNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    supplierField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    quantityField.setText(String.valueOf(getCurrentStock(itemCode)));
                }
            }
        });

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("PPE Item Details"));

        itemCodeField = new JTextField();
        itemNameField = new JTextField();
        supplierField = new JTextField();
        supplierField.setEditable(false);
        quantityField = new JTextField();

        inputPanel.add(new JLabel("Item Code:"));
        inputPanel.add(itemCodeField);
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Supplier Code:"));
        inputPanel.add(supplierField);
        inputPanel.add(new JLabel("Quantity (Boxes):"));
        inputPanel.add(quantityField);

        // Transaction Panel
        JPanel transactionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transaction"));

        transactionTypeCombo = new JComboBox<>(new String[] { "RECEIVE", "DISTRIBUTE" });
        sourceComboBox = new JComboBox<>(loadAllSources());
        transactionQuantityField = new JTextField();

        transactionPanel.add(new JLabel("Transaction Type:"));
        transactionPanel.add(transactionTypeCombo);
        transactionPanel.add(new JLabel("Source/Destination:"));
        transactionPanel.add(sourceComboBox);
        transactionPanel.add(new JLabel("Quantity:"));
        transactionPanel.add(transactionQuantityField);
        transactionPanel.add(new JLabel("Date:"));

        transactionDateSpinner = new JSpinner(new SpinnerDateModel());
        transactionDateSpinner.setEditor(new JSpinner.DateEditor(transactionDateSpinner, "yyyy-MM-dd"));
        transactionDateSpinner.setValue(new java.util.Date());
        transactionPanel.add(transactionDateSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");
        transactionButton = new JButton("Process Transaction");

        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(transactionButton);

        // Listeners
        updateButton.addActionListener(e -> updatePPEItem());
        deleteButton.addActionListener(e -> deletePPEItem());
        transactionButton.addActionListener(e -> processTransaction());

        // Layout
        add(new JScrollPane(ppeTable), BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(transactionPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        loadPPEItems();
    }

    private void loadPPEItems() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                    SELECT 
                        item_code,
                        item_name,
                        supplier_code,
                        quantity_in_boxes
                    FROM 
                        ppe_items
                    ORDER BY item_code
            """;

            ResultSet rs = stmt.executeQuery(sql);
            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                    rs.getString("item_code"),
                    rs.getString("item_name"),
                    rs.getString("supplier_code"),
                    rs.getInt("quantity_in_boxes")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading PPE items: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePPEItem() {
        try {
            String itemCode = itemCodeField.getText().trim();
            String itemName = itemNameField.getText().trim();
            String supplierCode = supplierField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (itemCode.isEmpty() || itemName.isEmpty() || supplierCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE ppe_items SET item_name = ?, supplier_code = ?, quantity_in_boxes = ? WHERE item_code = ?";

            try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, itemName);
                pstmt.setString(2, supplierCode);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, itemCode);
                pstmt.executeUpdate();
            }

            loadPPEItems();
            clearFields();
            JOptionPane.showMessageDialog(this, "Item details and quantity updated successfully.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deletePPEItem() {
        String itemCode = itemCodeField.getText().trim();
        if (itemCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM ppe_items WHERE item_code = ?")) {
                pstmt.setString(1, itemCode);
                pstmt.executeUpdate();
                loadPPEItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Item deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting PPE item: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processTransaction() {
        try {
            String itemCode = itemCodeField.getText().trim();
            String transactionType = (String) transactionTypeCombo.getSelectedItem();
            String sourceDest = (String) sourceComboBox.getSelectedItem();
            int quantity = Integer.parseInt(transactionQuantityField.getText().trim());
            java.util.Date date = (java.util.Date) transactionDateSpinner.getValue();
            String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);

            if ("DISTRIBUTE".equals(transactionType)) {
                int currentStock = getCurrentStock(itemCode);
                if (currentStock < quantity) {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock. Available: " + currentStock + " boxes.",
                            "Stock Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            PPETransaction transaction = new PPETransaction(itemCode, quantity, transactionType, sourceDest, formattedDate);
            if (transaction.processTransaction()) {
                loadPPEItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Transaction processed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Transaction failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        itemCodeField.setText("");
        itemNameField.setText("");
        quantityField.setText("");
        transactionQuantityField.setText("");
        supplierField.setText("");
        sourceComboBox.setSelectedIndex(0);
        transactionDateSpinner.setValue(new java.util.Date());
    }

    public void refresh() {
        loadPPEItems();
    }

    private int getCurrentStock(String itemCode) {
        int stock = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT quantity_in_boxes FROM ppe_items WHERE item_code = ?")) {
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("quantity_in_boxes");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving stock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return stock;
    }

    private String[] loadAllSources() {
        List<String> sources = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT supplierCode FROM suppliers");
            while (rs1.next()) {
                sources.add("suppliers: " + rs1.getString("supplierCode"));
            }
            ResultSet rs2 = conn.createStatement().executeQuery("SELECT hospitalCode FROM hospitals");
            while (rs2.next()) {
                sources.add("hospitals: " + rs2.getString("hospitalCode"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading sources: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return sources.toArray(new String[0]);
    }
}
