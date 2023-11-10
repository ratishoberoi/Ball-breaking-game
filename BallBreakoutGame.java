import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.Random;

public class BallBreakoutGame extends JPanel implements ActionListener {
    private int ballX = 150;
    private int ballY = 150;
    private int ballXSpeed = 2;
    private int ballYSpeed = 2;
    private int paddleX = 100;
    private int paddleWidth = 100;
    private int paddleHeight = 10;
    private int brickRows = 3;
    private int brickColumns = 5;
    private int brickWidth = 60;
    private int brickHeight = 20;
    private int airBrickRows = 3; // Number of rows of air bricks
    private int airBrickColumns = 5; // Number of columns of air bricks
    private int airBrickWidth = 60;
    private int airBrickHeight = 20;
    private boolean left = false;
    private boolean right = false;
    private boolean bricksVisible[][];
    private boolean airBricksVisible[][];
    private Color airBricksColors[][];
    private int airBrickHits[][];
    private int score = 0;
    private int level = 1;
    private int delay = 10; // Delay in milliseconds
    private int numAirBrickTypes = 3; // Number of air brick types
    private boolean won = false;

    public BallBreakoutGame() {
        bricksVisible = new boolean[brickRows][brickColumns];
        airBricksVisible = new boolean[airBrickRows][airBrickColumns];
        airBricksColors = new Color[airBrickRows][airBrickColumns];
        airBrickHits = new int[airBrickRows][airBrickColumns];

        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                bricksVisible[i][j] = true;
            }
        }
        for (int i = 0; i < airBrickRows; i++) {
            for (int j = 0; j < airBrickColumns; j++) {
                airBricksVisible[i][j] = true;
                airBrickHits[i][j] = generateAirBrickHits();
            }
        }

        Timer timer = new Timer(delay, this);
        timer.start();

        addKeyListener(new KeyAdapter());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveBall();
        movePaddle();
        checkCollision();
        repaint();
    }

    private void moveBall() {
        if (ballX <= 0 || ballX >= getWidth() - 30) {
            ballXSpeed = -ballXSpeed;
        }
        if (ballY <= 0) {
            ballYSpeed = -ballYSpeed;
        }
        if (ballY >= getHeight() - 30) {
            // Game over
            System.exit(0);
        }

        ballX += ballXSpeed;
        ballY += ballYSpeed;
    }

    private void movePaddle() {
        if (left && paddleX > 0) {
            paddleX -= 5;
        }
        if (right && paddleX < getWidth() - paddleWidth) {
            paddleX += 5;
        }
    }

    private void checkCollision() {
        // Check collision with the paddle
        if (ballY + 30 >= getHeight() - paddleHeight && ballX + 30 >= paddleX && ballX <= paddleX + paddleWidth) {
            ballYSpeed = -ballYSpeed;
        }

        // Check collision with regular bricks
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                if (bricksVisible[i][j]) {
                    int brickX = j * brickWidth;
                    int brickY = i * brickHeight;
                    if (ballX + 30 >= brickX && ballX <= brickX + brickWidth && ballY + 30 >= brickY && ballY <= brickY + brickHeight) {
                        ballYSpeed = -ballYSpeed;
                        bricksVisible[i][j] = false;
                        score += 10;
                    }
                }
            }
        }

        // Check collision with air bricks
        boolean airBlockHit = false; // Flag to check if an air block is hit
        for (int i = 0; i < airBrickRows; i++) {
            for (int j = 0; j < airBrickColumns; j++) {
                if (airBricksVisible[i][j]) {
                    int airBrickX = j * airBrickWidth;
                    int airBrickY = i * airBrickHeight;
                    if (ballX + 30 >= airBrickX && ballX <= airBrickX + airBrickWidth && ballY + 30 >= airBrickY && ballY <= airBrickY + airBrickHeight) {
                        // Ball hits the air block
                        if (!airBlockHit) {
                            int hitsRequired = airBrickHits[i][j];
                            if (hitsRequired > 1) {
                                airBrickHits[i][j]--;
                            } else {
                                ballYSpeed = -ballYSpeed;
                                airBricksVisible[i][j] = false;
                                score += 10;
                            }
                            airBlockHit = true;
                        }
                    }
                }
            }
        }

        // Check if all bricks (both regular and air bricks) are cleared
        boolean allBricksCleared = true;
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                if (bricksVisible[i][j]) {
                    allBricksCleared = false;
                    break;
                }
            }
        }
        for (int i = 0; i < airBrickRows; i++) {
            for (int j = 0; j < airBrickColumns; j++) {
                if (airBricksVisible[i][j]) {
                    allBricksCleared = false;
                    break;
                }
            }
        }

        if (allBricksCleared) {
            // Advance to the next level
            level++;
            if (level <= 3) {
                resetBricks();
            } else {
                // Player has won the game
                won = true;
                displayWinMessage();
            }
        }
    }

    private void resetBricks() {
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                bricksVisible[i][j] = true;
            }
        }
        generateAirBricks();
        ballX = 150;
        ballY = 150;
        ballXSpeed = 2;
        ballYSpeed = 2;
        paddleX = 100;
        score += 50;
    }

    private void generateAirBricks() {
        for (int i = 0; i < airBrickRows; i++) {
            for (int j = 0; j < airBrickColumns; j++) {
                int airBrickType = generateRandomAirBrickType();
                airBricksVisible[i][j] = true;
                airBrickHits[i][j] = calculateAirBrickHits(airBrickType);
                airBricksColors[i][j] = generateAirBrickColor(airBrickType);
            }
        }
    }

    private int generateAirBrickHits() {
        return new Random().nextInt(3) + 1;
    }

    private int calculateAirBrickHits(int airBrickType) {
        return airBrickType + 1;
    }

    private int generateRandomAirBrickType() {
        return new Random().nextInt(numAirBrickTypes);
    }

    private Color generateAirBrickColor(int airBrickType) {
        switch (airBrickType) {
            case 0:
                return Color.ORANGE;
            case 1:
                return Color.RED;
            case 2:
                return Color.BLUE;
            default:
                return Color.ORANGE;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Set background color
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        drawBall(g);
        drawPaddle(g);
        drawBricks(g);
        drawAirBricks(g);
        drawScore(g);
    }

    private void drawBall(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, 30, 30);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(paddleX, getHeight() - paddleHeight, paddleWidth, paddleHeight);
    }

    private void drawBricks(Graphics g) {
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickColumns; j++) {
                if (bricksVisible[i][j]) {
                    int brickX = j * brickWidth;
                    int brickY = i * brickHeight;
                    g.setColor(Color.GREEN);
                    g.fillRect(brickX, brickY, brickWidth, brickHeight);
                }
            }
        }
    }

    private void drawAirBricks(Graphics g) {
        for (int i = 0; i < airBrickRows; i++) {
            for (int j = 0; j < airBrickColumns; j++) {
                if (airBricksVisible[i][j]) {
                    int airBrickX = j * airBrickWidth;
                    int airBrickY = i * airBrickHeight;
                    g.setColor(airBricksColors[i][j]);
                    g.fillRect(airBrickX, airBrickY, airBrickWidth, airBrickHeight);
                }
            }
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Level: " + level, 10, 40);
    }

    private void displayWinMessage() {
        if (won) {
            JOptionPane.showMessageDialog(this, "YOU WON!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ball Breakout Game");
        BallBreakoutGame game = new BallBreakoutGame();
        frame.add(game);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class KeyAdapter extends java.awt.event.KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT) {
                left = true;
            }
            if (keyCode == KeyEvent.VK_RIGHT) {
                right = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT) {
                left = false;
            }
            if (keyCode == KeyEvent.VK_RIGHT) {
                right = false;
            }
        }
    }
}
