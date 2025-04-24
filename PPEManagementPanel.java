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
    private JButton addButton, updateButton, deleteButton;
    private JTextField itemCodeField, nameField, descriptionField, quantityField, 
                     minStockField, unitField, categoryField;
    private JComboBox<String> transactionTypeCombo;
    private JTextField sourceDestField, transactionQuantityField;

    public PPEManagementPanel() {
        setLayout(new BorderLayout());
        
        // Create table model
        String[] columnNames = {"Item Code", "Name", "Description", "Quantity", 
                              "Min Stock", "Unit", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ppeTable = new JTable(tableModel);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("PPE Item Details"));
        
        itemCodeField = new JTextField();
        nameField = new JTextField();
        descriptionField = new JTextField();
        quantityField = new JTextField();
        minStockField = new JTextField();
        unitField = new JTextField();
        categoryField = new JTextField();
        
        inputPanel.add(new JLabel("Item Code:"));
        inputPanel.add(itemCodeField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Min Stock Level:"));
        inputPanel.add(minStockField);
        inputPanel.add(new JLabel("Unit:"));
        inputPanel.add(unitField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryField);
        
        // Create transaction panel
        JPanel transactionPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transaction"));
        
        transactionTypeCombo = new JComboBox<>(new String[]{"RECEIVE", "DISTRIBUTE"});
        sourceDestField = new JTextField();
        transactionQuantityField = new JTextField();
        
        transactionPanel.add(new JLabel("Transaction Type:"));
        transactionPanel.add(transactionTypeCombo);
        transactionPanel.add(new JLabel("Source/Destination:"));
        transactionPanel.add(sourceDestField);
        transactionPanel.add(new JLabel("Quantity:"));
        transactionPanel.add(transactionQuantityField);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");
        JButton transactionButton = new JButton("Process Transaction");
        
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
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    rs.getInt("min_stock_level"),
                    rs.getString("unit"),
                    rs.getString("category")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading PPE items: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addPPEItem() {
        try {
            String itemCode = itemCodeField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            int minStock = Integer.parseInt(minStockField.getText());
            String unit = unitField.getText();
            String category = categoryField.getText();
            
            String sql = "INSERT INTO ppe_items (item_code, name, description, quantity, " +
                        "min_stock_level, unit, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, itemCode);
                pstmt.setString(2, name);
                pstmt.setString(3, description);
                pstmt.setInt(4, quantity);
                pstmt.setInt(5, minStock);
                pstmt.setString(6, unit);
                pstmt.setString(7, category);
                
                pstmt.executeUpdate();
                loadPPEItems();
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and min stock level",
                                        "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding PPE item: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePPEItem() {
        try {
            String itemCode = itemCodeField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            int minStock = Integer.parseInt(minStockField.getText());
            String unit = unitField.getText();
            String category = categoryField.getText();
            
            String sql = "UPDATE ppe_items SET name = ?, description = ?, quantity = ?, " +
                        "min_stock_level = ?, unit = ?, category = ? WHERE item_code = ?";
            
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, description);
                pstmt.setInt(3, quantity);
                pstmt.setInt(4, minStock);
                pstmt.setString(5, unit);
                pstmt.setString(6, category);
                pstmt.setString(7, itemCode);
                
                pstmt.executeUpdate();
                loadPPEItems();
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and min stock level",
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
            
            // Check if item exists
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT quantity FROM ppe_items WHERE item_code = ?")) {
                pstmt.setString(1, itemCode);
                ResultSet rs = pstmt.executeQuery();
                
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Item not found",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int currentQuantity = rs.getInt("quantity");
                
                // Check stock level for distribution
                if (transactionType.equals("DISTRIBUTE") && currentQuantity < quantity) {
                    JOptionPane.showMessageDialog(this, "Insufficient stock",
                                                "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update stock
                int newQuantity = transactionType.equals("RECEIVE") ? 
                    currentQuantity + quantity : currentQuantity - quantity;
                
                // Record transaction
                String transactionSql = "INSERT INTO ppe_transactions " +
                    "(item_code, quantity, transaction_type, source_destination) " +
                    "VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement transPstmt = conn.prepareStatement(transactionSql)) {
                    transPstmt.setString(1, itemCode);
                    transPstmt.setInt(2, quantity);
                    transPstmt.setString(3, transactionType);
                    transPstmt.setString(4, sourceDest);
                    transPstmt.executeUpdate();
                }
                
                // Update item quantity
                String updateSql = "UPDATE ppe_items SET quantity = ? WHERE item_code = ?";
                try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                    updatePstmt.setInt(1, newQuantity);
                    updatePstmt.setString(2, itemCode);
                    updatePstmt.executeUpdate();
                }
                
                // Check for low stock alert
                try (PreparedStatement checkPstmt = conn.prepareStatement(
                    "SELECT min_stock_level FROM ppe_items WHERE item_code = ?")) {
                    checkPstmt.setString(1, itemCode);
                    ResultSet checkRs = checkPstmt.executeQuery();
                    if (checkRs.next() && newQuantity <= checkRs.getInt("min_stock_level")) {
                        JOptionPane.showMessageDialog(this,
                            "Warning: Stock level is below minimum threshold!",
                            "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                loadPPEItems();
                clearFields();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity",
                                        "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error processing transaction: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFields() {
        itemCodeField.setText("");
        nameField.setText("");
        descriptionField.setText("");
        quantityField.setText("");
        minStockField.setText("");
        unitField.setText("");
        categoryField.setText("");
        sourceDestField.setText("");
        transactionQuantityField.setText("");
    }
} 