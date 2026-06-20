import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Project: Solo Lab 5 Assignment - AstroDefense
 * Purpose Details: Space shooter game with movement, shooting, shields, power-ups, timer, and sprite assets
 * Course:
 * Author:
 * Date Developed:
 * Last Date Changed:
 * Rev: 1.3
 */

public class AstroDefense extends JFrame implements KeyListener {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;

    private static final int PLAYER_WIDTH = 61;
    private static final int PLAYER_HEIGHT = 50;

    private static final int OBSTACLE_WIDTH = 40;
    private static final int OBSTACLE_HEIGHT = 40;

    private static final int PROJECTILE_WIDTH = 5;
    private static final int PROJECTILE_HEIGHT = 10;

    private static final int PLAYER_SPEED = 5;
    private static final int OBSTACLE_SPEED = 3;
    private static final int PROJECTILE_SPEED = 10;

    private JPanel gamePanel;
    private JLabel scoreLabel;
    private Timer timer;

    private int score = 0;
    private int health = 5;
    private int timeLeft = 60;
    private int frameCounter = 0;
    private int level = 1;

    private boolean isGameOver = false;
    private boolean isProjectileVisible = false;
    private boolean isFiring = false;
    private boolean shieldActive = false;

    private int playerX, playerY;
    private int projectileX, projectileY;

    private List<Point> obstacles = new ArrayList<>();
    private List<Point> stars = new ArrayList<>();
    private List<Color> starColors = new ArrayList<>();
    private List<Point> powerUps = new ArrayList<>();

    private BufferedImage playerImage;
    private Image asteroidImage;

    private Random rand = new Random();

    public AstroDefense() {

      try {
          playerImage = ImageIO.read(new File("resources/SpaceShip.png"));
      } catch (IOException ex){
          ex.printStackTrace();
      }
      setTitle("AstroDefense - Lab 7");

        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        gamePanel.setLayout(null);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setBounds(10, 10, 200, 20);
        gamePanel.add(scoreLabel);

        add(gamePanel);

        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);

        playerX = WIDTH / 2;
        playerY = HEIGHT - 80;

        System.out.println(getClass().getResource("/SpaceShip.png"));
        System.out.println(getClass().getResource("/Astriod.png"));

        // ⭐ STAR BACKGROUND
        for (int i = 0; i < 120; i++) {
            stars.add(new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
            starColors.add(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        }

        timer = new Timer(20, e -> {
            if (!isGameOver) {
                update();
                gamePanel.repaint();
            }
        });

        timer.start();
    }

    // ================= DRAW =================
    private void draw(Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // ⭐ stars
        for (int i = 0; i < stars.size(); i++) {
            g.setColor(starColors.get(i));
            Point p = stars.get(i);
            g.fillRect(p.x, p.y, 2, 2);
        }

        // 🚀 player
        g.drawImage(playerImage, playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT, null);


        // 🔫 projectile
        if (isProjectileVisible) {
            g.setColor(Color.GREEN);
            g.fillRect(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        }

        // ☄️ asteroids
        for (Point p : obstacles) {
            g.drawImage(asteroidImage, p.x, p.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT, null);
        }

        // 💊 power-ups
        g.setColor(Color.CYAN);
        for (Point p : powerUps) {
            g.fillOval(p.x, p.y, 10, 10);
        }

        // 🔵 HUD
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Health: " + health, 10, 40);
        g.drawString("Time: " + timeLeft, 10, 60);
        g.drawString("Level: " + level, 10, 80);
        g.drawString("Shield: " + (shieldActive ? "ON" : "OFF"), 10, 100);

        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    // ================= UPDATE =================
    private void update() {

        frameCounter++;

        // ⏱ timer
        if (frameCounter % 50 == 0 && timeLeft > 0) {
            timeLeft--;
        }

        if (timeLeft <= 0) isGameOver = true;

        // 📈 level system
        if (score > 100) level = 2;
        if (score > 200) level = 3;

        int speed = OBSTACLE_SPEED + level;

        // ☄️ move asteroids
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).y += speed;
            if (obstacles.get(i).y > HEIGHT) {
                obstacles.remove(i);
                i--;
            }
        }

        // ☄️ spawn asteroids
        if (rand.nextInt(40) == 1) {
            obstacles.add(new Point(rand.nextInt(WIDTH - 40), 0));
        }

        // 💊 spawn power-ups
        if (rand.nextInt(120) == 1) {
            powerUps.add(new Point(rand.nextInt(WIDTH - 20), 0));
        }

        // 💊 power-up movement
        for (int i = 0; i < powerUps.size(); i++) {
            Point p = powerUps.get(i);
            p.y += 2;

            Rectangle player = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
            Rectangle pu = new Rectangle(p.x, p.y, 10, 10);

            if (player.intersects(pu)) {
                health++;
                powerUps.remove(i);
                i--;
            }
        }

        Rectangle player = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        // 💥 collision
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obs = new Rectangle(obstacles.get(i).x, obstacles.get(i).y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);

            if (player.intersects(obs)) {
                if (!shieldActive) {
                    health--;
                }
                obstacles.remove(i);
                i--;
            }
        }

        if (health <= 0) isGameOver = true;

        // 🔫 projectile movement
        if (isProjectileVisible) {
            projectileY -= PROJECTILE_SPEED;
            if (projectileY < 0) isProjectileVisible = false;
        }

        // 🔫 projectile hit
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obs = new Rectangle(obstacles.get(i).x, obstacles.get(i).y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
            Rectangle proj = new Rectangle(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);

            if (proj.intersects(obs)) {
                obstacles.remove(i);
                score += 10;
                isProjectileVisible = false;
                break;
            }
        }

        scoreLabel.setText("Score: " + score);
    }

    // ================= CONTROLS =================
    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= PLAYER_SPEED;
        }

        if (key == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }

        if (key == KeyEvent.VK_SPACE && !isFiring) {
            isFiring = true;
            isProjectileVisible = true;

            projectileX = playerX + PLAYER_WIDTH / 2;
            projectileY = playerY;

            new Thread(() -> {
                try {
                    Thread.sleep(400);
                    isFiring = false;
                } catch (Exception ignored) {}
            }).start();
        }

        if (key == KeyEvent.VK_S) {
            shieldActive = !shieldActive;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AstroDefense().setVisible(true);
        });
    }
}