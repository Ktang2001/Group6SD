package com.game.network;

import ocsf.client.AbstractClient;
import com.game.network.messages.LoginResponse;
import com.game.network.messages.LoginMessage;
import com.game.network.messages.CreateAccountResponse;
import com.game.network.messages.CreateAccountMessage;
import com.game.network.messages.CreateMatchResponse;
import com.game.network.messages.MatchStartedMessage;
import com.game.network.messages.GameUpdateMessage;
import com.game.ui.GamePanel;

public class GameClient extends AbstractClient {

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
        }
        else if (msg instanceof CreateAccountResponse) {
            CreateAccountResponse response = (CreateAccountResponse) msg;
            System.out.println("Received create account response from server");
            if (createAccountResponseListener != null) {
                createAccountResponseListener.onCreateAccountResponse(response);
            }
        }
        else if (msg instanceof CreateMatchResponse) {
            CreateMatchResponse response = (CreateMatchResponse) msg;
            System.out.println("Received create match response from server: " + response.getMessage());
            if (lobbyResponseListener != null) {
                lobbyResponseListener.onCreateMatchResponse(response);
            }
        }
        else if (msg instanceof MatchStartedMessage) {
            MatchStartedMessage matchMsg = (MatchStartedMessage) msg;
            System.out.println("Received match started message from server: "
                    + matchMsg.getPlayer1() + " vs. " + matchMsg.getPlayer2());
            if (lobbyResponseListener != null) {
                lobbyResponseListener.onMatchStarted(matchMsg);
            }
        }
        else if (msg instanceof GameUpdateMessage) {
            GameUpdateMessage updateMsg = (GameUpdateMessage) msg;
            System.out.println("Received game update message from server");
            if (gamePanel != null) {
                gamePanel.updatePositions(updateMsg.getPlayerPositions());
            }
        }
    }
}