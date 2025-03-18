import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Checkers extends JFrame {
    private final int TILE_SIZE = 50;
    private final int GRID_SIZE = 8;
    private final Piece[][] board = new Piece[GRID_SIZE][GRID_SIZE];
    private final JButton[][] tiles = new JButton[GRID_SIZE][GRID_SIZE];
    private int currentPlayer = 1;
    private int[] pieceToMove = null;
    private int[] placeToMove = null;
    
    public Checkers() {
        setTitle("Checkers Game");
        setSize(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeGame();
        setupUI();
    }

    // Inner Piece object class to manage player pieces & king status
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

    private void initializeGame() {
        setupBoard();
    }

    private void setupBoard() {
        // Clear any pre-existing board pieces
        clearBoard();
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
    private void clearBoard() {
        // Iterate over the board and clear all cells
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                board[row][col] = null;
            }
        }
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
    

    private void movePiece(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        board[row2][col2] = board[row1][col1];
        board[row1][col1] = null;
        // Update the UI
        tiles[row2][col2].setText("●");
        tiles[row2][col2].setForeground(currentPlayer == 1 ? Color.RED : Color.ORANGE);
        tiles[row1][col1].setText("");
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
    private void validMove(int[] pos1, int[] pos2) {
        int row1 = pos1[0];
        int col1 = pos1[1];
        int row2 = pos2[0];
        int col2 = pos2[1];
        Piece piece = board[row1][col1];
        if (piece.getPlayer() != currentPlayer) {
            System.out.println("Invalid move: Not your piece!");
            return;
        }
        if (board[row2][col2] != null) {
            System.out.println("Invalid move: Destination is occupied!");
            return;
        }
        if (!piece.isKing() && currentPlayer == 1 && row1 < row2) {
            System.out.println("Invalid move: Only kings can move backwards!");
            return;
        } else if (!piece.isKing() && currentPlayer == 2 && row1 > row2) {
            System.out.println("Invalid move: Only kings can move backwards!");
            return;
        }
        // Check if the move is a simple one space move
        if (Math.abs(row2 - row1) == 1 && Math.abs(col2 - col1) == 1) {
            movePiece(pos1, pos2);
            currentPlayer = currentPlayer == 1 ? 2 : 1; // Switch player
        // Check if the move is a single capture move
        } else if (Math.abs(row2-row1) == 2 && Math.abs(col2-col1) == 2) {
            int midRow = (row1 + row2) / 2;
            int midCol = (col1 + col2) / 2;
            if (board[midRow][midCol] == null) {
                System.out.println("Invalid move: No piece to capture!");
                return;
            }
            if (board[midRow][midCol].getPlayer() == currentPlayer) {
                System.out.println("Invalid move: Cannot capture your own piece!");
                return;
            }
            if (board[row2][col2] != null && board[row2][col2].getPlayer() == currentPlayer) {
                System.out.println("Invalid move: Cannot capture your own piece!");
                return;
            }
            movePiece(pos1, pos2);
            board[midRow][midCol] = null; // Remove captured piece
            tiles[midRow][midCol].setText(""); // Update UI
            if (!checkExtraMoves(pos2)) {
                currentPlayer = currentPlayer == 1 ? 2 : 1; // Switch player
            }
        }
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel boardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton tile = new JButton();
                tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tile.setOpaque(true);
                tile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                if ((row + col) % 2 == 0) {
                    tile.setBackground(Color.WHITE);
                } else {
                    tile.setBackground(Color.BLACK);
                    tile.addActionListener(new CellInputListener(row, col));
                }
                if (board[row][col] != null) {
                    Piece piece = board[row][col];
                    tile.setText("●"); 
                    tile.setFont(new Font("Arial", Font.BOLD, 30));
                    tile.setHorizontalAlignment(JTextField.CENTER);
                    tile.setVerticalAlignment(JTextField.CENTER);
                    tile.setForeground(piece.getPlayer() == 1 ? Color.RED : Color.ORANGE);
                }
                tiles[row][col] = tile;
                boardPanel.add(tile);
            }
        }
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private class CellInputListener implements ActionListener {
        private int row, col;

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
                validMove(pieceToMove, placeToMove);

                // Reset selection for next move
                pieceToMove = null;
                placeToMove = null;
            }
        }

    }
    
    
}

