package org.example.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Receiver extends User {

    public Receiver(Connection connection) {
        super(connection);
    }

    @Override
    public String getTableName() {
        return "receiver";
    }

    @Override
    public void addUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Receiver Name: ");
        name = scanner.nextLine();

        System.out.print("Enter Contact Info: ");
        contactInfo = scanner.nextLine();

        String query = "INSERT INTO receiver (name, contact) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " receiver(s) added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding receiver: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Receiver ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "DELETE FROM receiver WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Receiver with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No receiver found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting receiver: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void showMenu() {
        System.out.println("Receiver Management Menu:");
        System.out.println("1. Add Receiver");
        System.out.println("2. Delete Receiver");
        System.out.println("3. View All Receivers");
        System.out.println("4. Find Receiver by ID");
        System.out.println("5. Press 'e' or 'Esc' to exit");
    }

    @Override
    public void handleChoice(int choice, Connection connection, Scanner scanner) {
        try {
            switch (choice) {
                case 1 -> addUser(connection, scanner);
                case 2 -> deleteUser(connection, scanner);
                case 3 -> findAll(connection);
                case 4 -> findById(connection, scanner);
                default -> System.out.println("Invalid choice. Try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
