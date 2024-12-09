package org.example;

import org.example.models.*;
import org.example.models.ConnectDB;

import java.sql.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConnectDB con = new ConnectDB();
        Connection connection = con.getConnection();
        runMainMenu(connection);
    }

    static public void runMainMenu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        int userInput;
        Main main = new Main();

        while (true) {
            showMainMenu();
            try {
                System.out.print("Enter your choice (or press 'Esc' to exit): ");
                userInput = System.in.read();
                if (userInput == 27 || userInput == 101) {
                    scanner.nextLine();
                    System.out.println("Exiting...");
                    break;
                }
                scanner.nextLine();
                processMainMenuChoice(userInput, connection, scanner, main);
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
            }
        }
    }
    static private  void showMainMenu() {
        System.out.println("Choose an option:");
        System.out.println("1. Supplier");
        System.out.println("2. Receiver");
        System.out.println("3. RawMaterial");
        System.out.println("4. Item");
        System.out.println("5. AddOrder");
        System.out.println("Press 'Esc' to Exit");
    }
    static private  void processMainMenuChoice(int userInput, Connection connection, Scanner scanner, Main main) {
        switch (userInput) {
            case '1': main.runEntityMenu(new Supplier(), "Supplier", connection, scanner); break;
            case '2': main.runEntityMenu(new Receiver(), "Receiver", connection, scanner); break;
            case '3': main.runEntityMenu(new Raw_Material(), "Raw Material", connection, scanner); break;
            case '4': main.runEntityMenu(new Items(), "Item", connection, scanner); break;
            case '5': main.runEntityMenu(new Order(), "Order", connection, scanner); break;
            default: System.out.println("Invalid choice. Please enter a valid option or press 'Esc' to exit.");
        }
    }

    private void runEntityMenu(Object entity, String entityName, Connection connection, Scanner scanner) {
        int userInput;

        while (true) {
            showEntityMenu(entityName);
            try {
                System.out.print("Enter your choice (or press 'Esc' to exit): ");
                userInput = System.in.read();
                if (userInput == 27 || userInput == 101) { // 'Esc' key or 'e' to exit
                    scanner.nextLine();
                    System.out.println("Going Back...");
                    break;
                }
                scanner.nextLine();
                handleEntityChoice(entity, userInput, connection, scanner);
            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
            }
        }
    }
    private void showEntityMenu(String entityName) {
        System.out.println("Choose an option for " + entityName + ":");
        System.out.println("1. Add " + entityName);
        System.out.println("2. Delete " + entityName);
        System.out.println("3. View All " + entityName + "s");
        System.out.println("4. Get " + entityName + " by ID.");

        System.out.println("Press 'Esc' to Exit");
    }
    private void handleEntityChoice(Object entity, int userInput, Connection connection, Scanner scanner) {
        try {
            switch (userInput) {
                case '1': addEntity(entity, connection, scanner); break;
                case '2': deleteEntity(entity, connection, scanner); break;
                case '3': viewAllEntities(entity, connection); break;
                case '4': getEntityById(entity,connection,scanner); break;
                default: System.out.println("Invalid choice. Please enter a valid number or press 'Esc' to exit."); break;
            }
        } catch (SQLException e) {
            System.out.println("Error with database operation.");
        }
    }
    private void addEntity(Object entity, Connection connection, Scanner scanner) throws SQLException {
        if (entity instanceof Supplier) {
            ((Supplier) entity).AddUser(connection, scanner);
        } else if (entity instanceof Receiver) {
            ((Receiver) entity).AddUser(connection, scanner);
        } else if (entity instanceof Raw_Material) {
            ((Raw_Material) entity).addRawMaterial(connection, scanner);
        } else if (entity instanceof Items) {
            ((Items) entity).addItem(connection, scanner);
        }else if (entity instanceof Order) {
            ((Order) entity).addOrder(connection,scanner);
        }
    }
    private void deleteEntity(Object entity, Connection connection, Scanner scanner) throws SQLException {
        if (entity instanceof Supplier) {
            ((Supplier) entity).DeleteUser(connection, scanner);
        } else if (entity instanceof Receiver) {
            ((Receiver) entity).DeleteUser(connection, scanner);
        } else if (entity instanceof Raw_Material) {
            ((Raw_Material) entity).deleteRawMaterial(connection, scanner);
        } else if (entity instanceof Items) {
            ((Items) entity).deleteItems(connection, scanner);
        } else if (entity instanceof Order) {
            ((Order) entity).deleteOrder(connection,scanner);
        }
    }
    private void viewAllEntities(Object entity, Connection connection) throws SQLException {
        if (entity instanceof Supplier) {
            ((Supplier) entity).findAll(connection, "suppliers");
        } else if (entity instanceof Receiver) {
            ((Receiver) entity).findAll(connection, "receiver");
        } else if (entity instanceof Raw_Material) {
            ((Raw_Material) entity).findAll(connection);
        } else if (entity instanceof Items) {
            ((Items) entity).findAll(connection);
        } else if (entity instanceof Order) {
            ((Order) entity).displayOrders(connection);
        }
    }
    private void getEntityById(Object entity, Connection connection,Scanner scanner) throws SQLException {
        if (entity instanceof Supplier) {
            ((Supplier) entity).findById(connection,scanner,"suppliers");
        }else if(entity instanceof Receiver){
            ((Receiver) entity).findById(connection,scanner,"receiver");
        } else if (entity instanceof Raw_Material) {
            ((Raw_Material) entity).findById(connection,scanner,"raw_materials");
        }
        else if (entity instanceof Order) {
            ((Order) entity).findById(connection,scanner,"orders");
        }
        else if (entity instanceof Items) {
            ((Items) entity).findById(connection,scanner,"items");
        }
    }
}
