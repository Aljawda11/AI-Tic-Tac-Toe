import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotAI {

    private String botSymbol;
    private String humanSymbol;
    private String difficulty;
    private Random random = new Random();

    public BotAI(String botSymbol, String humanSymbol, String difficulty) {
        this.botSymbol = botSymbol;
        this.humanSymbol = humanSymbol;
        this.difficulty = difficulty;
    }

    /**
     * Returns [row, col] for the bot's next move.
     */
    public int[] getBestMove(GameLogic game) {
        switch (difficulty) {
            case "Easy":   return getEasyMove(game);
            case "Hard":   return getHardMove(game);
            default:       return getMediumMove(game);
        }
    }

    // Easy: fully random move
    private int[] getEasyMove(GameLogic game) {
        List<int[]> empty = getEmptyCells(game);
        if (empty.isEmpty()) return null;
        return empty.get(random.nextInt(empty.size()));
    }

    // Medium: wins if possible, blocks if needed, otherwise random
    private int[] getMediumMove(GameLogic game) {
        int size = game.getSize();

        // Try to win
        int[] win = findWinningMove(game, botSymbol);
        if (win != null) return win;

        // Try to block human
        int[] block = findWinningMove(game, humanSymbol);
        if (block != null) return block;

        // Otherwise random
        return getEasyMove(game);
    }

    // Hard: full minimax (limited depth for boards > 3x3 to keep it fast)
    private int[] getHardMove(GameLogic game) {
        int size = game.getSize();
        int maxDepth = (size <= 3) ? 9 : (size <= 4) ? 4 : 3;

        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        String[][] board = game.getBoardCopy();

        List<int[]> empty = getEmptyCells(game);
        if (empty.isEmpty()) return null;

        for (int[] cell : empty) {
            board[cell[0]][cell[1]] = botSymbol;
            int score = minimax(board, size, 0, false, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board[cell[0]][cell[1]] = GameLogic.EMPTY;
            if (score > bestScore) {
                bestScore = score;
                bestMove = cell;
            }
        }
        return bestMove;
    }

    private int minimax(String[][] board, int size, int depth, boolean isMaximizing,
                        int maxDepth, int alpha, int beta) {

        String winner = checkWinner(board, size);
        if (winner != null) {
            return winner.equals(botSymbol) ? (10 - depth) : (depth - 10);
        }
        if (isBoardFull(board, size) || depth >= maxDepth) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            outer:
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (board[r][c].equals(GameLogic.EMPTY)) {
                        board[r][c] = botSymbol;
                        int score = minimax(board, size, depth + 1, false, maxDepth, alpha, beta);
                        board[r][c] = GameLogic.EMPTY;
                        best = Math.max(best, score);
                        alpha = Math.max(alpha, best);
                        if (beta <= alpha) break outer;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            outer:
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (board[r][c].equals(GameLogic.EMPTY)) {
                        board[r][c] = humanSymbol;
                        int score = minimax(board, size, depth + 1, true, maxDepth, alpha, beta);
                        board[r][c] = GameLogic.EMPTY;
                        best = Math.min(best, score);
                        beta = Math.min(beta, best);
                        if (beta <= alpha) break outer;
                    }
                }
            }
            return best;
        }
    }

    private String checkWinner(String[][] board, int size) {
        // Rows
        for (int r = 0; r < size; r++) {
            if (!board[r][0].equals(GameLogic.EMPTY)) {
                boolean win = true;
                for (int c = 1; c < size; c++) {
                    if (!board[r][c].equals(board[r][0])) { win = false; break; }
                }
                if (win) return board[r][0];
            }
        }
        // Columns
        for (int c = 0; c < size; c++) {
            if (!board[0][c].equals(GameLogic.EMPTY)) {
                boolean win = true;
                for (int r = 1; r < size; r++) {
                    if (!board[r][c].equals(board[0][c])) { win = false; break; }
                }
                if (win) return board[0][c];
            }
        }
        // Main diagonal
        if (!board[0][0].equals(GameLogic.EMPTY)) {
            boolean win = true;
            for (int i = 1; i < size; i++) {
                if (!board[i][i].equals(board[0][0])) { win = false; break; }
            }
            if (win) return board[0][0];
        }
        // Anti diagonal
        if (!board[0][size - 1].equals(GameLogic.EMPTY)) {
            boolean win = true;
            for (int i = 1; i < size; i++) {
                if (!board[i][size - 1 - i].equals(board[0][size - 1])) { win = false; break; }
            }
            if (win) return board[0][size - 1];
        }
        return null;
    }

    private boolean isBoardFull(String[][] board, int size) {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c].equals(GameLogic.EMPTY)) return false;
            }
        }
        return true;
    }

    private int[] findWinningMove(GameLogic game, String symbol) {
        int size = game.getSize();
        String[][] board = game.getBoardCopy();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c].equals(GameLogic.EMPTY)) {
                    board[r][c] = symbol;
                    if (checkWinner(board, size) != null) {
                        return new int[]{r, c};
                    }
                    board[r][c] = GameLogic.EMPTY;
                }
            }
        }
        return null;
    }

    private List<int[]> getEmptyCells(GameLogic game) {
        List<int[]> cells = new ArrayList<>();
        int size = game.getSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (game.isCellEmpty(r, c)) {
                    cells.add(new int[]{r, c});
                }
            }
        }
        return cells;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setSymbols(String botSymbol, String humanSymbol) {
        this.botSymbol = botSymbol;
        this.humanSymbol = humanSymbol;
    }
}
