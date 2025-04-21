package com.game.ui;

import com.game.network.GameClient;
import com.game.network.messages.*;

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
	
	private static final int MIN_X = 50, MAX_X = 750;
	private static final int MIN_Y = 50, MAX_Y = 550;
	
	private int p1Health = 100; // Health for Player 1
	private int p2Health = 100; // Health for Player 2
	private static final int MAX_HEALTH = 100; // Maximum health


    private final GameClient client;
    private final String thisUsername;
    private final String player1;
    private final String player2;

    // Positions for each stick figure
    private int p1X = 100, p1Y = 200;
    private int p2X = 600, p2Y = 200;

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

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int adjustedP1X = (int) (p1X * (panelWidth / 800.0));
        int adjustedP1Y = (int) (p1Y * (panelHeight / 600.0));
        int adjustedP2X = (int) (p2X * (panelWidth / 800.0));
        int adjustedP2Y = (int) (p2Y * (panelHeight / 600.0));

     // Draw health bars
        g.setColor(Color.RED);
        int p1BarWidth = (int) (200 * (p1Health / 100.0));
        g.fillRect(100, 10, p1BarWidth, 10);

        g.setColor(Color.BLUE);
        int p2BarWidth = (int) (200 * (p2Health / 100.0));
        g.fillRect(100, 30, p2BarWidth, 10);

        // Draw players (existing code for stick figures)
        drawStickFigure(g, p1X, p1Y, Color.RED);
        drawStickFigure(g, p2X, p2Y, Color.BLUE);
    }




    /**
     * Update local positions when the server sends a GameUpdateMessage.
     */
    public void updatePositions(Map<String, int[]> playerPositions) {
        int[] p1Pos = playerPositions.get(player1);
        if (p1Pos != null) {
            p1X = Math.max(MIN_X, Math.min(MAX_X, p1Pos[0]));
            p1Y = Math.max(MIN_Y, Math.min(MAX_Y, p1Pos[1]));
        }

        int[] p2Pos = playerPositions.get(player2);
        if (p2Pos != null) {
            p2X = Math.max(MIN_X, Math.min(MAX_X, p2Pos[0]));
            p2Y = Math.max(MIN_Y, Math.min(MAX_Y, p2Pos[1]));
        }

        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
    	
        int dx = 0, dy = 0;

        // Handle Player 1 movement
        if (thisUsername.equals(player1)) {
            if (e.getKeyCode() == KeyEvent.VK_A && p1X > MIN_X) { // Move left
                dx = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_D && p1X < MAX_X) { // Move right
                dx = MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_W && p1Y > MIN_Y) { // Move up
                dy = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_S && p1Y < MAX_Y) { // Move down
                dy = MOVE_SPEED;
            }
        }

        // Handle Player 2 movement
        if (thisUsername.equals(player2)) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && p2X > MIN_X) { // Move left
                dx = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && p2X < MAX_X) { // Move right
                dx = MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_UP && p2Y > MIN_Y) { // Move up
                dy = -MOVE_SPEED;
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN && p2Y < MAX_Y) { // Move down
                dy = MOVE_SPEED;
            }
        }
        
        if (thisUsername.equals(player1)) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) { // Attack key for Player 1
                sendAttackRequest(player1, player2);
            }
        } else if (thisUsername.equals(player2)) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Attack key for Player 2
                sendAttackRequest(player2, player1);
            }
        }

        // Send movement update to the server
        if (dx != 0 || dy != 0) {
            try {
                client.sendToServer(new MovementMessage(thisUsername, dx, dy));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

	
	/**
	 * Draws a stick-figure character with the specified parameters.
	 * @param g Graphics object for rendering.
	 * @param x X-coordinate for the character's position.
	 * @param y Y-coordinate for the character's position.
	 * @param color The color of the character.
	 */
	private void drawStickFigure(Graphics g, int x, int y, Color color) {
	    g.setColor(color);

	    // Draw head (circle)
	    g.fillOval(x - 10, y - 10, 20, 20);

	    // Draw torso (vertical line)
	    g.drawLine(x, y, x, y + 50);

	    // Draw arms (angled lines)
	    g.drawLine(x, y + 10, x - 10, y + 30); // Left arm
	    g.drawLine(x, y + 10, x + 10, y + 30); // Right arm

	    // Draw legs (angled lines)
	    g.drawLine(x, y + 50, x - 10, y + 70); // Left leg
	    g.drawLine(x, y + 50, x + 10, y + 70); // Right leg
	}
	
	public void updateHealth(int player1Health, int player2Health) {
	    this.p1Health = player1Health;
	    this.p2Health = player2Health;
	    revalidate();
	    repaint();
	}
	
	
	private void sendAttackRequest(String attacker, String opponent) {
	    try {
	        client.sendToServer(new AttackMessage(attacker, opponent));
	        System.out.println("Attack Request sent");
	    } catch (IOException ex) {
	        ex.printStackTrace();
	        System.out.println("Attack request Failed");
	    }
	}



    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}