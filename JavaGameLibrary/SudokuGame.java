import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class SudokuGame extends JFrame {
    private static final int GRID_SIZE = 9; // 9x9 grid for Sudoku
    private static final int CELL_SIZE = 60; // Size of each cell
    private JTextField[][] cells = new JTextField[GRID_SIZE][GRID_SIZE];
    private int[][] board = new int[GRID_SIZE][GRID_SIZE];
    private int[][] solution = new int[GRID_SIZE][GRID_SIZE];
    private JLabel statusLabel; // Label to display game status

    public SudokuGame() {
        setTitle("Sudoku Game");
        setSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE + 50); // Extra space for status label
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        // Generate a Sudoku puzzle
        generateSudoku();
    }

    private void generateSudoku() {
        // Step 1: Clear the board
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = 0;
            }
        }
    
        // Step 2: Randomly fill the diagonal 3x3 subgrids
        fillDiagonalSubgrids();
    
        // Step 3: Solve the partially filled board to get a valid solution
        solveSudoku(board);
    
        // Step 4: Copy the solved board to the solution array
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(board[i], 0, solution[i], 0, GRID_SIZE);
        }
    
        // Step 5: Remove some numbers to create the puzzle
        int clues = 30; // Adjust difficulty by changing the number of clues
        for (int i = 0; i < GRID_SIZE * GRID_SIZE - clues; i++) {
            int row = (int) (Math.random() * GRID_SIZE);
            int col = (int) (Math.random() * GRID_SIZE);
            if (board[row][col] != 0) {
                board[row][col] = 0;
            } else {
                i--; // Retry if the cell is already empty
            }
        }
    }
    private void fillDiagonalSubgrids() {
        for (int box = 0; box < 3; box++) {
            boolean[] usedNumbers = new boolean[GRID_SIZE + 1]; // Track used numbers
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    int num;
                    do {
                        num = (int) (Math.random() * GRID_SIZE) + 1;
                    } while (usedNumbers[num]);
                    usedNumbers[num] = true;
                    board[box * 3 + i][box * 3 + j] = num;
                }
            }
        }
    }

    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check row
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[row][i] == num) {
                return false;
            }
        }
        // Check column
        for (int i = 0; i < GRID_SIZE; i++) {
            if (board[i][col] == num) {
                return false;
            }
        }
        // Check 3x3 box
        int boxRow = row / 3 * 3;
        int boxCol = col / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[boxRow + i][boxCol + j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField cell = new JTextField();
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setHorizontalAlignment(JTextField.CENTER);

                // Set thicker borders for 3x3 subgrids and outer border
                int top = (row % 3 == 0) ? 3 : 1; // Thicker top border for subgrid boundaries
                int left = (col % 3 == 0) ? 3 : 1; // Thicker left border for subgrid boundaries
                int bottom = (row == GRID_SIZE - 1) ? 3 : 1; // Thicker bottom border for outer boundary
                int right = (col == GRID_SIZE - 1) ? 3 : 1; // Thicker right border for outer boundary

                cell.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                if (board[row][col] != 0) {
                    cell.setText(String.valueOf(board[row][col]));
                    cell.setEditable(false);
                    cell.setBackground(Color.LIGHT_GRAY);
                } else {
                    cell.addActionListener(new CellInputListener(row, col));
                }
                cells[row][col] = cell;
                gridPanel.add(cell);
            }
        }

        // Status label to display game status
        statusLabel = new JLabel("Fill in the numbers to solve the puzzle!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        //mainPanel.add(statusLabel, BorderLayout.SOUTH); //removed, not needed anymore

        // Back to Menu button
        JButton backButton = new JButton("Back to Menu");
        backButton.addActionListener(e -> {
            dispose(); // Close Sudoku window
            new GameLauncher(); // Open main menu
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(backButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

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
            JTextField cell = cells[row][col];
            String input = cell.getText().trim();
            if (input.length() == 1 && Character.isDigit(input.charAt(0))) {
                int num = Integer.parseInt(input);
                if (num >= 1 && num <= 9) {
                    if (num == solution[row][col]) {
                        cell.setBackground(Color.GREEN);
                        statusLabel.setText("Correct! Keep going!");
                    } else {
                        cell.setBackground(Color.RED);
                        statusLabel.setText("Incorrect. Try again!");
                    }
                    checkForCompletion();
                } else {
                    statusLabel.setText("Invalid input. Enter a number between 1 and 9.");
                }
            } else {
                statusLabel.setText("Invalid input. Enter a single digit.");
            }
        }
    }

    private void checkForCompletion() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String text = cells[row][col].getText().trim();
                if (text.isEmpty() || Integer.parseInt(text) != solution[row][col]) {
                    return; // Game is not yet completed
                }
            }
        }
    
        // Show completion message
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Replay", "Main Menu"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Congratulations! You solved the Sudoku puzzle!",
                "Puzzle Solved",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
    
            if (choice == 0) {
                restartGame();
            } else {
                dispose(); // Close the current window
                new GameLauncher(); // Assuming GameLauncher is the main menu class
            }
        });
    }

    private void restartGame() {
        initializeGame();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setText("");
                cells[row][col].setBackground(Color.WHITE);
                cells[row][col].setEditable(true);
            }
        }
        statusLabel.setText("Fill in the numbers to solve the puzzle!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SudokuGame::new);
    }
}