import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class UserManagerGUI extends JPanel {
    private ArrayList<User> users = new ArrayList<>();
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JTextField txtSearch = new JTextField(15);
    private DefaultListModel<User> listModel = new DefaultListModel<>();
    private JList<User> userList = new JList<>(listModel);
    private JComboBox<String> userTypeComboBox = new JComboBox<>(new String[]{"Staff", "Admin"});

    public UserManagerGUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // User Type
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        add(userTypeComboBox, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        buttonPanel.add(btnAdd); buttonPanel.add(btnEdit); buttonPanel.add(btnDelete);
        add(buttonPanel, gbc);

        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());

        // Search
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        add(txtSearch, gbc);
        JButton btnSearch = new JButton("Find");
        btnSearch.addActionListener(e -> searchUser());
        gbc.gridx = 2;
        add(btnSearch, gbc);

        // User list
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(450, 150));
        add(scrollPane, gbc);

        // Load on start
        loadUsersFromDatabase();

        // Populate fields when a user is selected
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                User selected = userList.getSelectedValue();
                if (selected != null) {
                    txtUsername.setText(selected.getUsername());
                    txtPassword.setText(""); // For security, don't load password
                    userTypeComboBox.setSelectedItem(selected.getUserType());
                }
            }
        });
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String userType = (String) userTypeComboBox.getSelectedItem();

        if (!username.isEmpty() && !password.isEmpty()) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String sql = "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, userType);
                pstmt.executeUpdate();
                loadUsersFromDatabase();
                JOptionPane.showMessageDialog(this, "User added successfully!");
                txtUsername.setText("");
                txtPassword.setText("");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.");
        }
    }

    private void editUser() {
        User selected = userList.getSelectedValue();
        if (selected != null) {
            String newPassword = new String(txtPassword.getPassword()).trim();
            String newUserType = (String) userTypeComboBox.getSelectedItem();

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                boolean updated = false;

                if (!newPassword.isEmpty() && !newPassword.equals(selected.getPassword())) {
                    String sql = "UPDATE users SET password = ? WHERE username = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newPassword);
                    pstmt.setString(2, selected.getUsername());
                    pstmt.executeUpdate();
                    updated = true;
                }

                if (!newUserType.equals(selected.getUserType())) {
                    String sql = "UPDATE users SET userType = ? WHERE username = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newUserType);
                    pstmt.setString(2, selected.getUsername());
                    pstmt.executeUpdate();
                    updated = true;
                }

                if (updated) {
                    loadUsersFromDatabase();
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "No changes detected.");
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error editing user: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        User selected = userList.getSelectedValue();
        if (selected != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
                String sql = "DELETE FROM users WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, selected.getUsername());
                pstmt.executeUpdate();
                loadUsersFromDatabase();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
            }
        }
    }

    private void searchUser() {
        String query = txtSearch.getText().trim().toLowerCase();
        listModel.clear();
        for (User u : users) {
            if (u.getUsername().toLowerCase().contains(query)) {
                listModel.addElement(u);
            }
        }
    }

    private void loadUsersFromDatabase() {
        users.clear();
        listModel.clear();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:new_ppe_inventory.db")) {
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String userType = rs.getString("userType");
                User user = new User(username, password, userType);
                users.add(user);
                listModel.addElement(user);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }
}
