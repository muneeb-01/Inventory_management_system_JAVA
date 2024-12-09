package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Raw_Material {
    private int id;
    private String name;
    private int quantity;
    private int supplierId;
    private int price;

    public void addRawMaterial(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Supplier ID: ");
        int supplierId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Price: ");
        int price = scanner.nextInt();

        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
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
            System.out.println("Error creating Raw Material's table");
        }
    }
    public void deleteRawMaterial(Connection connection, Scanner scanner) throws SQLException {
        createTableIfNotExists(connection);
        System.out.print("Enter Supplier ID: ");
        int SupplierId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Raw Material ID: ");
        int RawMaterialId = scanner.nextInt();
        scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM raw_materials WHERE supplier_id = ? AND id = ?")) {
            stmt.setInt(1, SupplierId);
            stmt.setInt(2, RawMaterialId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Raw material deleted successfully.");
            } else {
                System.out.println("No matching raw material found.");
            }
        }catch (SQLException e) {
            System.out.println("Error deleting raw material.");
        }
    }
    public void findAll(Connection connection) throws SQLException {
        createTableIfNotExists(connection);
        String selectAllQuery = "SELECT * FROM  raw_materials";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllQuery)) {
            System.out.println("ID\tName\t\tSupplier ID\t\tPrice\tQuantity");
            System.out.println("------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String supplierId = rs.getString("supplier_id");
                int price = rs.getInt("price");
                int quantity = rs.getInt("quantity");

                System.out.println(id + "\t" + name + "\t" + supplierId + "\t" + price + "\t" + quantity);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
            throw e;
        }
    }
    public void findById(Connection connection, Scanner scanner,String type) throws SQLException {
        createTableIfNotExists(connection);
        System.out.print("Enter ID : ");
        id = scanner.nextInt();
        scanner.nextLine();

        String selectQuery = "SELECT * FROM " + type + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID | Name         | supplier ID  | Quantity   | Price   ");
                    System.out.println("---------------------------------------------------------------------------------");

                    do {
                        id = rs.getInt("id");
                        name = rs.getString("name");
                        supplierId = rs.getInt("supplier_id");
                        quantity = rs.getInt("quantity");
                        price = rs.getInt("price");
                        // Print the details of the current row
                        System.out.printf("%d | %-10s | %8d | %8d | %8d ",
                                id, name, supplierId,quantity,price);
                    } while (rs.next());
                    System.out.println();
                } else {
                    System.out.println("No " + type + " found with ID = " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding " + type + ": " + e.getMessage());
            throw e;
        }
    }
}
