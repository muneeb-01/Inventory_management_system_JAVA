package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class Order {
    private String name;
    private int receiverID;
    private int itemId;
    private int quantity;
    private int isFullFilled = 0;

    public void addOrder(Connection connection, Scanner scanner) {
        try {
            // Step 1: Create the table if it doesn't exist
            createTablesIfNotExists(connection);

            // Step 2: Collect order details from the user
            System.out.print("Enter Name: ");
            this.name = scanner.nextLine();

            System.out.print("Enter Item ID: ");
            this.itemId = scanner.nextInt();

            if(!fetchItemByID(connection, this.itemId)) {
                System.out.print("Item not found. Please enter a valid Item ID.");
                return;
            }

            System.out.print("Enter Receiver ID: ");
            this.receiverID = scanner.nextInt();

            if(!fetchReceiverById(connection, this.receiverID)) {
                System.out.println("Receiver not found. Please enter a valid Receiver ID.");
                return;
            }

            System.out.print("Enter Quantity: ");
            this.quantity = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Is the order fulfilled? (0 = No, 1 = Yes): ");
            this.isFullFilled = scanner.nextInt();

            String getReceiverByID = "DELETE FROM receiver WHERE id = ?";

            // SQL insert query
            String insertOrderQuery = """
                INSERT INTO orders (name, itemId, receiverId, fulfilled,quantity) 
                VALUES (?, ?, ?, ?,?)
            """;

            // Use PreparedStatement for secure data insertion
            try (PreparedStatement pstmt = connection.prepareStatement(insertOrderQuery)) {
                pstmt.setString(1, this.name);
                pstmt.setInt(2, this.itemId);
                pstmt.setInt(3, this.receiverID);
                pstmt.setInt(4, this.isFullFilled);
                pstmt.setInt(5, this.quantity);

                // Execute the query
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Order added successfully!");
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
    private void createTablesIfNotExists(Connection connection) throws SQLException {
        String createOrdersTableQuery = """
            CREATE TABLE IF NOT EXISTS orders (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                itemId INTEGER,
                receiverId INTEGER,
                fulfilled INTEGER NOT NULL DEFAULT 0, -- 0 = FALSE, 1 = TRUE
                createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, -- Automatically stores the current time
                FOREIGN KEY(itemId) REFERENCES items(itemId),
                FOREIGN KEY(receiverId) REFERENCES receiver(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createOrdersTableQuery);
            System.out.println("Order table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating Order table: " + e.getMessage());
            throw e; // Rethrow exception to handle upstream if necessary
        }
    }
    private boolean fetchReceiverById(Connection connection, int receiverId) {
        String query = "SELECT * FROM receiver WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, receiverId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Receiver found: ID = " + rs.getInt("id") + ", Name = " + rs.getString("name"));
                    return true;
                } else {
                    System.out.println("No receiver found with ID: " + receiverId);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching receiver: " + e.getMessage());
            return false;
        }
    }
    private boolean fetchItemByID(Connection connection, int itemId) {
        String query = "SELECT * FROM items WHERE itemid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Item found: ID = " + rs.getInt("itemId") + ", Name = " + rs.getString("name"));
                    return true;
                } else {
                    System.out.println("No receiver found with ID: " + itemId);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching receiver: " + e.getMessage());
            return false;
        }
    }
    public  void  deleteOrder(Connection connection,Scanner scanner){
        System.out.print("Enter Item ID: ");
        int orderID = scanner.nextInt();
        scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM orders WHERE orderId = ?")) {
            stmt.setInt(1, orderID);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("No matching Item found.");
            }
        }catch (SQLException e) {
            System.out.println("Error deleting Item.");
        }}
    public void displayOrders(Connection connection) {
        String fetchOrdersQuery = "SELECT * FROM orders";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(fetchOrdersQuery)) {

            // Print column headers
            System.out.println("OrderID | Name       | Quantity | ItemID | ReceiverID | Fulfilled | CreatedAt");
            System.out.println("---------------------------------------------------------------------------------");

            // Iterate through the result set and print each row
            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int itemId = rs.getInt("itemId");
                int receiverId = rs.getInt("receiverId");
                int fulfilled = rs.getInt("fulfilled");
                String createdAt = rs.getString("createdAt");

                // Print the details of the current row
                System.out.printf("%7d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                        orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }
}
