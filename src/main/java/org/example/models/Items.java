package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Items {
    private int itemId;
    private String name;
    private int price_per_unit;

    public int getItemId() {

        return itemId;
    }
    public String getName() { return name; }

    public void addItem(Connection connection, Scanner scanner) throws SQLException {
    createTablesIfNotExists(connection);
    System.out.print("Number of Constituents of Item: ");
    int length = scanner.nextInt();
    scanner.nextLine();

    int[] raw_material_id = new int[length];

    for (int i = 0; i < length; i++) {
        System.out.print("Enter the Raw Material "+i+1+" ID: ");
        raw_material_id[i] = scanner.nextInt();
        scanner.nextLine();
    }

    for (int i = 0; i < length; i++) {
        String checkRawMaterialQuery = "SELECT 1 FROM raw_materials WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkRawMaterialQuery)) {
            checkStmt.setInt(1, raw_material_id[i]);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("Raw Material with ID " + raw_material_id + " does not exist. Cannot add Item.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking supplier existence: " + e.getMessage());
            throw e;
        }
    }

        System.out.print("Enter the Supplier ID: ");
        int supplierId = scanner.nextInt();
        scanner.nextLine();

        String checkSupplierQuery = "SELECT 1 FROM suppliers WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSupplierQuery)) {
            checkStmt.setInt(1, supplierId);
            try (ResultSet resultSet = checkStmt.executeQuery()) {
                if (!resultSet.next()) {
                    System.out.println("Supplier with ID " + supplierId + " does not exist. Cannot add raw material.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking supplier existence: " + e.getMessage());
            throw e;
        }

        System.out.print("Enter the Item Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the Item's Price per Unit: ");
        int price_per_unit = scanner.nextInt();
        scanner.nextLine();

        String insertQuery = "INSERT INTO items (name,price_per_unit,supplier_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, name);
            stmt.setInt(2, price_per_unit);
            stmt.setInt(3, supplierId);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Raw material added successfully.");
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemId = generatedKeys.getInt(1);
                        System.out.println("Added item with itemId: " + itemId);
                    } else {
                        System.out.println("Failed to retrieve the itemId.");
                    }
                }
            } else {
                System.out.println("Failed to add raw material.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding raw material: " + e.getMessage());
            throw e;
        }

        for (int i = 0; i<length; i++) {
            String items_raw_materials_query = "INSERT INTO items_raw_materials (itemId,raw_material_id) VALUES (?,?)";
            try(PreparedStatement stmt = connection.prepareStatement(items_raw_materials_query)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, raw_material_id[i]);
                stmt.executeUpdate();
            }
            catch (SQLException e){
                System.out.println("Error adding making relation between Item and Raw Material.");
            }
        }
    }

    private void createTablesIfNotExists(Connection connection) throws SQLException {
        String createItemsTableQuery = "CREATE TABLE IF NOT EXISTS items (itemId INTEGER PRIMARY KEY, name TEXT NOT NULL, price_per_unit INTEGER NOT NULL,supplier_id INTEGER, FOREIGN KEY(supplier_id) REFERENCES suppliers(id))";
        String createItemsRawMaterialTableQuery = "CREATE TABLE IF NOT EXISTS items_raw_materials (itemId INTEGER, raw_material_id INTEGER, FOREIGN KEY(itemId) REFERENCES items(itemId), FOREIGN KEY(raw_material_id) REFERENCES raw_materials(id),PRIMARY KEY(itemId, raw_material_id))";
        try(Statement stmt = connection.createStatement()) {
            stmt.execute(createItemsTableQuery);
            stmt.execute(createItemsRawMaterialTableQuery);
            System.out.println("Items table created");
        }
        catch (SQLException e) {
            System.out.println("Error creating Item's table");
        }

    }
}
