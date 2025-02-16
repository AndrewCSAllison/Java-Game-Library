import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class HangmanGUI extends JFrame {
    private static final List<String> PHRASES = List.of(
        "hello world", "java programming", "hangman game", "openai gpt", "artificial intelligence",
        "machine learning", "deep learning", "neural networks", "data science", "big data",
        "cloud computing", "web development", "mobile application", "software engineering", "algorithm design",
        "object oriented programming", "data structures", "computer science", "user interface design",
        "database management", "network security", "operating systems", "game development", "virtual reality",
        "augmented reality", "internet of things", "blockchain technology", "cyber security", "quantum computing",
        "natural language processing", "computer vision", "robotics", "embedded systems", "parallel computing",
        "distributed systems", "software testing", "agile methodology", "devops practices", "continuous integration",
        "version control systems", "frontend development", "backend development", "full stack development",
        "responsive web design", "progressive web apps", "cross platform development", "android development",
        "ios development", "flutter framework", "react native", "angular framework", "vue framework", "node js",
        "python programming", "ruby on rails", "django framework", "flask framework", "spring framework",
        "hibernate framework", "restful apis", "graphql", "microservices architecture", "serverless computing",
        "containerization", "kubernetes", "docker", "aws cloud", "google cloud", "microsoft azure", "terraform",
        "ansible", "jenkins", "gitlab", "github actions", "linux administration", "windows server", "mac os",
        "bash scripting", "powershell scripting", "sql databases", "nosql databases", "mongodb", "cassandra",
        "redis", "elasticsearch", "kafka", "rabbitmq", "nginx", "apache", "load balancing", "caching strategies",
        "performance optimization", "scalability", "high availability", "disaster recovery", "backup strategies",
        "data encryption", "ssl certificates", "firewall configuration", "intrusion detection", "penetration testing",
        "ethical hacking", "digital forensics", "incident response", "risk management", "compliance auditing"
    );
    private static final int MAX_TRIES = 6;
    private static final int MAX_HINTS = 3;

    private String phrase;
    private char[] guessedLetters;
    private int triesLeft;
    private int hintsUsed;
    private List<Character> guessedChars;
    private JLabel phraseLabel;
    private JLabel triesLabel;
    private JLabel guessedLettersLabel;
    private HangmanPanel hangmanPanel; // Custom panel for drawing the hangman
    private JTextField guessField;
    private JButton guessButton;
    private JButton hintButton;

    public HangmanGUI() {
        initializeGame();
        setupUI();
    }

    private void initializeGame() {
        Random random = new Random();
        phrase = PHRASES.get(random.nextInt(PHRASES.size()));
        guessedLetters = new char[phrase.length()];
        for (int i = 0; i < guessedLetters.length; i++) {
            guessedLetters[i] = (phrase.charAt(i) == ' ') ? '-' : '_';
        }
        triesLeft = MAX_TRIES;
        hintsUsed = 0;
        guessedChars = new ArrayList<>();
    }

    private void setupUI() {
        setTitle("Hangman Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hangman panel for drawing the hangman figure
        hangmanPanel = new HangmanPanel();
        hangmanPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(hangmanPanel);

        // Word display label
        phraseLabel = new JLabel(formatPhraseDisplay(guessedLetters));
        phraseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phraseLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        topPanel.add(Box.createVerticalStrut(20));
        topPanel.add(phraseLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for other controls
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        triesLabel = new JLabel("Tries left: " + triesLeft);
        triesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(triesLabel);

        guessedLettersLabel = new JLabel("Guessed letters: ");
        guessedLettersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(guessedLettersLabel);

        guessField = new JTextField(1);
        guessField.setMaximumSize(new Dimension(50, 20));
        guessField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(guessField);

        guessButton = new JButton("Guess");
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessButton.addActionListener(e -> processGuess());
        centerPanel.add(guessButton);

        hintButton = new JButton("Hint (" + (MAX_HINTS - hintsUsed) + ")");
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.addActionListener(e -> processHint());
        centerPanel.add(hintButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private String formatPhraseDisplay(char[] letters) {
        StringBuilder formatted = new StringBuilder();
        for (char c : letters) {
            if (c == '-') {
                formatted.append("- "); // Add a dash for spaces between words
            } else {
                formatted.append(c).append(" "); // Add space after each letter
            }
        }
        return formatted.toString().trim(); // Trim trailing space
    }

    private void processGuess() {
        String guessText = guessField.getText().toLowerCase();
        if (guessText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a letter.");
            return;
        }
        char guess = guessText.charAt(0);
        if (guessedChars.contains(guess)) {
            JOptionPane.showMessageDialog(this, "You already guessed that letter!");
            return;
        }
        guessedChars.add(guess);
        boolean correctGuess = false;
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == guess) {
                guessedLetters[i] = guess;
                correctGuess = true;
            }
        }
        if (!correctGuess) {
            triesLeft--;
            hangmanPanel.updateStage(MAX_TRIES - triesLeft); // Update the hangman drawing
        }
        phraseLabel.setText(formatPhraseDisplay(guessedLetters));
        triesLabel.setText("Tries left: " + triesLeft);
        guessedLettersLabel.setText("Guessed letters: " + guessedChars);
        if (new String(guessedLetters).equals(phrase.replace(' ', '-'))) {
            showWinMessage();
        } else if (triesLeft == 0) {
            JOptionPane.showMessageDialog(this, "You ran out of tries! The phrase was: " + phrase);
            dispose();
        }
        guessField.setText("");
    }

    private void showWinMessage() {
        String[] options = {"Replay", "Main Menu"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Congratulations! You guessed the phrase: " + phrase,
            "You Win!",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );
    
        if (choice == 0) {
            restartGame();
        } else {
            returnToMainMenu();
        }
    }

    private void restartGame() {
        initializeGame();
        phraseLabel.setText(formatPhraseDisplay(guessedLetters));
        triesLabel.setText("Tries left: " + triesLeft);
        guessedLettersLabel.setText("Guessed letters: ");
        hangmanPanel.updateStage(0);
        hintButton.setText("Hint (" + (MAX_HINTS - hintsUsed) + ")");
        hintButton.setEnabled(true);
    }

    private void returnToMainMenu() {
        dispose(); // Close the current game window
        new GameLauncher(); // Open the main menu (you can create a MainMenu class)
    }

    private void processHint() {
        if (hintsUsed >= MAX_HINTS) {
            JOptionPane.showMessageDialog(this, "You have used all your hints!");
            hintButton.setEnabled(false); // Disable the hint button
            return;
        }

        // Find the least frequent letter that hasn't been guessed yet
        char hintLetter = getLeastFrequentLetter();
        if (hintLetter == '\0') { // No more hints available
            JOptionPane.showMessageDialog(this, "No more hints available!");
            return;
        }

        // Automatically guess the hint letter
        guessedChars.add(hintLetter);
        for (int i = 0; i < phrase.length(); i++) {
            if (phrase.charAt(i) == hintLetter) {
                guessedLetters[i] = hintLetter;
            }
        }

        hintsUsed++;
        hintButton.setText("Hint (" + (MAX_HINTS - hintsUsed) + ")");

        // Update the UI
        phraseLabel.setText(formatPhraseDisplay(guessedLetters));
        StringBuilder guessedLettersStr = new StringBuilder();
        for (char c : guessedChars) {
            guessedLettersStr.append(c).append(", ");
        }
        if (guessedLettersStr.length() > 0) {
            guessedLettersStr.setLength(guessedLettersStr.length() - 2); // Remove trailing comma and space
        }
        guessedLettersLabel.setText("Guessed letters: " + guessedLettersStr.toString());

        if (new String(guessedLetters).equals(phrase.replace(' ', '-'))) {
            showWinMessage();
        }
    }

    private char getLeastFrequentLetter() {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : phrase.toCharArray()) {
            if (c != ' ' && c != '-' && !guessedChars.contains(c)) {
                frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            }
        }

        if (frequencyMap.isEmpty()) {
            return '\0'; // No more letters to hint
        }

        // Find the least frequent letter
        char leastFrequent = '\0';
        int minFrequency = Integer.MAX_VALUE;
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() < minFrequency) {
                minFrequency = entry.getValue();
                leastFrequent = entry.getKey();
            }
        }
        return leastFrequent;
    }

    // Custom JPanel for drawing the hangman figure
    private class HangmanPanel extends JPanel {
        private int stage = 0;

        public HangmanPanel() {
            setPreferredSize(new Dimension(300, 300));
            setBackground(Color.WHITE);
        }

        public void updateStage(int stage) {
            this.stage = stage;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));

            int width = getWidth();
            int height = getHeight();

            // Draw the gallows
            g2d.drawLine(50, height - 50, 250, height - 50); // Base
            g2d.drawLine(100, height - 50, 100, 50); // Vertical pole
            g2d.drawLine(100, 50, 200, 50); // Horizontal beam
            g2d.drawLine(200, 50, 200, 80); // Rope (shorter to make the character smaller)

            // Draw the hangman based on the stage (smaller size)
            if (stage >= 1) {
                g2d.drawOval(185, 80, 30, 30); // Smaller head
            }
            if (stage >= 2) {
                g2d.drawLine(200, 110, 200, 170); // Smaller body
            }
            if (stage >= 3) {
                g2d.drawLine(200, 120, 170, 140); // Smaller left arm
            }
            if (stage >= 4) {
                g2d.drawLine(200, 120, 230, 140); // Smaller right arm
            }
            if (stage >= 5) {
                g2d.drawLine(200, 170, 170, 200); // Smaller left leg
            }
            if (stage >= 6) {
                g2d.drawLine(200, 170, 230, 200); // Smaller right leg
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGUI::new);
    }
}