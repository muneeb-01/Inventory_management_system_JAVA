package org.example.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Supplier extends User {
    public Supplier(Connection connection){
        super(connection);
    }
    @Override
    public String getTableName() {
        return "suppliers";
    }
    @Override
    public void addUser(Connection connection, Scanner scanner) throws SQLException {
        name = super.getValidName(scanner);
        contactInfo = super.getValidContactInfo(scanner);

        String query = "INSERT INTO suppliers (name, contact) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            int rows = stmt.executeUpdate();
            System.out.println(rows + " supplier(s) added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding supplier: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void deleteUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter Supplier ID to delete: ");
        int id = super.getValidId(scanner);

        String query = "DELETE FROM suppliers WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Supplier with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No supplier found with ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error deleting supplier: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public void showMenu() {
        System.out.println("Supplier Management Menu:");
        System.out.println("1. Add Supplier");
        System.out.println("2. Delete Supplier");
        System.out.println("3. View All Suppliers");
        System.out.println("4. Find Supplier by ID");
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
