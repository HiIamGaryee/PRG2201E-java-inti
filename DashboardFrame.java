import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final JTabbedPane tabbedPane;
    private final PPEManagementPanel ppeManagementPanel;
    private final StockTrackingPanel stockTrackingPanel;
    private final SearchFilterPanel searchPanel;
    private final TransactionHistoryPanel transactionHistoryPanel;
    private final SupplierHospitalPanel supplierHospitalPanel;
    private UserManagerGUI userManagerPanel;
    private final String userType;

    public DashboardFrame(String userType) {
        this.userType = userType;

        setTitle("PPE Inventory Management System - " + userType);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize tabbed pane
        tabbedPane = new JTabbedPane();

        // Initialize panels
        ppeManagementPanel = new PPEManagementPanel();
        stockTrackingPanel = new StockTrackingPanel();
        searchPanel = new SearchFilterPanel();
        transactionHistoryPanel = new TransactionHistoryPanel();
        supplierHospitalPanel = new SupplierHospitalPanel();

        // Add Admin-only tab
        if (userType.equalsIgnoreCase("Admin")) {
            userManagerPanel = new UserManagerGUI();
            tabbedPane.addTab("User Management", userManagerPanel);
        }

        // Add common tabs for all users
        tabbedPane.addTab("Stock Management", ppeManagementPanel);
        tabbedPane.addTab("Supplier & Hospital", supplierHospitalPanel);
        tabbedPane.addTab("Stock Tracking", stockTrackingPanel);
        tabbedPane.addTab("Search", searchPanel);
        tabbedPane.addTab("Transaction History", transactionHistoryPanel);

        // Add tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);

        // Tab change listener to refresh data when switching tabs
        tabbedPane.addChangeListener(e -> {
            String selectedTab = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
            switch (selectedTab) {
                case "Stock Management" -> ppeManagementPanel.refresh();
                case "Stock Tracking" -> stockTrackingPanel.refresh();
                case "Supplier & Hospital" -> supplierHospitalPanel.refresh();
                case "Transaction History" -> transactionHistoryPanel.refresh();
            }
        });

        // Create and add logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose(); // Close dashboard
            new LoginFrame().setVisible(true); // Return to login screen
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame("ADMIN").setVisible(true)); // For testing
    }
}
