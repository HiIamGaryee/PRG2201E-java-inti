import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class UserManagerGUI extends JFrame {
    private ArrayList<User> users = new ArrayList<>();

    private JTextField txtUsername = new JTextField(15);
    private JTextField txtPassword = new JTextField(15);
    private JTextField txtSearch = new JTextField(15);
    private DefaultListModel<User> listModel = new DefaultListModel<>();
    private JList<User> userList = new JList<>(listModel);

    public UserManagerGUI() {
        setTitle("User Manager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400);

        // Use GridBagLayout for better control over layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Spacing between components

        // Username and Password input fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Add/Edit/Delete buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
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

        // Search functionality
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Search:"), gbc);
        gbc.gridx = 1;
        add(txtSearch, gbc);

        JButton btnSearch = new JButton("Find");
        btnSearch.addActionListener(e -> searchUser());
        gbc.gridx = 2;
        add(btnSearch, gbc);

        // User List
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JScrollPane listScrollPane = new JScrollPane(userList);
        add(listScrollPane, gbc);

        setVisible(true);
    }

    private void addUser() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        if (!username.isEmpty() && !password.isEmpty()) {
            User user = new User(username, password);
            users.add(user);
            listModel.addElement(user);
            txtUsername.setText("");
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in both fields.");
        }
    }

    private void editUser() {
        User selected = userList.getSelectedValue();
        if (selected != null) {
            selected.setUsername(txtUsername.getText().trim());
            selected.setPassword(txtPassword.getText().trim());
            userList.repaint(); // Refresh list
        }
    }

    private void deleteUser() {
        User selected = userList.getSelectedValue();
        if (selected != null) {
            users.remove(selected);
            listModel.removeElement(selected);
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

    public static void main(String[] args) {
        new UserManagerGUI();
    }
}
