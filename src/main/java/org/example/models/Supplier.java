package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Supplier extends User {
    @Override
    public void AddUser(Connection connection, Scanner scanner) throws SQLException {

        System.out.print("Enter Supplier Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Supplier Contact: ");
        String contact = scanner.nextLine();

        String createTableQuery = "CREATE TABLE IF NOT EXISTS suppliers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT NOT NULL)";

        String insertSupplierQuery = "INSERT INTO suppliers (name, contact) VALUES (?, ?)";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }

        try (PreparedStatement stmt = connection.prepareStatement(insertSupplierQuery)) {
            stmt.setString(1, name);
            stmt.setString(2, contact);
            stmt.executeUpdate();
            System.out.println("Supplier added successfully: " + name + " (" + contact + ")");
        } catch (SQLException e) {
            System.out.println("Error adding supplier: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void DeleteUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Supplier Name or ID: ");
        String userInput = scanner.nextLine().trim();

        String deleteByIdQuery = "DELETE FROM suppliers WHERE id = ?";
        String deleteByNameQuery = "DELETE FROM suppliers WHERE name = ?";

        try {
            int id = Integer.parseInt(userInput);

            try (PreparedStatement stmt = connection.prepareStatement(deleteByIdQuery)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Supplier with ID " + id + " has been deleted.");
                } else {
                    System.out.println("No supplier found with ID " + id);
                }
            }

        } catch (NumberFormatException e) {
            try (PreparedStatement stmt = connection.prepareStatement(deleteByNameQuery)) {
                stmt.setString(1, userInput);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Supplier with name " + userInput + " has been deleted.");
                } else {
                    System.out.println("No supplier found with name " + userInput);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting supplier: " + e.getMessage());
            throw e;
        }
    }
}

