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
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // User ID Label
        JLabel userIDLabel = new JLabel("User ID:");
        userIDLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userIDLabel, gbc);

        // User ID Text Field
        userIDField = new JTextField(20);
        userIDField.setFont(new Font("Arial", Font.PLAIN, 14));
        userIDField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(userIDField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
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
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(34, 167, 240));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(34, 167, 240), 2));
        loginButton.addActionListener(e -> authenticateUser());
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(loginButton, gbc);

        // Add KeyListener for the Enter key press on both fields
        userIDField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticateUser();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    authenticateUser();
                }
            }
        });

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
            Connection conn = DBConnection.getConnection();
            // ✅ REMARK: Correct table name assumed to be 'users' and checking userID+password
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userID);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // ✅ REMARK: Fetch the userType from DB
                String userType = rs.getString("userType");

                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + userID);

                // ✅ REMARK: Redirect to DashboardFrame with correct userType
                SwingUtilities.invokeLater(() -> {
                    new DashboardFrame(userType).setVisible(true);
                });

                dispose(); // ✅ REMARK: Close LoginFrame after successful login
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password.");
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
