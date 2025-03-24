package com.game.database;

import java.sql.*;
import com.game.network.messages.User;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "game_db";
    private static final String USER = "yourusername";  // <-- Replace
    private static final String PASS = "yourpassword";  // <-- Replace

    public static void initializeDatabase() {
        // Create the database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the Users table if it doesn't exist
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "wins INT DEFAULT 0, " +
                    "losses INT DEFAULT 0" +
                    ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticates the user with case-sensitive matching on username and password.
     */
    public static User authenticateUser(String username, String password) {
        // Use BINARY to ensure case sensitivity in the query
        String query = "SELECT * FROM Users WHERE BINARY username = ? AND BINARY password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int wins = rs.getInt("wins");
                int losses = rs.getInt("losses");
                return new User(username, wins, losses);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean createUser(String username, String password) {
        String insert = "INSERT INTO Users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, username);
            ps.setString(2, password);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}