package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Items implements EntityHandler {
    private int itemId;
    private String name;
    private int pricePerUnit;
    private int supplierId;

    public String getName() {
        return name;
    }

    @Override
    public void showMenu() {
        System.out.println("Items Management Menu:");
        System.out.println("1. Add Item");
        System.out.println("2. Delete Item");
        System.out.println("3. View All Items");
        System.out.println("4. Find Item by ID");
        System.out.println("5. Exit");
    }

    @Override
    public void handleChoice(int choice, Connection connection, Scanner scanner) {
        try {
            switch (choice) {
                case 1 -> addItem(connection, scanner);
                case 2 -> deleteItems(connection, scanner);
                case 3 -> findAll(connection);
                case 4 -> findById(connection, scanner);
                case 5 -> System.out.println("Exiting Items Management...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addItem(Connection connection, Scanner scanner) throws SQLException {
        createTablesIfNotExists(connection);

        System.out.print("Enter Number of Constituents of Item: ");
        int length = scanner.nextInt();
        scanner.nextLine();

        int[] rawMaterialIds = getRawMaterialIds(scanner, length);

        if (!validateRawMaterials(connection, rawMaterialIds)) {
            System.out.println("Invalid Raw Material IDs. Cannot add item.");
            return;
        }

        name = getItemName(scanner);
        pricePerUnit = getPricePerUnit(scanner);

        insertItemIntoDatabase(connection);
        linkRawMaterialsToItem(connection, rawMaterialIds);
    }

    public void deleteItems(Connection connection, Scanner scanner) throws SQLException {
        createTablesIfNotExists(connection);

        int itemIdToDelete = getItemId(scanner);

        if (itemIdToDelete != -1) {
            deleteItemFromDatabase(connection, itemIdToDelete);
        } else {
            System.out.println("Invalid Item ID.");
        }
    }

    public void findAll(Connection connection) throws SQLException {
        createTablesIfNotExists(connection);

        String selectAllQuery = "SELECT * FROM items";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllQuery)) {

            displayItems(rs);
        }
    }

    public void findById(Connection connection, Scanner scanner) throws SQLException {
        createTablesIfNotExists(connection);

        int itemIdToFind = getItemId(scanner);

        if (itemIdToFind != -1) {
            String selectQuery = "SELECT * FROM items WHERE itemId = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
                stmt.setInt(1, itemIdToFind);
                try (ResultSet rs = stmt.executeQuery()) {
                    displayItemDetails(rs);
                }
            }
        } else {
            System.out.println("Invalid Item ID.");
        }
    }

    private void createTablesIfNotExists(Connection connection) throws SQLException {
        String createItemsTable = """
                CREATE TABLE IF NOT EXISTS items (
                    itemId INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    price_per_unit INTEGER NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String createItemsRawMaterialsTable = """
                CREATE TABLE IF NOT EXISTS items_raw_materials (
                    itemId INTEGER,
                    raw_material_id INTEGER,
                    FOREIGN KEY (itemId) REFERENCES items(itemId),
                    FOREIGN KEY (raw_material_id) REFERENCES raw_materials(id),
                    PRIMARY KEY (itemId, raw_material_id)
                )
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createItemsTable);
            stmt.execute(createItemsRawMaterialsTable);
        }
    }

    private int[] getRawMaterialIds(Scanner scanner, int length) {
        int[] rawMaterialIds = new int[length];
        for (int i = 0; i < length; i++) {
            System.out.print("Enter the Raw Material " + (i + 1) + " ID: ");
            rawMaterialIds[i] = scanner.nextInt();
            scanner.nextLine();
        }
        return rawMaterialIds;
    }

    private boolean validateRawMaterials(Connection connection, int[] rawMaterialIds) throws SQLException {
        for (int rawMaterialId : rawMaterialIds) {
            if (!existsInTable(connection, "raw_materials", "id", rawMaterialId)) {
                return false;
            }
        }
        return true;
    }

    private String getItemName(Scanner scanner) {
        System.out.print("Enter the Item Name: ");
        return scanner.nextLine();
    }

    private int getPricePerUnit(Scanner scanner) {
        System.out.print("Enter the Item's Price per Unit: ");
        return scanner.nextInt();
    }

    private void insertItemIntoDatabase(Connection connection) throws SQLException {
        String insertItemQuery = "INSERT INTO items (name, price_per_unit) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertItemQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, pricePerUnit);
            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Item added successfully.");
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemId = generatedKeys.getInt(1);
                        System.out.println("Added item with itemId: " + itemId);
                    } else {
                        System.out.println("Failed to retrieve the itemId.");
                    }
                }
            } else {
                System.out.println("Failed to add item.");
            }
        }
    }

    private void linkRawMaterialsToItem(Connection connection, int[] rawMaterialIds) throws SQLException {
        String itemsRawMaterialsQuery = "INSERT INTO items_raw_materials (itemId, raw_material_id) VALUES (?, ?)";
        for (int rawMaterialId : rawMaterialIds) {
            try (PreparedStatement stmt = connection.prepareStatement(itemsRawMaterialsQuery)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, rawMaterialId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error linking item and raw materials: " + e.getMessage());
            }
        }
    }

    private int getItemId(Scanner scanner) {
        System.out.print("Enter Item ID: ");
        return scanner.nextInt();
    }

    private void deleteItemFromDatabase(Connection connection, int itemIdToDelete) throws SQLException {
        String deleteQuery = "DELETE FROM items WHERE itemId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, itemIdToDelete);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Item deleted successfully.");
            } else {
                System.out.println("No matching item found.");
            }
        }
    }

    private void displayItems(ResultSet rs) throws SQLException {
        System.out.println("Item ID\tName\t\tSupplier ID\tPrice per Unit");
        System.out.println("------------------------------------------------");

        while (rs.next()) {
            int id = rs.getInt("itemId");
            String name = rs.getString("name");
            int supplierId = rs.getInt("supplier_id");
            int price = rs.getInt("price_per_unit");
            System.out.printf("%d\t%-10s\t%d\t\t%d\n", id, name, supplierId, price);
        }
    }

    private void displayItemDetails(ResultSet rs) throws SQLException {
        if (rs.next()) {
            System.out.println("Item ID\tName\t\tPrice per Unit\tSupplier ID");
            System.out.println("------------------------------------------------");

            do {
                int id = rs.getInt("itemId");
                String name = rs.getString("name");
                int price = rs.getInt("price_per_unit");
                int supplierId = rs.getInt("supplier_id");
                System.out.printf("%d\t%-10s\t%d\t\t%d\n", id, name, price, supplierId);
            } while (rs.next());
        } else {
            System.out.println("No item found with the provided ID.");
        }
    }

    private boolean existsInTable(Connection connection, String tableName, String columnName, int value) throws SQLException {
        String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
