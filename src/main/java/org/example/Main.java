package org.example;

import  org.example.models.*;
import org.example.models.ConnectDB;

import java.util.Scanner;
import java.sql.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ConnectDB con = new ConnectDB();
        Connection connection = con.getConnection();
        runSwitchStatement(connection);
    }

    static public void runSwitchStatement(Connection connection) {
            Scanner scanner = new Scanner(System.in);
            int userInput = -1;
            Main main = new Main();
            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Supplier");
                System.out.println("2. Receiver");
                System.out.println("3. RawMaterial");
                System.out.println("4. Item");
                System.out.println("5. AddOrder");
                System.out.println("Press 'Esc' to Exit");

                try {
                    System.out.print("Enter your choice (or press 'Esc' to exit): ");
                    userInput = System.in.read();
                    if (userInput == 27 || userInput == 101) {
                        scanner.nextLine();
                        System.out.println("Exiting...");
                        break;
                    }
                    scanner.nextLine();
                    switch (userInput) {
                        case '1':
                            System.out.println("You selected Supplier.");
                            main.RunSupplier(connection,scanner);
                            break;

                        case '2':
                            System.out.println("You selected Receiver.");
                            main.RunReceiver(connection,scanner);
                            break;

                        case '3':
                            System.out.println("You selected RawMaterial.");
                            main.RunRawMaterial(connection,scanner);
                            break;

                        case '4':
                            System.out.println("You selected Item.");
                            main.RunItems(connection,scanner);
                            break;

                        case '5':
                            System.out.println("You selected AddOrder.");
                            break;

                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 5 or press 'Esc' to exit.");
                            break;
                    }

                } catch (IOException e) {
                    System.out.println("An error occurred while reading input.");
                }
            }
        }
        public void RunSupplier(Connection connection,Scanner scanner){
            int userInput = -1;
            Supplier sup = new Supplier();

            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Add Supplier");
                System.out.println("2. Delete Supplier");
                System.out.println("3. All Suppliers");
                System.out.println("Press 'Esc' to Exit");

                try {
                    System.out.print("Enter your choice (or press 'Esc' to exit): ");
                    userInput = System.in.read();
                    if (userInput == 27 || userInput == 101) {
                         scanner.nextLine();
                        System.out.println("Exiting...");
                        break;
                    }
                    scanner.nextLine();
                    switch (userInput) {
                        case '1':
                            System.out.println("You selected Add Supplier.");
                            try{
                                sup.AddUser(connection,scanner);
                            }
                            catch (SQLException e){
                                System.out.println("Error! Adding Supplier");
                            }
                            break;
                        case '2':
                            System.out.println("You selected Delete Supplier.");
                            try{
                                sup.DeleteUser(connection,scanner);
                            }
                            catch (SQLException e){
                                System.out.println("Error! Adding Supplier");
                            }
                            break;
                        case '3':
                            System.out.println("Finding All Supplier.");
                            try{
                                sup.findAll(connection,"suppliers");
                            }
                            catch (SQLException e){
                                System.out.println("Error! Adding Supplier");
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 5 or press 'Esc' to exit.");
                            break;
                    }

                } catch (IOException e) {
                    System.out.println("An error occurred while reading input.");
                }


        }
    }
        public void RunReceiver(Connection connection,Scanner scanner){
        int userInput = -1;
        Receiver receiver = new Receiver();

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Receiver");
            System.out.println("2. Delete Receiver");
            System.out.println("3. All Suppliers");
            System.out.println("Press 'Esc' to Exit");

            try {
                System.out.print("Enter your choice (or press 'Esc' to exit): ");
                userInput = System.in.read();
                if (userInput == 27 || userInput == 101) {
                    scanner.nextLine();
                    System.out.println("Exiting...");
                    break;
                }
                scanner.nextLine();
                switch (userInput) {
                    case '1':
                        System.out.println("You selected Add Receiver.");
                        try{
                            receiver.AddUser(connection,scanner);
                        }
                        catch (SQLException e){
                            System.out.println("Error! Adding Supplier");
                        }
                        break;
                    case '2':
                        System.out.println("You selected Delete Receiver.");
                        try{
                            receiver.DeleteUser(connection,scanner);
                        }
                        catch (SQLException e){
                            System.out.println("Error! Adding Supplier");
                        }
                        break;
                    case '3':
                        System.out.println("Finding All Supplier.");
                        try{
                            receiver.findAll(connection,"receiver");
                        }
                        catch (SQLException e){
                            System.out.println("Error! Adding Supplier");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5 or press 'Esc' to exit.");
                        break;
                }

            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
            }


        }
    }
        public void RunRawMaterial(Connection connection,Scanner scanner){
            int userInput = -1;
            Raw_Material rawMaterial = new Raw_Material();

            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1. Add Raw Material");
                System.out.println("2. Delete Raw Material");
                System.out.println("3. Raw Material's");
                System.out.println("Press 'Esc' to Exit");

                try {
                    System.out.print("Enter your choice (or press 'Esc' to exit): ");
                    userInput = System.in.read();
                    if (userInput == 27 || userInput == 101) {
                        scanner.nextLine();
                        System.out.println("Exiting...");
                        break;
                    }
                    scanner.nextLine();
                    switch (userInput) {
                        case '1':
                            System.out.println("You selected Add Raw Material.");
                            try{
                                rawMaterial.addRawMaterial(connection,scanner);
                            }
                            catch (SQLException e){
                                System.out.println("Error! Adding Supplier");
                            }
                            break;
                        case '2':
                            System.out.println("You selected Delete Raw Material.");
                            try {
                                rawMaterial.deleteRawMaterial(connection,scanner);
                            }catch (SQLException e){
                                System.out.println("Error! Deleting Raw Material.");
                            }
                            break;
                        case '3':
                            System.out.println("You selected Raw Materials.");
                            try {
                                rawMaterial.findAll(connection);
                            }catch (SQLException e){
                                System.out.println("Error! Getting Raw Material.");
                            }
                            break;
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 5 or press 'Esc' to exit.");
                            break;
                    }

                } catch (IOException e) {
                    System.out.println("An error occurred while reading input.");
                }


            }
        }
        public void RunItems(Connection connection,Scanner scanner){
        int userInput = -1;
        Items item = new Items();

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Add Item");
//            System.out.println("2. Delete Item");
//            System.out.println("3. Items");
            System.out.println("Press 'Esc' to Exit");

            try {
                System.out.print("Enter your choice (or press 'Esc' to exit): ");
                userInput = System.in.read();
                if (userInput == 27 || userInput == 101) {
                    scanner.nextLine();
                    System.out.println("Exiting...");
                    break;
                }
                scanner.nextLine();
                switch (userInput) {
                    case '1':
                        System.out.println("You selected Add Raw Material.");
                        try{
                            item.addItem(connection,scanner);
                        }
                        catch (SQLException e){
                            System.out.println("Error! Adding Supplier");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5 or press 'Esc' to exit.");
                        break;
                }

            } catch (IOException e) {
                System.out.println("An error occurred while reading input.");
            }


        }
    }
}

