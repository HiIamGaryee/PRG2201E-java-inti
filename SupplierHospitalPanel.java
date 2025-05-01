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

    // Supplier methods
    private void loadSuppliers() {
        supplierModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = """
                SELECT s.supplierCode, s.supplierName, s.contact, s.address,
                    GROUP_CONCAT(si.item_name, ', ') AS items
                FROM suppliers s
                LEFT JOIN supplier_items si ON s.supplierCode = si.supplierCode
                GROUP BY s.supplierCode
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                    rs.getString("supplierCode"),
                    rs.getString("supplierName"),
                    rs.getString("contact"),
                    rs.getString("address"),
                    rs.getString("items") != null ? rs.getString("items") : "—"
                });
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

            // Insert supplier (only if not already present)
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

            // Insert supplied item (can be multiple per supplier)
            String insertItem = "INSERT INTO supplier_items (supplierCode, item_name) VALUES (?, ?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insertItem)) {
                ps2.setString(1, code);
                ps2.setString(2, itemName);
                ps2.executeUpdate();
            }

            conn.commit();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to update.");
            return;
        }

        String code = supplierCodeField.getText().trim();
        String name = supplierNameField.getText().trim();
        String contact = supplierContactField.getText().trim();
        String address = supplierAddressField.getText().trim();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "UPDATE suppliers SET supplierName=?, contact=?, address=? WHERE supplierCode=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.setString(4, code);
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void deleteSupplier() {
        int row = supplierTable.getSelectedRow();
        if (row != -1) {
            String code = (String) supplierModel.getValueAt(row, 0);
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String sql = "DELETE FROM suppliers WHERE supplierCode = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, code);
                pstmt.executeUpdate();
                loadSuppliers();
                clearSupplierFields();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    // Hospital methods
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
