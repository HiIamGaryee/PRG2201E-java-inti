import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class SupplierHospitalPanel extends JPanel {

    private JTable supplierTable, hospitalTable;
    private DefaultTableModel supplierModel, hospitalModel;

    // Supplier Fields
    private JTextField supplierCodeField, supplierNameField, supplierContactField, supplierAddressField;
    private JComboBox<String> suppliedItemComboBox;
    // Hospital Fields
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

        // Row 1: Code
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Code:"), gbc);
        gbc.gridx = 1;
        supplierCodeField = new JTextField(20);
        inputPanel.add(supplierCodeField, gbc);

        // Row 2: Name
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        supplierNameField = new JTextField(20);
        inputPanel.add(supplierNameField, gbc);

        // Row 3: Contact
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        supplierContactField = new JTextField(20);
        inputPanel.add(supplierContactField, gbc);

        // Row 4: Address
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        supplierAddressField = new JTextField(20);
        inputPanel.add(supplierAddressField, gbc);

        // ✅ Row 5: Supplied Item Dropdown
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Supplied Item:"), gbc);
        gbc.gridx = 1;
        suppliedItemComboBox = new JComboBox<>(loadPPEItemNames());
        inputPanel.add(suppliedItemComboBox, gbc);

        // Row 6: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        addBtn.setPreferredSize(new Dimension(100, 40));
        updateBtn.setPreferredSize(new Dimension(100, 40));
        deleteBtn.setPreferredSize(new Dimension(100, 40));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        // ✅ Table with new column
        supplierModel = new DefaultTableModel(new String[]{"ID", "Code", "Name", "Contact", "Address", "Supplied Item"}, 0);
        supplierTable = new JTable(supplierModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Listeners
        addBtn.addActionListener(e -> addSupplier());
        updateBtn.addActionListener(e -> updateSupplier());
        deleteBtn.addActionListener(e -> deleteSupplier());
        supplierTable.getSelectionModel().addListSelectionListener(e -> fillSupplierFields());

        return panel;
    }


    private JPanel createHospitalPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input panel with GridBagLayout for neat arrangement
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Hospital Details"));

        // Define GridBag constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Adding padding between components
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Fill available width

        // Row 1: Hospital Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Code:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        hospitalCodeField = new JTextField(20);
        inputPanel.add(hospitalCodeField, gbc);

        // Row 2: Hospital Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        hospitalNameField = new JTextField(20);
        inputPanel.add(hospitalNameField, gbc);

        // Row 3: Hospital Contact
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Contact:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        hospitalContactField = new JTextField(20);
        inputPanel.add(hospitalContactField, gbc);

        // Row 4: Hospital Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        hospitalAddressField = new JTextField(20);
        inputPanel.add(hospitalAddressField, gbc);

        // Row 5: Buttons (Add, Update, Delete)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // Using FlowLayout for buttons
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        // Set the same size for all buttons
        addBtn.setPreferredSize(new Dimension(100, 40));
        updateBtn.setPreferredSize(new Dimension(100, 40));
        deleteBtn.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);  // Add button panel

        // Add inputPanel to the North of the main panel
        panel.add(inputPanel, BorderLayout.NORTH);

        // Table (with ScrollPane)
        hospitalModel = new DefaultTableModel(new String[]{"ID", "Code", "Name", "Contact", "Address"}, 0);
        hospitalTable = new JTable(hospitalModel);
        JScrollPane scrollPane = new JScrollPane(hospitalTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        addBtn.addActionListener(e -> addHospital());
        updateBtn.addActionListener(e -> updateHospital());
        deleteBtn.addActionListener(e -> deleteHospital());
        hospitalTable.getSelectionModel().addListSelectionListener(e -> fillHospitalFields());

        return panel;
    }

    // ===== SUPPLIER METHODS =====
    private void loadSuppliers() {
        supplierModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers")) {
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                        rs.getString("supplierID"),
                        rs.getString("supplierCode"),
                        rs.getString("supplierName"),
                        rs.getString("contact"),
                        rs.getString("address"),
                        rs.getString("supplied_item")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        String supplierCode = supplierCodeField.getText().trim();
        String supplierName = supplierNameField.getText().trim();
        String supplierContact = supplierContactField.getText().trim();
        String supplierAddress = supplierAddressField.getText().trim();

        if (supplierCode.isEmpty() || supplierName.isEmpty() || supplierContact.isEmpty() || supplierAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "INSERT INTO suppliers (supplierCode, supplierName, contact, address, supplied_item) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierCode);
            pstmt.setString(2, supplierName);
            pstmt.setString(3, supplierContact);
            pstmt.setString(4, supplierAddress);
            pstmt.setString(5, suppliedItemComboBox.getSelectedItem().toString());
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
            return;
        }

        String supplierID = (String) supplierModel.getValueAt(selectedRow, 0);
        String supplierCode = supplierCodeField.getText().trim();
        String supplierName = supplierNameField.getText().trim();
        String supplierContact = supplierContactField.getText().trim();
        String supplierAddress = supplierAddressField.getText().trim();

        if (supplierCode.isEmpty() || supplierName.isEmpty() || supplierContact.isEmpty() || supplierAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "UPDATE suppliers SET supplierCode=?, supplierName=?, contact=?, address=?, supplied_item=? WHERE supplierID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierCode);
            pstmt.setString(2, supplierName);
            pstmt.setString(3, supplierContact);
            pstmt.setString(4, supplierAddress);
            pstmt.setString(5, suppliedItemComboBox.getSelectedItem().toString());
            pstmt.setString(6, supplierID);
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No supplier selected.");
            return;
        }

        String supplierID = (String) supplierModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "DELETE FROM suppliers WHERE supplierID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierID);
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillSupplierFields() {
        int row = supplierTable.getSelectedRow();
        if (row != -1) {
            supplierCodeField.setText((String) supplierModel.getValueAt(row, 1));
            supplierNameField.setText((String) supplierModel.getValueAt(row, 2));
            supplierContactField.setText((String) supplierModel.getValueAt(row, 3));
            supplierAddressField.setText((String) supplierModel.getValueAt(row, 4));
        }
    }

    private void clearSupplierFields() {
        supplierCodeField.setText("");
        supplierNameField.setText("");
        supplierContactField.setText("");
        supplierAddressField.setText("");
        suppliedItemComboBox.setSelectedIndex(0);
    }

    // ===== HOSPITAL METHODS =====
    private void loadHospitals() {
        hospitalModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hospitals")) {
            while (rs.next()) {
                hospitalModel.addRow(new Object[]{
                        rs.getString("hospitalID"),
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
        String hospitalCode = hospitalCodeField.getText().trim();
        String hospitalName = hospitalNameField.getText().trim();
        String hospitalContact = hospitalContactField.getText().trim();
        String hospitalAddress = hospitalAddressField.getText().trim();

        if (hospitalCode.isEmpty() || hospitalName.isEmpty() || hospitalContact.isEmpty() || hospitalAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "INSERT INTO hospitals (hospitalCode, hospitalName, contact, address) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalCode);
            pstmt.setString(2, hospitalName);
            pstmt.setString(3, hospitalContact);
            pstmt.setString(4, hospitalAddress);
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateHospital() {
        int selectedRow = hospitalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No hospital selected.");
            return;
        }

        String hospitalID = (String) hospitalModel.getValueAt(selectedRow, 0);
        String hospitalCode = hospitalCodeField.getText().trim();
        String hospitalName = hospitalNameField.getText().trim();
        String hospitalContact = hospitalContactField.getText().trim();
        String hospitalAddress = hospitalAddressField.getText().trim();

        if (hospitalCode.isEmpty() || hospitalName.isEmpty() || hospitalContact.isEmpty() || hospitalAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "UPDATE hospitals SET hospitalCode=?, hospitalName=?, contact=?, address=? WHERE hospitalID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalCode);
            pstmt.setString(2, hospitalName);
            pstmt.setString(3, hospitalContact);
            pstmt.setString(4, hospitalAddress);
            pstmt.setString(5, hospitalID);
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteHospital() {
        int selectedRow = hospitalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No hospital selected.");
            return;
        }

        String hospitalID = (String) hospitalModel.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "DELETE FROM hospitals WHERE hospitalID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalID);
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillHospitalFields() {
        int row = hospitalTable.getSelectedRow();
        if (row != -1) {
            hospitalCodeField.setText((String) hospitalModel.getValueAt(row, 1));
            hospitalNameField.setText((String) hospitalModel.getValueAt(row, 2));
            hospitalContactField.setText((String) hospitalModel.getValueAt(row, 3));
            hospitalAddressField.setText((String) hospitalModel.getValueAt(row, 4));
        }
    }

    private void clearHospitalFields() {
        hospitalCodeField.setText("");
        hospitalNameField.setText("");
        hospitalContactField.setText("");
        hospitalAddressField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierHospitalPanel::new);
    }

    private String[] loadPPEItemNames() {
        java.util.List<String> items = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_name FROM ppe_items")) {
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading item names: " + e.getMessage());
        }
        return items.toArray(new String[0]);
    }

}
