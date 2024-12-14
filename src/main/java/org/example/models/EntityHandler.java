package org.example.models;

import java.sql.Connection;
import java.util.Scanner;

public interface EntityHandler {
    void showMenu();
    void handleChoice(int choice, Connection connection, Scanner scanner);
}
