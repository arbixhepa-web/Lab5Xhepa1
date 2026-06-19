import java.awt.*;

public class Player {

    int x, y;
    int health = 100;
    boolean shield = false;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y - 10, 50, 65);
    }

    public void draw(Graphics g) {

        // Rocket Body
        g.setColor(Color.CYAN);
        g.fillRect(x + 15, y + 10, 20, 40);

        // Rocket Nose
        g.setColor(Color.RED);
        int[] noseX = {x + 15, x + 35, x + 25};
        int[] noseY = {y + 10, y + 10, y - 10};
        g.fillPolygon(noseX, noseY, 3);

        // Left Wing
        g.setColor(Color.GRAY);
        int[] leftWingX = {x + 15, x, x + 15};
        int[] leftWingY = {y + 35, y + 50, y + 50};
        g.fillPolygon(leftWingX, leftWingY, 3);

        // Right Wing
        int[] rightWingX = {x + 35, x + 50, x + 35};
        int[] rightWingY = {y + 35, y + 50, y + 50};
        g.fillPolygon(rightWingX, rightWingY, 3);

        // Window
        g.setColor(Color.BLUE);
        g.fillOval(x + 20, y + 18, 10, 10);

        // Flame
        g.setColor(Color.ORANGE);
        int[] flameX = {x + 20, x + 30, x + 25};
        int[] flameY = {y + 50, y + 50, y + 65};
        g.fillPolygon(flameX, flameY, 3);

        if (shield) {
            g.setColor(Color.MAGENTA);
            g.drawOval(x - 5, y - 15, 60, 75);
        }
    }
}