package com.game.network;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import com.game.database.DatabaseManager;
import com.game.network.messages.*;
import java.util.concurrent.ConcurrentHashMap;
import com.game.network.messages.MovementMessage;
import com.game.network.messages.GameUpdateMessage;

import java.util.HashMap;
import java.util.Map;

public class GameServer extends AbstractServer {
    // Track connections by username so we can message them directly
    private Map<String, ConnectionToClient> clientConnections = new HashMap<>();
    private Map<String, int[]> playerPositions = new ConcurrentHashMap<>();

    // A single “waiting user” for a match (null if no one is waiting)
    private String waitingUser = null;

    public GameServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            // Keep track of the username -> client connection
            if (msg instanceof LoginMessage) {
                LoginMessage login = (LoginMessage) msg;
                User user = DatabaseManager.authenticateUser(login.getUsername(), login.getPassword());
                boolean valid = (user != null);
                client.sendToClient(new LoginResponse(valid, user));
                if (valid) {
                    clientConnections.put(login.getUsername(), client);
                }
            }
            else if (msg instanceof CreateAccountMessage) {
                CreateAccountMessage cam = (CreateAccountMessage) msg;
                boolean success = DatabaseManager.createUser(cam.getUsername(), cam.getPassword());
                String message = success ? "Account created successfully." : "Failed to create account. Username may already exist.";
                client.sendToClient(new CreateAccountResponse(success, message));
            }
            else if (msg instanceof CreateMatchMessage) {
                CreateMatchMessage cmm = (CreateMatchMessage) msg;
                String username = cmm.getUsername();

                // If no one is waiting, set this user as the waiting user
                if (waitingUser == null) {
                    waitingUser = username;
                    client.sendToClient(new CreateMatchResponse(true, "Waiting for other player..."));
                } else {
                    // If someone is already waiting, either reject or handle differently
                    client.sendToClient(new CreateMatchResponse(false, "Another user is already waiting. Please join instead."));
                }
            }
            else if (msg instanceof JoinMatchMessage) {
                JoinMatchMessage jmm = (JoinMatchMessage) msg;
                String username = jmm.getUsername();

                // If someone is waiting, pair them
                if (waitingUser != null && !waitingUser.equals(username)) {
                    String player1 = waitingUser;
                    String player2 = username;

                    // Clear waitingUser since we've paired them
                    waitingUser = null;

                    // Send MatchStartedMessage to both
                    ConnectionToClient client1 = clientConnections.get(player1);
                    ConnectionToClient client2 = clientConnections.get(player2);
                    if (client1 != null) {
                        client1.sendToClient(new MatchStartedMessage(player1, player2));
                    }
                    if (client2 != null) {
                        client2.sendToClient(new MatchStartedMessage(player1, player2));
                    }
                } else {
                    // No one is waiting, or you’re trying to join your own match
                    client.sendToClient(new CreateMatchResponse(false, "No available match to join."));
                }
            }
            if (msg instanceof MovementMessage) {
                MovementMessage mm = (MovementMessage) msg;
                String user = mm.getUsername();
                int[] pos = playerPositions.get(user);
                if (pos == null) {
                    // If not present yet, give them a default
                    pos = new int[]{100, 200};
                }
                // Apply movement
                pos[0] += mm.getDeltaX();
                pos[1] += mm.getDeltaY();
                playerPositions.put(user, pos);

                // Broadcast updated positions to ALL clients
                GameUpdateMessage gum = new GameUpdateMessage(playerPositions);
                sendToAllClients(gum);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


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