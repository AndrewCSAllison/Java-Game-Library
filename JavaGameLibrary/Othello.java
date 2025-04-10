import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Othello extends JFrame {
    private final int TILE_SIZE = 50;
    private final int GRID_SIZE = 8;
    private final String[][] board = new String[GRID_SIZE][GRID_SIZE];
    private final JButton[][] tiles = new JButton[GRID_SIZE][GRID_SIZE];
    private boolean isSinglePlayer = false;
    private int currentPlayer = 1;
    private int passingTurns = 0;
    private final JLabel currentPlayerLabel = new JLabel("Current Player: 1", SwingConstants.CENTER);
    private final JLabel player1Label = new JLabel("",SwingConstants.CENTER);
    private final JLabel player2Label = new JLabel("",SwingConstants.CENTER);

    public Othello() {
        showHomeScreen();
    }

    // Initialize the game board
    private void initializeBoard() {
        clearGameBoard();
        // Set initial pieces
        board[3][3] = "1";
        board[3][4] = "2";
        board[4][3] = "2";
        board[4][4] = "1";
        // Set the initial pieces on the board
        tiles[3][3].setText("●");
        tiles[3][3].setForeground(Color.BLACK);
        tiles[3][4].setText("●");
        tiles[3][4].setForeground(Color.WHITE);
        tiles[4][3].setText("●");
        tiles[4][3].setForeground(Color.WHITE);
        tiles[4][4].setText("●");
        tiles[4][4].setForeground(Color.BLACK);
    }
    

    // Clear the game board
    private void clearGameBoard() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                board[row][col] = null; // empty cell
                tiles[row][col].setText(""); // clear UI cell
            }
        }
    }

    // A valid move must capture at least one opponent's piece in any direction
    private boolean isValidMove(int row, int col) {
        // Check if the cell is empty
        if (board[row][col] != null) {
            return false;
        }
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // vertical and horizontal
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonals
        };
        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            int r = row + dRow;
            int c = col + dCol;
            boolean hasOpponentPiece = false;
            while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                if (board[r][c] == null) {
                    break; // empty cell, stop checking this direction
                } else if (board[r][c].equals(String.valueOf(currentPlayer))) {
                    if (hasOpponentPiece) {
                        return true; // valid move found
                    } else {
                        break; // no opponent pieces in between, stop checking this direction
                    }
                } else {
                    hasOpponentPiece = true; // found an opponent piece
                }
                r += dRow;
                c += dCol;
            }
        }
        return false; // no valid move found in any direction
    }

    // Flip opponent pieces in all directions after placing a piece
    // Both on internal game board and on the UI
    private void flipPieces(int row, int col) {
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // vertical and horizontal
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonals
        };
        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            int r = row + dRow;
            int c = col + dCol;
            boolean hasOpponentPiece = false;
            while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                if (board[r][c] == null) {
                    break; // empty cell, stop checking this direction
                } else if (board[r][c].equals(String.valueOf(currentPlayer))) {
                    if (hasOpponentPiece) {
                        // Flip opponent pieces in this direction
                        while (r != row || c != col) {
                            board[r][c] = String.valueOf(currentPlayer);
                            tiles[r][c].setText("●");
                            tiles[r][c].setForeground(currentPlayer == 1 ? Color.BLACK : Color.WHITE);
                            r -= dRow;
                            c -= dCol;
                        }
                        break; // valid move found in this direction
                    } else {
                        break; // no opponent pieces in between, stop checking this direction
                    }
                } else {
                    hasOpponentPiece = true; // found an opponent piece
                }
                r += dRow;
                c += dCol;
            }
        }
    }

    // Place a piece on the board and flip opponent pieces 
    private void placePiece(int row, int col) {
        board[row][col] = (currentPlayer == 1) ? "1" : "2";
        flipPieces(row,col);
    }

    // Update the UI to show the placed piece
    private void updateUI(int row, int col) { 
        tiles[row][col].setText("●");
        tiles[row][col].setForeground(currentPlayer == 1 ? Color.BLACK : Color.WHITE);
    }

    // Count the number of pieces for the given player
    private int countPieces(int player) {
        int count = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] != null && board[row][col].equals(String.valueOf(player))) {
                    count++;
                }
            }
        }
        return count;
    }

    // Switch the current player & update the UI
    private void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        currentPlayerLabel.setText("Player " + currentPlayer + "'s Turn");
        currentPlayerLabel.setForeground(currentPlayer == 1 ? Color.BLACK : Color.WHITE);
    }

    // Handle player move
    private void handlePlayerMove(int row, int col) {
        if (isValidMove(row, col)) {
            placePiece(row, col);
            updateUI(row, col);
            switchPlayer();
            player1Label.setText("Player 1: " + countPieces(1));
            player2Label.setText("Player 2: " + countPieces(2));
            checkGameState();
        } 
    }

    // AI move for single-player mode ()
    private void aiMove() {
        int[] bestMove = getBestMove();
        if (bestMove[0] != -1 && bestMove[1] != -1) {
            placePiece(bestMove[0], bestMove[1]);
            updateUI(bestMove[0], bestMove[1]);
            switchPlayer();
            player1Label.setText("Player 1: " + countPieces(1));
            player2Label.setText("Player 2: " + countPieces(2));
            checkGameState();
        } 
    }

    // Helper method that determines the best move for the AI
    private int[] getBestMove() {
        int bestRow = -1, bestCol = -1;
        int maxFlips = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (isValidMove(row, col)) {
                    int flips = countFlips(row, col);
                    if (flips > maxFlips) {
                        maxFlips = flips;
                        bestRow = row;
                        bestCol = col;
                    }
                }
            }
        }
        return new int[]{bestRow, bestCol};
    }

    // Count the number of pieces that would be flipped if a piece is placed at (row, col)
    private int countFlips(int row, int col) {
        int flips = 0;
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // vertical and horizontal
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonals
        };
        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            int r = row + dRow;
            int c = col + dCol;
            boolean hasOpponentPiece = false;
            while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                if (board[r][c] == null) {
                    break; // empty cell, stop checking this direction
                } else if (board[r][c].equals(String.valueOf(currentPlayer))) {
                    if (hasOpponentPiece) {
                        flips += countFlipsInDirection(row, col, dRow, dCol);
                        break; // valid move found in this direction
                    } else {
                        break; // no opponent pieces in between, stop checking this direction
                    }
                } else {
                    hasOpponentPiece = true; // found an opponent piece
                }
                r += dRow;
                c += dCol;
            }
        }
        return flips;
    }

    // Count the number of pieces that would be flipped in a specific direction
    private int countFlipsInDirection(int row, int col, int dRow, int dCol) {
        int flips = 0;
        int r = row + dRow;
        int c = col + dCol;
        while (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
            if (board[r][c] == null) {
                break; // empty cell, stop checking this direction
            } else if (board[r][c].equals(String.valueOf(currentPlayer))) {
                return flips; // valid move found in this direction
            } else {
                flips++; // found an opponent piece to flip
            }
            r += dRow;
            c += dCol;
        }
        return 0; // no valid move found in this direction
    }

    // Check if the game is over or a player no longer has valid moves 
    private void checkGameState() {
        boolean hasValidMove = false;
        
        // Check if any player can make a valid move
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == null) {
                    if (isValidMove(row, col)) {
                        hasValidMove = true;
                        break;
                    }
                }
            }
            if (hasValidMove) {
                break;
            }
        }
    
        // If neither player has a valid move, increment passing turns
        if (!hasValidMove) {
            passingTurns++;
            if (passingTurns == 2) {
                gameOver(); // Both players have passed, game over
            } else {
                // Switch to the other player and reset passing turns if needed
                switchPlayer();
                passingTurns = 0; // Reset passing turns after switching players
            }
        }
    
        // If board is full, end the game
        boolean boardFull = true;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == null) {
                    boardFull = false;
                    break;
                }
            }
            if (!boardFull) {
                break;
            }
        }
    
        // End the game if the board is full or there are no valid moves
        if (boardFull || passingTurns == 2) {
            gameOver();
        }
    }
    

    // Display game over message and prompt for restart or the main menu
    private void gameOver() {
        int player1Count = countPieces(1);
        int player2Count = countPieces(2);
        String winner = (player1Count > player2Count) ? "Player 1" : (player2Count > player1Count) ? "Player 2" : "Draw";
        String message = "Game Over! " + winner + " wins!\n" +
                "Player 1: " + player1Count + "\n" +
                "Player 2: " + player2Count;
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            clearGameBoard();
            initializeBoard();
        } else {
            dispose();
        }
    }
    
    // Create the GUI components and layout
    private void createGUI() {
        // Setup the main frame
        JFrame frame = new JFrame("Othello");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(500, 550);
        frame.setLayout(new BorderLayout());
        // Score panel at the top
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.setBackground(Color.LIGHT_GRAY);
        player1Label.setFont(new Font("Arial", Font.BOLD, 20));
        player2Label.setFont(new Font("Arial", Font.BOLD, 20));
        player1Label.setText("Player 1: 2");
        player2Label.setText("Player 2: 2");
        player1Label.setForeground(Color.BLACK);
        player2Label.setForeground(Color.WHITE);
        scorePanel.add(player1Label);
        scorePanel.add(player2Label);
        // Turn indicator at the bottom
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        currentPlayerLabel.setForeground(Color.BLACK);
        currentPlayerLabel.setBackground(Color.GRAY);
        currentPlayerLabel.setOpaque(true);
        // Board panel in the center 
        JPanel boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                tiles[row][col] = new JButton();
                tiles[row][col].setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tiles[row][col].setBackground(Color.GREEN);
                tiles[row][col].setOpaque(true);
                tiles[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                tiles[row][col].setFont(new Font("Arial", Font.BOLD, 50));
                tiles[row][col].setForeground(Color.BLACK);
                tiles[row][col].addActionListener(new CellInputListener(row,col));
                boardPanel.add(tiles[row][col]);
            }
        }
        // Add components to the frame
        frame.add(scorePanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(currentPlayerLabel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private class CellInputListener implements ActionListener {
        private final int row, col;
        public CellInputListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (board[row][col] == null) {
                System.out.println("Clicked on cell: " + row + ", " + col);
                handlePlayerMove(row, col);
                if (isSinglePlayer && currentPlayer == 2) {
                    Timer timer = new Timer(2000, event -> aiMove());
                    timer.setRepeats(false); 
                    timer.start(); 
                }
                passingTurns = 0; // Reset passing turns after a valid move
            }
        }
    }

    // Show the home screen to select game mode
    private void showHomeScreen() {
        String[] options = {"Singleplayer", "Two-Player"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose Game Mode:",
                "Othello",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        switch (choice) {
            case 0 -> isSinglePlayer = true;
            case 1 -> isSinglePlayer = false;
            default -> System.exit(0);
        }
        createGUI();
        initializeBoard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Othello::new);
    }

    
}
