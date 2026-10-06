import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tictactoe";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Please add the connector JAR.", e);
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // Create settings table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS settings (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "setting_key VARCHAR(100) NOT NULL UNIQUE," +
                "setting_value VARCHAR(255) NOT NULL" +
                ")"
            );

            // Create game history table
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS game_history (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "player1_name VARCHAR(100)," +
                "player2_name VARCHAR(100)," +
                "winner_name VARCHAR(100)," +
                "result VARCHAR(20)," +
                "board_size INT," +
                "game_mode VARCHAR(30)," +
                "duration_seconds INT," +
                "played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // Insert default settings if not present
            insertDefaultIfMissing(conn, "gamemode", "Singleplayer");
            insertDefaultIfMissing(conn, "board_size", "3");
            insertDefaultIfMissing(conn, "show_timer", "true");
            insertDefaultIfMissing(conn, "show_board_info", "true");
            insertDefaultIfMissing(conn, "show_player_counter", "true");
            insertDefaultIfMissing(conn, "difficulty", "Medium");
            insertDefaultIfMissing(conn, "player1_symbol", "X");
            insertDefaultIfMissing(conn, "player2_symbol", "O");
            insertDefaultIfMissing(conn, "player1_name", "Player 1");
            insertDefaultIfMissing(conn, "player2_name", "Player 2");

            stmt.close();
            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }

    private static void insertDefaultIfMissing(Connection conn, String key, String value) throws SQLException {
        PreparedStatement check = conn.prepareStatement(
            "SELECT COUNT(*) FROM settings WHERE setting_key = ?"
        );
        check.setString(1, key);
        ResultSet rs = check.executeQuery();
        rs.next();
        if (rs.getInt(1) == 0) {
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)"
            );
            insert.setString(1, key);
            insert.setString(2, value);
            insert.executeUpdate();
            insert.close();
        }
        check.close();
    }

    public static String getSetting(String key) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT setting_value FROM settings WHERE setting_key = ?"
            );
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("setting_value");
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting setting [" + key + "]: " + e.getMessage());
        }
        return null;
    }

    public static void saveSetting(String key, String value) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE setting_value = ?"
            );
            stmt.setString(1, key);
            stmt.setString(2, value);
            stmt.setString(3, value);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error saving setting [" + key + "]: " + e.getMessage());
        }
    }

    public static void saveGameHistory(String player1, String player2, String winner,
                                       String result, int boardSize, String gameMode, int duration) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO game_history (player1_name, player2_name, winner_name, result, board_size, game_mode, duration_seconds) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, player1);
            stmt.setString(2, player2);
            stmt.setString(3, winner);
            stmt.setString(4, result);
            stmt.setInt(5, boardSize);
            stmt.setString(6, gameMode);
            stmt.setInt(7, duration);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error saving game history: " + e.getMessage());
        }
    }

    public static void resetAllSettings() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM settings");
            stmt.close();
            initializeDatabase();
        } catch (SQLException e) {
            System.err.println("Error resetting settings: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
