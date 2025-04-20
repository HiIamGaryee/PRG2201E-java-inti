import javax.swing.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("PPE Inventory Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // center on screen

        // Create the tabbed pane
        JTabbedPane tabs = new JTabbedPane();

        // Add Member 1 and 2 placeholders (to integrate later)
        tabs.addTab("User Management", new JPanel()); // Member 1
        tabs.addTab("Inventory Updates", new JPanel()); // Member 2

        // Add your components
        tabs.addTab("Stock Tracker", new StockTrackingPanel());  // ✅ your completed part
        tabs.addTab("Search", new SearchPanel());                // ✅ your next part

        // Optional: Add logout or welcome panel
        tabs.addTab("Welcome", new JLabel("Welcome to the PPE Inventory Dashboard!", SwingConstants.CENTER));

        // Add tabs to the main frame
        add(tabs);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Launch GUI
        SwingUtilities.invokeLater(() -> new DashboardFrame());
    }
}
