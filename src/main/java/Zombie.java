import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Zombie extends HostileGameEntity {
    private static final String SPRITE_PATH = "Zombie.png";
    private static final int ANIMATION_SPEED = 8;

    private BufferedImage spriteSheet;
    private BufferedImage[] downFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] leftFrames;

    public Zombie(int x, int y) {
        setvariables(x, y);
        loadSpriteSheet();
        loadAnimationFrame();
        CollidableRepository.getInstance().registerEntity(this);
    }

    public void update() {
        updateAnimationFrame();
        super.updateAttackCooldown();
        super.moveTowardsPlayer();
        super.damagePlayer();
    }

    public void draw(Buffer buffer) {
        Direction direction = getDirection();
        if (direction == Direction.LEFT) {
            buffer.drawImage(leftFrames[currentAnimationFrame], x, y);
        } else if (direction == Direction.RIGHT) {
            buffer.drawImage(rightFrames[currentAnimationFrame], x, y);
        } else if (direction == Direction.DOWN) {
            buffer.drawImage(downFrames[currentAnimationFrame], x, y);
        } else if (direction == Direction.UP) {
            buffer.drawImage(upFrames[currentAnimationFrame], x, y);
        }
    }

    public void receiveDamage(double damage) {
        life -= damage;
        if (life < 0) {
            life = 0;
        }
    }

    private void setvariables(int x, int y) {
        super.setDimension(23, 32);
        super.setSpeed(1);
        super.life = 15;
        super.x = x;
        super.y = y;
        super.attackCooldown = 10;
        super.damage = 4;
        super.setSounds("zombAttack.wav", "zombDeath.wav");
    }

    private int playerPosition(String wantedPosition) {
        return wantedPosition.equals("x") ? player.getX() + (player.getWidth() / 2): player.getY() + (player.getHeight() / 2);
    }

    private void updateAnimationFrame() {
        if (super.hasMoved()) {
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

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
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
        downFrames[0] = spriteSheet.getSubimage(32,0,23,32);
        downFrames[1] = spriteSheet.getSubimage(0, 0, 23, 32);
        downFrames[2] = spriteSheet.getSubimage(64, 0, 23, 32);
    }

    private void loadUPFrames() {
        upFrames = new BufferedImage[3];
        upFrames[0] = spriteSheet.getSubimage(32, 96, 23, 32);
        upFrames[1] = spriteSheet.getSubimage(0, 96, 23,32);
        upFrames[2] = spriteSheet.getSubimage(64, 96, 23, 32);
    }

    private void loadRightFrames() {
        rightFrames = new BufferedImage[3];
        rightFrames[0] = spriteSheet.getSubimage(32,64,23,32);
        rightFrames[1] = spriteSheet.getSubimage(0,64, 23, 32);
        rightFrames[2] = spriteSheet.getSubimage(64,64, 23, 32);
    }

    private void loadLeftFrames() {
        leftFrames = new BufferedImage[3];
        leftFrames[0] = spriteSheet.getSubimage(31, 32, 23, 32);
        leftFrames[1] = spriteSheet.getSubimage(0,32, 23, 32);
        leftFrames[2] = spriteSheet.getSubimage(64, 32, 23, 32);
    }
}
