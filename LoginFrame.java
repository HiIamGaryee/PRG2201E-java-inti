import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login System");
        setSize(400, 250); // Adjusted size for better appearance
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window
        setLayout(new GridBagLayout()); // Use GridBagLayout for better control

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components

        // User ID Label
        JLabel userIDLabel = new JLabel("User ID:");
        userIDLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for label
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userIDLabel, gbc);

        // User ID Text Field
        userIDField = new JTextField(20);
        userIDField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        userIDField.setPreferredSize(new Dimension(200, 30)); // Adjust size
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(userIDField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font
        passwordField.setPreferredSize(new Dimension(200, 30)); // Adjust size
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Empty label for alignment
        JLabel emptyLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(emptyLabel, gbc);

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14)); // Set font
        loginButton.setBackground(new Color(34, 167, 240)); // Set background color
        loginButton.setForeground(Color.WHITE); // Set text color
        loginButton.setPreferredSize(new Dimension(100, 40)); // Button size
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(34, 167, 240), 2)); // Add border
        loginButton.addActionListener(e -> authenticateUser());
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(loginButton, gbc);

        setVisible(true);
    }

    private void authenticateUser() {
        String userID = userIDField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();

        if (userID.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Password.");
            return;
        }

        try {
            // Connect to SQLite (or your DB)
            Connection conn = DriverManager.getConnection("jdbc:sqlite:ppe_inventory.db");
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userID);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userType = rs.getString("userType");
                JOptionPane.showMessageDialog(this, "Login successful! User Type: " + userType);

                // Open dashboard or menu based on userType
                // new AdminDashboard(userID); // for admin
                // new StaffDashboard(userID); // for staff

                dispose(); // Close login frame
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
