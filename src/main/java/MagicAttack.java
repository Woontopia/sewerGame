import engine.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MagicAttack extends MovableGameEntity {
    private static final String SPRITE_PATH = "magicAttack.png";

    private Player player = Player.getInstance(new MovementController());
    private double damage = 50;
    private BufferedImage[] frames;
    private BufferedImage spriteSheet;
    private boolean teleported = false;
    private int frameNumber = 0;
    private int teleportCountdown = 50;

    public MagicAttack(MovableGameEntity attacker) {
        setSpeed(2);
        super.setDirection(attacker.getDirection());
        spawnAttack(attacker);
        super.setDimension(22, 20);
        loadSpriteSheet();
        loadFrames();
        CollidableRepository.getInstance().registerEntity(this);
    }

    public void update() {
        move(getDirection());
        --teleportCountdown;
        updateAnimationFrame();
        if (teleportCountdown == 5) {
            teleportAttack();
        }
    }

    public void draw(Buffer buffer) {
        buffer.drawImage(frames[frameNumber], x, y);
    }

    public void damagePlayer() {
        player.receiveDamage(damage);
    }

    // Teleports attack on players back
    private void teleportAttack() {
        teleported = true;
        super.setDirection(player.getDirection());
        if (player.getDirection() == Direction.LEFT) {
            teleport(player.getX() + player.getWidth() + 50, (player.getY() + player.getHeight() / 2) - height / 2);
        } else if (player.getDirection() == Direction.RIGHT) {
            teleport(player.getX() - 50, (player.getY() + player.getHeight() / 2) - height / 2);
        } else if (player.getDirection() == Direction.UP) { //Filthy thing
            teleport((player.getX() + player.getWidth() / 2) - width / 2, player.getY() + player.getHeight() + 50);
        } else if (player.getDirection() == Direction.DOWN) {
            teleport((player.getX() + player.getWidth() / 2) - width / 2, player.getY() - 50);
        }
    }

    private void updateAnimationFrame() {
        if (teleported) {
            frameNumber = 2;
        } else if (teleportCountdown == 10) {
            frameNumber = 1;
        }
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException ex) {}
    }

    private void loadFrames() {
        frames = new BufferedImage[3];
        frames[0] = spriteSheet.getSubimage(72, 7, 22, 20);
        frames[1] = spriteSheet.getSubimage(40, 8, 22, 20);
        frames[2] = spriteSheet.getSubimage(6, 7, 22, 20);
    }

    private void spawnAttack(MovableGameEntity attacker) {
        if (getDirection() == Direction.RIGHT) {
            teleport(attacker.getX() + attacker.getWidth() + 1, (attacker.getY() + attacker.getHeight() / 2) - height / 2);
        } else if (getDirection() == Direction.LEFT) {
            teleport(attacker.getX() - 1, (attacker.getY() + attacker.getHeight() / 2) - height / 2);
        } else if (getDirection() == Direction.DOWN) {
            teleport((attacker.getX() + attacker.getWidth() / 2) - width / 2, attacker.getY() + attacker.getHeight() + 1);
        } else if (getDirection() == Direction.UP) {
            teleport((attacker.getX() + attacker.getWidth() / 2) - width / 2, attacker.getY() -1);
        }
    }
}
