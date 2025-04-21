package com.game.network;

import com.game.network.messages.*;
import com.game.ui.MainFrame;
import ocsf.client.AbstractClient;
import com.game.ui.GamePanel;

import javax.swing.*;



public class GameClient extends AbstractClient {

    private MainFrame mainFrame;
    private User user;

    public void setLoggedInUser(User user) {
        this.user = user;
    }

    public User getLoggedInUser() {
        return user;
    }

    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    public interface LoginResponseListener {
        void onLoginResponse(LoginResponse response);
    }

    private static LoginResponseListener loginResponseListener;

    public static void setLoginResponseListener(LoginResponseListener listener) {
        loginResponseListener = listener;
    }

    public interface CreateAccountResponseListener {
        void onCreateAccountResponse(CreateAccountResponse response);
    }

    private static CreateAccountResponseListener createAccountResponseListener;

    public static void setCreateAccountResponseListener(CreateAccountResponseListener listener) {
        createAccountResponseListener = listener;
    }

    public interface LobbyResponseListener {
        void onCreateMatchResponse(CreateMatchResponse response);

        void onMatchStarted(MatchStartedMessage msg);
    }

    private static LobbyResponseListener lobbyResponseListener;

    public static void setLobbyResponseListener(LobbyResponseListener listener) {
        lobbyResponseListener = listener;
    }

    // For handling GameUpdateMessage
    private GamePanel gamePanel;

    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }

    public GameClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof LoginResponse) {
            LoginResponse response = (LoginResponse) msg;
            System.out.println("Received login response from server");
            if (loginResponseListener != null) {
                loginResponseListener.onLoginResponse(response);
            }
            this.user = response.getUser();
        } else if (msg instanceof CreateAccountResponse) {
            CreateAccountResponse response = (CreateAccountResponse) msg;
            System.out.println("Received create account response from server");
            if (createAccountResponseListener != null) {
                createAccountResponseListener.onCreateAccountResponse(response);
            }
        } else if (msg instanceof CreateMatchResponse) {
            CreateMatchResponse response = (CreateMatchResponse) msg;
            System.out.println("Received create match response from server: " + response.getMessage());
            if (lobbyResponseListener != null) {
                lobbyResponseListener.onCreateMatchResponse(response);
            }
        } else if (msg instanceof MatchStartedMessage) {
            MatchStartedMessage matchMsg = (MatchStartedMessage) msg;
            System.out.println("Received match started message from server: "
                    + matchMsg.getPlayer1() + " vs. " + matchMsg.getPlayer2());
            if (lobbyResponseListener != null) {
                lobbyResponseListener.onMatchStarted(matchMsg);
            }
        } else if (msg instanceof GameUpdateMessage) {
            GameUpdateMessage updateMsg = (GameUpdateMessage) msg;
            System.out.println("Received game update message from server");
            if (gamePanel != null) {
                gamePanel.updatePositions(updateMsg.getPlayerPositions());
                gamePanel.updateHealth(updateMsg.getP1Health(), updateMsg.getP2Health());
            }
        } else if (msg instanceof GameOverMessage) {
            GameOverMessage gom = (GameOverMessage) msg;
            System.out.println("Game Over! Winner: " + gom.getWinner());

            if (mainFrame != null) {
                SwingUtilities.invokeLater(() -> {
                    mainFrame.showGameOver(gom.getWinner(), gom.getLoser());
                });
            }
        }
    }
}