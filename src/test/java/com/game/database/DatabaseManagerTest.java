import static org.junit.Assert.*;
import org.junit.*;

import com.game.database.DatabaseManager;
import com.game.network.messages.User;

import java.sql.*;

public class DatabaseManagerTest {

    @BeforeClass
    public static void setup() {
        DatabaseManager.initializeDatabase();
    }

    @Before
    public void clearTestUser() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM GameUsers WHERE username = ?");
        ps.setString(1, "testuser");
        ps.executeUpdate();
    }

    @Test
    public void testCreateUser() throws SQLException {
        boolean result = DatabaseManager.createUser("testuser", "Test123");
        assertTrue(result);
    }

    @Test
    public void testAuthenticateUser_Success() throws SQLException {
        DatabaseManager.createUser("testuser", "Test123");
        User user = DatabaseManager.authenticateUser("testuser", "Test123");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    public void testAuthenticateUser_Failure() throws SQLException {
        User user = DatabaseManager.authenticateUser("nonexistent", "wrongpass");
        assertNull(user);
    }
}
