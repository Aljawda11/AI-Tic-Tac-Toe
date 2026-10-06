public class GameLogic {

    private String[][] board;
    private int size;
    private int spotsTaken;

    public static final String EMPTY = "";

    public GameLogic(int size) {
        this.size = size;
        this.board = new String[size][size];
        this.spotsTaken = 0;
        resetBoard();
    }

    public void resetBoard() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                board[r][c] = EMPTY;
            }
        }
        spotsTaken = 0;
    }

    public boolean makeMove(int row, int col, String symbol) {
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        if (!board[row][col].equals(EMPTY)) return false;
        board[row][col] = symbol;
        spotsTaken++;
        return true;
    }

    public boolean isWinner(String symbol) {
        // Check rows
        for (int r = 0; r < size; r++) {
            boolean win = true;
            for (int c = 0; c < size; c++) {
                if (!board[r][c].equals(symbol)) { win = false; break; }
            }
            if (win) return true;
        }

        // Check columns
        for (int c = 0; c < size; c++) {
            boolean win = true;
            for (int r = 0; r < size; r++) {
                if (!board[r][c].equals(symbol)) { win = false; break; }
            }
            if (win) return true;
        }

        // Check main diagonal
        boolean win = true;
        for (int i = 0; i < size; i++) {
            if (!board[i][i].equals(symbol)) { win = false; break; }
        }
        if (win) return true;

        // Check anti diagonal
        win = true;
        for (int i = 0; i < size; i++) {
            if (!board[i][size - 1 - i].equals(symbol)) { win = false; break; }
        }
        return win;
    }

    public boolean isDraw() {
        return spotsTaken == size * size;
    }

    public boolean isGameOver(String p1Symbol, String p2Symbol) {
        return isWinner(p1Symbol) || isWinner(p2Symbol) || isDraw();
    }

    public String getCell(int row, int col) {
        return board[row][col];
    }

    public boolean isCellEmpty(int row, int col) {
        return board[row][col].equals(EMPTY);
    }

    public int getSize() {
        return size;
    }

    public int getSpotsTaken() {
        return spotsTaken;
    }

    public String[][] getBoard() {
        return board;
    }

    // Returns a copy of the board for AI use
    public String[][] getBoardCopy() {
        String[][] copy = new String[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                copy[r][c] = board[r][c];
            }
        }
        return copy;
    }

    public int[] getWinningLine(String symbol) {
        // Returns [startRow, startCol, endRow, endCol] or null
        for (int r = 0; r < size; r++) {
            boolean win = true;
            for (int c = 0; c < size; c++) {
                if (!board[r][c].equals(symbol)) { win = false; break; }
            }
            if (win) return new int[]{r, 0, r, size - 1};
        }
        for (int c = 0; c < size; c++) {
            boolean win = true;
            for (int r = 0; r < size; r++) {
                if (!board[r][c].equals(symbol)) { win = false; break; }
            }
            if (win) return new int[]{0, c, size - 1, c};
        }
        boolean win = true;
        for (int i = 0; i < size; i++) {
            if (!board[i][i].equals(symbol)) { win = false; break; }
        }
        if (win) return new int[]{0, 0, size - 1, size - 1};

        win = true;
        for (int i = 0; i < size; i++) {
            if (!board[i][size - 1 - i].equals(symbol)) { win = false; break; }
        }
        if (win) return new int[]{0, size - 1, size - 1, 0};

        return null;
    }
}
