import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class UserManagerGUI extends JPanel {
    private ArrayList<User> users = new ArrayList<>();
    private JTextField txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15); // Use JPasswordField for password input
    private JTextField txtSearch = new JTextField(15);
    private DefaultListModel<User> listModel = new DefaultListModel<>();
    private JList<User> userList = new JList<>(listModel);
    private JComboBox<String> userTypeComboBox = new JComboBox<>(new String[]{"Staff", "Admin"}); // ComboBox for user type

    public UserManagerGUI() {
        // Use GridBagLayout for better control over layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components
        gbc.anchor = GridBagConstraints.WEST;  // Align components to the left

        // Username label and text field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(txtUsername, gbc);

        // Password label and text field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtPassword, gbc);

        // User type label and ComboBox
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("User Type:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(userTypeComboBox, gbc);

        // Add/Edit/Delete buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        btnAdd.addActionListener(e -> addUser());
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        add(buttonPanel, gbc);

        // Search label and text field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(new JLabel("Search:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        add(txtSearch, gbc);

        // Search button
        JButton btnSearch = new JButton("Find");
        btnSearch.addActionListener(e -> searchUser());
        gbc.gridx = 2;
        gbc.gridy = 4;
        add(btnSearch, gbc);

        // User List
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        JScrollPane listScrollPane = new JScrollPane(userList);
        listScrollPane.setPreferredSize(new Dimension(450, 150));  // Adjust list size
        add(listScrollPane, gbc);

        // Load users from the database on initialization
        loadUsersFromDatabase();
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim(); // Get the password from JPasswordField
        String userType = (String) userTypeComboBox.getSelectedItem(); // Get selected user type
        if (!username.isEmpty() && !password.isEmpty()) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
                String sql = "INSERT INTO users (username, password, userType) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, password);  // Store password as plain text
                pstmt.setString(3, userType);  // Store user type
                pstmt.executeUpdate();
                loadUsersFromDatabase();  // Refresh the user list from the database
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
            String newPassword = new String(txtPassword.getPassword()).trim();  // Get the new password
            String newUserType = (String) userTypeComboBox.getSelectedItem(); // Get selected user type
            if (!newPassword.isEmpty() || !newUserType.equals(selected.getUserType())) { // Only update if changes are made
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
                    String sql = "UPDATE users SET password = ?, userType = ? WHERE username = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, newPassword);  // Update password
                    pstmt.setString(2, newUserType);  // Update user type
                    pstmt.setString(3, selected.getUsername());  // Use selected user's username
                    pstmt.executeUpdate();
                    loadUsersFromDatabase();  // Refresh the user list from the database
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error editing user: " + e.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a new password or change the user type.");
            }
        }
    }

    private void deleteUser() {
        User selected = userList.getSelectedValue();
        if (selected != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
                String sql = "DELETE FROM users WHERE username = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, selected.getUsername());
                pstmt.executeUpdate();
                loadUsersFromDatabase();  // Refresh the user list from the database
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

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db")) {
            String sql = "SELECT * FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String userType = rs.getString("userType");  // Fetch user type
                users.add(new User(username, password, userType));  // Load users from DB
                listModel.addElement(new User(username, password, userType));  // Add to JList
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }
}
