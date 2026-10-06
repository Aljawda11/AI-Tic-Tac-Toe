import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame {

    // Colors
    private static final Color BG_COLOR       = new Color(30, 30, 30);
    private static final Color PANEL_COLOR    = new Color(45, 45, 45);
    private static final Color TEXT_COLOR     = new Color(220, 220, 220);
    private static final Color ACCENT_COLOR   = new Color(70, 130, 180);
    private static final Color CELL_BG        = new Color(50, 50, 50);
    private static final Color CELL_HOVER     = new Color(65, 65, 65);
    private static final Color CELL_WIN       = new Color(60, 110, 60);
    private static final Color CELL_BORDER    = new Color(80, 80, 80);
    private static final Color X_COLOR        = new Color(100, 180, 255);
    private static final Color O_COLOR        = new Color(255, 140, 100);
    private static final Color STATUS_WIN     = new Color(100, 220, 100);
    private static final Color STATUS_LOSE    = new Color(220, 80, 80);
    private static final Color STATUS_DRAW    = new Color(220, 180, 80);

    private GameSettings settings;
    private GameLogic game;
    private BotAI botAI;

    private JLabel statusLabel;
    private JLabel timerLabel;
    private JLabel spotsLabel;
    private JLabel p1WinLabel;
    private JLabel p2WinLabel;
    private JPanel boardPanel;
    private JButton[][] cellButtons;
    private JButton actionBtn;

    private boolean isPlayer1Turn = true;
    private boolean gameOver = false;
    private int player1Wins = 0;
    private int player2Wins = 0;
    private int timerSeconds = 0;
    private Timer swingTimer;
    private boolean isSinglePlayer;
    private String p1Symbol;
    private String p2Symbol;

    public GameFrame() {
        this.settings = GameSettings.getInstance();
        this.isSinglePlayer = settings.getGameMode().equals("Singleplayer");
        this.p1Symbol = settings.getPlayer1Symbol();
        this.p2Symbol = settings.getPlayer2Symbol();

        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);

        initGame();
        buildUI();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        startTimer();
    }

    private void initGame() {
        game = new GameLogic(settings.getBoardSize());
        if (isSinglePlayer) {
            botAI = new BotAI(p2Symbol, p1Symbol, settings.getDifficulty());
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // Top panel: status + info bar
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));

        // Status label
        statusLabel = new JLabel("Your Turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(statusLabel);

        topPanel.add(Box.createVerticalStrut(8));

        // Info bar
        JPanel infoBar = buildInfoBar();
        infoBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(infoBar);

        add(topPanel, BorderLayout.NORTH);

        // Board
        boardPanel = buildBoard();
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        boardWrapper.setBackground(BG_COLOR);
        boardWrapper.add(boardPanel);
        add(boardWrapper, BorderLayout.CENTER);

        // Bottom panel: action button + menu
        JPanel bottomPanel = buildBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel buildInfoBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4));
        bar.setBackground(PANEL_COLOR);
        bar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        timerLabel   = createInfoLabel("Time: 0s",   settings.isShowTimer());
        spotsLabel   = createInfoLabel("Spots: 0",   settings.isShowBoardInfo());
        p1WinLabel   = createInfoLabel(settings.getPlayer1Name() + ": 0", settings.isShowPlayerCounter());
        p2WinLabel   = createInfoLabel((isSinglePlayer ? "Bot" : settings.getPlayer2Name()) + ": 0", settings.isShowPlayerCounter());

        bar.add(timerLabel);
        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(spotsLabel);
        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(p1WinLabel);
        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(p2WinLabel);

        return bar;
    }

    private JLabel createInfoLabel(String text, boolean visible) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(TEXT_COLOR);
        lbl.setVisible(visible);
        return lbl;
    }

    private JPanel buildBoard() {
        int size = settings.getBoardSize();
        int cellSize = Math.max(70, 210 / size);

        JPanel panel = new JPanel(new GridLayout(size, size, 4, 4));
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        cellButtons = new JButton[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton cell = new JButton("");
                cell.setFont(new Font("Arial", Font.BOLD, cellSize / 2));
                cell.setBackground(CELL_BG);
                cell.setForeground(X_COLOR);
                cell.setFocusPainted(false);
                cell.setBorderPainted(false);
                cell.setOpaque(true);
                cell.setPreferredSize(new Dimension(cellSize, cellSize));
                cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

                final int row = r, col = c;

                cell.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (cell.getText().isEmpty() && !gameOver)
                            cell.setBackground(CELL_HOVER);
                    }
                    public void mouseExited(MouseEvent e) {
                        if (cell.getText().isEmpty())
                            cell.setBackground(CELL_BG);
                    }
                });

                cell.addActionListener(e -> handleCellClick(row, col));
                cellButtons[r][c] = cell;
                panel.add(cell);
            }
        }

        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        panel.setBackground(BG_COLOR);

        actionBtn = new JButton("Play Again");
        actionBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        actionBtn.setBackground(ACCENT_COLOR);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorderPainted(false);
        actionBtn.setOpaque(true);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.setVisible(false);
        actionBtn.addActionListener(e -> resetGame());

        JButton menuBtn = new JButton("Main Menu");
        menuBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        menuBtn.setBackground(new Color(70, 70, 70));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setFocusPainted(false);
        menuBtn.setBorderPainted(false);
        menuBtn.setOpaque(true);
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuBtn.addActionListener(e -> goToMainMenu());

        JButton muteBtn = new JButton(AudioManager.isMuted() ? "[Music: OFF]" : "[Music: ON]");
        muteBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        muteBtn.setBackground(new Color(60, 60, 60));
        muteBtn.setForeground(new Color(180, 180, 180));
        muteBtn.setFocusPainted(false);
        muteBtn.setBorderPainted(false);
        muteBtn.setOpaque(true);
        muteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        muteBtn.addActionListener(e -> {
            AudioManager.toggleMute();
            muteBtn.setText(AudioManager.isMuted() ? "[Music: OFF]" : "[Music: ON]");
        });

        panel.add(actionBtn);
        panel.add(menuBtn);
        panel.add(muteBtn);
        return panel;
    }

    private void handleCellClick(int row, int col) {
        if (gameOver) return;
        if (!game.isCellEmpty(row, col)) return;

        // In singleplayer, only allow moves on player 1's turn
        if (isSinglePlayer && !isPlayer1Turn) return;

        String currentSymbol = isPlayer1Turn ? p1Symbol : p2Symbol;
        game.makeMove(row, col, currentSymbol);
        updateCell(row, col, currentSymbol);
        updateInfoBar();

        if (checkGameOver(currentSymbol)) return;

        isPlayer1Turn = !isPlayer1Turn;
        updateStatusLabel();

        // Bot move in singleplayer
        if (isSinglePlayer && !isPlayer1Turn && !gameOver) {
            disableBoard();
            SwingUtilities.invokeLater(this::doBotMove);
        }
    }

    private void doBotMove() {
        int[] move = botAI.getBestMove(game);
        if (move == null) return;

        game.makeMove(move[0], move[1], p2Symbol);
        updateCell(move[0], move[1], p2Symbol);
        updateInfoBar();
        enableBoard();

        if (checkGameOver(p2Symbol)) return;

        isPlayer1Turn = true;
        updateStatusLabel();
    }

    private boolean checkGameOver(String symbol) {
        if (game.isWinner(symbol)) {
            gameOver = true;
            stopTimer();

            boolean isP1 = symbol.equals(p1Symbol);
            String winnerName = isP1 ? settings.getPlayer1Name()
                                     : (isSinglePlayer ? "Bot" : settings.getPlayer2Name());

            highlightWinningCells(symbol);

            if (isP1) {
                player1Wins++;
                statusLabel.setText("You won!");
                statusLabel.setForeground(STATUS_WIN);
                actionBtn.setText("Play Again");
            } else {
                if (isSinglePlayer) {
                    statusLabel.setText("You lost!");
                    statusLabel.setForeground(STATUS_LOSE);
                    actionBtn.setText("Try Again");
                } else {
                    player2Wins++;
                    statusLabel.setText(winnerName + " wins!");
                    statusLabel.setForeground(STATUS_WIN);
                    actionBtn.setText("Play Again");
                }
            }

            if (!isP1) player2Wins++;

            updateWinLabels();
            actionBtn.setVisible(true);
            disableBoard();

            // Save to DB
            String p2name = isSinglePlayer ? settings.getDifficulty() + " Bot" : settings.getPlayer2Name();
            DatabaseManager.saveGameHistory(
                settings.getPlayer1Name(), p2name, winnerName,
                isP1 ? "win" : "loss",
                settings.getBoardSize(), settings.getGameMode(), timerSeconds
            );

            return true;
        }

        if (game.isDraw()) {
            gameOver = true;
            stopTimer();
            statusLabel.setText("Draw!");
            statusLabel.setForeground(STATUS_DRAW);
            actionBtn.setText("Play Again");
            actionBtn.setVisible(true);
            disableBoard();

            String p2name = isSinglePlayer ? settings.getDifficulty() + " Bot" : settings.getPlayer2Name();
            DatabaseManager.saveGameHistory(
                settings.getPlayer1Name(), p2name, "None",
                "draw", settings.getBoardSize(), settings.getGameMode(), timerSeconds
            );

            return true;
        }

        return false;
    }

    private void highlightWinningCells(String symbol) {
        int[] line = game.getWinningLine(symbol);
        if (line == null) return;

        int r1 = line[0], c1 = line[1], r2 = line[2], c2 = line[3];

        if (r1 == r2) {
            for (int c = Math.min(c1, c2); c <= Math.max(c1, c2); c++)
                cellButtons[r1][c].setBackground(CELL_WIN);
        } else if (c1 == c2) {
            for (int r = Math.min(r1, r2); r <= Math.max(r1, r2); r++)
                cellButtons[r][c1].setBackground(CELL_WIN);
        } else {
            int size = settings.getBoardSize();
            if (c1 < c2) {
                for (int i = 0; i < size; i++) cellButtons[i][i].setBackground(CELL_WIN);
            } else {
                for (int i = 0; i < size; i++) cellButtons[i][size - 1 - i].setBackground(CELL_WIN);
            }
        }
    }

    private void updateCell(int row, int col, String symbol) {
        JButton cell = cellButtons[row][col];
        cell.setText(symbol);
        cell.setForeground(symbol.equals("X") ? X_COLOR : O_COLOR);
        cell.setEnabled(false);
    }

    private void updateStatusLabel() {
        if (isSinglePlayer) {
            statusLabel.setText(isPlayer1Turn ? "Your Turn" : "Bot Thinking...");
        } else {
            String name = isPlayer1Turn ? settings.getPlayer1Name() : settings.getPlayer2Name();
            statusLabel.setText(name + "'s Turn");
        }
        statusLabel.setForeground(TEXT_COLOR);
    }

    private void updateInfoBar() {
        spotsLabel.setText("Spots: " + game.getSpotsTaken());
    }

    private void updateWinLabels() {
        p1WinLabel.setText(settings.getPlayer1Name() + ": " + player1Wins);
        String p2name = isSinglePlayer ? "Bot" : settings.getPlayer2Name();
        p2WinLabel.setText(p2name + ": " + player2Wins);
    }

    private void startTimer() {
        if (!settings.isShowTimer()) return;
        timerSeconds = 0;
        swingTimer = new Timer(1000, e -> {
            timerSeconds++;
            timerLabel.setText("Time: " + timerSeconds + "s");
        });
        swingTimer.start();
    }

    private void stopTimer() {
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
        }
    }

    private void disableBoard() {
        int size = settings.getBoardSize();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (game.isCellEmpty(r, c)) cellButtons[r][c].setEnabled(false);
    }

    private void enableBoard() {
        int size = settings.getBoardSize();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (game.isCellEmpty(r, c)) cellButtons[r][c].setEnabled(true);
    }

    private void resetGame() {
        stopTimer();
        game.resetBoard();
        gameOver = false;
        isPlayer1Turn = true;

        int size = settings.getBoardSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cellButtons[r][c].setText("");
                cellButtons[r][c].setBackground(CELL_BG);
                cellButtons[r][c].setEnabled(true);
            }
        }

        statusLabel.setText("Your Turn");
        statusLabel.setForeground(TEXT_COLOR);
        spotsLabel.setText("Spots: 0");
        actionBtn.setVisible(false);
        startTimer();
    }

    private void goToMainMenu() {
        stopTimer();
        dispose();
        new WelcomeFrame();
    }
}
