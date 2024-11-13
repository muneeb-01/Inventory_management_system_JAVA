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
}
