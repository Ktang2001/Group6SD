package com.game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.game.network.GameClient;
import com.game.network.messages.LoginMessage;
import com.game.network.messages.LoginResponse;
import com.game.network.messages.CreateAccountMessage;
import com.game.network.messages.CreateAccountResponse;

public class LoginPanel extends JPanel implements GameClient.LoginResponseListener, GameClient.CreateAccountResponseListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JLabel statusLabel;
    private GameClient client;

    private MainFrame mainFrame; // Reference to the main frame for panel switching.

    public LoginPanel(MainFrame mainFrame, GameClient client) {
        this.mainFrame = mainFrame;
        this.client = client;
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10,10,10,10);
        add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy++;
        add(new JLabel("Username: "), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password: "), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        add(loginButton, gbc);

        // Create Account button added below the login button
        gbc.gridy++;
        createAccountButton = new JButton("Create Account");
        add(createAccountButton, gbc);

        gbc.gridy++;
        statusLabel = new JLabel(" ");
        add(statusLabel, gbc);

        // Login button action listener.
        loginButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Create Account button action listener.
        createAccountButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                performCreateAccount();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if(username.isEmpty() || password.isEmpty()){
            statusLabel.setText("Please enter both username and password.");
            return;
        }
        loginButton.setEnabled(false);
        createAccountButton.setEnabled(false);
        statusLabel.setText("Logging in...");
        // Send the login message via the GameClient.
        try {
            client.sendToServer(new LoginMessage(username, password));
        } catch(Exception e) {
            statusLabel.setText("Error sending login message.");
            e.printStackTrace();
            loginButton.setEnabled(true);
            createAccountButton.setEnabled(true);
        }
    }

    private void performCreateAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if(username.isEmpty() || password.isEmpty()){
            statusLabel.setText("Please enter both username and password to create an account.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Create account with username: " + username + "?", "Confirm Account Creation", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            loginButton.setEnabled(false);
            createAccountButton.setEnabled(false);
            statusLabel.setText("Creating account...");
            try {
                client.sendToServer(new CreateAccountMessage(username, password));
            } catch(Exception e) {
                statusLabel.setText("Error sending create account message.");
                e.printStackTrace();
                loginButton.setEnabled(true);
                createAccountButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onLoginResponse(LoginResponse response) {
        SwingUtilities.invokeLater(() -> {
            if(response.isSuccess()) {
                statusLabel.setText("Login successful!");
                mainFrame.showLobby(response.getUser());
            } else {
                statusLabel.setText("Login failed. Check credentials.");
                loginButton.setEnabled(true);
                createAccountButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onCreateAccountResponse(CreateAccountResponse response) {
        SwingUtilities.invokeLater(() -> {
            if(response.isSuccess()) {
                statusLabel.setText("Account created successfully! You can now log in.");
            } else {
                statusLabel.setText("Account creation failed: " + response.getMessage());
            }
            loginButton.setEnabled(true);
            createAccountButton.setEnabled(true);
        });
    }
}