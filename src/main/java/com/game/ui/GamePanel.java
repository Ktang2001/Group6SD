package com.game.ui;

import com.game.network.GameClient;
import com.game.network.messages.MovementMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Map;

/**
 * A GamePanel that handles local key input, sends MovementMessage to the server,
 * and draws two stick-figure players (Player 1 in red, Player 2 in blue).
 *
 * It also includes updatePositions(...) so the client can receive GameUpdateMessage
 * from the server and update local coordinates.
 */
public class GamePanel extends JPanel implements KeyListener {

    private final GameClient client;
    private final String thisUsername;
    private final String player1;
    private final String player2;

    // Positions for each stick figure
    private int p1X = 100, p1Y = 200;
    private int p2X = 400, p2Y = 200;

    private static final int MOVE_SPEED = 5;

    public GamePanel(GameClient client, String thisUsername, String player1, String player2) {
        this.client = client;
        this.thisUsername = thisUsername;
        this.player1 = player1;
        this.player2 = player2;

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        // Let the client know this panel is where we draw updates
        client.setGamePanel(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawString("Player 1: " + player1, 20, 20);
        g.drawString("Player 2: " + player2, 20, 40);

        // Player 1 (RED)
        g.setColor(Color.RED);
        g.fillOval(p1X - 10, p1Y - 10, 20, 20);
        g.drawLine(p1X, p1Y, p1X, p1Y + 50);
        g.drawLine(p1X, p1Y + 10, p1X - 10, p1Y + 30);
        g.drawLine(p1X, p1Y + 10, p1X + 10, p1Y + 30);
        g.drawLine(p1X, p1Y + 50, p1X - 10, p1Y + 70);
        g.drawLine(p1X, p1Y + 50, p1X + 10, p1Y + 70);

        // Player 2 (BLUE)
        g.setColor(Color.BLUE);
        g.fillOval(p2X - 10, p2Y - 10, 20, 20);
        g.drawLine(p2X, p2Y, p2X, p2Y + 50);
        g.drawLine(p2X, p2Y + 10, p2X - 10, p2Y + 30);
        g.drawLine(p2X, p2Y + 10, p2X + 10, p2Y + 30);
        g.drawLine(p2X, p2Y + 50, p2X - 10, p2Y + 70);
        g.drawLine(p2X, p2Y + 50, p2X + 10, p2Y + 70);
    }

    /**
     * Update local positions when the server sends a GameUpdateMessage.
     */
    public void updatePositions(Map<String, int[]> playerPositions) {
        int[] p1Pos = playerPositions.get(player1);
        if (p1Pos != null) {
            p1X = p1Pos[0];
            p1Y = p1Pos[1];
        }

        int[] p2Pos = playerPositions.get(player2);
        if (p2Pos != null) {
            p2X = p2Pos[0];
            p2Y = p2Pos[1];
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int dx = 0, dy = 0;

        if (thisUsername.equals(player1)) {
            if (e.getKeyCode() == KeyEvent.VK_A) {
                dx = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_D) {
                dx = MOVE_SPEED;
            }
            // Add W/S if you want vertical movement for Player 1
        }

        if (thisUsername.equals(player2)) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                dx = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                dx = MOVE_SPEED;
            }
            // Add UP/DOWN if you want vertical movement for Player 2
        }

        if (dx != 0 || dy != 0) {
            try {
                client.sendToServer(new MovementMessage(thisUsername, dx, dy));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}