/*
 * DatabaseManager.java - Written by Mason Simpson
 * Similar to lab8out - simple database using MySQL that stores and authenticates usernames and passwords.
 */

package com.game.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import com.game.network.messages.User;
import java.util.*;

public class DatabaseManager {
	private static Connection conn;
    private static final String propertiesFilePath = "db.properties";  

    /**
     * Initializes database with db.properties file
     */
    public static void initializeDatabase() {
    	Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
            prop.load(fis);
            String url = prop.getProperty("url");
            String user = prop.getProperty("user");
            String pass = prop.getProperty("password");
            conn = DriverManager.getConnection(url, user, pass);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
  
    }
    public static Connection getConnection() {
    	return conn;
    }

    /**
     * Authenticates the user with case-sensitive matching on username and password.
     */
    public static User authenticateUser(String username, String password) throws SQLException {
        // Use BINARY to ensure case sensitivity in the query
        String query = "SELECT * FROM GameUsers WHERE BINARY username = ? AND BINARY password = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int wins = rs.getInt("wins");
            int losses = rs.getInt("losses");
            return new User(username, wins, losses);
        }
        
        return null;
    }

    public static boolean createUser(String username, String password) throws SQLException {
        String insert = "INSERT INTO GameUsers (username, password) VALUES (?, ?)";
        PreparedStatement ps = conn.prepareStatement(insert);
        ps.setString(1, username);
        ps.setString(2, password);
        return ps.executeUpdate() > 0;
    }
}
