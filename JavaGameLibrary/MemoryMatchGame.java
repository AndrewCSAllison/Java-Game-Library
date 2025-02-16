import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class MemoryMatchGame extends JFrame {
    private static final int GRID_SIZE = 10; // 10x10 grid (100 cards, 50 pairs)
    private static final int CARD_SIZE = 80; // Larger card size for 10x10 grid
    private static final int DELAY = 1000; // Delay in milliseconds for hiding mismatched cards
    private static final int COOLDOWN = 250; // Cooldown period in milliseconds (0.25 seconds)
    private JButton[][] cards = new JButton[GRID_SIZE][GRID_SIZE];
    private String[][] values = new String[GRID_SIZE][GRID_SIZE];
    private JButton firstCard = null;
    private JButton secondCard = null;
    private int matchesFound = 0;
    private int turnsTaken = 0; // Track the number of turns
    private JLabel statusLabel; // Label to display pairs found and turns taken
    private boolean isWaiting = false; // Flag to track if the game is waiting for cards to be unflipped

    public MemoryMatchGame() {
        setTitle("Memory Match Game");
        setSize(GRID_SIZE * CARD_SIZE, GRID_SIZE * CARD_SIZE + 50); // Extra space for status label
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        // Create a list of symbols (50 unique symbols, each appearing twice)
        List<String> symbolList = Arrays.asList(
            "1♠", "2♠", "3♠", "4♠", "5♠", "6♠", "7♠", "8♠", "9♠", "10♠",
            "1♥", "2♥", "3♥", "4♥", "5♥", "6♥", "7♥", "8♥", "9♥", "10♥",
            "1♦", "2♦", "3♦", "4♦", "5♦", "6♦", "7♦", "8♦", "9♦", "10♦", 
            "1♣", "2♣", "3♣", "4♣", "5♣", "6♣", "7♣", "8♣", "9♣", "10♣",
            "1♫", "2♫", "3♫", "4♫", "5♫", "6♫", "7♫", "8♫", "9♫", "10♫",
            "1♠", "2♠", "3♠", "4♠", "5♠", "6♠", "7♠", "8♠", "9♠", "10♠",
            "1♥", "2♥", "3♥", "4♥", "5♥", "6♥", "7♥", "8♥", "9♥", "10♥",
            "1♦", "2♦", "3♦", "4♦", "5♦", "6♦", "7♦", "8♦", "9♦", "10♦", 
            "1♣", "2♣", "3♣", "4♣", "5♣", "6♣", "7♣", "8♣", "9♣", "10♣",
            "1♫", "2♫", "3♫", "4♫", "5♫", "6♫", "7♫", "8♫", "9♫", "10♫"
        );

        // Ensure the list has exactly 100 elements (50 pairs)
        if (symbolList.size() != GRID_SIZE * GRID_SIZE) {
            throw new IllegalArgumentException("Symbol list must contain exactly " + (GRID_SIZE * GRID_SIZE) + " elements.");
        }

        // Shuffle the symbols
        Collections.shuffle(symbolList);

        // Assign symbols to the 2D array
        int index = 0;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                values[row][col] = symbolList.get(index++);
            }
        }
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton card = new JButton();
                card.setFont(new Font("Arial", Font.BOLD, 24));
                card.setBackground(getRandomLightColor());
                card.setOpaque(true);
                card.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                card.addActionListener(new CardClickListener(row, col));
                cards[row][col] = card;
                gridPanel.add(card);
            }
        }

        // Status label to display pairs found and turns taken
        statusLabel = new JLabel("Pairs Found: 0 / 50 | Turns Taken: 0");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private Color getRandomLightColor() {
        // Generate a random light shade color
        int red = 200 + (int) (Math.random() * 56); // 200-255
        int green = 200 + (int) (Math.random() * 56); // 200-255
        int blue = 200 + (int) (Math.random() * 56); // 200-255
        return new Color(red, green, blue);
    }

    private class CardClickListener implements ActionListener {
        private int row, col;
    
        public CardClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isWaiting) {
                return; // Ignore clicks while waiting for cards to be unflipped
            }
            JButton clickedCard = cards[row][col];
            
            // Ignore clicks on already matched or flipped cards
            if (clickedCard.getText().isEmpty() && firstCard != clickedCard) {
                clickedCard.setText(values[row][col]);
                clickedCard.setBackground(Color.WHITE);
                
                if (firstCard == null) {
                    // First card flipped
                    firstCard = clickedCard;
                } else if (secondCard == null) {
                    // Second card flipped
                    secondCard = clickedCard;
                    turnsTaken++; // Increment turns taken
                    isWaiting = true; // Disable further clicks
                    checkForMatch();
                }
            }
        }
    }

    private void checkForMatch() {
        if (firstCard.getText().equals(secondCard.getText())) {
            // Match found
            firstCard.setEnabled(false);
            secondCard.setEnabled(false);
            matchesFound += 2;
            updateStatusLabel();
            
            if (matchesFound == GRID_SIZE * GRID_SIZE) {
                String[] options = {"Replay", "Main Menu"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    "Congratulations! You've matched all the cards!",
                    "Game Over",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                if (choice == 0) {
                    restartGame();
                } else {
                    dispose();
                    new GameLauncher();
                }
            }
            
            // Reset firstCard and secondCard after a match
            firstCard = null;
            secondCard = null;
            isWaiting = false; // Re-enable clicks after match
            
        } else {
            // No match, hide the cards after a delay
            Timer timer = new Timer(DELAY, e -> {
                firstCard.setText("");
                secondCard.setText("");
                firstCard.setBackground(getRandomLightColor());
                secondCard.setBackground(getRandomLightColor());
                
                // Reset firstCard and secondCard after hiding mismatched cards
                firstCard = null;
                secondCard = null;
                isWaiting = false; // Re-enable clicks after cards are unflipped
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void updateStatusLabel() {
        statusLabel.setText("Pairs Found: " + (matchesFound / 2) + " / 50 | Turns Taken: " + turnsTaken);
    }

    private void restartGame() {
        matchesFound = 0;
        turnsTaken = 0;
        initializeGame();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cards[row][col].setText("");
                cards[row][col].setBackground(getRandomLightColor());
                cards[row][col].setEnabled(true);
            }
        }
        updateStatusLabel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryMatchGame::new);
    }
}