package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class FinishGoods {
    private int itemId;
    private String name;
    private int quantity;
    private int receiverID;
    private int id;
    private int isFullFilled;

    static private void createTablesIfNotExists(Connection connection) {
        String createOrdersTableQuery = """
            CREATE TABLE IF NOT EXISTS finish_goods (
                orderId INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                itemId INTEGER,
                receiverId INTEGER,
                fulfilled INTEGER NOT NULL DEFAULT 1, -- 0 = FALSE, 1 = TRUE
                createdAt DATETIME DEFAULT CURRENT_TIMESTAMP, -- Automatically stores the current time
                FOREIGN KEY(itemId) REFERENCES items(itemId),
                FOREIGN KEY(receiverId) REFERENCES receiver(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createOrdersTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating Order table: " + e.getMessage());
        }
    }
    public void displayOrders(Connection connection) {
        createTablesIfNotExists(connection);
        String fetchOrdersQuery = "SELECT * FROM finish_goods";

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

                    // Print the details of the current row
                    System.out.printf("%7d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                            orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
                }

        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }
    public void findById(Connection connection, Scanner scanner,String type) throws SQLException {
        createTablesIfNotExists(connection);
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

                        // Print the details of the current row
                        System.out.printf("%d | %-10s | %8d | %8d | %8d | %s",
                                id, name, quantity,receiverID,itemId,isFullFilled == 1 ? "Yes" : "No");
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
    public void AddFinishGoods(Connection connection, Scanner scanner,String type) throws SQLException {

    }
}

