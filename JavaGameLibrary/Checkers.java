import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Checkers extends JFrame {
    // Constants for board and tile size
    private final int TILE_SIZE = 50;
    private final int GRID_SIZE = 8;
    private final Piece[][] board = new Piece[GRID_SIZE][GRID_SIZE];
    private final JButton[][] tiles = new JButton[GRID_SIZE][GRID_SIZE];
    // Game state variables
    private int[] pieceToMove = null;
    private int[] placeToMove = null;
    private int player1Score = 0;
    private int player2Score = 0;
    private int currentPlayer = 1;
    private boolean isSinglePlayer = false;
    // Score and player labels
    private final JLabel currentPlayerLabel = new JLabel("Player 1's Turn", SwingConstants.CENTER);
    private final JLabel player1Label = new JLabel("Player 1: 0", SwingConstants.CENTER);
    private final JLabel player2Label = new JLabel("Player 2: 0", SwingConstants.CENTER);
    
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
    
    // Setup player pieces on the board
    private void initializeBoard() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if ((row + col) % 2 == 1) {
                    if (row < 3) {
                        board[row][col] = new Piece(2, false); 
                    } else if (row >= GRID_SIZE - 3) {
                        board[row][col] = new Piece(1, false); 
                    }
                }
            }
        }
    }

    // Move piece on the game board and clear any captured pieces
    private void movePiece(int[] pos1, int[] pos2) {
        if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
            int midRow = (pos1[0] + pos2[0]) / 2;
            int midCol = (pos1[1] + pos2[1]) / 2;
            board[midRow][midCol] = null; 
        }
        board[pos2[0]][pos2[1]] = board[pos1[0]][pos1[1]];
        board[pos1[0]][pos1[1]] = null;    
    }

    // Update the UI to reflect the move made on the board
    private void updateUI(int[] pos1, int[] pos2) {
        if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
            int midRow = (pos1[0] + pos2[0]) / 2;
            int midCol = (pos1[1] + pos2[1]) / 2;
            tiles[midRow][midCol].setText(""); 
        }
        tiles[pos2[0]][pos2[1]].setText(board[pos2[0]][pos2[1]].isKing() ? "♔" : "●"); 
        tiles[pos2[0]][pos2[1]].setForeground(currentPlayer == 1 ? Color.RED : Color.ORANGE);
        tiles[pos1[0]][pos1[1]].setText("");
    }

    // Check if the player has any extra moves available after a capture
    private boolean checkExtraMoves(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        Piece piece = board[row][col];
        
        if (piece == null) return false; // No piece, no extra move 
        int[][] directions;
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
            if (isWithinBounds(newRow, newCol)) {
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
    
    // Validate the move based on game rules
    private boolean isValidMove(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        Piece piece = board[row1][col1];
        // Check if the move is within bounds
        if (!isWithinBounds(row2, col2)) {
            return false;
        }
        // Check if the piece is exist
        if (board[row1][col1] == null) {
            return false;
        }
        // Check if the piece belongs to the current player
        if (piece.getPlayer() != currentPlayer) {
            return false;
        }
        // Check if the destination is empty
        if (board[row2][col2] != null) {
            return false;
        }
        // Check if the move is a valid diagonal move
        if (!piece.isKing() && currentPlayer == 1 && row1 < row2) {
            return false;
        } else if (!piece.isKing() && currentPlayer == 2 && row1 > row2) {
            return false;
        }
        // Check if the move is a simple one-space move
        if (Math.abs(row2 - row1) == 1 && Math.abs(col2 - col1) == 1) {
            return true;
        }
        // Check if the move is a capture move
        if (Math.abs(row2 - row1) == 2 && Math.abs(col2 - col1) == 2) {
            int midRow = (row1 + row2) / 2;
            int midCol = (col1 + col2) / 2;
            Piece midPiece = board[midRow][midCol];
            if (midPiece != null && midPiece.getPlayer() != piece.getPlayer()) {
                return true;
            }
        }
        return false;
    }

    // Check if the position is within the bounds of the board
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    // Swap the current player & update the UI accordingly
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

    // Handle player moves and update the game state accordingly
    private void handlePlayerMove(int[] pos1, int[] pos2) {
        movePiece(pos1, pos2);
        updateUI(pos1, pos2);
        if (checkGameState()) {
            return;
        }
        // Only check for extra moves if the move is a capture
        if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
            if (!checkExtraMoves(pos2)) {
                swapPlayer(); 
            }    
        } else {
            swapPlayer(); 
        }
        activateKing(pos2[0], pos2[1]);
    }

    // Handle AI moves and update the game state accordingly
    private void aiMove() {
        int[][] bestMove = determineMove();
        if (bestMove != null) {
            int[] pos1 = bestMove[0];
            int[] pos2 = bestMove[1];
            movePiece(pos1, pos2);
            updateUI(pos1, pos2);
            if (checkGameState()) {
                return;
            }
            // Only check for extra moves if the move is a capture
            if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
                if (checkExtraMoves(pos2)) {
                    Timer timer = new Timer(2500, event -> aiMove());
                    timer.setRepeats(false); 
                    timer.start();
                } else {
                    swapPlayer(); // If no extra moves, switch player
                }    
            } else {
                swapPlayer(); // If not a capture, switch player
            }
            activateKing(pos2[0], pos2[1]);
        }
    }
    // Create a deep copy of the board to simulate moves without affecting the original board
    private Piece[][] copyBoard(Piece[][] board) {
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

    // Algorithm to determine and score the best move for the AI (with basic heuristics)
    // The AI will prioritize capturing moves, then protecting vulnerable pieces, and finally normal moves
    // If no other options exist, it will select a random vulnerable move
    private int[][] determineMove() {
        ArrayList<int[][]> captureMoves = new ArrayList<>();
        ArrayList<int[][]> normalMoves = new ArrayList<>();
        ArrayList<int[][]> vulnerableMoves = new ArrayList<>();
        ArrayList<int[][]> protectiveMoves = new ArrayList<>();

        getAllPossibleMoves(2, captureMoves, normalMoves, vulnerableMoves, protectiveMoves);

        // Check if there are any capture moves available, if so, prioritize them
        if (!captureMoves.isEmpty()) return captureMoves.get(new Random().nextInt(captureMoves.size()));
    
        // Try to protect vulnerable pieces if possible
        if (!protectiveMoves.isEmpty()) return protectiveMoves.get(new Random().nextInt(protectiveMoves.size()));

        // If no capture moves, check for normal moves, prioitizing non-vulnerable ones
        ArrayList<int[][]> safeMoves = new ArrayList<>();
        for (int[][] move : normalMoves) {
            if (!isMoveVulnerable(move)) {
                safeMoves.add(move);
            }
        }
        if (!safeMoves.isEmpty()) return safeMoves.get(new Random().nextInt(safeMoves.size()));
        
        // If no safe moves, return a random vulnerable move (since the only available moves are vulnerable)
        return vulnerableMoves.get(new Random().nextInt(vulnerableMoves.size()));
    }
    
    // Get all possible moves for the current player, including capture, normal, vulnerable, and protective moves
    private void getAllPossibleMoves(int player, ArrayList<int[][]> captureMoves, 
            ArrayList<int[][]> normalMoves, ArrayList<int[][]> vulnerableMoves, ArrayList<int[][]> protectiveMoves) {
        protectiveMoves.addAll(getVulnerableComputerPieces()); 
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    addPossibleMovesForPiece(piece, row, col, captureMoves, normalMoves, vulnerableMoves);
                }
            }
        }        
    }
     
    // Add possible moves for a piece based on its type and position
    private void addPossibleMovesForPiece(Piece piece, int row, int col, 
                                      ArrayList<int[][]> captureMoves, 
                                      ArrayList<int[][]> normalMoves, 
                                      ArrayList<int[][]> vulnerableMoves) {
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
                int[][] move = new int[][]{{row, col}, {newRow, newCol}};
                if (isMoveVulnerable(move)) {
                    vulnerableMoves.add(move);
                } else {
                    normalMoves.add(move);
                }
            }

            int jumpRow = row + (dir[0] * 2);
            int jumpCol = col + (dir[1] * 2);
 
            if (isValidMove(new int[]{row, col}, new int[]{jumpRow, jumpCol})) {
                captureMoves.add(new int[][]{{row, col}, {jumpRow, jumpCol}});
            }
        }
    }

    // Simulate a move on a copy of the board to check for vulnerabilities
    private void simulateMove(Piece[][] boardCopy, int[] pos1, int[] pos2) {
        Piece movingPiece = boardCopy[pos1[0]][pos1[1]];
        boardCopy[pos1[0]][pos1[1]] = null;
        boardCopy[pos2[0]][pos2[1]] = movingPiece;
        // Check if a piece is captured, remove it from the copied board
        if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
            int midRow = (pos1[0] + pos2[0]) / 2;
            int midCol = (pos1[1] + pos2[1]) / 2;
            boardCopy[midRow][midCol] = null; 
        }
        // Check if the piece becomes a king
        if (movingPiece.getPlayer() == 1 && pos2[0] == 0) {
            movingPiece.makeKing(); 
        } else if (movingPiece.getPlayer() == 2 && pos2[0] == GRID_SIZE - 1) {
            movingPiece.makeKing(); 
        }
    }

    // Check if the move makes a piece vulnerable to being captured 
    private boolean isMoveVulnerable(int[][] move) {
        int newRow = move[1][0];
        int newCol = move[1][1];
    
        Piece[][] boardCopy = copyBoard(board);
        simulateMove(boardCopy, move[0], move[1]);
    
        // Check if the moved piece is vulnerable in its new location
        if (isPositionVulnerable(boardCopy, newRow, newCol)) {
            return true;
        }
    
        // Check if any other computer piece are vulnerable
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = boardCopy[row][col];
                if (piece != null && piece.getPlayer() == 2) {
                    if (isPositionVulnerable(boardCopy, row, col)) {
                        return true;
                    }
                }
            }
        }
    
        return false;
    }

    // Check if a position is vulnerable to being captured by the opponent
    private boolean isPositionVulnerable(Piece[][] boardState, int row, int col) {
        for (int[] dir : new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}) {
            int attackerRow = row + dir[0];
            int attackerCol = col + dir[1];
            int landingRow = row - dir[0];
            int landingCol = col - dir[1];
    
            if (isWithinBounds(attackerRow, attackerCol) && isWithinBounds(landingRow, landingCol)) {
                Piece attacker = boardState[attackerRow][attackerCol];
                if (attacker != null && attacker.getPlayer() == 1 && boardState[landingRow][landingCol] == null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Get list of moves for vulnerable pieces of the computer player that can escape capture
    private ArrayList<int[][]> getVulnerableComputerPieces() {
        ArrayList<int[][]> vulnerablePiecesWithEscapeMoves = new ArrayList<>();
    
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == 2) { // Computer's pieces
                    int[][] directions = piece.isKing()
                        ? new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}
                        : new int[][]{{1, -1}, {1, 1}}; // Player 2 moves down
    
                    boolean isVulnerable = false;
    
                    // Check if any opponent can capture this piece
                    for (int[] dir : new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}) {
                        int attackerRow = row + dir[0];
                        int attackerCol = col + dir[1];
                        int landingRow = row - dir[0];
                        int landingCol = col - dir[1];
    
                        if (isWithinBounds(attackerRow, attackerCol) && isWithinBounds(landingRow, landingCol)) {
                            Piece attacker = board[attackerRow][attackerCol];
                            if (attacker != null && attacker.getPlayer() == 1 && board[landingRow][landingCol] == null) {
                                isVulnerable = true;
                            }
                        }
                    }
    
                    if (isVulnerable) {
                        for (int[] dir : directions) {
                            int newRow = row + dir[0];
                            int newCol = col + dir[1];
                            int[][] move = new int[][]{{row, col}, {newRow, newCol}};
                            if (isValidMove(new int[]{row, col}, new int[]{newRow, newCol}) && !isMoveVulnerable(move)) {
                                vulnerablePiecesWithEscapeMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
    
        return vulnerablePiecesWithEscapeMoves;
    }
    
    // Create the GUI for the game
    private void createGUI() {
        // Set up the main frame
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

    // Show the home screen to select game mode
    private void showHomeScreen() {
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

    // Listener for tile clicks to handle player input
    private class CellInputListener implements ActionListener {
        private final int row, col;

        public CellInputListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // If the player clicks on their own piece, allow re-selection
            if (board[row][col] != null && board[row][col].getPlayer() == currentPlayer) {
                pieceToMove = new int[]{row, col}; // Update selected piece
                placeToMove = null; // Reset destination selection
                System.out.println("Selected piece to move: " + row + ", " + col);
            } 
            // If a piece is already selected, set the destination
            else if (pieceToMove != null) {
                placeToMove = new int[]{row, col};
                System.out.println("Destination selected: " + row + ", " + col);
                // Validate and execute the move
                if (isValidMove(pieceToMove, placeToMove)) {
                    handlePlayerMove(pieceToMove, placeToMove);
                    pieceToMove = null;
                    placeToMove = null;
                    // Trigger AI move if in single-player mode
                    if (isSinglePlayer && currentPlayer == 2) {
                        Timer timer = new Timer(2000, event -> aiMove()); // 1000ms = 1 second delay
                        timer.setRepeats(false); // Ensure the timer only runs once
                        timer.start();
                    }
                }
            }
        }

    }

    // Check the game state to determine if a player has won or if the game is over
    private boolean checkGameState() {  
        int player1Pieces = 0;
        int player2Pieces = 0;
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
            return true;
        } else if (player2Pieces == 0) {
            player1Score++;
            handleGameOver("Player 1 wins!");
            return true; 
        }
        return false; 
    }

    // Display game over message and prompt for replay or the main menu
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

    // Reset the game state, pieces, board, UI, and update scores
    private void resetGame() {
        currentPlayer = 1;

        // Clear the board array
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                board[row][col] = null; // Clear the board
            }
        }

        // Reinitialize the board with pieces
        initializeBoard();

        // Update the score labels
        player1Label.setText("Player 1: " + player1Score);
        player2Label.setText("Player 2: " + player2Score);

        // Update the UI tiles
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
                } else {
                    tiles[row][col].setText(""); // Ensure non-playable tiles are cleared
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Checkers::new);
    }
}