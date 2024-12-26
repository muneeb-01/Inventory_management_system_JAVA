package org.example;

import org.example.models.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ConnectDB connectDB = new ConnectDB();
        Connection connection = connectDB.getConnection();
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
            case 1 : entityHandler = new Supplier(connection); break;
            case 2 : entityHandler = new Receiver(connection); break;
            case 3 : {
                new Supplier(connection);
                new Raw_Material(connection);
                break;
            }
            case 4 : {
                new Raw_Material(connection);
                entityHandler = new Items(connection);
                break;
            }
            case 5 : {
                new Raw_Material(connection);
                new Items(connection);
                entityHandler = new Order(connection);
                break;
            }
            case 6 : entityHandler = new FinishGoods(connection); break;
            default : System.out.println("Invalid choice. Please try again.");
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
}
