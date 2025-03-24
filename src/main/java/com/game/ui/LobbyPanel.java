package com.game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.game.network.GameClient;
import com.game.network.messages.*;
import com.game.network.messages.User; // If your User class is here

public class LobbyPanel extends JPanel implements GameClient.LobbyResponseListener {
    private JLabel welcomeLabel;
    private JLabel statsLabel;
    private JButton createMatchButton;
    private JButton joinMatchButton;
    private JLabel statusLabel;

    private User user;           // The logged-in user info (username, wins, losses)
    private GameClient client;   // Reference to the client
    private MainFrame mainFrame; // Reference to the main frame so we can switch panels

    public LobbyPanel(User user, GameClient client, MainFrame mainFrame) {
        this.user = user;
        this.client = client;
        this.mainFrame = mainFrame;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        // Show the user's wins/losses
        statsLabel = new JLabel("Wins: " + user.getWins() + "   Losses: " + user.getLosses());
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(statsLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        createMatchButton = new JButton("Create Match");
        joinMatchButton = new JButton("Join Match");
        buttonPanel.add(createMatchButton);
        buttonPanel.add(joinMatchButton);

        // We'll add a statusLabel below the buttons to show waiting/error messages
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Container for the buttons and status label
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Action listeners for the buttons
        createMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCreateMatch();
            }
        });

        joinMatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleJoinMatch();
            }
        });
    }

    private void handleCreateMatch() {
        // Send a CreateMatchMessage to the server
        try {
            client.sendToServer(new CreateMatchMessage(user.getUsername()));
            statusLabel.setText("Requesting to create match...");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error sending create match request.");
        }
    }

    private void handleJoinMatch() {
        // Send a JoinMatchMessage to the server
        try {
            client.sendToServer(new JoinMatchMessage(user.getUsername()));
            statusLabel.setText("Requesting to join match...");
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Error sending join match request.");
        }
    }

    // --------------------
    // LOBBY RESPONSE LISTENER IMPLEMENTATION
    // --------------------
    @Override
    public void onCreateMatchResponse(CreateMatchResponse response) {
        // Called when the server responds to CreateMatchMessage
        SwingUtilities.invokeLater(() -> {
            if (response.isSuccess()) {
                // The server set us as waitingUser
                statusLabel.setText("Waiting for other player...");
            } else {
                statusLabel.setText("Create match failed: " + response.getMessage());
            }
        });
    }

    @Override
    public void onMatchStarted(MatchStartedMessage msg) {
        // Called when the server pairs two players
        SwingUtilities.invokeLater(() -> {
            // If this user is one of the players, we transition to the game panel
            if (msg.getPlayer1().equals(user.getUsername()) || msg.getPlayer2().equals(user.getUsername())) {
                mainFrame.showGamePanel(msg.getPlayer1(), msg.getPlayer2());
            } else {
                // If we aren't in this match, ignore or show a message
                statusLabel.setText("A match started between other players.");
            }
        });
    }
}