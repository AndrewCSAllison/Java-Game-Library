import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener {
    private final int TILE_SIZE = 20;
    private final int GRID_WIDTH = 20;
    private final int GRID_HEIGHT = 20;
    private final int TOTAL_TILES = GRID_WIDTH * GRID_HEIGHT;
    private final int[] x = new int[TOTAL_TILES];
    private final int[] y = new int[TOTAL_TILES];
    private int snakeLength;
    private Color lastFoodColor = null;
    private boolean running = false;
    private char direction = 'R';
    private Timer timer;
    private double speedMultiplier = 1.0;
    private Random rand = new Random();
    private JFrame frame;
    private List<Food> foodList = new ArrayList<>();
    private boolean startDelay = true;

    public SnakeGame() {
        this.frame = new JFrame("Snake Game");
        initialize();
    }

    public SnakeGame(JFrame frame) {
        this.frame = frame;
        initialize();
    }
    private void initialize() {
        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                    case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                    case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                    case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
                }
            }
        });
        startGame();

        // Add this panel to the frame and make it visible
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    private void startGame() {
        snakeLength = 3;
        direction = 'R';
        speedMultiplier = 1.0;
        for (int i = 0; i < snakeLength; i++) {
            x[i] = (GRID_WIDTH / 2) * TILE_SIZE - i * TILE_SIZE;
            y[i] = (GRID_HEIGHT / 2) * TILE_SIZE;
        }
        lastFoodColor = null;
        foodList.clear(); // Clear any existing food
        placeFood();
        running = true;
    
        timer = new Timer((int) (100 / speedMultiplier), this);
        timer.start();
    }
    
    private void handleGameOver() {
        timer.stop();
    
        String[] options = {"Replay", "Main Menu"};
        int choice = JOptionPane.showOptionDialog(
            frame,
            "Game Over!",
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
    
    private void resetGame() {
        running = false;
        timer.stop();
        startGame();
    }
    private void placeFood() {
        Color[] foodTypes = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.PINK};
        Color newFoodColor;
    
        do {
            newFoodColor = foodTypes[rand.nextInt(foodTypes.length)];
        } while (lastFoodColor != null && newFoodColor.equals(lastFoodColor));
    
        lastFoodColor = newFoodColor;
    
        int newFoodX, newFoodY;
        do {
            newFoodX = rand.nextInt(GRID_WIDTH - 2) * TILE_SIZE + TILE_SIZE;
            newFoodY = rand.nextInt(GRID_HEIGHT - 2) * TILE_SIZE + TILE_SIZE;
        } while (isOccupiedBySnake(newFoodX, newFoodY));
    
        foodList.add(new Food(newFoodX, newFoodY, newFoodColor));
    }
    
    private boolean isOccupiedBySnake(int xPos, int yPos) {
        for (int i = 0; i < snakeLength; i++) {
            if (x[i] == xPos && y[i] == yPos) {
                return true;
            }
        }
        return false;
    }
    private class Food {
        int x, y;
        Color color;
    
        Food(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            // Draw food
            for (Food food : foodList) {
                g.setColor(food.color);
                g.fillOval(food.x, food.y, TILE_SIZE, TILE_SIZE);
            }

            // Draw snake
            for (int i = 0; i < snakeLength; i++) {
                g.setColor(i == 0 ? Color.GREEN : Color.YELLOW);
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }

            // Draw the border
            g.setColor(Color.RED);
            g.fillRect(0, 0, GRID_WIDTH * TILE_SIZE, TILE_SIZE);
            g.fillRect(0, 0, TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
            g.fillRect((GRID_WIDTH - 1) * TILE_SIZE, 0, TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
            g.fillRect(0, (GRID_HEIGHT - 1) * TILE_SIZE, GRID_WIDTH * TILE_SIZE, TILE_SIZE);
        }
        // Do not call handleGameOver() here
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
            checkFood();
        } else {
            handleGameOver(); // Handle game over only when running is false
        }
        repaint();
    }
    
    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= TILE_SIZE; break;
            case 'D': y[0] += TILE_SIZE; break;
            case 'L': x[0] -= TILE_SIZE; break;
            case 'R': x[0] += TILE_SIZE; break;
        }
    }
    
    private void checkCollision() {
        // Check for collision with itself
        for (int i = snakeLength; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                return;
            }
        }
        // Check for collision with walls
        if (x[0] < TILE_SIZE || x[0] >= (GRID_WIDTH - 1) * TILE_SIZE || y[0] < TILE_SIZE || y[0] >= (GRID_HEIGHT - 1) * TILE_SIZE) {
            running = false;
        }
    }
    
    private void checkFood() {
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            if (x[0] == food.x && y[0] == food.y) {
                int tailX = x[snakeLength - 1];
                int tailY = y[snakeLength - 1];
    
                if (food.color.equals(Color.RED)) {
                    increaseLength(1, tailX, tailY);
                } else if (food.color.equals(Color.BLUE)) {
                    speedMultiplier *= 1.1;
                    decreaseLength(2);
                } else if (food.color.equals(Color.GREEN)) {
                    speedMultiplier *= 0.7;
                    increaseLength(2, tailX, tailY);
                } else if (food.color.equals(Color.YELLOW)) {
                    increaseLength(1, tailX, tailY);
                    placeFood();
                    placeFood();
                    placeFood();
                } else if (food.color.equals(Color.MAGENTA)) {
                    speedMultiplier = rand.nextBoolean() ? speedMultiplier * 0.5 : speedMultiplier * 1.25;
                    increaseLength(5, tailX, tailY);
                } else if (food.color.equals(Color.PINK)) {
                    speedMultiplier *= 2;
                    decreaseLength(2);
                }
    
                foodList.remove(i);
                i--; // Adjust loop index after removal
            }
        }
    
        // Ensure at least one food is always present
        if (foodList.isEmpty()) {
            placeFood();
        }
    }
    private void increaseLength(int amount, int tailX, int tailY) {
        for (int i = 0; i < amount; i++) {
            x[snakeLength] = tailX;
            y[snakeLength] = tailY;
            snakeLength++;
        }
    }
    
    private void decreaseLength(int amount) {
        snakeLength = Math.max(1, snakeLength - amount);
    }
    
    public static void main(String[] args) {
        new SnakeGame();
    }
}
