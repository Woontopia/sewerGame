import engine.Buffer;
import engine.Direction;
import engine.MovableGameEntity;

import java.awt.*;

public class Bullet extends MovableGameEntity {
    private Direction direction;
    private Color color;
    private double damage;

    public Bullet(Player player, int height, int width, double damage, Color color) {
        this.color = color;
        this.damage = damage;
        direction = player.getDirection();
        super.setDimension(width, height);
        setSpeed(8);
        if (direction == Direction.RIGHT) {
            super.teleport(player.getX() + player.getWidth() + 1, player.getY() + 20);
            super.setDimension(width, height);
        } else if (direction == Direction.LEFT) {
            super.teleport(player.getX() - 9, player.getY() + 20);
            super.setDimension(width, height);
        } else if (direction == Direction.DOWN) {
            super.teleport(player.getX() + 13, player.getY() + player.getHeight() + 1);
            super.setDimension(height, width);
        } else if (direction == Direction.UP) {
            super.teleport(player.getX() + 13, player.getY() - 9);
            super.setDimension(height, width);
        }
    }

    public void update() {
        move(direction);
    }

    public void draw(Buffer buffer) {
        buffer.drawRectangle(x, y, width, height, color);
    }

    public double getDamage() {
        return damage;
    }
}
