import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class Checkers extends JFrame {
    // Constants for board and tile size
    private final int TILE_SIZE = 50;
    private final int GRID_SIZE = 8;
    private final Piece[][] board = new Piece[GRID_SIZE][GRID_SIZE];
    private final JButton[][] tiles = new JButton[GRID_SIZE][GRID_SIZE];
    // Game state variables
    private int player1Score = 0;
    private int player2Score = 0;
    private int totalPieces = 24;
    private int currentPlayer = 1;
    private int[] pieceToMove = null;
    private int[] placeToMove = null;
    private boolean isSinglePlayer = false;
    // Score and player labels
    private final JLabel currentPlayerLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
    private final JLabel player1Label = new JLabel("Player 1: " + player1Score, SwingConstants.CENTER);
    private final JLabel player2Label = new JLabel("Player 2: " + player2Score, SwingConstants.CENTER);
    
    public Checkers() {
        showHomeScreen();
    }

    // Piece object class to manage player piece ownership & king status
    private static class Piece {
        private final int player;
        private boolean isKing;

        public Piece(int player, boolean isKing) {
            this.player = player;
            this.isKing = isKing;
        }

        public int getPlayer() {
            return player;
        }

        public boolean isKing() {
            return isKing;
        }

        public void makeKing() {
            isKing = true;
        }
    }

    private void initializeBoard() {
        // Setup player 1 and player 2 pieces
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        board[row][col] = new Piece(2, false); // Player 1 piece
                    } else if (row >= GRID_SIZE - 3) {
                        board[row][col] = new Piece(1, false); // Player 2 piece
                    }
                }
            }
        }
    }

    private void movePiece(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        int midRow = (row1 + row2) / 2;
        int midCol = (col1 + col2) / 2;
        board[row2][col2] = board[row1][col1];
        board[row1][col1] = null;
        // Clear captured piece from game memory 
        board[midRow][midCol] = null; 
        totalPieces--; // Decrement total pieces
    }

    private void updateUI(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        int midRow = (row1 + row2) / 2;
        int midCol = (col1 + col2) / 2;
        tiles[row2][col2].setText(board[row2][col2].isKing() == true ? "♔" : "●"); 
        tiles[row2][col2].setForeground(currentPlayer == 1 ? Color.RED : Color.ORANGE);
        tiles[row1][col1].setText("");
        // Clear the UI tile of the captured piece
        tiles[midRow][midCol].setText(""); 

    }

    private boolean checkExtraMoves(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        Piece piece = board[row][col];
        
        if (piece == null) return false; // No piece, no extra move 
        int[][] directions;
        // Determine which directions the current piece can jump based on its type
        if (piece.isKing()) {
            directions = new int[][]{{-2, -2}, {-2, 2}, {2, -2}, {2, 2}}; // Kings move both directions
        } else if (piece.getPlayer() == 1) {
            directions = new int[][]{{-2, -2}, {-2, 2}}; // Player 1 moves upwards
        } else {
            directions = new int[][]{{2, -2}, {2, 2}}; // Player 2 moves downwards
        }
        
        // Check for possible jumps in all directions
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int midRow = row + dir[0] / 2;
            int midCol = col + dir[1] / 2;
    
            // Check if new position is within bounds and empty
            if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                if (board[newRow][newCol] == null) { // Destination is empty
                    Piece midPiece = board[midRow][midCol];
                    if (midPiece != null && midPiece.getPlayer() != piece.getPlayer()) {
                        return true; // Valid jump available
                    }
                }
            }
        }
        return false;
    }
    
    /* 
    * Six possible moves in Checkers:
    * 1. Normal move: 1 step diagonally
    * 2. Capture move: 2 steps diagonally to capture opponent's piece
    * 3. Multiple capture move: Capture multiple opponent's pieces in one turn (one direction)
    * 4. King move: 1 step diagonally in any direction
    * 5. King capture move: 2 steps diagonally in any direction to capture opponent's piece
    * 6. King multiple capture move: Capture multiple opponent's pieces in one turn (any direction)
    */ 
    private boolean isValidMove(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        Piece piece = board[row1][col1];
        if (piece.getPlayer() != currentPlayer) {
            System.out.println("Invalid move: Not your piece!");
            return false;
        }
        if (board[row2][col2] != null) {
            System.out.println("Invalid move: Destination is occupied!");
            return false;
        }
        if (!piece.isKing() && currentPlayer == 1 && row1 < row2) {
            System.out.println("Invalid move: Only kings can move backwards!");
            return false;
        } else if (!piece.isKing() && currentPlayer == 2 && row1 > row2) {
            System.out.println("Invalid move: Only kings can move backwards!");
            return false;
        }
        // Check if the move is a simple one space move
        if (Math.abs(row2 - row1) == 1 && Math.abs(col2 - col1) == 1) {
            movePiece(pos1, pos2);
            updateUI(pos1, pos2);
            swapPlayer();
        // Check if the move is a single capture move
        } else if (Math.abs(row2-row1) == 2 && Math.abs(col2-col1) == 2) {
            movePiece(pos1, pos2);
            updateUI(pos1, pos2);
            checkGameState(); // Check if the game is over
            if (!checkExtraMoves(pos2)) {
                swapPlayer(); // If no extra moves, switch player
            }
        }
        activateKing(row2, col2);
        return true;
    }

    // Swap the current player
    private void swapPlayer() {
        currentPlayer = currentPlayer == 1 ? 2 : 1;
        currentPlayerLabel.setText("Player " + currentPlayer + "'s Turn");
        currentPlayerLabel.setForeground(currentPlayer == 1 ? Color.RED : Color.ORANGE);
    }

    // Activate king status if a piece reaches the opposite end of the board
    private void activateKing(int row, int col) {
        Piece piece = board[row][col];
        if (piece.getPlayer() == 1 && row == 0) {
            piece.makeKing();
            tiles[row][col].setText("♔");
        } else if (piece.getPlayer() == 2 && row == GRID_SIZE - 1) {
            piece.makeKing();
            tiles[row][col].setText("♔");
        }
    }

    private Piece[][] copyBoard() {
        Piece[][] newBoard = new Piece[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] != null) {
                    newBoard[row][col] = new Piece(board[row][col].getPlayer(), board[row][col].isKing());
                }
            }
        }
        return newBoard;
    }

    private int evaluateBoard(Piece[][] board) {
        int score = 0;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null) {
                    if (piece.getPlayer() == 1) {
                        score += (piece.isKing() ? 3 : 1);
                    } else if (piece.getPlayer() == 2) {
                        score -= (piece.isKing() ? 3 : 1);
                    }
                }
            }
        }
    
        return score;
    }
    
    private ArrayList<int[][]> getAllPossibleMoves() {
        ArrayList<int[][]> possibleMoves = new ArrayList<>();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == currentPlayer) {
                    addPossibleMovesForPiece(piece, row, col, possibleMoves);
                }
            }
        }
        return possibleMoves;
    }


    private void addPossibleMovesForPiece(Piece piece, int row, int col, ArrayList<int[][]> possibleMoves) {
        int[][] directions;
        if (piece.isKing()) {
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; // Kings move both directions
        } else if (piece.getPlayer() == 1) {
            directions = new int[][]{{-1, -1}, {-1, 1}}; // Player 1 moves upwards
        } else {
            directions = new int[][]{{1, -1}, {1, 1}}; // Player 2 moves downwards
        }
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isValidMove(new int[]{row, col}, new int[]{newRow, newCol})) {
                possibleMoves.add(new int[][]{{row, col}, {newRow, newCol}});
            }
            // Check for captures
            newRow = row + (dir[0] * 2);
            newCol = col + (dir[1] * 2);
            if (isValidMove(new int[]{row, col}, new int[]{newRow, newCol})) {
                possibleMoves.add(new int[][]{{row, col}, {newRow, newCol}});
            }
        }
    }

    private void createGUI() {
        // Window frame setup
        JFrame frame = new JFrame("Checkers Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(600,600);
        frame.setLayout(new BorderLayout());

        // Score panel at the top
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.setBackground(Color.GRAY);

        // Player score labels
        player1Label.setFont(new Font("Arial", Font.BOLD, 20));
        player2Label.setFont(new Font("Arial", Font.BOLD, 20));
        player1Label.setForeground(Color.RED);
        player2Label.setForeground(Color.ORANGE);
        scorePanel.add(player1Label);
        scorePanel.add(player2Label);

        // Turn label at the bottom
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        currentPlayerLabel.setForeground(Color.RED);
        currentPlayerLabel.setOpaque(true);
        currentPlayerLabel.setBackground(Color.GRAY);

        // Main panel and board panel setup
        JPanel boardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton tile = new JButton();
                tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tile.setOpaque(true);
                tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                tile.setFont(new Font("Arial Unicode MS", Font.BOLD, 30));
                if ((row + col) % 2 == 0) {
                    tile.setBackground(Color.WHITE);
                } else {
                    tile.setBackground(Color.BLACK);
                    tile.addActionListener(new CellInputListener(row, col));
                }
                if (board[row][col] != null) {
                    Piece piece = board[row][col];
                    tile.setText("●"); 
                    tile.setFont(new Font("Arial Unicode MS", Font.BOLD, 30));
                    tile.setHorizontalAlignment(JTextField.CENTER);
                    tile.setVerticalAlignment(JTextField.CENTER);
                    tile.setForeground(piece.getPlayer() == 1 ? Color.RED : Color.ORANGE);
                }
                tiles[row][col] = tile;
                boardPanel.add(tile);
            }
        }

        // Add panels to the main frame
        frame.add(scorePanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(currentPlayerLabel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void showHomeScreen() {
        // Implement the main menu screen here
        String[] options = {"Singleplayer", "Two-Player"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose Game Mode:",
                "Checkers Game",
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
        initializeBoard();
        createGUI();
    }

    private class CellInputListener implements ActionListener {
        private final int row, col;

        public CellInputListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (pieceToMove == null && board[row][col] != null) {
                // First click: Select piece to move
                pieceToMove = new int[]{row, col};
                System.out.println("Selected piece to move: " + row + ", " + col);
            } else if (pieceToMove != null) {
                // Second click: Select destination
                placeToMove = new int[]{row, col};
                System.out.println("Destination selected: " + row + ", " + col);

                // Now we have two clicks, validate the move
                isValidMove(pieceToMove, placeToMove);

                // Reset selection for next move
                pieceToMove = null;
                placeToMove = null;
            }
            
        }

    }

    private void checkGameState() {
        int player1Pieces = 0;
        int player2Pieces = 0;
        if (totalPieces > 12) {
            return; // Game is not over
        }
        // Count the number of pieces each player has remaining
        for (Piece[] row : board) {
            for (Piece piece : row) {
                if (piece != null && piece.getPlayer() == 1) {
                    player1Pieces++;    
                } else if (piece != null && piece.getPlayer() == 2) {
                    player2Pieces++;
                }
            }
        }
        if (player1Pieces == 0) {
            player2Score++;
            handleGameOver("Player 2 wins!");
        } else if (player2Pieces == 0) {
            player1Score++;
            handleGameOver("Player 1 wins!");
        }
    }

    // Display game over message and prompt for replay or main menu
    private void handleGameOver(String message) {
        SwingUtilities.invokeLater(() -> {
                String[] options = {"Replay", "Main Menu"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
        
                if (choice == 0) {
                    resetGame();
                } else {
                    dispose(); // Close the current window
                    new GameLauncher(); // Assuming GameLauncher is the main menu class
            }
        });
    }

    // Reset the game state, pieces, board, UI, and update scoreboard
    private void resetGame() {
        totalPieces = 24;
        currentPlayer = 1;
        pieceToMove = null;
        placeToMove = null;
        initializeBoard();
        player1Label.setText("Player 1: " + player1Score);
        player2Label.setText("Player 2: " + player2Score);
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (board[row][col] != null) {
                        Piece piece = board[row][col];
                        tiles[row][col].setText("●"); 
                        tiles[row][col].setForeground(piece.getPlayer() == 1 ? Color.RED : Color.ORANGE);
                    } else {
                        tiles[row][col].setText("");
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Checkers::new);
    }
}