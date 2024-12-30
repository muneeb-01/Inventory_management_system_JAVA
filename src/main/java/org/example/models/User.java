    package org.example.models;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.Scanner;

    public abstract class User implements EntityHandler {
        protected int id;
        protected String name;
        protected String contactInfo;
        public abstract String getTableName();
        public abstract void addUser(Connection connection, Scanner scanner) throws SQLException;
        public abstract void deleteUser(Connection connection, Scanner scanner) throws SQLException;
        public void findAll(Connection connection) throws SQLException {
            // Validate the connection
            if (connection == null) {
                throw new SQLException("Connection cannot be null.");
            }

            // Get and validate the table name
            String tableName = getTableName();
            if (tableName == null || tableName.trim().isEmpty()) {
                throw new SQLException("Table name is invalid or empty.");
            }

            // Construct the query safely (Assuming table names are predefined and validated)
            String query = "SELECT * FROM " + tableName;

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                System.out.println("ID\tName\t\tContact");
                System.out.println("------------------------------------");

                // Iterate through result set
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String contact = rs.getString("contact");
                    System.out.printf("%d\t%-10s\t%s\n", id, name, contact);
                }
            } catch (SQLException e) {
                System.out.println("Error retrieving records: " + e.getMessage());
                throw e; // Re-throw the exception to propagate it to the caller
            }
        }
        public int getValidId(Scanner scanner) {
            int id = -1;
            boolean validInput = false;

            // Keep asking for the ID until valid input is received
            while (!validInput) {
                try {
                    // Read the integer input
                    id = Integer.parseInt(scanner.nextLine().trim());

                    // Check if ID is valid (assuming IDs should be positive integers)
                    if (id <= 0) {
                        System.out.println("Error: ID must be a positive number.");
                    } else {
                        validInput = true; // Valid input, exit the loop
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    System.out.println("Error: Invalid input. Please enter a valid integer ID.");
                }
            }

            return id; // Return the validated ID
        }
        public void findById(Connection connection, Scanner scanner) throws SQLException {
            // Validate the connection
            if (connection == null) {
                throw new SQLException("Connection cannot be null.");
            }

            // Get and validate the ID using the getValidId method
            System.out.print("Enter "+ getTableName()+ " ID : ");
            int id = getValidId(scanner);

            // Construct the SQL query
            String query = "SELECT * FROM " + getTableName() + " WHERE id = ?";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id); // Set the ID parameter in the query

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Record found, print details
                        System.out.println("ID\tName\t\tContact");
                        System.out.println("------------------------------------");
                        do {
                            int userId = rs.getInt("id");
                            String name = rs.getString("name");
                            String contact = rs.getString("contact");
                            System.out.printf("%d\t%-10s\t%s\n", userId, name, contact);
                        } while (rs.next());
                    } else {
                        // No record found
                        System.out.println("No record found with ID " + id);
                    }
                }
            } catch (SQLException e) {
                // Handle SQL exceptions (e.g., syntax errors, connection issues)
                System.out.println("Error retrieving record: " + e.getMessage());
                throw e; // Re-throw the exception to allow the caller to handle it
            }
        }
        public String getValidName(Scanner scanner) {
            String name;
            while (true) {
                System.out.print("Enter "+getTableName()+" Name: ");
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Error: Supplier name cannot be empty.");
                } else {
                    break; // Valid name
                }
            }
            return name;
        }
        public String getValidContactInfo(Scanner scanner) {
            String contactInfo;
            while (true) {
                System.out.print("Enter Contact Info: ");
                contactInfo = scanner.nextLine().trim();
                if (contactInfo.isEmpty()) {
                    System.out.println("Error: Contact info cannot be empty.");
                } else {
                    break; // Valid contact info
                }
            }
            return contactInfo;
        }
    }
