package org.example.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Order implements EntityHandler {
    private String name;
    private int receiverID;
    private int itemId;
    private int quantity;
    private int isFullFilled = 0;
    private int id;

    public Order(Connection connection) {
        createTablesIfNotExists(connection);
    }

    @Override
    public void showMenu() {
        System.out.println("Order Management Menu:");
        System.out.println("1. Add Order");
        System.out.println("2. Delete Order");
        System.out.println("3. Display Orders");
        System.out.println("4. Find Order by ID");
        System.out.println("5. Mark Order as Completed");
        System.out.println("6. Update Order Quantity");
        System.out.println("7. Press 'e' or 'Esc' to exit");
    }

    @Override
    public void handleChoice(int choice, Connection connection, Scanner scanner) {
        switch (choice) {
            case 1:
                addOrder(connection, scanner);
                break;
            case 2:
                deleteOrder(connection, scanner);
                break;
            case 3:
                displayOrders(connection);
                break;
            case 4:
                findById(connection, scanner, "orders");
                break;
            case 5:
                completion(connection, scanner);
                break;
            case 6:
                updateOrderQuantity(connection,scanner);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public void addOrder(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter Name: ");
            this.name = scanner.nextLine();
            System.out.print("Enter Item ID: ");
            this.itemId = scanner.nextInt();
            scanner.nextLine();

            if (!fetchItemByID(connection, this.itemId)) {
                System.out.println("Item not found. Please enter a valid Item ID.");
                return;
            }

            System.out.print("Enter Receiver ID: ");
            this.receiverID = scanner.nextInt();
            scanner.nextLine();

            if (!fetchReceiverById(connection, this.receiverID)) {
                System.out.println("Receiver not found. Please enter a valid Receiver ID.");
                return;
            }

            System.out.print("Enter Quantity: ");
            this.quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (!getRawMaterialInformation(connection, this.quantity, this.itemId)) {
                System.out.println("Failed to add the order.");
                return;
            }

            System.out.print("Is the order fulfilled? (0 = No, 1 = Yes): ");
            this.isFullFilled = scanner.nextInt();
            scanner.nextLine();


            String insertOrderQuery = """
                INSERT INTO orders (name, itemId, receiverId, fulfilled, quantity) 
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pstmt = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, this.name);
                pstmt.setInt(2, this.itemId);
                pstmt.setInt(3, this.receiverID);
                pstmt.setInt(4, this.isFullFilled);
                pstmt.setInt(5, this.quantity);

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Order added successfully!");
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        id = generatedKeys.getInt(1);
                        if (isFullFilled == 1) {
                            isFullFilled = 0;
                            ifFullfilled(connection);
                        }
                    }
                } else {
                    System.out.println("Failed to add the order.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding the order: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
            scanner.nextLine(); // Clear buffer in case of invalid input
        }
    }

    public void deleteOrder(Connection connection, Scanner scanner) {
        System.out.print("Enter Order ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM orders WHERE orderId = ?")) {
            stmt.setInt(1, orderID);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("No matching Order found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting Order: " + e.getMessage());
        }
    }

    public void displayOrders(Connection connection) {
        String fetchOrdersQuery = "SELECT * FROM orders";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(fetchOrdersQuery)) {

            System.out.println("OrderID | Name       | Quantity | ItemID | ReceiverID | Fulfilled | CreatedAt");
            System.out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int itemId = rs.getInt("itemId");
                int receiverId = rs.getInt("receiverId");
                int fulfilled = rs.getInt("fulfilled");
                String createdAt = rs.getString("createdAt");

                System.out.printf("%7d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                        orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    public void findById(Connection connection, Scanner scanner, String type) {
        System.out.print("Enter ID : ");
        id = scanner.nextInt();
        scanner.nextLine();

        String selectQuery = "SELECT * FROM " + type + " WHERE orderId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID | Name         | Quantity   | Receiver ID   | Item ID    | FullFilled  ");
                    System.out.println("---------------------------------------------------------------------------------");

                    do {
                        id = rs.getInt("orderId");
                        name = rs.getString("name");
                        quantity = rs.getInt("quantity");
                        receiverID = rs.getInt("receiverId");
                        itemId = rs.getInt("itemId");
                        isFullFilled = rs.getInt("fulfilled");

                        System.out.printf("%d | %-10s | %8d | %8d | %8d | %s%n",
                                id, name, quantity, receiverID, itemId, isFullFilled == 1 ? "Yes" : "No");
                    } while (rs.next());
                } else {
                    System.out.println("No Order found with ID = " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding Order: " + e.getMessage());
        }
    }

    public void ifFullfilled(Connection connection) throws SQLException {
        String selectQuery = "SELECT * FROM orders WHERE orderId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String insertOrderQuery = """
                            INSERT INTO finish_goods (name, itemId, receiverId, fulfilled, quantity) 
                            VALUES (?, ?, ?, ?, ?)
                        """;

                    try (PreparedStatement pstmt = connection.prepareStatement(insertOrderQuery)) {
                        pstmt.setString(1, this.name);
                        pstmt.setInt(2, this.itemId);
                        pstmt.setInt(3, this.receiverID);
                        pstmt.setInt(4, this.isFullFilled);
                        pstmt.setInt(5, this.quantity);
                        pstmt.executeUpdate();
                    }

                    String deleteOrderStatement = "DELETE FROM orders WHERE orderId = ?";

                    try (PreparedStatement pstmt = connection.prepareStatement(deleteOrderStatement)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }

                    System.out.println("Automatically Added to the Finishgoods.");
                } else {
                    System.out.println("No Order found with ID = " + id);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void completion(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter ID : ");
            id = scanner.nextInt();
            scanner.nextLine();

            String selectQuery = "SELECT * FROM orders WHERE orderId = ?";

            try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String insertOrderQuery = """
                            INSERT INTO finish_goods (name, itemId, receiverId, fulfilled, quantity) 
                            VALUES (?, ?, ?, ?, ?)
                        """;

                        try (PreparedStatement pstmt = connection.prepareStatement(insertOrderQuery)) {
                            pstmt.setString(1, this.name);
                            pstmt.setInt(2, this.itemId);
                            pstmt.setInt(3, this.receiverID);
                            pstmt.setInt(4, this.isFullFilled);
                            pstmt.setInt(5, this.quantity);
                            pstmt.executeUpdate();
                        }

                        String deleteOrderStatement = "DELETE FROM orders WHERE orderId = ?";

                        try (PreparedStatement pstmt = connection.prepareStatement(deleteOrderStatement)) {
                            pstmt.setInt(1, id);
                            pstmt.executeUpdate();
                        }

                        System.out.println("Order Completion successful.");
                    } else {
                        System.out.println("No Order found with ID = " + id);
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablesIfNotExists(Connection connection) {
        String createOrdersTableQuery = """
            CREATE TABLE IF NOT EXISTS orders (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                itemId INTEGER NOT NULL,
                receiverId INTEGER NOT NULL,
                fulfilled INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createFinishGoodsTableQuery = """
            CREATE TABLE IF NOT EXISTS finish_goods (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                itemId INTEGER NOT NULL,
                receiverId INTEGER NOT NULL,
                fulfilled INTEGER NOT NULL,
                quantity INTEGER NOT NULL,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createOrdersTableQuery);
            stmt.execute(createFinishGoodsTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    private boolean fetchItemByID(Connection connection, int itemId) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM items WHERE itemId = ?")) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching item by ID: " + e.getMessage());
        }
        return false;
    }

    private boolean fetchReceiverById(Connection connection, int receiverID) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM receiver WHERE id = ?")) {
            pstmt.setInt(1, receiverID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching receiver by ID: " + e.getMessage());
        }
        return false;
    }

    private boolean getRawMaterialInformation(Connection connection, int quantity, int itemId) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM items_raw_materials WHERE itemId = ?")) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (!findRawMaterialById(connection,rs.getInt("raw_material_id"))){
                        return false;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error checking raw materials: " + e.getMessage());
        }
        return true;
    }

    public Boolean findRawMaterialById(Connection connection,int RawMaterial_id) {
        String selectQuery = "SELECT * FROM raw_materials WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, RawMaterial_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    do {
                        String name = rs.getString("name");
                        int quantity = rs.getInt("quantity");
                        if (quantity < this.quantity) {
                            System.out.println("Not enough "+name+"("+RawMaterial_id+")"+" in inventory");
                            return false;
                        }else{
                            updateRawMaterialQuantity(connection, RawMaterial_id, this.quantity);
                            return true;
                        }
                    } while (rs.next());
                } else {
                    System.out.println("No raw_materials found with ID = " + RawMaterial_id);
                return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding raw_materials: " + e.getMessage());
        }
        return false;
    }

    public void updateOrderQuantity(Connection connection, Scanner scanner) {
        System.out.print("Enter Order ID: ");
        int orderId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter New Quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Retrieve the current quantity and itemId for the given orderId
            String selectOrderQuery = "SELECT quantity, itemId FROM orders WHERE orderId = ?";
            int currentQuantity = 0;
            itemId = 0;

            try (PreparedStatement pstmt = connection.prepareStatement(selectOrderQuery)) {
                pstmt.setInt(1, orderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        currentQuantity = rs.getInt("quantity");
                        itemId = rs.getInt("itemId");
                    } else {
                        System.out.println("Order not found with ID = " + orderId);
                        return;
                    }
                }
            }

            // Check the availability of raw materials for the item
            int additionalQuantityRequired = newQuantity - currentQuantity;

            if (additionalQuantityRequired > 0) { // Only check if the new quantity is greater
                String rawMaterialsQuery = """
                    SELECT 
                        ir.itemId AS ItemID,
                        r.id AS rawMaterialID,
                        r.quantity AS available_quantity
                    FROM items_raw_materials AS ir
                    JOIN raw_materials AS r ON ir.raw_material_id = r.id
                    WHERE ir.itemId = ?
                """;


                try (PreparedStatement pstmt = connection.prepareStatement(rawMaterialsQuery)) {
                    pstmt.setInt(1, itemId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        boolean sufficientMaterials = true;

                        while (rs.next()) {
                            int rawMaterialId = rs.getInt("rawMaterialID");
                            int availableQuantity = rs.getInt("available_quantity");

                            if (additionalQuantityRequired > availableQuantity) {
                                System.out.printf("Insufficient raw material (ID: %d). Required: %d, Available: %d%n",
                                        rawMaterialId, additionalQuantityRequired, availableQuantity);
                                sufficientMaterials = false;
                            }else {
                                updateRawMaterialQuantity(connection, rawMaterialId, additionalQuantityRequired);
                            }
                        }
                        if (!sufficientMaterials) {
                            System.out.println("Order update failed due to insufficient raw materials.");
                            return;
                        }
                    }
                }
            }

            // Update the order quantity
            String updateOrderQuery = "UPDATE orders SET quantity = ? WHERE orderId = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateOrderQuery)) {
                pstmt.setInt(1, newQuantity);
                pstmt.setInt(2, orderId);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Order quantity updated successfully!");
                } else {
                    System.out.println("Failed to update the order quantity.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating order quantity: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
            scanner.nextLine(); // Clear buffer in case of invalid input
        }
    }

    private void updateRawMaterialQuantity(Connection connection, int rawMaterialId, int quantity) {
        String updateRawMaterialQuery = "UPDATE raw_materials SET quantity = quantity - ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateRawMaterialQuery)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, rawMaterialId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating raw material quantity: " + e.getMessage());
        }
    }

}
