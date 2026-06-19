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

    private int frameCounter = 0;

    public AstroDefense() {
        setTitle("Astro Defense");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // ✅ FIX: force dark window background
        getContentPane().setBackground(new Color(5, 5, 20));

        GamePanel panel = new GamePanel();
        panel.setBackground(new Color(5, 5, 20));
        panel.setOpaque(true);

        add(panel);

        addKeyListener(this);

        timer = new Timer(30, e -> updateGame());
        timer.start();
    }

    // ================= GAME LOOP =================
    private void updateGame() {

        if (gameOver) return;

        if (rand.nextInt(10) < 2) {
            stars.add(new Point(rand.nextInt(WIDTH), rand.nextInt(HEIGHT)));
            starColors.add(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        }

        if (rand.nextInt(100) < 5) {
            enemies.add(new Point(rand.nextInt(WIDTH - 50), -40));
        }

        if (rand.nextInt(100) < 2) {
            powerUps.add(new Point(rand.nextInt(WIDTH - 30), 0));
        }

        for (Point p : enemies) p.y += 3;
        for (Point b : bullets) b.y -= 8;
        for (Point p : powerUps) p.y += 2;

        bullets.removeIf(b -> b.y < 0);
        enemies.removeIf(e -> e.y > HEIGHT);

        // BULLET COLLISION
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

        // PLAYER COLLISION
        Rectangle player = new Rectangle(playerX, playerY - 10, 50, 65);

        for (int i = 0; i < enemies.size(); i++) {
            Rectangle e = new Rectangle(enemies.get(i).x, enemies.get(i).y, 40, 40);

            if (player.intersects(e)) {
                if (!shield) health -= 20;
                enemies.remove(i);
                break;
            }
        }

        // POWERUPS
        for (int i = 0; i < powerUps.size(); i++) {
            Rectangle p = new Rectangle(powerUps.get(i).x, powerUps.get(i).y, 15, 15);

            if (player.intersects(p)) {
                health += 20;
                powerUps.remove(i);
                break;
            }
        }

        if (score > level * 50) level++;

        frameCounter++;
        if (frameCounter >= 33) {
            timeLeft--;
            frameCounter = 0;
        }

        if (timeLeft <= 0 || health <= 0) {
            gameOver = true;
        }

        repaint();
    }

    // ================= DRAW =================
    class GamePanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // ✅ DARK BACKGROUND FIX
            setBackground(new Color(5, 5, 20));
            g.setColor(new Color(5, 5, 20));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // STARS
            for (int i = 0; i < stars.size(); i++) {
                g.setColor(starColors.get(i));
                Point s = stars.get(i);
                g.fillOval(s.x, s.y, 3, 3);
            }

            // ROCKET PLAYER
            g.setColor(Color.CYAN);
            g.fillRect(playerX + 15, playerY + 10, 20, 40);

            g.setColor(Color.RED);
            int[] noseX = {playerX + 15, playerX + 35, playerX + 25};
            int[] noseY = {playerY + 10, playerY + 10, playerY - 10};
            g.fillPolygon(noseX, noseY, 3);

            g.setColor(Color.GRAY);
            int[] lwx = {playerX + 15, playerX, playerX + 15};
            int[] lwy = {playerY + 35, playerY + 50, playerY + 50};
            g.fillPolygon(lwx, lwy, 3);

            int[] rwx = {playerX + 35, playerX + 50, playerX + 35};
            int[] rwy = {playerY + 35, playerY + 50, playerY + 50};
            g.fillPolygon(rwx, rwy, 3);

            g.setColor(Color.BLUE);
            g.fillOval(playerX + 20, playerY + 18, 10, 10);

            g.setColor(Color.ORANGE);
            int[] fx = {playerX + 20, playerX + 30, playerX + 25};
            int[] fy = {playerY + 50, playerY + 50, playerY + 65};
            g.fillPolygon(fx, fy, 3);

            if (shield) {
                g.setColor(Color.MAGENTA);
                g.drawOval(playerX - 5, playerY - 15, 60, 75);
            }

            // ASTEROIDS
            for (Point e : enemies) {

                g.setColor(Color.LIGHT_GRAY);

                int[] xPoints = {
                        e.x, e.x + 8, e.x + 18, e.x + 32, e.x + 28, e.x + 10
                };

                int[] yPoints = {
                        e.y + 5, e.y, e.y + 6, e.y + 12, e.y + 20, e.y + 18
                };

                g.fillPolygon(xPoints, yPoints, 6);

                g.setColor(Color.DARK_GRAY);
                g.fillOval(e.x + 8, e.y + 6, 5, 5);
                g.fillOval(e.x + 18, e.y + 10, 4, 4);
                g.fillOval(e.x + 22, e.y + 14, 3, 3);
            }

            // BULLETS
            g.setColor(Color.YELLOW);
            for (Point b : bullets) {
                g.fillRect(b.x, b.y, 5, 10);
            }

            // POWERUPS
            g.setColor(Color.YELLOW);
            for (Point p : powerUps) {
                g.fillOval(p.x, p.y, 15, 15);
            }

            // UI
            g.setColor(Color.BLUE);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Health: " + health, 10, 40);
            g.drawString("Level: " + level, 10, 60);
            g.drawString("Time: " + timeLeft, 10, 80);

            if (gameOver) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("GAME OVER", 200, 300);
            }
        }
    }

    // INPUT
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) playerX -= 10;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) playerX += 10;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            bullets.add(new Point(playerX + 25, playerY - 10));
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