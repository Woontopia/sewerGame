import engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Monk extends MovableGameEntity {
    private static final String SPRITE_PATH = "Monk2.0.png";
    private static final int ANIMATION_SPEED = 8;
    private static Player player;

    private BufferedImage spriteSheet;
    private BufferedImage[] downFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] leftFrames;
    private int currentAnimationFrame = 0;
    private int nextFrame = 15;
    private ArrayList<Position> path = new ArrayList<>();
    private int positionIndex = 0;
    private int gap = 15;
    private SoundEffect singing = new SoundEffect("TuvanSinging.wav");
    private boolean dead = false;
    private int deadFloor = -1;

    public Monk(MovementController controller, int x, int y) {
        player = Player.getInstance(controller);
        super.setDimension(30, 34);
        super.setSpeed(3);
        teleport(x, y);
        loadSpriteSheet();
        loadAnimationFrame();
        beginPath();
        singing.loop();
    }

    public void update() {
        if (!dead) {
            updateAnimationFrame();
            updatePositionIndex();
            updatePath();
        }
    }

    public void draw(Buffer buffer, int currentFloor) {
        if (!dead) {
            if (path.get(1).getX() > path.get(0).getX()) {
                buffer.drawImage(rightFrames[currentAnimationFrame], x, y);
            } else if (path.get(1).getX() < path.get(0).getX()) {
                buffer.drawImage(leftFrames[currentAnimationFrame], x, y);
            } else if (path.get(1).getY() > path.get(0).getY()) {
                buffer.drawImage(downFrames[currentAnimationFrame], x, y);
            } else {
                buffer.drawImage(upFrames[currentAnimationFrame], x, y);
            }
        }else if (currentFloor == deadFloor) {
            buffer.drawImage(spriteSheet, x, y);
        }
    }

    private void beginPath() {
        path.add(new Position(x, y));
        for(int i = x; i < player.getX(); i++) {
            path.add(new Position(i, y));
        }
    }

    private boolean playerWithinDistance() {
        return path.size() - gap >= positionIndex;
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException ex) {}
    }

    private void loadSpriteSheet(String path) {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(path));
        }catch (IOException ex) {}
    }

    private void loadAnimationFrame() {
        loadDownFrames();
        loadUPFrames();
        loadRightFrames();
        loadLeftFrames();
    }

    private void loadDownFrames() {
        downFrames = new BufferedImage[3];
        downFrames[0] = spriteSheet.getSubimage(0, 35, 30, 34);
        downFrames[1] = spriteSheet.getSubimage(31, 35, 30, 34);
        downFrames[2] = spriteSheet.getSubimage(62, 35, 30, 34);
    }

    private void loadUPFrames() {
        upFrames = new BufferedImage[3];
        upFrames[0] = spriteSheet.getSubimage(0, 70, 30, 34);
        upFrames[1] = spriteSheet.getSubimage(31,70, 30, 34);
        upFrames[2] = spriteSheet.getSubimage(62, 70, 30, 34);
    }

    private void loadRightFrames() {
        rightFrames = new BufferedImage[3];
        rightFrames[0] = spriteSheet.getSubimage(0,0,30, 34);
        rightFrames[1] = spriteSheet.getSubimage(31,0, 30, 34);
        rightFrames[2] = spriteSheet.getSubimage(62,0,30,34);
    }

    private void loadLeftFrames() {
        leftFrames = new BufferedImage[3];
        leftFrames[0] = spriteSheet.getSubimage(0, 105, 30, 34);
        leftFrames[1] = spriteSheet.getSubimage(31, 105, 30, 34);
        leftFrames[2] = spriteSheet.getSubimage(62, 105, 30, 34);
    }

    private void updatePositionIndex() {
        if (playerWithinDistance()) {
            teleport(path.get(positionIndex).getX(), path.get(positionIndex).getY());
            path.remove(positionIndex);
        }
    }

    private void updatePath() {
        if (player.hasMoved()) {
            path.add(new Position(player.getX(), player.getY()));
        }
    }

    private void updateAnimationFrame() {
        if (player.hasMoved()) {
            --nextFrame;
            if (nextFrame == 0) {
                ++currentAnimationFrame;
                if (currentAnimationFrame >= downFrames.length) {
                    currentAnimationFrame = 0;
                }
                nextFrame = ANIMATION_SPEED;
            }
        } else {
            currentAnimationFrame = 0;
        }
    }

    public void explode(int floor) {
        if (!dead) {
            SoundEffect explosion = new SoundEffect("boom.wav");
            explosion.play();
            loadSpriteSheet("blood.png");
            deadFloor = floor;
            dead = true;
            stopSinging();
        }
    }

    public void draw(Buffer buffer) {

    }

    public void changeFloor(int x, int y) {
        if (!dead) {
            path.clear();
            teleport(x, y);
            path.add(new Position(x, y));
            path.add(new Position(x, y));
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void reanimate() {
        dead = false;
    }

    public void changeDirection(Direction direction) {
        super.setDirection(direction);
    }

    public void stopSinging() {
        singing.stop();
    }
}
