package GroupProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class HomePageFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection connection;
    private int vendorId;

    // Constructor that takes in vendorId and connection
    public HomePageFrame(int vendorId, Connection connection) {
        this.vendorId = vendorId;
        this.connection = connection;

        // Setting up the frame
        setTitle("Home Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create the home panel and add it to the main panel
        JPanel homePanel = createHomePanel();
        mainPanel.add(homePanel, "Home");

        add(mainPanel);
        setVisible(true); // Make the frame visible
    }

    // Method to create the home panel with buttons
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));

        JButton addOrderButton = new JButton("Add Orders");
        JButton addCustomerButton = new JButton("Add Customers");
        JButton productsButton = new JButton("Products");
        JButton viewOrdersButton = new JButton("View Orders");
        JButton viewCustomersButton = new JButton("View Customers");
        JButton logoutButton = new JButton("Logout");

        // Add action listeners to the buttons
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Addpage(vendorId, connection); // Pass vendorId to Addpage
            }
        });

        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Addpage(vendorId, connection); // Pass vendorId to Addpage
            }
        });

        productsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Productspage(connection); // Replace with the actual class for viewing products
            }
        });

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Viewpage(connection); // Use the existing Viewpage class for viewing orders
            }
        });

        viewCustomersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Viewpage(connection); // Replace with the actual class for viewing customers
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the current frame on logout
                dispose();
                JOptionPane.showMessageDialog(null, "Logged out successfully", "Logout", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        panel.add(addOrderButton);
        panel.add(addCustomerButton);
        panel.add(productsButton);
        panel.add(viewOrdersButton);
        panel.add(viewCustomersButton);
        panel.add(logoutButton);

        return panel;
    }
}
