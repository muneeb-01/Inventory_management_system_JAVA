package org.example.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private Connection connection;

    public ConnectDB() {
        String url = "jdbc:sqlite:mydb.db";
        try{
            connection = DriverManager.getConnection(url);
            System.out.println("Connected to database");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public Connection getConnection() {
        return connection;
    }
    public void closeConnection() {
        try{
            if (connection != null) {
                connection.close();
                System.out.println("disconnected from database");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();

        }
    }
}
