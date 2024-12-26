package org.example;

import org.example.models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConnectDB connectDB = new ConnectDB();
        Connection connection = connectDB.getConnection();
        createTablesIfNotExist(connection);
        runMainMenu(connection);
    }
    static void runMainMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMainMenu();
            int userInput = getUserInput(scanner);
            if (userInput == -1) {
                System.out.println("Exiting...");
                break;
            }
            processMainMenuChoice(userInput, connection, scanner);
        }
    }
    static void showMainMenu() {

        System.out.printf("%n %50s %n ","Supply Chain Management System");
        System.out.println("\nMain Menu:");
        System.out.println("1. Supplier");
        System.out.println("2. Receiver");
        System.out.println("3. Raw Material");
        System.out.println("4. Item");
        System.out.println("5. Order");
        System.out.println("6. Finish Goods");
        System.out.println("Press 'Esc' or 'e' to Exit");
    }
    static void processMainMenuChoice(int choice, Connection connection, Scanner scanner) {
        EntityHandler entityHandler = null;

        switch (choice) {
            case 1 -> entityHandler = new Supplier();
            case 2 -> entityHandler = new Receiver();
            case 3 -> entityHandler = new Raw_Material();
            case 4 -> entityHandler = new Items();
            case 5 -> entityHandler = new Order();
            case 6 -> entityHandler = new FinishGoods(connection);
            default -> System.out.println("Invalid choice. Please try again.");
        }
        if (entityHandler != null) {
            runEntityMenu(entityHandler, connection, scanner);
        }else{
            try{
                if(connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    static void runEntityMenu(EntityHandler entityHandler, Connection connection, Scanner scanner) {
        while (true) {
            entityHandler.showMenu();
            int choice = getUserInput(scanner);
            if (choice == -1) {
                System.out.println("Going Back...");
                break;
            }
            entityHandler.handleChoice(choice, connection, scanner);
        }
    }
    static int getUserInput(Scanner scanner) {
        System.out.print("Enter your choice (or press 'Esc'/'e' to exit): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("e") || input.equalsIgnoreCase("esc")) {
            return -1;
        }

        if (input.isEmpty()) {
            System.out.println("Input cannot be empty. Please enter a valid number.");
            return 0;}

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return 0;
        }
    }
    static void createTablesIfNotExist(Connection connection){
        //Creating Supplier Table For the very First Time if Not Exist
        String createTableQueryForSupplier = "CREATE TABLE IF NOT EXISTS suppliers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "contact TEXT NOT NULL)";

            try (PreparedStatement stmt = connection.prepareStatement(createTableQueryForSupplier)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error creating table for Supplier");
                e.printStackTrace();
            }

        //Creating Supplier Table For the very First Time if Not Exist
        String createTableQueryForReceiver = "CREATE TABLE IF NOT EXISTS receiver (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT NOT NULL)";
        try (PreparedStatement stmt = connection.prepareStatement(createTableQueryForReceiver)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating table for receiver");
            e.printStackTrace();
        }

        //Creating Raw Material Table For the very First Time if Not Exist
        String createTableQueryForRawMaterial = "CREATE TABLE IF NOT EXISTS raw_materials (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "supplier_id INTEGER, " +
                "price INTEGER, " +
                "quantity INTEGER NOT NULL, " +
                "FOREIGN KEY (supplier_id) REFERENCES suppliers(id))";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQueryForRawMaterial);
        } catch (SQLException e) {
            System.out.println("Error creating Raw Material's table");
        }

        // Create Order and Finish goods Table's if not exist
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

        // creating Items Table If not exists
        String createItemsTable = """
                CREATE TABLE IF NOT EXISTS items (
                    itemId INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    price_per_unit INTEGER NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        // creating junction Table for Items and raw material to know which item will have which raw material
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
        }catch (SQLException e){
            System.out.println("Error creating Items Table.");
        }
    }
}
