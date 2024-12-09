package org.example.models;

import java.sql.*;
import java.util.Scanner;
public class User {
    private int Id;
    private String name;
    private String contactInfo;

    public void AddUser(Connection connection, Scanner scanner) throws SQLException {}
    public void DeleteUser(Connection connection, Scanner scanner) throws SQLException {}
    public void findAll(Connection connection, String type) throws SQLException {
        String selectAllQuery = "SELECT * FROM " + type;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAllQuery)) {
            System.out.println("ID\tName\tContact");
            System.out.println("------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String contact = rs.getString("contact");

                System.out.println(id + "\t" + name + "\t" + contact);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
            throw e;
        }
    }
    public void findById(Connection connection, Scanner scanner, String type) throws SQLException {
        System.out.print("Enter ID : ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String selectQuery = "SELECT * FROM " + type + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ID | Name         | Contact ");
                    System.out.println("---------------------------------------------------------------------------------");

                    do {
                        id = rs.getInt("id");
                        String name = rs.getString("name");
                        int contact = rs.getInt("contact");

                        // Print the details of the current row
                        System.out.printf("%7d | %-10s | %8d",
                                id, name, contact);
                    } while (rs.next());
                } else {
                    System.out.println("No " + type + " found with ID = " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding " + type + ": " + e.getMessage());
            throw e;
        }
    }

}
