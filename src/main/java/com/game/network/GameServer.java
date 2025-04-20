package com.game.network;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import com.game.database.DatabaseManager;
import com.game.network.messages.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;

public class GameServer extends AbstractServer {
    // Track connections by username
    private Map<String, ConnectionToClient> clientConnections = new HashMap<>();
    private Map<String, int[]> playerPositions = new ConcurrentHashMap<>();
    private static final int MIN_X = 50;
    private static final int MAX_X = 750;
    private static final int MIN_Y = 50;
    private static final int MAX_Y = 550;

    // Player health variables
    private int p1Health = 100; // Player 1 health
    private int p2Health = 100; // Player 2 health

    // A single “waiting user” for a match (null if no one is waiting)
    private String waitingUser = null;

    public GameServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            // Handle different types of messages
            if (msg instanceof LoginMessage) {
                LoginMessage login = (LoginMessage) msg;
                User user = DatabaseManager.authenticateUser(login.getUsername(), login.getPassword());
                boolean valid = (user != null);
                client.sendToClient(new LoginResponse(valid, user));
                if (valid) {
                    clientConnections.put(login.getUsername(), client);
                }
            } else if (msg instanceof CreateAccountMessage) {
                CreateAccountMessage cam = (CreateAccountMessage) msg;
                boolean success = DatabaseManager.createUser(cam.getUsername(), cam.getPassword());
                String message = success ? "Account created successfully." : "Failed to create account. Username may already exist.";
                client.sendToClient(new CreateAccountResponse(success, message));
            } else if (msg instanceof CreateMatchMessage) {
                CreateMatchMessage cmm = (CreateMatchMessage) msg;
                String username = cmm.getUsername();

                // Handle match creation
                if (waitingUser == null) {
                    waitingUser = username;
                    client.sendToClient(new CreateMatchResponse(true, "Waiting for other player..."));
                } else {
                    client.sendToClient(new CreateMatchResponse(false, "Another user is already waiting. Please join instead."));
                }
            } else if (msg instanceof JoinMatchMessage) {
                JoinMatchMessage jmm = (JoinMatchMessage) msg;
                String username = jmm.getUsername();

                // Handle match joining
                if (waitingUser != null && !waitingUser.equals(username)) {
                    String player1 = waitingUser;
                    String player2 = username;

                    // Clear waitingUser since we've paired them
                    waitingUser = null;

                    // Notify both players that the match has started
                    ConnectionToClient client1 = clientConnections.get(player1);
                    ConnectionToClient client2 = clientConnections.get(player2);
                    if (client1 != null) {
                        client1.sendToClient(new MatchStartedMessage(player1, player2));
                    }
                    if (client2 != null) {
                        client2.sendToClient(new MatchStartedMessage(player1, player2));
                    }
                } else {
                    client.sendToClient(new CreateMatchResponse(false, "No available match to join."));
                }
            } else if (msg instanceof MovementMessage) { // Fixed 'else' alignment
                MovementMessage mm = (MovementMessage) msg;
                String user = mm.getUsername();
                int[] pos = playerPositions.get(user);
                if (pos == null) {
                    pos = new int[]{100, 200}; // Default position
                }

                // Apply movement and enforce boundaries
                pos[0] = Math.max(MIN_X, Math.min(MAX_X, pos[0] + mm.getDeltaX())); // Horizontal boundaries
                pos[1] = Math.max(MIN_Y, Math.min(MAX_Y, pos[1] + mm.getDeltaY())); // Vertical boundaries

                playerPositions.put(user, pos);

                // Broadcast updated positions to ALL clients
                GameUpdateMessage gum = new GameUpdateMessage(playerPositions, p1Health, p2Health);
                sendToAllClients(gum);
            }
            else if (msg instanceof AttackMessage) {
                AttackMessage attackMsg = (AttackMessage) msg;
                String attacker = attackMsg.getAttacker();
                String opponent = attacker.equals("player1") ? "player2" : "player1";
                
                System.out.println("Attack received from " + attacker + " targeting " + opponent);

                int[] attackerPos = playerPositions.get(attacker);
                int[] opponentPos = playerPositions.get(opponent);

                if (attackerPos != null && opponentPos != null) {
                    double distance = Math.sqrt(Math.pow(attackerPos[0] - opponentPos[0], 2) + Math.pow(attackerPos[1] - opponentPos[1], 2));
                    if (distance <= 50) { // Check if within attack range
                        if (opponent.equals("player1")) {
                            p1Health = Math.max(0, p1Health - 5); // Deduct health for Player 1
                            System.out.println("player1 was attacked");
                            
                        } else if (opponent.equals("player2")) {
                            p2Health = Math.max(0, p2Health - 5); // Deduct health for Player 2
                            System.out.println("player2 was attacked");
                        }

                        // Broadcast updated health and positions
                        GameUpdateMessage gum = new GameUpdateMessage(playerPositions, p1Health, p2Health);
                        sendToAllClients(gum);
                    }
                }
            }


        } catch (Exception e) { // Added matching 'catch' block
            e.printStackTrace(); // Log any exceptions that occur
        }
    }

    // Handle health updates
    public void handleHealthUpdate(String player, int newHealth) {
        if (player.equals("player1")) { // Replace with actual player identifiers
            p1Health = newHealth;
            System.out.println("Health Update player 1");
        } else if (player.equals("player2")) {
            p2Health = newHealth;
            System.out.println("Health Update player 2");
            
        }

        // Create and send the updated message
        GameUpdateMessage gum = new GameUpdateMessage(playerPositions, p1Health, p2Health);
        sendToAllClients(gum);
    }

    @Override
    protected void serverStarted() {
        System.out.println("Server started on port " + getPort());
    }

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        GameServer server = new GameServer(8300);
        try {
            server.listen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
