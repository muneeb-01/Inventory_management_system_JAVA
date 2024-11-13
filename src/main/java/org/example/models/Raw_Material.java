package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Raw_Material {
    private String name;
    private int quantity;
    private int supplierId;
    private int price;

    public void addRawMaterial(Connection connection, Scanner scanner) throws SQLException {
        // Prompt the user for input
        System.out.print("Enter Supplier ID: ");
        int supplierId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Price: ");
        int price = scanner.nextInt();

        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        // Set the class attributes for raw material
        this.name = name;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.price = price;

        // SQL query for inserting raw material
        String insertQuery = "INSERT INTO raw_materials (name, quantity, supplier_id, price) VALUES (?, ?, ?, ?)";

        // Ensure table exists, this should ideally be done once during setup, not every time a new material is added
        createTableIfNotExists(connection);

        // Execute the insert query to add the new raw material
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setInt(3, supplierId);
            stmt.setInt(4, price);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Raw material added successfully.");
            } else {
                System.out.println("Failed to add raw material.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding raw material: " + e.getMessage());
            throw e;  // Propagate the exception for further handling
        }
    }

    // Method to create the table if it does not already exist
    private void createTableIfNotExists(Connection connection) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS raw_materials (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "supplier_id INTEGER, " +
                "price INTEGER, " +
                "quantity INTEGER NOT NULL, " +
                "FOREIGN KEY (supplier_id) REFERENCES suppliers(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

}
