import engine.Buffer;
import engine.GameEntity;

public class Border extends GameEntity {
    private int originalX;
    private int originalY;

    public Border(int x, int y, int width, int height) {
        originalX = x;
        originalY = y;
        super.teleport(x, y);
        super.setDimension(width,height);
    }

    public void draw(Buffer buffer) {
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }
}
