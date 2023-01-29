import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class Rat extends HostileGameEntity {
    private static final String SPRITE_PATH = "Rats.png";
    private static final int ANIMATION_SPEED = 8;

    private SoundEffect agroSound = new SoundEffect("rat.wav");
    private BufferedImage spriteSheet;
    private BufferedImage[] downFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] leftFrames;
    private int gap = 100;

    public Rat(int x, int y) {
        setVariables(x, y);
        loadSpriteSheet();
        loadAnimationFrame(new Random().nextInt(2));
        CollidableRepository.getInstance().registerEntity(this);
        agroSound.setVolume(SoundEffect.Volume.LOW);
    }

    public void update() {
        updateAnimationFrame();
        super.updateAttackCooldown();
        if (isPlayerWithDistance()) {
            playAgroSound();
            moveTowardsPlayer();
        } else {
            stopMovement();
        }
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

    private void setVariables(int x, int y) {
        super.setDimension(31, 21);
        super.setSpeed(2);
        super.life = 20;
        super.x = x;
        super.y = y;
        super.attackCooldown = 15;
        super.damage = 5;
        super.setSounds("ratAttack.wav", "ratDeath.wav");
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

    private boolean isPlayerWithDistance() {
        return isPlayerBetweenXRange() && isPlayerBetweenYRange();
    }

    private boolean isPlayerBetweenXRange() {
        int playerMidX = playerMidPosition("x");
        return x - gap < playerMidX && playerMidX < x + width + gap;
    }

    private boolean isPlayerBetweenYRange() {
        int playerMidY = playerMidPosition("y");
        return y - gap < playerMidY && playerMidY < y + height + gap;
    }

    private int playerMidPosition(String wantedPosition) {
        return wantedPosition.equals("x") ? player.getX() + (player.getWidth() / 2): player.getY() + (player.getHeight() / 2);
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException ex) {}
    }

    private void loadAnimationFrame(int ratNumber) {
        loadDownFrames(ratNumber);
        loadUPFrames(ratNumber);
        loadRightFrames(ratNumber);
        loadLeftFrames(ratNumber);
    }

    private void loadDownFrames(int number) {
        downFrames = new BufferedImage[3];
        if (number == 1) {
            downFrames[0] = spriteSheet.getSubimage(127,0,31,33);
            downFrames[1] = spriteSheet.getSubimage(96, 0, 31, 33);
            downFrames[2] = spriteSheet.getSubimage(161, 0, 31, 33);
        } else {
            downFrames[0] = spriteSheet.getSubimage(31,0,31,33);
            downFrames[1] = spriteSheet.getSubimage(0, 0, 31, 33);
            downFrames[2] = spriteSheet.getSubimage(62, 0, 31, 33);
        }
    }

    private void loadUPFrames(int number) {
        upFrames = new BufferedImage[3];
        if (number == 1) {
            upFrames[0] = spriteSheet.getSubimage(126, 93, 31, 33);
            upFrames[1] = spriteSheet.getSubimage(95, 93, 31,33);
            upFrames[2] = spriteSheet.getSubimage(157, 93, 31, 33);
        } else {
            upFrames[0] = spriteSheet.getSubimage(31, 93, 31, 33);
            upFrames[1] = spriteSheet.getSubimage(0, 93, 31,33);
            upFrames[2] = spriteSheet.getSubimage(62, 93, 31, 33);
        }
    }

    private void loadRightFrames(int number) {
        rightFrames = new BufferedImage[3];
        if (number == 1) {
            rightFrames[0] = spriteSheet.getSubimage(96,62,31,33);
            rightFrames[1] = spriteSheet.getSubimage(128,62, 31, 33);
            rightFrames[2] = spriteSheet.getSubimage(161,62, 31, 33);
        } else {
            rightFrames[0] = spriteSheet.getSubimage(0,62,31,33);
            rightFrames[1] = spriteSheet.getSubimage(32,62, 31, 33);
            rightFrames[2] = spriteSheet.getSubimage(65,62, 31, 33);
        }
    }

    private void loadLeftFrames(int number) {
        leftFrames = new BufferedImage[3];
        if (number == 1) {
            leftFrames[0] = spriteSheet.getSubimage(96, 32, 31, 33);
            leftFrames[1] = spriteSheet.getSubimage(129,32, 31, 33);
            leftFrames[2] = spriteSheet.getSubimage(161, 32, 31, 33);
        } else {
            leftFrames[0] = spriteSheet.getSubimage(0, 32, 31, 33);
            leftFrames[1] = spriteSheet.getSubimage(32,31, 31, 33);
            leftFrames[2] = spriteSheet.getSubimage(65, 32, 31, 33);
        }
    }

    private void playAgroSound() {
        if (!agroSound.isRunning()) {
            agroSound.play();
        }
    }
}
