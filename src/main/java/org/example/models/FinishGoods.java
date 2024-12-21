package org.example.models;

import java.sql.*;
import java.util.Scanner;

public class FinishGoods implements EntityHandler {
    private int itemId;
    private String name;
    private int quantity;
    private int receiverID;
    private int id;
    private int isFullFilled;

    public FinishGoods(Connection connection) {
        createTablesIfNotExists(connection);
    }

    static private void createTablesIfNotExists(Connection connection) {
        String createFinishGoodsTableQuery = """
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
            stmt.execute(createFinishGoodsTableQuery);
        } catch (SQLException e) {
            System.out.println("Error creating Finish Goods table: " + e.getMessage());
        }
    }

    @Override
    public void showMenu() {
        System.out.println("Finish Goods Management Menu:");
        System.out.println("1. Display Finish Goods");
        System.out.println("2. Find Finish Good by ID");
        System.out.println("3. Show Delivered Items");
        System.out.println("4. Show Remaining Finish Goods");
        System.out.println("5. Update Fulfilled Status");
        System.out.println("6. Press 'e' or 'Esc' to exit");
    }

    @Override
    public void handleChoice(int choice, Connection connection, Scanner scanner) {
        switch (choice) {
            case 1:
                displayOrders(connection);
                break;
            case 2:
                findById(connection, scanner, "finish_goods");
                break;
            case 3:
                showDeliveredItems(connection);
                break;
            case 4:
                showRemainingFinishGoods(connection);
                break;
            case 5:
                delivered(connection, scanner);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    public void displayOrders(Connection connection) {
        createTablesIfNotExists(connection);
        String fetchOrdersQuery = "SELECT * FROM finish_goods";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(fetchOrdersQuery)) {

            System.out.println("ID | Name       | Quantity | ItemID | ReceiverID | Fulfilled | CreatedAt");
            System.out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int itemId = rs.getInt("itemId");
                int receiverId = rs.getInt("receiverId");
                int fulfilled = rs.getInt("fulfilled");
                String createdAt = rs.getString("createdAt");

                System.out.printf("%d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                        orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    public void findById(Connection connection, Scanner scanner, String type) {
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

                        System.out.printf("%d | %-10s | %8d | %8d | %8d | %s%n",
                                id, name, quantity, receiverID, itemId, isFullFilled == 1 ? "Yes" : "No");
                    } while (rs.next());
                    System.out.println();
                } else {
                    System.out.println("No " + type + " found with ID = " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding " + type + ": " + e.getMessage());
        }
    }

    public void delivered(Connection connection, Scanner scanner) {
        createTablesIfNotExists(connection);

        System.out.print("Enter Finish Goods ID to mark as delivered: ");
        int orderId = scanner.nextInt();
        scanner.nextLine();

        String updateQuery = "UPDATE finish_goods SET fulfilled = 1 WHERE orderId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, orderId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Finish Good with ID " + orderId + " has been marked as delivered.");
            } else {
                System.out.println("No Finish Good found with ID " + orderId + ".");
            }

        } catch (SQLException e) {
            System.out.println("Error marking Finish Good as delivered: " + e.getMessage());
        }
    }

    // Show delivered items (fulfilled = 0)
    public void showDeliveredItems(Connection connection) {
        createTablesIfNotExists(connection);

        String fetchDeliveredItemsQuery = "SELECT * FROM finish_goods WHERE fulfilled = 1";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(fetchDeliveredItemsQuery)) {

            System.out.println("ID | Name       | Quantity | ItemID | ReceiverID | Fulfilled | CreatedAt");
            System.out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int itemId = rs.getInt("itemId");
                int receiverId = rs.getInt("receiverId");
                int fulfilled = rs.getInt("fulfilled");
                String createdAt = rs.getString("createdAt");

                System.out.printf("%d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                        orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching delivered items: " + e.getMessage());
        }
    }

    // Show remaining finish goods (fulfilled = 1)
    public void showRemainingFinishGoods(Connection connection) {
        createTablesIfNotExists(connection);

        String fetchRemainingItemsQuery = "SELECT * FROM finish_goods WHERE fulfilled = 0";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(fetchRemainingItemsQuery)) {

            System.out.println("ID | Name       | Quantity | ItemID | ReceiverID | Fulfilled | CreatedAt");
            System.out.println("---------------------------------------------------------------------------------");

            while (rs.next()) {
                int orderId = rs.getInt("orderId");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int itemId = rs.getInt("itemId");
                int receiverId = rs.getInt("receiverId");
                int fulfilled = rs.getInt("fulfilled");
                String createdAt = rs.getString("createdAt");

                System.out.printf("%d | %-10s | %8d | %6d | %10d | %9s | %s%n",
                        orderId, name, quantity, itemId, receiverId, (fulfilled == 1 ? "Yes" : "No"), createdAt);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching remaining items: " + e.getMessage());
        }
    }

}
