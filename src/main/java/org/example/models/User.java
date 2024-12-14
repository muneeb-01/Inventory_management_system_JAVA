package org.example.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public abstract class User implements EntityHandler {
    protected int id;
    protected String name;
    protected String contactInfo;

    User(Connection connection) {
        createTableIfNotExists(connection);
    }

    // Abstract methods for subclass-specific behavior
    public abstract String getTableName();

    public abstract void addUser(Connection connection, Scanner scanner) throws SQLException;

    public abstract void deleteUser(Connection connection, Scanner scanner) throws SQLException;

    // Common methods for viewing all and finding by ID
    public void findAll(Connection connection) throws SQLException {
        String query = "SELECT * FROM " + getTableName();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println("ID\tName\t\tContact");
            System.out.println("------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String contact = rs.getString("contact");
                System.out.printf("%d\t%-10s\t%s\n", id, name, contact);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
            throw e;
        }
    }

    public void findById(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID\tName\t\tContact");
                    System.out.println("------------------------------------");
                    do {
                        int userId = rs.getInt("id");
                        String name = rs.getString("name");
                        String contact = rs.getString("contact");
                        System.out.printf("%d\t%-10s\t%s\n", userId, name, contact);
                    } while (rs.next());
                } else {
                    System.out.println("No record found with ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving record: " + e.getMessage());
            throw e;
        }
    }

    protected void createTableIfNotExists(Connection connection) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + getTableName() + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT NOT NULL)";

        try (PreparedStatement stmt = connection.prepareStatement(createTableQuery)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating table for " + getTableName());
            e.printStackTrace();
        }
    }

}
