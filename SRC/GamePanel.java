import javax.swing.*;
import javax.swing.Timer;
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

public class GamePanel extends JPanel implements KeyListener, ActionListener {

    Player player;
    java.util.List<Enemy> enemies = new ArrayList<>();
    java.util.List<Bullet> bullets = new ArrayList<>();
    java.util.List<PowerUp> powerUps = new ArrayList<>();

    java.util.List<Point> stars = new ArrayList<>();
    java.util.List<Color> starColors = new ArrayList<>();

    Timer timer;
    Random rand = new Random();

    int score = 0;
    int level = 1;
    int time = 60;
    boolean gameOver = false;

    AudioManager audio = new AudioManager();

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);

        player = new Player(250, 500);

        timer = new Timer(30, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) updateGame();
        repaint();
    }

    private void updateGame() {

        // ⭐ Stars
        if (rand.nextInt(10) < 2) {
            stars.add(new Point(rand.nextInt(600), rand.nextInt(600)));
            starColors.add(new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
        }

        // Enemies
        if (rand.nextInt(100) < 5) {
            enemies.add(new Enemy(rand.nextInt(550), 0));
        }

        // PowerUps
        if (rand.nextInt(100) < 2) {
            powerUps.add(new PowerUp(rand.nextInt(550), 0));
        }

        // Update enemies
        for (Enemy e : enemies) e.update();

        // Update bullets
        for (Bullet b : bullets) b.update();

        // Collision bullets vs enemies
        for (int i = 0; i < bullets.size(); i++) {
            for (int j = 0; j < enemies.size(); j++) {
                if (bullets.get(i).getBounds().intersects(enemies.get(j).getBounds())) {
                    enemies.remove(j);
                    bullets.remove(i);
                    score += 10;
                    audio.playFire();
                    break;
                }
            }
        }

        // Player collision
        for (int i = 0; i < enemies.size(); i++) {
            if (player.getBounds().intersects(enemies.get(i).getBounds())) {
                if (!player.shield) {
                    player.health -= 20;
                    audio.playHit();
                }
                enemies.remove(i);
                break;
            }
        }

        // PowerUps
        for (int i = 0; i < powerUps.size(); i++) {
            if (player.getBounds().intersects(powerUps.get(i).getBounds())) {
                player.health += 20;
                powerUps.remove(i);
                break;
            }
        }

        // Level system
        if (score > level * 50) level++;

        // Timer
        time--;
        if (time <= 0 || player.health <= 0) gameOver = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 600, 600);

        // Stars
        for (int i = 0; i < stars.size(); i++) {
            g.setColor(starColors.get(i));
            Point s = stars.get(i);
            g.fillOval(s.x, s.y, 3, 3);
        }

        player.draw(g);

        for (Enemy e : enemies) e.draw(g);
        for (Bullet b : bullets) b.draw(g);
        for (PowerUp p : powerUps) p.draw(g);

        // UI
        g.setColor(Color.BLUE);
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Health: " + player.health, 10, 40);
        g.drawString("Level: " + level, 10, 60);
        g.drawString("Time: " + time, 10, 80);

        if (gameOver) {
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", 250, 300);
        }
    }

    // INPUT
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_LEFT) player.x -= 10;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) player.x += 10;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            bullets.add(new Bullet(player.x + 20, player.y));
            audio.playFire();
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.shield = !player.shield;
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
}