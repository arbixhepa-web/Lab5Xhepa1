import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


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

    // asteroid animation frame
    private int asteroidFrame = 0;

    public AstroDefense() {
        setTitle("Astro Defense");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(new Color(5, 5, 20));

        GamePanel panel = new GamePanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(5, 5, 20));

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

        // bullet collision
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

        // player collision
        Rectangle player = new Rectangle(playerX, playerY - 10, 50, 65);

        for (int i = 0; i < enemies.size(); i++) {
            Rectangle e = new Rectangle(enemies.get(i).x, enemies.get(i).y, 40, 40);

            if (player.intersects(e)) {
                if (!shield) health -= 20;
                enemies.remove(i);
                break;
            }
        }

        // powerups
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

        asteroidFrame++;
        if (asteroidFrame >= 6) asteroidFrame = 0;

        if (timeLeft <= 0 || health <= 0) {
            gameOver = true;
        }

        repaint();
    }

    // ================= DRAW =================
    class GamePanel extends JPanel {

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // DARK SPACE BACKGROUND
            g.setColor(new Color(5, 5, 20));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // STARS
            for (int i = 0; i < stars.size(); i++) {
                g.setColor(starColors.get(i));
                Point s = stars.get(i);
                g.fillOval(s.x, s.y, 3, 3);
            }

            // ================= PLAYER ROCKET =================
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

            // ================= ANIMATED ASTEROIDS =================
            for (Point e : enemies) {

                int f = asteroidFrame;

                int offsetX = (int)(Math.sin(f + e.y * 0.1) * 3);
                int offsetY = (int)(Math.cos(f + e.x * 0.1) * 2);

                int cx = e.x + offsetX;
                int cy = e.y + offsetY;

                int[][] xFrames = {
                        {cx, cx + 10, cx + 22, cx + 30, cx + 18, cx + 5},
                        {cx + 2, cx + 12, cx + 20, cx + 28, cx + 16, cx + 6},
                        {cx + 1, cx + 9, cx + 18, cx + 32, cx + 20, cx + 7},
                        {cx + 3, cx + 11, cx + 23, cx + 29, cx + 17, cx + 4},
                        {cx, cx + 8, cx + 19, cx + 27, cx + 21, cx + 6},
                        {cx + 2, cx + 10, cx + 21, cx + 31, cx + 19, cx + 8}
                };

                int[][] yFrames = {
                        {cy + 5, cy, cy + 6, cy + 12, cy + 20, cy + 18},
                        {cy + 4, cy + 1, cy + 7, cy + 13, cy + 19, cy + 16},
                        {cy + 6, cy + 2, cy + 8, cy + 14, cy + 22, cy + 17},
                        {cy + 3, cy, cy + 6, cy + 11, cy + 21, cy + 15},
                        {cy + 5, cy + 1, cy + 7, cy + 13, cy + 18, cy + 16},
                        {cy + 4, cy + 2, cy + 6, cy + 12, cy + 20, cy + 14}
                };

                g.setColor(new Color(120, 120, 120));
                g.fillPolygon(xFrames[f], yFrames[f], 6);

                g.setColor(new Color(70, 70, 70));
                g.fillOval(cx + (f % 3), cy + 6, 5, 5);
            }

            // ================= ENERGY CORE POWER-UPS =================
            for (Point p : powerUps) {

                int pulse = (int)(Math.sin(frameCounter * 0.2) * 4);

                g.setColor(new Color(0, 255, 255, 40));
                g.fillOval(p.x - 10 - pulse, p.y - 10 - pulse, 35 + pulse * 2, 35 + pulse * 2);

                g.setColor(Color.CYAN);
                g.drawOval(p.x, p.y, 18, 18);

                int[] dx = {p.x + 9, p.x + 18, p.x + 9, p.x};
                int[] dy = {p.y, p.y + 9, p.y + 18, p.y + 9};

                g.setColor(Color.WHITE);
                g.fillPolygon(dx, dy, 4);

                g.setColor(Color.BLUE);
                g.drawLine(p.x + 2, p.y + 9, p.x + 16, p.y + 9);
            }

            // BULLETS
            g.setColor(Color.YELLOW);
            for (Point b : bullets) {
                g.fillRect(b.x, b.y, 5, 10);
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

    // ================= INPUT =================
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