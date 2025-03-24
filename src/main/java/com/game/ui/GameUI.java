package com.game.ui;

import javax.swing.SwingUtilities;
import com.game.network.GameClient;

public class GameUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClient client = new GameClient("localhost", 8300);
            try {
                client.openConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainFrame mainFrame = new MainFrame(client);
            mainFrame.setVisible(true);
        });
    }
}
