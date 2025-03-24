package com.game.ui;

import com.game.network.GameClient;
import com.game.network.messages.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private GameClient client;

    public MainFrame(GameClient client) {
        super("Street Fighter Stick Figures");
        this.client = client;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this, client);
        GameClient.setLoginResponseListener(loginPanel);
        GameClient.setCreateAccountResponseListener(loginPanel);

        cardPanel.add(loginPanel, "login");

        getContentPane().add(cardPanel);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    public void showLobby(User user) {
        LobbyPanel lobbyPanel = new LobbyPanel(user, client, this);
        GameClient.setLobbyResponseListener(lobbyPanel);

        cardPanel.add(lobbyPanel, "lobby");
        cardLayout.show(cardPanel, "lobby");
        setSize(600, 400);
    }

    public void showGamePanel(String player1, String player2) {
        // Assuming 'player1' is the local user controlling this client:
        // Pass the client and all usernames to GamePanel’s constructor that includes networking logic.
        GamePanel gamePanel = new GamePanel(client, player1, player1, player2);
        cardPanel.add(gamePanel, "game");
        cardLayout.show(cardPanel, "game");
        setSize(800, 600);
        SwingUtilities.invokeLater(() -> {
            gamePanel.requestFocusInWindow();
        });
    }
}