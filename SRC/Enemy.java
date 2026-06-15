
/**
 * Project: Solo Lab 5 Assignment
 * Purpose Details: Space shooter game with UI, audio, and gameplay features
 * Course: IST 242 (or your course)
 * Author: Arbi Xhepa
 * Date Developed: 06/15/2026
 * Last Date Changed: 06/15/2026
 * Rev: 1
 */

import java.awt.*;

public class Enemy {

    int x, y;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += 3;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 40, 40);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, 40, 40);
    }
}