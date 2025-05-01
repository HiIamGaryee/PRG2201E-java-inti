import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class SupplierHospitalPanel extends JPanel {
    private JTable supplierTable, hospitalTable;
    private DefaultTableModel supplierModel, hospitalModel;

    private JTextField supplierCodeField, supplierNameField, supplierContactField, supplierAddressField;
    private JComboBox<String> suppliedItemComboBox;

    private JTextField hospitalCodeField, hospitalNameField, hospitalContactField, hospitalAddressField;

    public SupplierHospitalPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Suppliers", createSupplierPanel());
        tabs.addTab("Hospitals", createHospitalPanel());
        add(tabs, BorderLayout.CENTER);
        loadSuppliers();
        loadHospitals();
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1;
        supplierCodeField = new JTextField(20);
        inputPanel.add(supplierCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        supplierNameField = new JTextField(20);
        inputPanel.add(supplierNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        supplierContactField = new JTextField(20);
        inputPanel.add(supplierContactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        supplierAddressField = new JTextField(20);
        inputPanel.add(supplierAddressField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Supplied Item:"), gbc);
        gbc.gridx = 1;
        suppliedItemComboBox = new JComboBox<>(loadPPEItemNames());
        inputPanel.add(suppliedItemComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);
        panel.add(inputPanel, BorderLayout.NORTH);

        supplierModel = new DefaultTableModel(new String[]{"Code", "Name", "Contact", "Address", "Supplied Items"}, 0);
        supplierTable = new JTable(supplierModel);
        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addSupplier());
        updateBtn.addActionListener(e -> updateSupplier());
        deleteBtn.addActionListener(e -> deleteSupplier());
        supplierTable.getSelectionModel().addListSelectionListener(e -> fillSupplierFields());

        return panel;
    }

    private JPanel createHospitalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Hospital Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1;
        hospitalCodeField = new JTextField(20);
        inputPanel.add(hospitalCodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        hospitalNameField = new JTextField(20);
        inputPanel.add(hospitalNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        hospitalContactField = new JTextField(20);
        inputPanel.add(hospitalContactField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        hospitalAddressField = new JTextField(20);
        inputPanel.add(hospitalAddressField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);
        panel.add(inputPanel, BorderLayout.NORTH);

        hospitalModel = new DefaultTableModel(new String[]{"Code", "Name", "Contact", "Address"}, 0);
        hospitalTable = new JTable(hospitalModel);
        panel.add(new JScrollPane(hospitalTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addHospital());
        updateBtn.addActionListener(e -> updateHospital());
        deleteBtn.addActionListener(e -> deleteHospital());
        hospitalTable.getSelectionModel().addListSelectionListener(e -> fillHospitalFields());

        return panel;
    }

    public void refresh() {
        loadSuppliers();
        loadHospitals();
    }

    private void loadSuppliers() {
        supplierModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = """
                SELECT DISTINCT s.supplierCode, s.supplierName, s.contact, s.address, si.item_name
                FROM suppliers s
                JOIN supplier_items si ON s.supplierCode = si.supplierCode
                ORDER BY s.supplierCode, si.item_name
            """;
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    supplierModel.addRow(new Object[]{
                        rs.getString("supplierCode"),
                        rs.getString("supplierName"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        rs.getString("item_name") != null ? rs.getString("item_name") : "—"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        String code = supplierCodeField.getText().trim();
        String name = supplierNameField.getText().trim();
        String contact = supplierContactField.getText().trim();
        String address = supplierAddressField.getText().trim();
        String itemName = suppliedItemComboBox.getSelectedItem().toString();

        if (code.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            conn.setAutoCommit(false);

            // Step 1: Add or ignore supplier (only added if not exists)
            String insertSupplier = """
                INSERT OR IGNORE INTO suppliers (supplierCode, supplierName, contact, address)
                VALUES (?, ?, ?, ?)
            """;
            try (PreparedStatement ps1 = conn.prepareStatement(insertSupplier)) {
                ps1.setString(1, code);
                ps1.setString(2, name);
                ps1.setString(3, contact);
                ps1.setString(4, address);
                ps1.executeUpdate();
            }

            // Step 2: Check if this item already exists for this supplier
            String check = "SELECT COUNT(*) FROM supplier_items WHERE supplierCode = ? AND item_name = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(check)) {
                psCheck.setString(1, code);
                psCheck.setString(2, itemName);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "This item is already assigned to the supplier.");
                    conn.rollback();
                    return;
                }
            }

            // Step 3: Insert the new supplier-item link
            String insertItem = "INSERT INTO supplier_items (supplierCode, item_name) VALUES (?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insertItem)) {
                ps2.setString(1, code);
                ps2.setString(2, itemName);
                ps2.executeUpdate();
            }

            // Step 4: Ensure this PPE item exists (item_code must be unique)
            String itemCode = getItemCodeFromName(itemName);
            insertPPEItemIfNotExists(conn, itemCode, itemName, code);

            conn.commit();
            loadSuppliers();
            clearSupplierFields();
            JOptionPane.showMessageDialog(this, "Supplier and item added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void updateSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier item to update.");
            return;
        }

        String oldSupplierCode = (String) supplierModel.getValueAt(selectedRow, 0);
        String oldItemName = (String) supplierModel.getValueAt(selectedRow, 4);

        String newSupplierCode = supplierCodeField.getText().trim();
        String newSupplierName = supplierNameField.getText().trim();
        String newContact = supplierContactField.getText().trim();
        String newAddress = supplierAddressField.getText().trim();
        String newItemName = suppliedItemComboBox.getSelectedItem().toString();

        if (newSupplierCode.isEmpty() || newSupplierName.isEmpty() || newContact.isEmpty() || newAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            conn.setAutoCommit(false);

            // Step 1: Update supplier basic details
            String updateSql = "UPDATE suppliers SET supplierName=?, contact=?, address=? WHERE supplierCode=?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newSupplierName);
                ps.setString(2, newContact);
                ps.setString(3, newAddress);
                ps.setString(4, newSupplierCode);
                ps.executeUpdate();
            }

            // Step 2: If item changed, update mapping in supplier_items
            if (!oldItemName.equals(newItemName)) {
                // First, delete the old mapping
                String deleteOld = "DELETE FROM supplier_items WHERE supplierCode=? AND item_name=?";
                try (PreparedStatement psDel = conn.prepareStatement(deleteOld)) {
                    psDel.setString(1, oldSupplierCode);
                    psDel.setString(2, oldItemName);
                    psDel.executeUpdate();
                }

                // Then insert the new mapping if not duplicate
                String insertNew = "INSERT OR IGNORE INTO supplier_items (supplierCode, item_name) VALUES (?, ?)";
                try (PreparedStatement psIns = conn.prepareStatement(insertNew)) {
                    psIns.setString(1, newSupplierCode);
                    psIns.setString(2, newItemName);
                    psIns.executeUpdate();
                }

                // Step 3: Also update PPE item if needed
                String itemCode = getItemCodeFromName(newItemName);
                insertPPEItemIfNotExists(conn, itemCode, newItemName, newSupplierCode);
            }

            conn.commit();
            loadSuppliers();
            clearSupplierFields();
            JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating supplier: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void insertPPEItemIfNotExists(Connection conn, String itemCode, String itemName, String supplierCode) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM ppe_items WHERE item_code = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, itemCode);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO ppe_items (item_code, item_name, supplier_code, quantity_in_boxes) VALUES (?, ?, ?, 0)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, itemCode);
                    insertStmt.setString(2, itemName);
                    insertStmt.setString(3, supplierCode);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row != -1) {
            String code = (String) supplierModel.getValueAt(row, 0); // supplierCode
            String itemName = (String) supplierModel.getValueAt(row, 4); // supplied item name

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the item '" + itemName + "' for supplier '" + code + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                    conn.setAutoCommit(false);

                    // Step 1: Delete from supplier_items
                    String deleteItemSql = "DELETE FROM supplier_items WHERE supplierCode = ? AND item_name = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteItemSql)) {
                        pstmt.setString(1, code);
                        pstmt.setString(2, itemName);
                        pstmt.executeUpdate();
                    }

                    // Step 2 (Optional): Delete related PPE item if it exists
                    String itemCode = getItemCodeFromName(itemName); // FS, GL, etc.
                    String deletePPESql = "DELETE FROM ppe_items WHERE item_code = ? AND supplier_code = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deletePPESql)) {
                        ps.setString(1, itemCode);
                        ps.setString(2, code);
                        ps.executeUpdate();
                    }

                    // Step 3: If supplier has no more items, delete supplier record
                    String checkSql = "SELECT COUNT(*) FROM supplier_items WHERE supplierCode = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setString(1, code);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) == 0) {
                            String deleteSupplierSql = "DELETE FROM suppliers WHERE supplierCode = ?";
                            try (PreparedStatement delStmt = conn.prepareStatement(deleteSupplierSql)) {
                                delStmt.setString(1, code);
                                delStmt.executeUpdate();
                            }
                        }
                    }

                    conn.commit();
                    loadSuppliers();
                    clearSupplierFields();
                    JOptionPane.showMessageDialog(this, "Deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting supplier/item: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
        }
    }


    private void fillSupplierFields() {
        int row = supplierTable.getSelectedRow();
        if (row != -1) {
            supplierCodeField.setText((String) supplierModel.getValueAt(row, 0));
            supplierNameField.setText((String) supplierModel.getValueAt(row, 1));
            supplierContactField.setText((String) supplierModel.getValueAt(row, 2));
            supplierAddressField.setText((String) supplierModel.getValueAt(row, 3));
        }
    }

    private void clearSupplierFields() {
        supplierCodeField.setText("");
        supplierNameField.setText("");
        supplierContactField.setText("");
        supplierAddressField.setText("");
        suppliedItemComboBox.setSelectedIndex(0);
    }

    private void loadHospitals() {
        hospitalModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hospitals")) {
            while (rs.next()) {
                hospitalModel.addRow(new Object[]{
                        rs.getString("hospitalCode"),
                        rs.getString("hospitalName"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addHospital() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "INSERT INTO hospitals (hospitalCode, hospitalName, contact, address) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalCodeField.getText().trim());
            pstmt.setString(2, hospitalNameField.getText().trim());
            pstmt.setString(3, hospitalContactField.getText().trim());
            pstmt.setString(4, hospitalAddressField.getText().trim());
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateHospital() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "UPDATE hospitals SET hospitalName=?, contact=?, address=? WHERE hospitalCode=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalNameField.getText().trim());
            pstmt.setString(2, hospitalContactField.getText().trim());
            pstmt.setString(3, hospitalAddressField.getText().trim());
            pstmt.setString(4, hospitalCodeField.getText().trim());
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteHospital() {
        int row = hospitalTable.getSelectedRow();
        if (row != -1) {
            String code = (String) hospitalModel.getValueAt(row, 0);
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String sql = "DELETE FROM hospitals WHERE hospitalCode=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, code);
                pstmt.executeUpdate();
                loadHospitals();
                clearHospitalFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillHospitalFields() {
        int row = hospitalTable.getSelectedRow();
        if (row != -1) {
            hospitalCodeField.setText((String) hospitalModel.getValueAt(row, 0));
            hospitalNameField.setText((String) hospitalModel.getValueAt(row, 1));
            hospitalContactField.setText((String) hospitalModel.getValueAt(row, 2));
            hospitalAddressField.setText((String) hospitalModel.getValueAt(row, 3));
        }
    }

    private void clearHospitalFields() {
        hospitalCodeField.setText("");
        hospitalNameField.setText("");
        hospitalContactField.setText("");
        hospitalAddressField.setText("");
    }

    private String[] loadPPEItemNames() {
        return new String[] {
            "Face Shield",
            "Gloves",
            "Gown",
            "Head Cover",
            "Mask",
        };
    }

    private String getItemCodeFromName(String itemName) {
        return switch (itemName) {
            case "Face Shield" -> "FS";
            case "Gloves" -> "GL";
            case "Gown" -> "GW";
            case "Head Cover" -> "HC";
            case "Mask" -> "MS";
            default -> throw new IllegalArgumentException("Unknown item: " + itemName);
        };
    }



    private String fetchItemCodeFromName(Connection conn, String itemName) throws SQLException {
        String sql = "SELECT item_code FROM ppe_items WHERE item_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("item_code");
            } else {
                throw new SQLException("Item name not found in ppe_items: " + itemName);
            }
        }
    }


}
