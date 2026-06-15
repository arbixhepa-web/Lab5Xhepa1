import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Project: Solo Lab 5 Assignment
 * Purpose Details: Space shooter game with UI, audio, and gameplay features
 * Course: IST 242 (or your course)
 * Author: Arbi Xhepa
 * Date Developed: 06/15/2026
 * Last Date Changed: 06/15/2026
 * Rev: 1
 */


public class AstroDefense extends JFrame implements KeyListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private final int PLAYER_SIZE = 50;

    private int playerX = 250;
    private int playerY = 500;

    private ArrayList<Point> enemies = new ArrayList<>();
    private ArrayList<Point> bullets = new ArrayList<>();
    private ArrayList<Point> stars = new ArrayList<>();
    private ArrayList<Point> powerUps = new ArrayList<>();
    private ArrayList<Color> starColors = new ArrayList<>();

    private int score = 0;
    private int health = 100;
    private int level = 1;
    private int timeLeft = 60;

    private boolean shield = false;
    private boolean gameOver = false;

    private Random rand = new Random();
    private Timer timer;

    // ✅ FIX: proper timer control
    private int frameCounter = 0;

    public AstroDefense() {
        setTitle("Astro Defense");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);

        addKeyListener(this);

        timer = new Timer(30, e -> updateGame());
        timer.start();
    }

    // ================= GAME LOOP =================
    private void updateGame() {

        if (gameOver) return;

        // ⭐ Stars
        if (rand.nextInt(10) < 2) {
            stars.add(new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
            starColors.add(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        }

        // 👾 Enemies (FIXED spawn position)
        if (rand.nextInt(100) < 5) {
            enemies.add(new Point(rand.nextInt(WIDTH - 50), -40));
        }

        // 🍀 Power-ups
        if (rand.nextInt(100) < 2) {
            powerUps.add(new Point(rand.nextInt(WIDTH - 30), 0));
        }

        // Move enemies
        for (Point p : enemies) p.y += 3;

        // Move bullets
        for (Point b : bullets) b.y -= 8;

        // Move powerups
        for (Point p : powerUps) p.y += 2;

        bullets.removeIf(b -> b.y < 0);
        enemies.removeIf(e -> e.y > HEIGHT);

        // ================= BULLET COLLISION =================
        for (int i = 0; i < bullets.size(); i++) {
            Rectangle b = new Rectangle(bullets.get(i).x, bullets.get(i).y, 5, 10);

            for (int j = 0; j < enemies.size(); j++) {
                Rectangle e = new Rectangle(enemies.get(j).x, enemies.get(j).y, 40, 40);

                if (b.intersects(e)) {
                    enemies.remove(j);
                    bullets.remove(i);
                    score += 10;
                    break;
                }
            }
        }

        // ================= PLAYER COLLISION =================
        Rectangle player = new Rectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

        for (int i = 0; i < enemies.size(); i++) {
            Rectangle e = new Rectangle(enemies.get(i).x, enemies.get(i).y, 40, 40);

            if (player.intersects(e)) {
                if (!shield) {
                    health -= 20;
                }
                enemies.remove(i);
                break;
            }
        }

        // ================= POWERUP =================
        for (int i = 0; i < powerUps.size(); i++) {
            Rectangle p = new Rectangle(powerUps.get(i).x, powerUps.get(i).y, 15, 15);

            if (player.intersects(p)) {
                health += 20;
                powerUps.remove(i);
                break;
            }
        }

        // ================= LEVEL =================
        if (score > level * 50) level++;

        // ================= FIXED TIMER =================
        frameCounter++;

        if (frameCounter >= 33) { // ~1 second
            timeLeft--;
            frameCounter = 0;
        }

        // ================= GAME OVER =================
        if (timeLeft <= 0 || health <= 0) {
            gameOver = true;
        }

        repaint();
    }

    // ================= DRAW =================
    class GamePanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // Stars
            for (int i = 0; i < stars.size(); i++) {
                g.setColor(starColors.get(i));
                Point s = stars.get(i);
                g.fillOval(s.x, s.y, 3, 3);
            }

            // Player
            g.setColor(Color.CYAN);
            g.fillRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);

            // Shield
            if (shield) {
                g.setColor(Color.MAGENTA);
                g.drawOval(playerX - 5, playerY - 5, 60, 60);
            }

            // Enemies
            g.setColor(Color.RED);
            for (Point e : enemies) {
                g.fillRect(e.x, e.y, 40, 40);
            }

            // Bullets
            g.setColor(Color.GREEN);
            for (Point b : bullets) {
                g.fillRect(b.x, b.y, 5, 10);
            }

            // Powerups
            g.setColor(Color.YELLOW);
            for (Point p : powerUps) {
                g.fillOval(p.x, p.y, 15, 15);
            }

            // UI (BLUE SCORE REQUIRED)
            g.setColor(Color.BLUE);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Health: " + health, 10, 40);
            g.drawString("Level: " + level, 10, 60);
            g.drawString("Time: " + timeLeft, 10, 80);

            if (gameOver) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("GAME OVER", 200, 300);
            }
        }
    }

    // ================= INPUT =================
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) playerX -= 10;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerX += 10;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            bullets.add(new Point(playerX + 20, playerY));
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            shield = !shield;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AstroDefense().setVisible(true);
        });
    }
}