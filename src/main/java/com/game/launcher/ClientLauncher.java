package com.game.launcher;

import com.game.network.GameClient;
import com.game.ui.MainFrame;

import javax.swing.SwingUtilities;

public class ClientLauncher {
    public static void main(String[] args) {
        // This one just starts a client, assuming a server is already running.
        GameClient client = null;
        try {
            client = new GameClient("localhost", 8300);
            client.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the server. Make sure it's running!");
            return;
        }

        // Launch the GUI
        GameClient finalClient = client;
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(finalClient);
            mainFrame.setVisible(true);
        });
    }
}
