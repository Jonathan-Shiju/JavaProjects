package GroupProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class Loginpage extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Connection connection; // Connection will be created here
    private int vendorId;

    public Loginpage() {
        // Create connection within the class
        createConnection();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create Login and Signup Panels
        JPanel loginPanel = createLoginPanel();
        JPanel signUpPanel = createSignUpPanel();
        JPanel homePanel = createHomePanel();

        // Add panels to main panel
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(signUpPanel, "SignUp");
        mainPanel.add(homePanel, "Home");

        // Set up frame
        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
        setTitle("Login Application");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true); // Make the frame visible here
    }

    private void createConnection() {
        String url = "jdbc:mysql://localhost:3306/inventorymanagement"; // Replace with your database name
        String user = "root"; // Replace with your MySQL username
        String password = "Bcnof0912$"; // Replace with your MySQL password

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if connection fails
        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    String sql = "SELECT * FROM Vendors WHERE username = ? AND password = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, username);
                    statement.setString(2, password);
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        vendorId = resultSet.getInt("vendor_id");
                        cardLayout.show(mainPanel, "Home");
                    } else {
                        JOptionPane.showMessageDialog(Loginpage.this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Loginpage.this, "Error executing query: " + ex.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "SignUp");
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signUpButton);

        return panel;
    }

    private JPanel createSignUpPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton signUpButton = new JButton("Sign Up");
        JButton backButton = new JButton("Back to Login");

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (!username.isEmpty() && !password.isEmpty()) {
                    try {
                        String checkSql = "SELECT * FROM Vendors WHERE username = ?";
                        PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                        checkStatement.setString(1, username);
                        ResultSet resultSet = checkStatement.executeQuery();

                        if (!resultSet.next()) { // Username is available
                            String insertSql = "INSERT INTO Vendors (username, password) VALUES (?, ?)";
                            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                            insertStatement.setString(1, username);
                            insertStatement.setString(2, password);
                            insertStatement.executeUpdate();

                            cardLayout.show(mainPanel, "Login");
                            JOptionPane.showMessageDialog(Loginpage.this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(Loginpage.this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(Loginpage.this, "Error executing query: " + ex.getMessage(), "Query Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Loginpage.this, "Please enter a valid username and password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(signUpButton);
        panel.add(backButton);

        return panel;
    }

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
                new Viewpage(connection); // Use the existing Viewpage class
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
                cardLayout.show(mainPanel, "Login");
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
