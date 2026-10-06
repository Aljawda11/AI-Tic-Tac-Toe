import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NameInputDialog extends JDialog {

    private static final Color BG_COLOR     = new Color(40, 40, 40);
    private static final Color TEXT_COLOR   = new Color(220, 220, 220);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color FIELD_BG     = new Color(55, 55, 55);
    private static final Color FIELD_FG     = new Color(220, 220, 220);

    private JTextField player1Field;
    private JTextField player2Field;
    private boolean confirmed = false;
    private GameSettings settings;

    public NameInputDialog(JFrame parent, GameSettings settings) {
        super(parent, "Enter Player Names", true);
        this.settings = settings;

        setSize(340, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new GridBagLayout());

        buildUI();
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        JLabel title = new JLabel("Enter Player Names");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        add(title, gbc);

        // Player 1
        JLabel p1Label = new JLabel("Player 1 Name:");
        p1Label.setFont(new Font("Arial", Font.PLAIN, 13));
        p1Label.setForeground(TEXT_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 20, 2, 20);
        add(p1Label, gbc);

        player1Field = createStyledField(settings.getPlayer1Name());
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 20, 5, 20);
        add(player1Field, gbc);

        // Player 2 (only shown in multiplayer, else show bot label)
        boolean isMultiplayer = settings.getGameMode().equals("Multiplayer");
        String p2LabelText = isMultiplayer ? "Player 2 Name:" : "Bot Name:";
        String p2Default   = isMultiplayer ? settings.getPlayer2Name() : "Bot";

        JLabel p2Label = new JLabel(p2LabelText);
        p2Label.setFont(new Font("Arial", Font.PLAIN, 13));
        p2Label.setForeground(TEXT_COLOR);
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 20, 2, 20);
        add(p2Label, gbc);

        player2Field = createStyledField(p2Default);
        if (!isMultiplayer) {
            player2Field.setEditable(false);
            player2Field.setBackground(new Color(45, 45, 45));
        }
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 20, 15, 20);
        add(player2Field, gbc);

        // Confirm button
        JButton confirmBtn = new JButton("Start Game");
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 14));
        confirmBtn.setBackground(ACCENT_COLOR);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setOpaque(true);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> confirmNames());

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 20, 15, 20);
        add(confirmBtn, gbc);
    }

    private JTextField createStyledField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBackground(FIELD_BG);
        field.setForeground(FIELD_FG);
        field.setCaretColor(FIELD_FG);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private void confirmNames() {
        String p1 = player1Field.getText().trim();
        String p2 = player2Field.getText().trim();

        if (p1.isEmpty()) p1 = "Player 1";
        if (p2.isEmpty()) p2 = settings.getGameMode().equals("Multiplayer") ? "Player 2" : "Bot";

        settings.setPlayer1Name(p1);
        settings.setPlayer2Name(p2);
        settings.saveAll();

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
