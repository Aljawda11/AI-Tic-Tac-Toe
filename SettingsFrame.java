import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsFrame extends JFrame {

    private static final Color BG_COLOR     = new Color(30, 30, 30);
    private static final Color PANEL_COLOR  = new Color(45, 45, 45);
    private static final Color TEXT_COLOR   = new Color(220, 220, 220);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color LABEL_DIM    = new Color(160, 160, 160);

    private GameSettings settings;
    private boolean fromWelcome;

    // General settings controls
    private JComboBox<String> gameModeBox;
    private JComboBox<String> boardSizeBox;
    private JComboBox<String> difficultyBox;
    private JComboBox<String> p1SymbolBox;
    private JComboBox<String> p2SymbolBox;

    // Match info toggles
    private JCheckBox timerToggle;
    private JCheckBox boardInfoToggle;
    private JCheckBox playerCounterToggle;

    public SettingsFrame(boolean fromWelcome) {
        this.settings = GameSettings.getInstance();
        this.fromWelcome = fromWelcome;

        setTitle("Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);

        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Settings");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(TEXT_COLOR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Main scrollable content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        // --- General Section ---
        content.add(buildSectionHeader("General"));

        content.add(buildSettingRow(
            "Gamemode",
            "Singleplayer or Multiplayer",
            gameModeBox = buildComboBox(new String[]{"Singleplayer", "Multiplayer"}, settings.getGameMode())
        ));

        content.add(buildSettingRow(
            "Board Size",
            "Default 3x3 or custom up to 6x6",
            boardSizeBox = buildComboBox(new String[]{"3","4","5","6"}, String.valueOf(settings.getBoardSize()))
        ));

        content.add(buildSettingRow(
            "Difficulty",
            "Bot difficulty (Singleplayer only)",
            difficultyBox = buildComboBox(new String[]{"Easy","Medium","Hard"}, settings.getDifficulty())
        ));

        content.add(buildSettingRow(
            "Player 1 Symbol",
            "Symbol used by Player 1",
            p1SymbolBox = buildComboBox(new String[]{"X","O"}, settings.getPlayer1Symbol())
        ));

        content.add(buildSettingRow(
            "Player 2 Symbol",
            "Symbol used by Player 2 or Bot",
            p2SymbolBox = buildComboBox(new String[]{"O","X"}, settings.getPlayer2Symbol())
        ));

        // Sync symbols so they can't be the same
        p1SymbolBox.addActionListener(e -> syncSymbols(true));
        p2SymbolBox.addActionListener(e -> syncSymbols(false));

        content.add(Box.createVerticalStrut(10));

        // --- Match Info Section ---
        content.add(buildSectionHeader("Match Info"));

        timerToggle = buildToggleRow(content, "Match Timer", "Keep track of how long the match takes", settings.isShowTimer());
        boardInfoToggle = buildToggleRow(content, "Board Info", "Displays number of spots taken", settings.isShowBoardInfo());
        playerCounterToggle = buildToggleRow(content, "Player Counter", "Human or bot wins count", settings.isShowPlayerCounter());

        content.add(Box.createVerticalStrut(16));

        // --- Buttons ---
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(BG_COLOR);

        JButton saveBtn = createButton("Save", ACCENT_COLOR);
        saveBtn.addActionListener(e -> saveSettings());

        JButton resetBtn = createButton("Reset to Default", new Color(100, 60, 60));
        resetBtn.addActionListener(e -> resetSettings());

        JButton backBtn = createButton("Back", new Color(70, 70, 70));
        backBtn.addActionListener(e -> goBack());

        btnRow.add(saveBtn);
        btnRow.add(resetBtn);
        btnRow.add(backBtn);
        btnRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(btnRow);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG_COLOR);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_COLOR);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel buildSectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(ACCENT_COLOR);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildSettingRow(String title, String subtitle, JComponent control) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(PANEL_COLOR);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(PANEL_COLOR);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLbl.setForeground(TEXT_COLOR);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        subLbl.setForeground(LABEL_DIM);

        textPanel.add(titleLbl);
        textPanel.add(subLbl);

        row.add(textPanel, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);

        return row;
    }

    private JCheckBox buildToggleRow(JPanel parent, String title, String subtitle, boolean selected) {
        JCheckBox box = new JCheckBox();
        box.setSelected(selected);
        box.setBackground(PANEL_COLOR);
        box.setFocusPainted(false);

        JPanel row = buildSettingRow(title, subtitle, box);
        parent.add(row);
        parent.add(Box.createVerticalStrut(2));
        return box;
    }

    private JComboBox<String> buildComboBox(String[] items, String selected) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setSelectedItem(selected);
        box.setBackground(new Color(55, 55, 55));
        box.setForeground(TEXT_COLOR);
        box.setFont(new Font("Arial", Font.PLAIN, 12));
        box.setFocusable(false);
        box.setPreferredSize(new Dimension(130, 30));
        return box;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void syncSymbols(boolean p1Changed) {
        String p1 = (String) p1SymbolBox.getSelectedItem();
        String p2 = (String) p2SymbolBox.getSelectedItem();
        if (p1 != null && p1.equals(p2)) {
            if (p1Changed) {
                p2SymbolBox.setSelectedItem(p1.equals("X") ? "O" : "X");
            } else {
                p1SymbolBox.setSelectedItem(p2.equals("X") ? "O" : "X");
            }
        }
    }

    private void saveSettings() {
        settings.setGameMode((String) gameModeBox.getSelectedItem());
        settings.setBoardSize(Integer.parseInt((String) boardSizeBox.getSelectedItem()));
        settings.setDifficulty((String) difficultyBox.getSelectedItem());
        settings.setPlayer1Symbol((String) p1SymbolBox.getSelectedItem());
        settings.setPlayer2Symbol((String) p2SymbolBox.getSelectedItem());
        settings.setShowTimer(timerToggle.isSelected());
        settings.setShowBoardInfo(boardInfoToggle.isSelected());
        settings.setShowPlayerCounter(playerCounterToggle.isSelected());
        settings.saveAll();

        JOptionPane.showMessageDialog(this, "Settings saved!", "Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Reset all settings to default?", "Reset",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            settings.resetToDefaults();
            settings = GameSettings.getInstance();
            dispose();
            new SettingsFrame(fromWelcome);
        }
    }

    private void goBack() {
        dispose();
        new WelcomeFrame();
    }
}
