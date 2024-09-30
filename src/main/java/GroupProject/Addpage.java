package GroupProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

class Addpage extends JFrame {
    private JTextField serialNoField, orderIdField, customerNameField, productIdField, productQuantityField, productUnitPriceField, totalAmountField, statusField, dateAddedField;
    private JTextField customerNameFieldCust, customerAddressField, customerEmailField, customerContactNumField;
    private JButton addOrderButton, addCustomerButton, homeButton;
    private int vendorId; // To store the vendor ID
    private Connection conn; // Database connection

    public Addpage(int vendorId, Connection conn) {
        this.vendorId = vendorId; // Initialize vendor ID
        this.conn = conn; // Initialize database connection

        // Setting up the frame
        setTitle("Order & Customer Management");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create Order panel
        JPanel orderPanel = createOrderPanel();

        // Create Customer panel
        JPanel customerPanel = createCustomerPanel();

        // Add both panels to the tabbed pane
        tabbedPane.addTab("Add Orders", orderPanel);
        tabbedPane.addTab("Add Customers", customerPanel);

        // Create a panel for Home button
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Add Home button
        homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Navigate to the HomePageFrame, passing the connection
                new HomePageFrame(vendorId, conn); // Assuming HomePageFrame accepts Connection in its constructor
                dispose(); // Close the Addpage frame
            }
        });

        // Add the Home button to the homePanel
        homePanel.add(homeButton);

        // Add the homePanel to the frame at the top
        add(homePanel, BorderLayout.NORTH);

        // Add the tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // Create the order panel for adding orders
    private JPanel createOrderPanel() {
        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(new GridLayout(10, 2, 10, 10));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        orderPanel.setPreferredSize(new Dimension(400, 400));

        orderPanel.add(new JLabel("Serial No:"));
        serialNoField = new JTextField(15);
        orderPanel.add(serialNoField);

        orderPanel.add(new JLabel("Order ID:"));
        orderIdField = new JTextField(15);
        orderPanel.add(orderIdField);

        orderPanel.add(new JLabel("Customer Name:"));
        customerNameField = new JTextField(15);
        orderPanel.add(customerNameField);

        orderPanel.add(new JLabel("Product ID:"));
        productIdField = new JTextField(15);
        orderPanel.add(productIdField);

        orderPanel.add(new JLabel("Product Quantity:"));
        productQuantityField = new JTextField(15);
        orderPanel.add(productQuantityField);

        orderPanel.add(new JLabel("Product Unit Price:"));
        productUnitPriceField = new JTextField(15);
        orderPanel.add(productUnitPriceField);

        orderPanel.add(new JLabel("Total Amount:"));
        totalAmountField = new JTextField(15);
        totalAmountField.setEditable(false);
        orderPanel.add(totalAmountField);

        orderPanel.add(new JLabel("Status:"));
        statusField = new JTextField(15);
        orderPanel.add(statusField);

        orderPanel.add(new JLabel("Date Added:"));
        dateAddedField = new JTextField(15);
        dateAddedField.setEditable(false);
        orderPanel.add(dateAddedField);

        // Auto-fill current date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dateAddedField.setText(formatter.format(date));

        // Button for adding order
        addOrderButton = new JButton("Add Order");
        addOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serialNo = serialNoField.getText();
                String orderId = orderIdField.getText();
                String customerName = customerNameField.getText();
                String productId = productIdField.getText();
                String quantityStr = productQuantityField.getText();
                String unitPriceStr = productUnitPriceField.getText();

                // Calculate total amount and set it
                try {
                    double quantity = Double.parseDouble(quantityStr);
                    double unitPrice = Double.parseDouble(unitPriceStr);
                    double totalAmount = quantity * unitPrice;
                    totalAmountField.setText(String.valueOf(totalAmount));

                    String status = statusField.getText();
                    String dateAdded = dateAddedField.getText();

                    // Show confirmation dialog
                    int response = JOptionPane.showConfirmDialog(
                        null,
                        "Please confirm the following order details:\n\n" +
                        "Serial No: " + serialNo + "\n" +
                        "Order ID: " + orderId + "\n" +
                        "Customer Name: " + customerName + "\n" +
                        "Product ID: " + productId + "\n" +
                        "Product Quantity: " + quantity + "\n" +
                        "Product Unit Price: " + unitPrice + "\n" +
                        "Total Amount: " + totalAmount + "\n" +
                        "Status: " + status + "\n" +
                        "Date Added: " + dateAdded,
                        "Confirm Order",
                        JOptionPane.OK_CANCEL_OPTION
                    );

                    // If OK is pressed, save data to the database
                    if (response == JOptionPane.OK_OPTION) {
                        addOrder(serialNo, orderId, customerName, productId, quantity, unitPrice, totalAmount, status, dateAdded);
                        JOptionPane.showMessageDialog(null, "Order added successfully!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for quantity and unit price.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        orderPanel.add(addOrderButton);

        return orderPanel;
    }

    // Create the customer panel for adding customers
    private JPanel createCustomerPanel() {
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridLayout(5, 2, 10, 10));
        customerPanel.setBackground(Color.WHITE);
        customerPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        customerPanel.setPreferredSize(new Dimension(400, 400));

        customerPanel.add(new JLabel("Customer Name:"));
        customerNameFieldCust = new JTextField(15);
        customerPanel.add(customerNameFieldCust);

        customerPanel.add(new JLabel("Customer Address:"));
        customerAddressField = new JTextField(15);
        customerPanel.add(customerAddressField);

        customerPanel.add(new JLabel("Customer Email:"));
        customerEmailField = new JTextField(15);
        customerPanel.add(customerEmailField);

        customerPanel.add(new JLabel("Customer Contact Number:"));
        customerContactNumField = new JTextField(15);
        customerPanel.add(customerContactNumField);

        // Button for adding customer
        addCustomerButton = new JButton("Add Customer");
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerName = customerNameFieldCust.getText();
                String customerAddress = customerAddressField.getText();
                String customerEmail = customerEmailField.getText();
                String customerContactNum = customerContactNumField.getText();

                // Show confirmation dialog
                int response = JOptionPane.showConfirmDialog(
                        null,
                        "Please confirm the following customer details:\n\n" +
                                "Customer Name: " + customerName + "\n" +
                                "Customer Address: " + customerAddress + "\n" +
                                "Customer Email: " + customerEmail + "\n" +
                                "Customer Contact Number: " + customerContactNum,
                        "Confirm Customer",
                        JOptionPane.OK_CANCEL_OPTION
                );

                // If OK is pressed, save data to the database
                if (response == JOptionPane.OK_OPTION) {
                    addCustomer(customerName, customerAddress, customerEmail, customerContactNum);
                    JOptionPane.showMessageDialog(null, "Customer added successfully!");
                }
            }
        });

        customerPanel.add(addCustomerButton);

        return customerPanel;
    }

    // Method to add customer to the database
    private void addCustomer(String customerName, String address, String email, String contactNum) {
        try {
            String sql = "INSERT INTO Customers (customer_name, address, email, contact_num) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, customerName);
            statement.setString(2, address);
            statement.setString(3, email);
            statement.setString(4, contactNum);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to add order to the database
    private void addOrder(String serialNo, String orderId, String customerName, String productId, double quantity, double unitPrice, double totalAmount, String status, String dateAdded) {
        try {
            String sql = "INSERT INTO Orders (order_id, customer_name, product_id, product_quantity, product_unit_price, total_amount, status, date_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, orderId);
            statement.setString(2, customerName);
            statement.setString(3, productId);
            statement.setDouble(4, quantity);
            statement.setDouble(5, unitPrice);
            statement.setDouble(6, totalAmount);
            statement.setString(7, status);
            statement.setString(8, dateAdded);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
