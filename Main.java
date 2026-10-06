import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize database first
        try {
            DatabaseManager.initializeDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Could not connect to MySQL database.\n" +
                "Please make sure XAMPP is running and MySQL is started.\n\n" +
                "Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("DB init failed: " + e.getMessage());
        }

        // Launch UI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set look and feel: " + e.getMessage());
            }
            new WelcomeFrame();
        });

        // Shutdown hook to close DB connection cleanly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            AudioManager.stopMusic();
            DatabaseManager.closeConnection();
        }));
    }
}
