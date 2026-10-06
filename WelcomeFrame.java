import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeFrame extends JFrame {

    private static final Color BG_COLOR     = new Color(30, 30, 30);
    private static final Color CARD_COLOR   = new Color(45, 45, 45);
    private static final Color CARD_BORDER  = new Color(80, 80, 80);
    private static final Color TEXT_COLOR   = new Color(220, 220, 220);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color BTN_HOVER    = new Color(90, 150, 200);
    private static final Color BTN_BG       = new Color(55, 55, 55);
    private static final Color BTN_HOVER2   = new Color(70, 70, 70);

    public WelcomeFrame() {
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new GridBagLayout());

        if (!AudioManager.isPlaying() && !AudioManager.isMuted()) {
            AudioManager.playMusic("music.wav");
        }

        buildUI();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // Outer padding
        gbc.gridy = 0;
        add(Box.createVerticalStrut(40), gbc);

        // Card panel - matches the UI in the brief
        JPanel card = buildCard();
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 0, 40);
        add(card, gbc);

        // Bottom padding
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(Box.createVerticalStrut(40), gbc);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        // Title
        JLabel title = new JLabel("Tic-Tac-Toe");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        // Divider line under title
        JSeparator sep = new JSeparator();
        sep.setForeground(CARD_BORDER);
        sep.setBackground(CARD_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(Box.createVerticalStrut(16));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        // Play button  - triangle icon drawn with text ">" to avoid symbol issues
        JButton playBtn = createCardButton("> Play", true);
        playBtn.addActionListener(e -> openNameInput());
        card.add(playBtn);

        card.add(Box.createVerticalStrut(10));

        // Settings button - gear-like prefix using simple text
        JButton settingsBtn = createCardButton("* Settings", false);
        settingsBtn.addActionListener(e -> openSettings());
        card.add(settingsBtn);

        return card;
    }

    private JButton createCardButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setBackground(isPrimary ? ACCENT_COLOR : BTN_BG);
        btn.setForeground(TEXT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));

        Color normal = isPrimary ? ACCENT_COLOR : BTN_BG;
        Color hover  = isPrimary ? BTN_HOVER    : BTN_HOVER2;

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(normal); }
        });

        return btn;
    }

    private void openNameInput() {
        GameSettings settings = GameSettings.getInstance();
        NameInputDialog dialog = new NameInputDialog(this, settings);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            dispose();
            new GameFrame();
        }
    }

    private void openSettings() {
        dispose();
        new SettingsFrame(true);
    }
}
