package GroupProject;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Viewpage {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JTable orderTable, customerTable;
    private DefaultTableModel orderTableModel, customerTableModel;
    private JTextField searchFieldOrders, searchFieldCustomers;
    private JComboBox<String> sortBoxOrders, sortBoxCustomers;
    private Connection connection; // Database connection
    private int vendorId; // Vendor ID

    // Constructor that takes a Connection and vendorId object
    public Viewpage(Connection connection) {
        this.connection = connection;
        this.vendorId = vendorId;

        frame = new JFrame("Inventory Management System");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();

        // Add Home button at the top
        addHomeButton();

        setupOrderTable();
        setupCustomerTable();

        frame.add(tabbedPane);
        frame.setVisible(true);

        loadOrderData();
        loadCustomerData();
    }

    // Method to add Home button and its action
    private void addHomeButton() {
        JPanel homePanel = new JPanel(new BorderLayout());

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            // Navigate to HomePageFrame, passing both vendorId and connection
            new HomePageFrame(vendorId, connection);
            frame.dispose(); // Close the current frame (Viewpage)
        });

        homePanel.add(homeButton, BorderLayout.NORTH);
        frame.add(homePanel, BorderLayout.NORTH);
    }

    private void setupOrderTable() {
        String[] columnNames = {"SI. No.", "Order ID", "Customer Name", "Product ID", "Product Quantity", 
                                "Product Unit Price", "Total Amount", "Status", "Date Added"};
        orderTableModel = new DefaultTableModel(columnNames, 0);
        orderTable = new JTable(orderTableModel);

        JPanel panel = new JPanel(new BorderLayout());

        // Search Field for Orders
        searchFieldOrders = new JTextField(15);
        searchFieldOrders.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchOrders(searchFieldOrders.getText());
            }
        });

        // Sort Box for Orders
        sortBoxOrders = new JComboBox<>(columnNames);
        sortBoxOrders.addActionListener(e -> sortOrders((String) sortBoxOrders.getSelectedItem()));

        // Edit Button for Orders
        JButton editButtonOrders = new JButton("Edit Order");
        editButtonOrders.addActionListener(e -> editSelectedOrder());

        JPanel topPanelOrders = new JPanel();
        topPanelOrders.add(new JLabel("Search Orders: "));
        topPanelOrders.add(searchFieldOrders);
        topPanelOrders.add(new JLabel("Sort By: "));
        topPanelOrders.add(sortBoxOrders);
        topPanelOrders.add(editButtonOrders);

        panel.add(topPanelOrders, BorderLayout.NORTH);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        tabbedPane.addTab("View Orders", panel);
    }

    private void setupCustomerTable() {
        String[] columnNames = {"SI. No.", "Customer ID", "Customer Name", "Contact", "Email", "Address"};
        customerTableModel = new DefaultTableModel(columnNames, 0);
        customerTable = new JTable(customerTableModel);

        JPanel panel = new JPanel(new BorderLayout());

        // Search Field for Customers
        searchFieldCustomers = new JTextField(15);
        searchFieldCustomers.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchCustomers(searchFieldCustomers.getText());
            }
        });

        // Sort Box for Customers
        sortBoxCustomers = new JComboBox<>(columnNames);
        sortBoxCustomers.addActionListener(e -> sortCustomers((String) sortBoxCustomers.getSelectedItem()));

        // Edit Button for Customers
        JButton editButtonCustomers = new JButton("Edit Customer");
        editButtonCustomers.addActionListener(e -> editSelectedCustomer());

        JPanel topPanelCustomers = new JPanel();
        topPanelCustomers.add(new JLabel("Search Customers: "));
        topPanelCustomers.add(searchFieldCustomers);
        topPanelCustomers.add(new JLabel("Sort By: "));
        topPanelCustomers.add(sortBoxCustomers);
        topPanelCustomers.add(editButtonCustomers);

        panel.add(topPanelCustomers, BorderLayout.NORTH);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        tabbedPane.addTab("View Customers", panel);
    }

    // Method to load and search orders
    private void searchOrders(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(orderTableModel);
        orderTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }

    // Method to load and search customers
    private void searchCustomers(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(customerTableModel);
        customerTable.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter(query));
    }

    // Sorting orders
    private void sortOrders(String column) {
        orderTable.getRowSorter().toggleSortOrder(orderTableModel.findColumn(column));
    }

    // Sorting customers
    private void sortCustomers(String column) {
        customerTable.getRowSorter().toggleSortOrder(customerTableModel.findColumn(column));
    }

    // Edit selected order
    private void editSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow != -1) {
            // Logic to edit the selected row
            orderTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
        }
    }

    // Edit selected customer
    private void editSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            // Logic to edit the selected row
            customerTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
        }
    }

    // Load order data from the database
    private void loadOrderData() {
        String query = "SELECT * FROM Orders"; // Update with your actual Orders table name
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String orderId = resultSet.getString("order_id");
                String customerName = resultSet.getString("customer_name");
                String productId = resultSet.getString("product_id");
                String productQuantity = resultSet.getString("product_quantity");
                String productUnitPrice = resultSet.getString("product_unit_price");
                String totalAmount = resultSet.getString("total_amount");
                String status = resultSet.getString("status");
                String dateAdded = resultSet.getString("date_added");

                orderTableModel.addRow(new Object[]{orderTableModel.getRowCount() + 1, orderId, customerName, productId, productQuantity, productUnitPrice, totalAmount, status, dateAdded});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load customer data from the database
    private void loadCustomerData() {
        String query = "SELECT * FROM Customers"; // Update with your actual Customers table name
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String customerId = resultSet.getString("customer_id");
                String customerName = resultSet.getString("customer_name");
                String contact = resultSet.getString("contact_num"); // Update with the actual column name for contact number
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");

                customerTableModel.addRow(new Object[]{customerTableModel.getRowCount() + 1, customerId, customerName, contact, email, address});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
