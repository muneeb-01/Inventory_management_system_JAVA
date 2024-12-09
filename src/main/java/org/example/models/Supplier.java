package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Supplier extends User {
    private void initializeSuppliersTable(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS suppliers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT NOT NULL)";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating suppliers table: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void AddUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Supplier Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Supplier Contact: ");
        String contact = scanner.nextLine().trim();

        // Initialize the suppliers table if it doesn't exist
        initializeSuppliersTable(connection);

        String insertSupplierQuery = "INSERT INTO suppliers (name, contact) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertSupplierQuery)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.executeUpdate();
            System.out.println("Supplier added successfully: " + name + " (" + contact + ")");
        } catch (SQLException e) {
            System.out.println("Error adding supplier: " + e.getMessage());
            throw e; // Rethrow the exception after logging the error
        }
    }
    @Override
    public void DeleteUser(Connection connection, Scanner scanner) throws SQLException {
        initializeSuppliersTable(connection);
        System.out.print("Enter Supplier Name or ID to delete: ");
        String userInput = scanner.nextLine().trim();

        // Queries for deletion by ID or name
        String deleteByIdQuery = "DELETE FROM suppliers WHERE id = ?";
        String deleteByNameQuery = "SELECT id, name FROM suppliers WHERE name = ?";
        // Try to delete by ID if the user input is numeric
        try {
            int id = Integer.parseInt(userInput);  // Check if the input is an integer (ID)
            deleteSupplierById(connection, id, deleteByIdQuery);
        } catch (NumberFormatException e) {
            // If it's not a number, we assume it's a name and proceed with the name search
            deleteSupplierByName(connection, userInput, deleteByNameQuery, scanner);
        } catch (SQLException e) {
            System.out.println("Error deleting supplier: " + e.getMessage());
            throw e; // Rethrow the exception after logging the error
        }
    }
    // Delete supplier by ID
    private void deleteSupplierById(Connection connection, int id, String deleteByIdQuery) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(deleteByIdQuery)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Supplier with ID " + id + " has been deleted.");
            } else {
                System.out.println("No supplier found with ID " + id);
            }
        }
    }
    // Delete supplier by name, with additional logic to handle duplicate names
    private void deleteSupplierByName(Connection connection, String name, String deleteByNameQuery, Scanner scanner) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(deleteByNameQuery)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            // Collect all suppliers with the same name
            int count = 0;
            while (rs.next()) {
                count++;
            }

            if (count == 0) {
                System.out.println("No supplier found with name " + name);
            } else if (count == 1) {
                // If only one supplier found, delete directly
                rs.beforeFirst(); // Move cursor back to the start
                if (rs.next()) {
                    int id = rs.getInt("id");
                    deleteSupplierById(connection, id, "DELETE FROM suppliers WHERE id = ?");
                }
            } else {
                // If multiple suppliers with the same name, ask for the ID
                System.out.println("Multiple suppliers found with name: " + name);
                System.out.print("Enter the ID of the supplier you want to delete: ");
                int supplierId = scanner.nextInt();
                deleteSupplierById(connection, supplierId, "DELETE FROM suppliers WHERE id = ?");
            }
        }
    }
}
