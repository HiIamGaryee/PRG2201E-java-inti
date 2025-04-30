import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private PPEManagementPanel ppeManagementPanel;
    private StockTrackingPanel stockTrackingPanel;
    private SearchFilterPanel searchPanel;
    private TransactionHistoryPanel transactionHistoryPanel;
    private UserManagerGUI userManagerPanel;
    private SupplierHospitalPanel supplierHospitalPanel;
    private String userType;
    
    public DashboardFrame(String userType) {
        this.userType = userType;
        setTitle("PPE Inventory Management System - " + userType);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Create panels
        ppeManagementPanel = new PPEManagementPanel();
        stockTrackingPanel = new StockTrackingPanel();
        searchPanel = new SearchFilterPanel();
        transactionHistoryPanel = new TransactionHistoryPanel();
        supplierHospitalPanel = new SupplierHospitalPanel();
        
        //Create UserManagerGUI panel
        if (userType.equalsIgnoreCase("Admin")) {
            userManagerPanel = new UserManagerGUI();
            tabbedPane.addTab("User Management", userManagerPanel);
        }

        // Show all tabs to all users
        tabbedPane.addTab("Stock Management", ppeManagementPanel);
        tabbedPane.addTab("Supplier & Hospital", supplierHospitalPanel);
        tabbedPane.addTab("Stock Tracking", stockTrackingPanel);
        tabbedPane.addTab("Search", searchPanel);

        // Add tabbed pane to frame
        add(tabbedPane);

        // Add logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DashboardFrame("ADMIN").setVisible(true); // Default to ADMIN for testing
        });
    }
}
