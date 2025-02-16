import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent; // Added for KeyEvent support
import java.util.Random;
import javax.swing.*;

public class Game2048 {
    private JFrame frame;
    private int[][] board;
    private int gridSize = 4; // Default grid size
    private int score = 0;

    public Game2048() {
        initializeGame();
    }

    private void initializeGame() {
        board = new int[gridSize][gridSize];
        initializeBoard(board);
        createGUI();
    }

    private void initializeBoard(int[][] board) {
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            spawnRandomTile(board, random);
        }
    }

    private void spawnRandomTile(int[][] board, Random random) {
        int emptyCells = 0;
        for (int[] row : board) {
            for (int cell : row) {
                if (cell == 0) {
                    emptyCells++;
                }
            }
        }
        if (emptyCells == 0) {
            return;
        }
        int target = random.nextInt(emptyCells);
        int value = random.nextInt(10) < 9 ? 2 : 4;
        int index = 0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 0) {
                    if (index == target) {
                        board[r][c] = value;
                        return;
                    }
                    index++;
                }
            }
        }
    }

    private void createGUI() {
        frame = new JFrame("2048 Game (" + gridSize + "x" + gridSize + ")");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(gridSize, gridSize));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gamePanel.setBackground(Color.LIGHT_GRAY);

        JButton[][] buttons = new JButton[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                buttons[i][j].setFocusPainted(false);
                updateButton(buttons[i][j], board[i][j]);
                gamePanel.add(buttons[i][j]);
            }
        }

        frame.add(gamePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JLabel scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        controlPanel.add(scoreLabel, BorderLayout.NORTH);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            frame.dispose();
            new GameLauncher();
        });
        controlPanel.add(backButton, BorderLayout.SOUTH);

        frame.add(controlPanel, BorderLayout.SOUTH);

        // Add Key Bindings
        InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = frame.getRootPane().getActionMap();

        // Arrow Keys
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");

        // WASD Keys
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight");

        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tilt(board, Side.NORTH);
                spawnRandomTile(board, new Random());
                updateButtons(buttons, board);
                scoreLabel.setText("Score: " + score);
                checkGameOver(board, frame);
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tilt(board, Side.SOUTH);
                spawnRandomTile(board, new Random());
                updateButtons(buttons, board);
                scoreLabel.setText("Score: " + score);
                checkGameOver(board, frame);
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tilt(board, Side.WEST);
                spawnRandomTile(board, new Random());
                updateButtons(buttons, board);
                scoreLabel.setText("Score: " + score);
                checkGameOver(board, frame);
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tilt(board, Side.EAST);
                spawnRandomTile(board, new Random());
                updateButtons(buttons, board);
                scoreLabel.setText("Score: " + score);
                checkGameOver(board, frame);
            }
        });

        frame.setVisible(true);
    }

    private void updateButtons(JButton[][] buttons, int[][] board) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                updateButton(buttons[i][j], board[i][j]);
            }
        }
    }

    private void updateButton(JButton button, int value) {
        if (value == 0) {
            button.setText("");
            button.setBackground(Color.WHITE);
        } else {
            button.setText(String.valueOf(value));
            button.setBackground(getColorForValue(value));
        }
    }

    private Color getColorForValue(int value) {
        return switch (value) {
            case 2 -> new Color(238, 228, 218);
            case 4 -> new Color(237, 224, 200);
            case 8 -> new Color(242, 177, 121);
            case 16 -> new Color(245, 149, 99);
            case 32 -> new Color(246, 124, 95);
            case 64 -> new Color(246, 94, 59);
            case 128 -> new Color(237, 207, 114);
            case 256 -> new Color(237, 204, 97);
            case 512 -> new Color(237, 200, 80);
            case 1024 -> new Color(237, 197, 63);
            case 2048 -> new Color(237, 194, 46);
            default -> Color.WHITE;
        };
    }

    private boolean isGameOver(int[][] board) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                if (board[r][c] == 0) {
                    return false;
                }
                if (r > 0 && board[r][c] == board[r - 1][c]) {
                    return false;
                }
                if (c > 0 && board[r][c] == board[r][c - 1]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkGameOver(int[][] board, JFrame frame) {
        if (isGameOver(board)) {
            JOptionPane.showMessageDialog(frame, "Game Over! No more moves available.");
            frame.dispose();
            new GameLauncher();
        }
    }

    public void tilt(int[][] board, Side side) {
        if (side == Side.EAST) {
            rotateLeft(board);
            tiltUp(board);
            rotateRight(board);
        } else if (side == Side.WEST) {
            rotateRight(board);
            tiltUp(board);
            rotateLeft(board);
        } else if (side == Side.SOUTH) {
            rotateRight(board);
            rotateRight(board);
            tiltUp(board);
            rotateLeft(board);
            rotateLeft(board);
        } else {
            tiltUp(board);
        }
    }

    public void tiltUp(int[][] board) {
        for (int c = 0; c < board[0].length; c++) {
            tiltColumn(board, c);
        }
    }

    public void tiltColumn(int[][] board, int c) {
        int minR = 0;
        for (int r = 0; r < board.length; r++) {
            if (board[r][c] != 0) {
                minR = moveTileUpAsFarAsPossible(board, r, c, minR);
            }
        }
    }

    public int moveTileUpAsFarAsPossible(int[][] board, int r, int c, int minR) {
        int tempRow = r;
        while (tempRow > minR && board[tempRow - 1][c] == 0) {
            tempRow--;
        }
        if (tempRow > minR && board[tempRow - 1][c] == board[r][c]) {
            int mergedValue = board[r][c] + board[tempRow - 1][c];
            board[tempRow - 1][c] = mergedValue;
            board[r][c] = 0;
            score += mergedValue * 10;
            return tempRow;
        }
        if (tempRow != r) {
            board[tempRow][c] = board[r][c];
            board[r][c] = 0;
        }
        return 0;
    }

    public void rotateLeft(int[][] board) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = temp;
            }
        }
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n / 2; i++) {
                int temp = board[i][j];
                board[i][j] = board[n - 1 - i][j];
                board[n - 1 - i][j] = temp;
            }
        }
    }

    public void rotateRight(int[][] board) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = temp;
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = board[i][j];
                board[i][j] = board[i][n - 1 - j];
                board[i][n - 1 - j] = temp;
            }
        }
    }

    public enum Side {
        NORTH, SOUTH, EAST, WEST
    }
}