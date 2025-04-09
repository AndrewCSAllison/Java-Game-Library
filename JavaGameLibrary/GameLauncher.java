import java.awt.*;
import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameLauncher::new);
    }

    public GameLauncher() {
        showMainMenu();
    }

    private void showMainMenu() {
        // Create a panel with a grid layout (2 rows, 3 columns)
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5)); // 2 rows, 3 columns
        String[] options = {"Tic-Tac-Toe", "2048", "Hangman", "Memory Matching", "Snake", "Sudoku", "Checkers", "Exit"};
        
        // Add buttons to the panel
        for (int i = 0; i < options.length; i++) {
            JButton button = new JButton(options[i]);
            final int choice = i; // Capture the index for the action listener
            button.addActionListener(e -> {
                handleGameSelection(choice);
                SwingUtilities.getWindowAncestor(panel).dispose(); // Close the dialog after selection
            });
            panel.add(button);
        }
    
        // Display the panel in an undecorated JOptionPane (removes "OK" button)
        JOptionPane.showOptionDialog(
                null,
                panel,
                "Main Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new Object[]{},  // No extra buttons
                null
        );
    }
    

    private void handleGameSelection(int choice) {
        switch (choice) {
            case 0: // Tic-Tac-Toe
                new TicTacToe();
                break;
            case 1: // 2048
                new Game2048();
                break;
            case 2: // Hangman
                new HangmanGUI();
                break;
            case 3: // Memory Matching
                new MemoryMatchGame();
                break;
            case 4: // Snake
                new SnakeGame();
                break;
            case 5: // Sudoku
                new SudokuGame();
                break;
            case 6 : // Checkers
                new Checkers();
                break;
            default: // Exit
                System.exit(0); // Terminate the program
        }
    }
}