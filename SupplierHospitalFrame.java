import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SupplierHospitalFrame extends JFrame {

    private JTable supplierTable, hospitalTable;
    private DefaultTableModel supplierModel, hospitalModel;

    // Supplier Fields
    private JTextField supplierIDField, supplierNameField, supplierContactField, supplierAddressField;
    // Hospital Fields
    private JTextField hospitalIDField, hospitalNameField, hospitalContactField, hospitalAddressField;

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

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Supplier Details"));

        supplierIDField = new JTextField();
        supplierNameField = new JTextField();
        supplierContactField = new JTextField();
        supplierAddressField = new JTextField();

        inputPanel.add(new JLabel("Supplier ID:"));
        inputPanel.add(supplierIDField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(supplierNameField);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(supplierContactField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(supplierAddressField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        inputPanel.add(addBtn);
        inputPanel.add(updateBtn);
        inputPanel.add(deleteBtn);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table
        supplierModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Address"}, 0);
        supplierTable = new JTable(supplierModel);
        panel.add(new JScrollPane(supplierTable), BorderLayout.CENTER);

        // Button Actions
        addBtn.addActionListener(e -> addSupplier());
        updateBtn.addActionListener(e -> updateSupplier());
        deleteBtn.addActionListener(e -> deleteSupplier());
        supplierTable.getSelectionModel().addListSelectionListener(e -> fillSupplierFields());

        return panel;
    }

    private JPanel createHospitalPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Hospital Details"));

        hospitalIDField = new JTextField();
        hospitalNameField = new JTextField();
        hospitalContactField = new JTextField();
        hospitalAddressField = new JTextField();

        inputPanel.add(new JLabel("Hospital ID:"));
        inputPanel.add(hospitalIDField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(hospitalNameField);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(hospitalContactField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(hospitalAddressField);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        inputPanel.add(addBtn);
        inputPanel.add(updateBtn);
        inputPanel.add(deleteBtn);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Table
        hospitalModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Address"}, 0);
        hospitalTable = new JTable(hospitalModel);
        panel.add(new JScrollPane(hospitalTable), BorderLayout.CENTER);

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
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers")) {
            while (rs.next()) {
                supplierModel.addRow(new Object[]{
                        rs.getString("supplierID"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addSupplier() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "INSERT INTO suppliers VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierIDField.getText());
            pstmt.setString(2, supplierNameField.getText());
            pstmt.setString(3, supplierContactField.getText());
            pstmt.setString(4, supplierAddressField.getText());
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateSupplier() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "UPDATE suppliers SET name=?, contact=?, address=? WHERE supplierID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierNameField.getText());
            pstmt.setString(2, supplierContactField.getText());
            pstmt.setString(3, supplierAddressField.getText());
            pstmt.setString(4, supplierIDField.getText());
            pstmt.executeUpdate();
            loadSuppliers();
            clearSupplierFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSupplier() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "DELETE FROM suppliers WHERE supplierID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, supplierIDField.getText());
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
            supplierIDField.setText((String) supplierModel.getValueAt(row, 0));
            supplierNameField.setText((String) supplierModel.getValueAt(row, 1));
            supplierContactField.setText((String) supplierModel.getValueAt(row, 2));
            supplierAddressField.setText((String) supplierModel.getValueAt(row, 3));
        }
    }

    private void clearSupplierFields() {
        supplierIDField.setText("");
        supplierNameField.setText("");
        supplierContactField.setText("");
        supplierAddressField.setText("");
    }

    // ===== HOSPITAL METHODS =====
    private void loadHospitals() {
        hospitalModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM hospitals")) {
            while (rs.next()) {
                hospitalModel.addRow(new Object[]{
                        rs.getString("hospitalID"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("address")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addHospital() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "INSERT INTO hospitals VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalIDField.getText());
            pstmt.setString(2, hospitalNameField.getText());
            pstmt.setString(3, hospitalContactField.getText());
            pstmt.setString(4, hospitalAddressField.getText());
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateHospital() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "UPDATE hospitals SET name=?, contact=?, address=? WHERE hospitalID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalNameField.getText());
            pstmt.setString(2, hospitalContactField.getText());
            pstmt.setString(3, hospitalAddressField.getText());
            pstmt.setString(4, hospitalIDField.getText());
            pstmt.executeUpdate();
            loadHospitals();
            clearHospitalFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteHospital() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:inventory.db")) {
            String sql = "DELETE FROM hospitals WHERE hospitalID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hospitalIDField.getText());
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
            hospitalIDField.setText((String) hospitalModel.getValueAt(row, 0));
            hospitalNameField.setText((String) hospitalModel.getValueAt(row, 1));
            hospitalContactField.setText((String) hospitalModel.getValueAt(row, 2));
            hospitalAddressField.setText((String) hospitalModel.getValueAt(row, 3));
        }
    }

    private void clearHospitalFields() {
        hospitalIDField.setText("");
        hospitalNameField.setText("");
        hospitalContactField.setText("");
        hospitalAddressField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SupplierHospitalFrame::new);
    }
}
