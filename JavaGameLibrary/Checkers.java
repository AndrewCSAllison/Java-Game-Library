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

    // Class to manage the score and move of the AI
    private class MoveScore {
        private final int score;
        private final int[][] move;

        public MoveScore(int score, int[][] move) {
            this.score = score;
            this.move = move;
        }
        public int getScore() {
            return score;
        }
        public int[][] getMove() {
            return move;
        }
        @Override
        public String toString() {
            String moveString = "null";
            if (move != null && move.length == 2) {
                moveString = move[0][0] + "," + move[0][1] + " -> " + move[1][0] + "," + move[1][1];
            }
            return "Score: " + score + ", Move: " + moveString;
        }
    }
    
    // Setup player pieces on the board
    private void initializeBoard() {
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
    
    // Validate the move based on game rules
    private boolean isValidMove(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        Piece piece = board[row1][col1];
        // Check if the move is within bounds
        if (row2 < 0 || row2 >= GRID_SIZE || col2 < 0 || col2 >= GRID_SIZE) {
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

    // Determine which directions the current piece can jump based on its type
    private int[][] getDirections(Piece piece) {
        // Kings move in both directions
        if (piece.isKing()) { 
            return new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; 
        // Player 1 moves upwards 
        } else if (piece.getPlayer() == 1) { 
            return new int[][]{{-1, -1}, {-1, 1}}; 
        // Player 2 moves downwards
        } else { 
            return new int[][]{{1, -1}, {1, 1}}; 
        }
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
            System.out.println("Extra moves? " + checkExtraMoves(pos2));
            System.out.println(currentPlayer);
            if (!checkExtraMoves(pos2)) {
                swapPlayer(); 
            }    
        } else {
            swapPlayer(); 
        }
        activateKing(pos2[0], pos2[1]);
        System.out.println(currentPlayer);
    }

    // Handle AI moves and update the game state accordingly
    private void aiMove() {
        MoveScore bestMove = minimax(3, true);
        if (bestMove.getMove() != null) {
            int[] pos1 = bestMove.getMove()[0];
            int[] pos2 = bestMove.getMove()[1];
            movePiece(pos1, pos2);
            updateUI(pos1, pos2);
            if (checkGameState()) {
                return;
            }
            // Only check for extra moves if the move is a capture
            if (Math.abs(pos2[0] - pos1[0]) == 2 && Math.abs(pos2[1] - pos1[1]) == 2) {
                if (checkExtraMoves(pos2)) {
                    Timer timer = new Timer(500, event -> aiMove()); // 1000ms = 1 second delay
                    timer.setRepeats(false); // Ensure the timer only runs once
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
    // Create a deep copy of the board for minimax evaluation
    // to avoid modifying the original board during the evaluation process
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
    private MoveScore minimax(int depth, boolean isMaximizing) {
        if (depth == 0 || checkGameState()) {
            return new MoveScore(evaluateBoard(board), null);
        }
    
        ArrayList<int[][]> bestMoves = new ArrayList<>();
        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    
        ArrayList<int[][]> possibleMoves = getAllPossibleMoves(isMaximizing ? 2 : 1);
        
        if (possibleMoves.isEmpty()) {
            return new MoveScore(evaluateBoard(board), null); // No moves available, return evaluation
        }
    
        for (int[][] move : possibleMoves) {
            Piece[][] newBoard = copyBoard();
            simulateMove(newBoard, move[0], move[1]);
            
            int eval = minimax(depth - 1, !isMaximizing).getScore();
            if ((isMaximizing && eval > bestScore) || (!isMaximizing && eval < bestScore)) {
                bestScore = eval;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (eval == bestScore) {
                bestMoves.add(move);
            }
        }
        
        // Choose one random move out of the equally scoring ones
        int[][] bestMove = bestMoves.isEmpty() ? null : bestMoves.get(new Random().nextInt(bestMoves.size()));
        return new MoveScore(bestScore, bestMove);
    }
    
   


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

    private int evaluateBoard(Piece[][] boardCopy) {
        int score = 0;
    
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = boardCopy[row][col];
                if (piece != null) {
                    int pieceValue = piece.isKing() ? 7 : 3;
                    int positionValue = 0;
    
                    // Encourage advancing pieces
                    if (!piece.isKing()) {
                        positionValue = (piece.getPlayer() == 2) ? row : (GRID_SIZE - 1 - row);
                        positionValue *= 1.5;
                    }
    
                    // Calculate total value for the piece
                    int totalValue = pieceValue + positionValue;
                    // Add or subtract based on the player
                    if (piece.getPlayer() == 2) {
                        score += totalValue; // AI's pieces
                    } else {
                        score -= totalValue; // Opponent's pieces
                    }
                }
            }
        }
        return score;
    }
    
    // Get all possible moves for the current player, prioritizing captures
    private ArrayList<int[][]> getAllPossibleMoves(int player) {
        ArrayList<int[][]> captureMoves = new ArrayList<>();
        ArrayList<int[][]> normalMoves = new ArrayList<>();
    
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getPlayer() == player) {
                    addPossibleMovesForPiece(piece, row, col, captureMoves, normalMoves);
                }
            }
        }
        if (!captureMoves.isEmpty()) {
            return captureMoves; // Prioritize captures
        }
        return normalMoves;
    }
    
    // Add possible moves for a piece based on its type and position
    private void addPossibleMovesForPiece(Piece piece, int row, int col, ArrayList<int[][]> captureMoves, ArrayList<int[][]> normalMoves) {
        int[][] directions = getDirections(piece);
        // Iterate over all possible moves
        for (int[] dir : directions) { 
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            // Check for normal moves 
            if (isValidMove(new int[]{row, col}, new int[]{newRow, newCol})) {
                normalMoves.add(new int[][]{{row, col}, {newRow, newCol}});
            }
            int jumpRow = row + (dir[0] * 2);
            int jumpCol = col + (dir[1] * 2);
            // Check for capture moves 
            if (isValidMove(new int[]{row, col}, new int[]{jumpRow, jumpCol})) {
                captureMoves.add(new int[][]{{row, col}, {jumpRow, jumpCol}});
            }
        }
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

    // Listener for tile clicks to handle player input
    private class CellInputListener implements ActionListener {
        private final int row, col;
        // Constructor to initialize row and column for the button
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
                        Timer timer = new Timer(500, event -> aiMove()); // 1000ms = 1 second delay
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
        if (player1Pieces == 0 && getAllPossibleMoves(1).isEmpty()) {
            player2Score++;
            handleGameOver("Player 2 wins!");
            return true;
        } else if (player2Pieces == 0 && getAllPossibleMoves(2).isEmpty()) {
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