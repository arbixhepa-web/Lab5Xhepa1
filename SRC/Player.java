import java.awt.*;
/**
 * Project: Solo Lab 5 Assignment
 * Purpose Details: Space shooter game with UI, audio, and gameplay features
 * Course: IST 242 (or your course)
 * Author: Arbi Xhepa
 * Date Developed: 06/15/2026
 * Last Date Changed: 06/15/2026
 * Rev: 1
 */

public class Player {

    int x, y;
    int health = 100;
    boolean shield = false;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 50, 50);
    }

    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, 50, 50);

        if (shield) {
            g.setColor(Color.MAGENTA);
            g.drawOval(x - 5, y - 5, 60, 60);
        }
    }
}