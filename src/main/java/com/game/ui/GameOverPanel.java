package com.game.ui;
import com.game.network.GameClient;
import com.game.network.messages.User;
import com.game.database.*;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {
    public GameOverPanel(String winner, String loser, GameClient client, MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel message = new JLabel("Game Over! " + winner + " wins!", SwingConstants.CENTER);
        message.setFont(new Font("Arial", Font.BOLD, 24));

        JButton backButton = new JButton("Return to Lobby");
        backButton.addActionListener(e -> {
            User currentUser = client.getLoggedInUser();
            if (currentUser != null) {
            	if (DatabaseManager.getConnection() == null) {
            	    DatabaseManager.initializeDatabase();
            	}
                User refreshedUser = DatabaseManager.getUserStats(currentUser.getUsername());
                if (refreshedUser != null) {
                    client.setLoggedInUser(refreshedUser); 
                    frame.showLobby(refreshedUser);
                } else {
                    System.out.println("Failed to refresh user stats.");
                }
            } else {
                System.out.println("No user logged in.");
            }
        });


        add(message, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}