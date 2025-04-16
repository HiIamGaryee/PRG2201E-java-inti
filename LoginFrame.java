import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField userIDField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login System");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center window
        setLayout(new GridLayout(4, 2, 10, 10));

        // Labels and Fields
        add(new JLabel("User ID:"));
        userIDField = new JTextField();
        add(userIDField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        // Empty label to align button
        add(new JLabel(""));
        loginButton = new JButton("Login");
        add(loginButton);

        // Action Listener
        loginButton.addActionListener(e -> authenticateUser());

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
            Connection conn = DriverManager.getConnection("ppe_inventory.db");
            String sql = "SELECT * FROM users WHERE userID = ? AND password = ?";
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

                dispose(); // close login frame
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
