package GroupProject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Productspage {

    private DefaultTableModel bottomTableModel; // Table model for the bottom table
    private DefaultTableModel topTableModel;    // Table model for the top table
    private JTable topTable;                    // Top product table
    private JTable bottomTable;                 // Bottom table (list of products)
    private int editingRow = -1;                // Track which row is being edited
    private Connection connection;               // Database connection

    public Productspage(Connection connection) {
        this.connection = connection; // Store the connection

        // Creating a Main Frame for Products Page
        JFrame frame = new JFrame("Products Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1720, 1000);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints c1 = new GridBagConstraints();

        // Top Header Section (Add / View Products)
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 2;
        c1.gridheight = 1;
        c1.weightx = 1.0;
        c1.weighty = 0.1;
        c1.fill = GridBagConstraints.BOTH;
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JTextField headerTitle = new JTextField("ADD / VIEW PRODUCTS");
        headerTitle.setEditable(false);
        headerTitle.setHorizontalAlignment(JTextField.CENTER);
        headerTitle.setPreferredSize(new Dimension(75, 50));
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        frame.add(headerPanel, c1);

        // Home Button
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dummyVendorId = 123; // Dummy vendor ID
                frame.dispose(); // Close current window
                new HomePageFrame(dummyVendorId, connection); // Open the homepage frame with vendor ID
            }
        });
        headerPanel.add(homeButton, BorderLayout.WEST); // Add the home button to the header

        // Left Panel (Menu Buttons: Orders, Customers, Logout)
        c1.gridx = 0;
        c1.gridy = 1;
        c1.gridwidth = 1;
        c1.gridheight = 2;
        c1.weightx = 0.25;
        c1.weighty = 0.9;
        c1.fill = GridBagConstraints.BOTH;
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1, 10, 10)); // Three buttons with some gap

        // Create buttons with smaller preferred sizes
        JButton ordersButton = new JButton("Orders");
        JButton customersButton = new JButton("Customers");
        JButton logoutButton = new JButton("Logout");

        Dimension buttonSize = new Dimension(140, 30); // Set smaller preferred sizes for buttons
        ordersButton.setPreferredSize(buttonSize);
        customersButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        // Add buttons to the left panel
        leftPanel.add(ordersButton);
        leftPanel.add(customersButton);
        leftPanel.add(logoutButton);
        frame.add(leftPanel, c1);

        // Top Panel (Product Details Table) - Single row table for input
        c1.gridx = 1;
        c1.gridy = 1;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weightx = 0.75;
        c1.weighty = 0.6;
        c1.fill = GridBagConstraints.BOTH;

        JPanel topPanel = new JPanel();
        topTableModel = new DefaultTableModel(new Object[]{"Sl. No", "Products ID", "Products Description", "Unit Price", "Quantity", "Date Added"}, 0);
        topTable = new JTable(topTableModel);
        topTableModel.addRow(new Object[]{"", "", "", "", "", ""}); // Only one row for adding/editing
        JScrollPane topScrollPane = new JScrollPane(topTable);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(topScrollPane, BorderLayout.CENTER);
        frame.add(topPanel, c1);

        // Button panel for Edit / Add buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 0));
        JButton editButton = new JButton("Edit");
        JButton addButton = new JButton("Add");

        buttonPanel.add(editButton);
        buttonPanel.add(addButton);

        c1.gridx = 1;
        c1.gridy = 2;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weightx = 0.75;
        c1.weighty = 0.3;
        c1.fill = GridBagConstraints.BOTH;
        frame.add(buttonPanel, c1);

        // Bottom Panel (List of Products)
        JPanel bottomPanel = new JPanel();
        bottomTableModel = new DefaultTableModel(new Object[]{"Sl. No", "Products ID", "Products Description", "Unit Price", "Quantity", "Date Added"}, 0);
        bottomTable = new JTable(bottomTableModel);
        JScrollPane bottomScrollPane = new JScrollPane(bottomTable);
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(bottomScrollPane, BorderLayout.CENTER);

        c1.gridx = 0;
        c1.gridy = 3;
        c1.gridwidth = 2;
        c1.gridheight = 1;
        c1.weightx = 1.0;
        c1.weighty = 0.4;
        c1.fill = GridBagConstraints.BOTH;
        frame.add(bottomPanel, c1);

        // Load existing products from the database into the bottom table
        loadProductsFromDatabase();

        // Add button functionality
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get data from the top table
                String[] rowData = new String[6];
                for (int i = 0; i < 6; i++) {
                    rowData[i] = topTableModel.getValueAt(0, i).toString();
                }

                // Set the current date and time
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = LocalDateTime.now().format(formatter);
                rowData[5] = currentDateTime; // Set the date added field

                // Check if we're editing an existing row
                if (editingRow >= 0) {
                    // Update the selected row in the database
                    updateProductInDatabase(rowData);
                    // Update the selected row in the bottom table
                    for (int i = 0; i < 6; i++) {
                        bottomTableModel.setValueAt(rowData[i], editingRow, i);
                    }
                    editingRow = -1; // Reset editing row index
                } else {
                    // Add new product to the bottom table and database
                    addProductToDatabase(rowData);
                }

                // Clear the input row after adding
                for (int i = 0; i < 6; i++) {
                    topTableModel.setValueAt("", 0, i);
                }
            }
        });

        // Edit button functionality
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bottomTable.getSelectedRow();
                if (selectedRow >= 0) {
                    editingRow = selectedRow; // Set the row index for editing

                    // Copy the data from the selected row in the bottom table to the top table
                    for (int i = 0; i < 6; i++) {
                        topTableModel.setValueAt(bottomTableModel.getValueAt(selectedRow, i), 0, i);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a product to edit.");
                }
            }
        });

        // Frame visibility
        frame.setVisible(true);
    }

    // Load products from the database
    private void loadProductsFromDatabase() {
        String query = "SELECT * FROM Products"; // Update with your actual table name
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Assuming columns are indexed starting from 1
                String productsId = resultSet.getString("products_id");
                String productsDescription = resultSet.getString("products_description");
                String unitPrice = resultSet.getString("unit_price");
                String quantity = resultSet.getString("quantity");
                String dateAdded = resultSet.getString("date_added");

                bottomTableModel.addRow(new Object[]{bottomTableModel.getRowCount() + 1, productsId, productsDescription, unitPrice, quantity, dateAdded});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add product to the database
    private void addProductToDatabase(String[] rowData) {
        String insertSql = "INSERT INTO Products (product_id, product_desc, unit_price, quantity, date_added) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, rowData[1]);
            preparedStatement.setString(2, rowData[2]);
            preparedStatement.setString(3, rowData[3]);
            preparedStatement.setString(4, rowData[4]);
            preparedStatement.setString(5, rowData[5]);
            preparedStatement.executeUpdate();

            // Add new row to the bottom table
            bottomTableModel.addRow(new Object[]{bottomTableModel.getRowCount() + 1, rowData[1], rowData[2], rowData[3], rowData[4], rowData[5]});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update product in the database
    private void updateProductInDatabase(String[] rowData) {
        String updateSql = "UPDATE Products SET product_desc = ?, unit_price = ?, quantity = ?, date_added = ? WHERE product_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, rowData[2]);
            preparedStatement.setString(2, rowData[3]);
            preparedStatement.setString(3, rowData[4]);
            preparedStatement.setString(4, rowData[5]);
            preparedStatement.setString(5, rowData[1]);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
