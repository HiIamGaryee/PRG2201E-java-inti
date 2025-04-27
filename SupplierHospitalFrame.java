import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierHospitalFrame extends JFrame {

    private JTable supplierTable, hospitalTable;
    private DefaultTableModel supplierModel, hospitalModel;

    // Supplier Fields
    private JTextField supplierNameField, supplierContactField, supplierAddressField;
    // Hospital Fields
    private JTextField hospitalNameField, hospitalContactField, hospitalAddressField;

    public SupplierHospitalFrame() {
        setTitle("Supplier & Hospital Management");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Suppliers", createSupplierPanel());
        tabs.addTab("Hospitals", createHospitalPanel());

        add(tabs);
        setVisible(true);

        loadSuppliers();
        loadHospitals();
    }

    private JPanel createSupplierPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input panel with GridBagLayout for neat arrangement
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        
        // Define GridBag constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Adding padding between components
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Fill available width

        // Row 1: Supplier Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        supplierNameField = new JTextField(20);
        inputPanel.add(supplierNameField, gbc);

        // Row 2: Supplier Contact
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Contact:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        supplierContactField = new JTextField(20);
        inputPanel.add(supplierContactField, gbc);

        // Row 3: Supplier Address
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        supplierAddressField = new JTextField(20);
        inputPanel.add(supplierAddressField, gbc);

        // Row 4: Buttons (Add, Update, Delete)
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
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);  // Add button panel

        // Add inputPanel to the North of the main panel
        panel.add(inputPanel, BorderLayout.NORTH);

        // Table (with ScrollPane)
        supplierModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Address"}, 0);
        supplierTable = new JTable(supplierModel);
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
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

        // Row 1: Hospital Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        hospitalNameField = new JTextField(20);
        inputPanel.add(hospitalNameField, gbc);

        // Row 2: Hospital Contact
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Contact:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        hospitalContactField = new JTextField(20);
        inputPanel.add(hospitalContactField, gbc);

        // Row 3: Hospital Address
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        hospitalAddressField = new JTextField(20);
        inputPanel.add(hospitalAddressField, gbc);

        // Row 4: Buttons (Add, Update, Delete)
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
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(buttonPanel, gbc);  // Add button panel

        // Add inputPanel to the North of the main panel
        panel.add(inputPanel, BorderLayout.NORTH);

        // Table (with ScrollPane)
        hospitalModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Address"}, 0);
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
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers")) {
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                        rs.getString("supplierID"),
                        rs.getString("supplierName"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        String supplierName = supplierNameField.getText().trim();
        String supplierContact = supplierContactField.getText().trim();
        String supplierAddress = supplierAddressField.getText().trim();

        if (supplierName.isEmpty() || supplierContact.isEmpty() || supplierAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String sql = "INSERT INTO suppliers (supplierName, contact, address) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierName);
            pstmt.setString(2, supplierContact);
            pstmt.setString(3, supplierAddress);
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
        String supplierName = supplierNameField.getText().trim();
        String supplierContact = supplierContactField.getText().trim();
        String supplierAddress = supplierAddressField.getText().trim();

        if (supplierName.isEmpty() || supplierContact.isEmpty() || supplierAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String sql = "UPDATE suppliers SET supplierName=?, contact=?, address=? WHERE supplierID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierName);
            pstmt.setString(2, supplierContact);
            pstmt.setString(3, supplierAddress);
            pstmt.setString(4, supplierID);
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

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
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
            supplierNameField.setText((String) supplierModel.getValueAt(row, 1));
            supplierContactField.setText((String) supplierModel.getValueAt(row, 2));
            supplierAddressField.setText((String) supplierModel.getValueAt(row, 3));
        }
    }

    private void clearSupplierFields() {
        supplierNameField.setText("");
        supplierContactField.setText("");
        supplierAddressField.setText("");
    }

    // ===== HOSPITAL METHODS =====
    private void loadHospitals() {
        hospitalModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hospitals")) {
            while (rs.next()) {
                hospitalModel.addRow(new Object[]{
                        rs.getString("hospitalID"),
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
        String hospitalName = hospitalNameField.getText().trim();
        String hospitalContact = hospitalContactField.getText().trim();
        String hospitalAddress = hospitalAddressField.getText().trim();

        if (hospitalName.isEmpty() || hospitalContact.isEmpty() || hospitalAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String sql = "INSERT INTO hospitals (hospitalName, contact, address) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalName);
            pstmt.setString(2, hospitalContact);
            pstmt.setString(3, hospitalAddress);
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
        String hospitalName = hospitalNameField.getText().trim();
        String hospitalContact = hospitalContactField.getText().trim();
        String hospitalAddress = hospitalAddressField.getText().trim();

        if (hospitalName.isEmpty() || hospitalContact.isEmpty() || hospitalAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String sql = "UPDATE hospitals SET hospitalName=?, contact=?, address=? WHERE hospitalID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalName);
            pstmt.setString(2, hospitalContact);
            pstmt.setString(3, hospitalAddress);
            pstmt.setString(4, hospitalID);
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

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
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
            hospitalNameField.setText((String) hospitalModel.getValueAt(row, 1));
            hospitalContactField.setText((String) hospitalModel.getValueAt(row, 2));
            hospitalAddressField.setText((String) hospitalModel.getValueAt(row, 3));
        }
    }

    private void clearHospitalFields() {
        hospitalNameField.setText("");
        hospitalContactField.setText("");
        hospitalAddressField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierHospitalFrame::new);
    }
}
