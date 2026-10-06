public class GameSettings {

    private String gameMode;
    private int boardSize;
    private boolean showTimer;
    private boolean showBoardInfo;
    private boolean showPlayerCounter;
    private String difficulty;
    private String player1Symbol;
    private String player2Symbol;
    private String player1Name;
    private String player2Name;

    private static GameSettings instance = null;

    private GameSettings() {
        loadFromDatabase();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public void loadFromDatabase() {
        gameMode        = getOrDefault("gamemode", "Singleplayer");
        boardSize       = Integer.parseInt(getOrDefault("board_size", "3"));
        showTimer       = Boolean.parseBoolean(getOrDefault("show_timer", "true"));
        showBoardInfo   = Boolean.parseBoolean(getOrDefault("show_board_info", "true"));
        showPlayerCounter = Boolean.parseBoolean(getOrDefault("show_player_counter", "true"));
        difficulty      = getOrDefault("difficulty", "Medium");
        player1Symbol   = getOrDefault("player1_symbol", "X");
        player2Symbol   = getOrDefault("player2_symbol", "O");
        player1Name     = getOrDefault("player1_name", "Player 1");
        player2Name     = getOrDefault("player2_name", "Player 2");
    }

    private String getOrDefault(String key, String defaultVal) {
        String val = DatabaseManager.getSetting(key);
        return (val != null) ? val : defaultVal;
    }

    public void saveAll() {
        DatabaseManager.saveSetting("gamemode", gameMode);
        DatabaseManager.saveSetting("board_size", String.valueOf(boardSize));
        DatabaseManager.saveSetting("show_timer", String.valueOf(showTimer));
        DatabaseManager.saveSetting("show_board_info", String.valueOf(showBoardInfo));
        DatabaseManager.saveSetting("show_player_counter", String.valueOf(showPlayerCounter));
        DatabaseManager.saveSetting("difficulty", difficulty);
        DatabaseManager.saveSetting("player1_symbol", player1Symbol);
        DatabaseManager.saveSetting("player2_symbol", player2Symbol);
        DatabaseManager.saveSetting("player1_name", player1Name);
        DatabaseManager.saveSetting("player2_name", player2Name);
    }

    public void resetToDefaults() {
        DatabaseManager.resetAllSettings();
        instance = null;
        instance = new GameSettings();
    }

    // Getters
    public String getGameMode()          { return gameMode; }
    public int getBoardSize()            { return boardSize; }
    public boolean isShowTimer()         { return showTimer; }
    public boolean isShowBoardInfo()     { return showBoardInfo; }
    public boolean isShowPlayerCounter() { return showPlayerCounter; }
    public String getDifficulty()        { return difficulty; }
    public String getPlayer1Symbol()     { return player1Symbol; }
    public String getPlayer2Symbol()     { return player2Symbol; }
    public String getPlayer1Name()       { return player1Name; }
    public String getPlayer2Name()       { return player2Name; }

    // Setters
    public void setGameMode(String gameMode)                  { this.gameMode = gameMode; }
    public void setBoardSize(int boardSize)                   { this.boardSize = boardSize; }
    public void setShowTimer(boolean showTimer)               { this.showTimer = showTimer; }
    public void setShowBoardInfo(boolean showBoardInfo)       { this.showBoardInfo = showBoardInfo; }
    public void setShowPlayerCounter(boolean showPlayerCounter){ this.showPlayerCounter = showPlayerCounter; }
    public void setDifficulty(String difficulty)              { this.difficulty = difficulty; }
    public void setPlayer1Symbol(String s)                    { this.player1Symbol = s; }
    public void setPlayer2Symbol(String s)                    { this.player2Symbol = s; }
    public void setPlayer1Name(String n)                      { this.player1Name = n; }
    public void setPlayer2Name(String n)                      { this.player2Name = n; }
}
