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

public class Bullet {

    int x, y;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= 8;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 5, 10);
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, 5, 10);
    }
}