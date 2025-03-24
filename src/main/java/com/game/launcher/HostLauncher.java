package com.game.launcher;

import com.game.network.GameServer;
import com.game.network.GameClient;
import com.game.ui.MainFrame;

import javax.swing.SwingUtilities;
import java.net.Socket;

public class HostLauncher {
    public static void main(String[] args) {
        // 1) Check if server is running
        boolean serverRunning = false;
        try (Socket socket = new Socket("localhost", 8300)) {
            serverRunning = true;
        } catch (Exception e) {
            serverRunning = false;
        }

        // 2) If not, start the server in a new thread
        if (!serverRunning) {
            new Thread(() -> {
                GameServer.main(null);
            }).start();
            // Give the server a moment to initialize
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        // 3) Start the client
        GameClient client = null;
        try {
            client = new GameClient("localhost", 8300);
            client.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4) Launch the GUI on the Swing thread
        GameClient finalClient = client;
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(finalClient);
            mainFrame.setVisible(true);
        });
    }
}