import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PPEManagementPanel extends JPanel {
    private JTable ppeTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, transactionButton;
    private JTextField itemCodeField, itemNameField, supplierCodeField, quantityField;
    private JComboBox<String> transactionTypeCombo;
    private JTextField sourceDestField, transactionQuantityField;
    private JComboBox<String> supplierCombo;
    private JComboBox<String> hospitalCombo;

    public PPEManagementPanel() {
        setLayout(new BorderLayout());

        // Create table model
        String[] columnNames = { "Item Code", "Item Name", "Supplier Code", "Quantity (Boxes)" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        ppeTable = new JTable(tableModel);

        // Add selection listener to populate fields
        ppeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ppeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    itemCodeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    itemNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    supplierCodeField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    quantityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("PPE Item Details"));

        itemCodeField = new JTextField();
        itemNameField = new JTextField();
        supplierCodeField = new JTextField();
        quantityField = new JTextField();

        inputPanel.add(new JLabel("Item Code:"));
        inputPanel.add(itemCodeField);
        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Supplier Code:"));
        inputPanel.add(supplierCodeField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        // Create transaction panel
        JPanel transactionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transaction"));

        transactionTypeCombo = new JComboBox<>(new String[] { "RECEIVE", "DISTRIBUTE" });
        sourceDestField = new JTextField();
        transactionQuantityField = new JTextField();
        supplierCombo = new JComboBox<>();
        hospitalCombo = new JComboBox<>();

        transactionTypeCombo.addActionListener(e -> {
            String selectedType = (String) transactionTypeCombo.getSelectedItem();
            if ("RECEIVE".equals(selectedType)) {
                supplierCombo.setVisible(true);
                hospitalCombo.setVisible(false);
            } else {
                supplierCombo.setVisible(false);
                hospitalCombo.setVisible(true);
            }
        });

        transactionPanel.add(new JLabel("Transaction Type:"));
        transactionPanel.add(transactionTypeCombo);
        transactionPanel.add(new JLabel("Source/Destination:"));
        transactionPanel.add(sourceDestField);
        transactionPanel.add(new JLabel("Quantity:"));
        transactionPanel.add(transactionQuantityField);
        transactionPanel.add(new JLabel("Supplier:"));
        transactionPanel.add(supplierCombo);
        transactionPanel.add(new JLabel("Hospital:"));
        transactionPanel.add(hospitalCombo);

        hospitalCombo.setVisible(false);

        loadSuppliersAndHospitals();

        // Create button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");
        transactionButton = new JButton("Process Transaction");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(transactionButton);

        // Add action listeners
        addButton.addActionListener(e -> addPPEItem());
        updateButton.addActionListener(e -> updatePPEItem());
        deleteButton.addActionListener(e -> deletePPEItem());
        transactionButton.addActionListener(e -> processTransaction());

        // Add components to main panel
        add(new JScrollPane(ppeTable), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(transactionPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Load initial data
        loadPPEItems();
    }

    private void loadPPEItems() {
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM ppe_items")) {

            tableModel.setRowCount(0); // Clear existing data

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

    private void loadSuppliersAndHospitals() {
        try (Connection conn = DBConnection.getConnection()) {
            String supplierSql = "SELECT id, username FROM users WHERE user_type = 'Supplier'";
            PreparedStatement supplierStmt = conn.prepareStatement(supplierSql);
            ResultSet supplierRs = supplierStmt.executeQuery();
            supplierCombo.removeAllItems();
            while (supplierRs.next()) {
                supplierCombo.addItem(supplierRs.getString("username") + " (ID: " + supplierRs.getInt("id") + ")");
            }

            String hospitalSql = "SELECT id, username FROM users WHERE user_type = 'Hospital'";
            PreparedStatement hospitalStmt = conn.prepareStatement(hospitalSql);
            ResultSet hospitalRs = hospitalStmt.executeQuery();
            hospitalCombo.removeAllItems();
            while (hospitalRs.next()) {
                hospitalCombo.addItem(hospitalRs.getString("username") + " (ID: " + hospitalRs.getInt("id") + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers and hospitals: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPPEItem() {
        try {
            String itemCode = itemCodeField.getText();
            String itemName = itemNameField.getText();
            String supplierCode = supplierCodeField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            String sql = "INSERT INTO ppe_items (item_code, item_name, supplier_code, quantity_in_boxes) " +
                    "VALUES (?, ?, ?, ?)";

            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, itemCode);
                pstmt.setString(2, itemName);
                pstmt.setString(3, supplierCode);
                pstmt.setInt(4, quantity);

                pstmt.executeUpdate();
                loadPPEItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Item added successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding PPE item: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePPEItem() {
        try {
            String itemCode = itemCodeField.getText();
            String itemName = itemNameField.getText();
            String supplierCode = supplierCodeField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            String sql = "UPDATE ppe_items SET item_name = ?, supplier_code = ?, quantity_in_boxes = ? " +
                    "WHERE item_code = ?";

            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, itemName);
                pstmt.setString(2, supplierCode);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, itemCode);

                pstmt.executeUpdate();
                loadPPEItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating PPE item: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePPEItem() {
        String itemCode = itemCodeField.getText();
        if (itemCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                            "DELETE FROM ppe_items WHERE item_code = ?")) {
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
            String itemCode = itemCodeField.getText();
            String transactionType = (String) transactionTypeCombo.getSelectedItem();
            String sourceDest = sourceDestField.getText();
            int quantity = Integer.parseInt(transactionQuantityField.getText());

            Integer supplierId = null;
            Integer hospitalId = null;

            if ("RECEIVE".equals(transactionType)) {
                String supplierSelection = (String) supplierCombo.getSelectedItem();
                if (supplierSelection != null) {
                    supplierId = Integer.parseInt(supplierSelection.substring(supplierSelection.indexOf("ID: ") + 4,
                            supplierSelection.indexOf(")")));
                }
            } else {
                String hospitalSelection = (String) hospitalCombo.getSelectedItem();
                if (hospitalSelection != null) {
                    hospitalId = Integer.parseInt(hospitalSelection.substring(hospitalSelection.indexOf("ID: ") + 4,
                            hospitalSelection.indexOf(")")));
                }
            }

            PPETransaction transaction = new PPETransaction(itemCode, quantity, transactionType, sourceDest,
                    supplierId, hospitalId);
            if (transaction.processTransaction()) {
                loadPPEItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Transaction processed successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Transaction Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        itemCodeField.setText("");
        itemNameField.setText("");
        supplierCodeField.setText("");
        quantityField.setText("");
        sourceDestField.setText("");
        transactionQuantityField.setText("");
        supplierCombo.setSelectedIndex(0);
        hospitalCombo.setSelectedIndex(0);
    }
}