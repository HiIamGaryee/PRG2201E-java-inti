import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private PPEManagementPanel ppeManagementPanel;
    private StockTrackingPanel stockTrackingPanel;
    private SearchPanel searchPanel;

    public DashboardFrame() {
        setTitle("PPE Inventory Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        ppeManagementPanel = new PPEManagementPanel();
        stockTrackingPanel = new StockTrackingPanel();
        searchPanel = new SearchPanel();
        
        // Add panels to tabbed pane
        tabbedPane.addTab("PPE Management", ppeManagementPanel);
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
            new DashboardFrame().setVisible(true);
        });
    }
}
