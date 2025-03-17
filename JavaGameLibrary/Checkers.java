import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Checkers extends JFrame {
    private final int TILE_SIZE = 50;
    private final int GRID_SIZE = 8;
    private final Piece[][] board = new Piece[GRID_SIZE][GRID_SIZE];
    private final JButton[][] tiles = new JButton[GRID_SIZE][GRID_SIZE];
    
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
                        board[row][col] = new Piece(1, false); // Player 1 piece
                    } else if (row >= GRID_SIZE - 3) {
                        board[row][col] = new Piece(2, false); // Player 2 piece
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
                    tile.addAddActionListener(new CellInputListener(row, col));
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
            // Handle player moves here
        }

    
}

