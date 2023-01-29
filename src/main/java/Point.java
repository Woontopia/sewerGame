import engine.Buffer;
import engine.GameEntity;

public class Point extends GameEntity {
    private String name;
    private int originalX;
    private int originalY;

    public Point(String name, int x, int y, int width, int height) {
        this.name = name;
        originalX = x;
        originalY = y;
        super.teleport(x, y);
        super.setDimension(width,height);
    }

    public void draw(Buffer buffer) {
    }

    public String getName() {
        return name;
    }

    public int getOriginalX() {
        return originalX;
    }

    public int getOriginalY() {
        return originalY;
    }
}
