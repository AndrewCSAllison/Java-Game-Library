import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class TicTacToe {
    private char[][] board;
    private char currentPlayer;
    private JButton[][] buttons;
    private JFrame frame;
    private boolean isSinglePlayer;
    private Random random;

    // Match scores
    private int playerXScore = 0;
    private int playerOScore = 0;

    // Score labels as instance variables
    private JLabel playerXLabel;
    private JLabel playerOLabel;

    public TicTacToe() {
        random = new Random();
        showHomeScreen();
    }

    private void initializeBoard() {
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        currentPlayer = 'X'; // Player 1 starts
    }

    private void createGUI() {
        frame = new JFrame("Tic-Tac-Toe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Score Panel at the Top
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new GridLayout(1, 2));
        scorePanel.setBackground(Color.LIGHT_GRAY);

        // Initialize score labels
        playerXLabel = new JLabel("Player X: " + playerXScore, SwingConstants.CENTER);
        playerOLabel = new JLabel("Player O: " + playerOScore, SwingConstants.CENTER);

        // Style score labels
        playerXLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerOLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerXLabel.setForeground(Color.RED); // Red for Player X
        playerOLabel.setForeground(Color.BLUE); // Blue for Player O

        scorePanel.add(playerXLabel);
        scorePanel.add(playerOLabel);

        // Game Board Panel
        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Color.DARK_GRAY);

        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 70)); // Bold and larger font
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(Color.WHITE); // Default button color
                buttons[i][j].setForeground(Color.BLACK); // Default text color
                buttons[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Add border

                int row = i;
                int col = j;
                buttons[i][j].addActionListener(e -> {
                    if (board[row][col] == '-') {
                        buttons[row][col].setText(String.valueOf(currentPlayer));
                        buttons[row][col].setForeground(currentPlayer == 'X' ? Color.RED : Color.BLUE);
                        buttons[row][col].setBackground(currentPlayer == 'X' ? Color.PINK : Color.CYAN);
                        board[row][col] = currentPlayer;

                        if (checkForWin(currentPlayer)) {
                            handleGameOver("Player " + currentPlayer + " wins!", currentPlayer);
                        } else if (isBoardFull()) {
                            handleGameOver("The game is a draw!", '-');
                        } else {
                            changePlayer();
                            if (isSinglePlayer && currentPlayer == 'O') {
                                computerMove();
                            }
                        }
                    }
                });
                boardPanel.add(buttons[i][j]);
            }
        }

        frame.add(scorePanel, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void showHomeScreen() {
        String[] options = {"Singleplayer", "Two-Player"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose Game Mode:",
                "Tic-Tac-Toe",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            isSinglePlayer = true;
        } else if (choice == 1) {
            isSinglePlayer = false;
        } else {
            System.exit(0);
        }
        initializeBoard();
        createGUI();
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkForWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true; // Row win
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true; // Column win
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true; // Diagonal win
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true; // Anti-diagonal win
        }
        return false;
    }

    private void changePlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private void resetGame() {
        initializeBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(Color.WHITE);
                buttons[i][j].setForeground(Color.BLACK);
            }
        }
        currentPlayer = 'X';
    }

    private void computerMove() {
        if (random.nextInt(10) == 0) {
            makeRandomMove();
        } else {
            if (!tryToWinOrBlock('O')) {
                if (!tryToWinOrBlock('X')) {
                    makeRandomMove();
                }
            }
        }
        if (checkForWin('O')) {
            handleGameOver("Computer wins!", 'O');
        } else if (isBoardFull()) {
            handleGameOver("The game is a draw!", '-');
        } else {
            changePlayer();
        }
    }

    private boolean tryToWinOrBlock(char player) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = player;
                    if (checkForWin(player)) {
                        board[i][j] = 'O';
                        buttons[i][j].setText("O");
                        buttons[i][j].setForeground(Color.BLUE);
                        buttons[i][j].setBackground(Color.CYAN);
                        return true;
                    }
                    board[i][j] = '-';
                }
            }
        }
        return false;
    }

    private void makeRandomMove() {
        boolean moveMade = false;
        while (!moveMade) {
            int row = random.nextInt(3);
            int col = random.nextInt(3);
            if (board[row][col] == '-') {
                board[row][col] = 'O';
                buttons[row][col].setText("O");
                buttons[row][col].setForeground(Color.BLUE);
                buttons[row][col].setBackground(Color.CYAN);
                moveMade = true;
            }
        }
    }

    private void handleGameOver(String message, char winner) {
        if (winner == 'X') {
            playerXScore++;
        } else if (winner == 'O') {
            playerOScore++;
        }
        playerXLabel.setText("Player X: " + playerXScore);
        playerOLabel.setText("Player O: " + playerOScore);
    
        String[] options = {"Replay", "Main Menu"};
        int choice = JOptionPane.showOptionDialog(
            frame,
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
            frame.dispose();
            new GameLauncher();
        }
    }
}