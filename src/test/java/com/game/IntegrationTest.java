/*
 * IntegrationTest.java - Written by Mason Simpson
 * Tests that creating a user, logging in, and sending a match request all work together correctly
 * Uses Mockito to mock a GameClient without actually starting a server
 */

package com.game;

import com.game.database.DatabaseManager;
import com.game.network.GameClient;
import com.game.network.messages.User;
import com.game.network.messages.CreateMatchMessage;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.sql.*;

public class IntegrationTest {
	
	@BeforeClass
    public static void setup() {
        DatabaseManager.initializeDatabase();
    }
	
	// Clears test user so that you don't have to change the test username every time you run the test
	@Before
    public void clearTestUser() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM GameUsers WHERE username = ?");
        ps.setString(1, "testuser");
        ps.executeUpdate();
    }

    @Test
    public void testUserCreationAndMatchRequest() throws Exception {   	
        // Create user
        boolean created = DatabaseManager.createUser("testuser", "testpass");
        assertTrue(created);

        // Authenticate
        User user = DatabaseManager.authenticateUser("testuser", "testpass");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());

        // Simulate sending match request using mock GameClient
        GameClient mockClient = mock(GameClient.class);
        CreateMatchMessage matchRequest = new CreateMatchMessage(user.getUsername());

        // Simulate sending without throwing
        doNothing().when(mockClient).sendToServer(any());

        // Send the message
        mockClient.sendToServer(matchRequest);

        // Verify that the message was sent
        verify(mockClient).sendToServer(matchRequest);
    }
}
