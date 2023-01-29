import engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Boss extends HostileGameEntity {
    private static final String SPRITE_PATH = "RatMage.png";
    private static final int ANIMATION_SPEED = 8;

    private BufferedImage spriteSheet;
    private BufferedImage[] downFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] leftFrames;
    private ArrayList<MagicAttack> attacks = new ArrayList<>();
    private int teleportCooldown = 0;
    private int lifeStealCooldown = 0;

    public Boss(int x, int y) {
        super.teleport(x, y);
        super.setDimension(22, 36);
        super.setSpeed(2);
        // TODO set life to 500
        super.life = 500;
        super.damage = 100;
        super.setSounds("bossAttack.wav", "BossDeath.wav");
        new SoundEffect("machine.wav").play();
        loadSpriteSheet();
        loadAnimationFrames();
        CollidableRepository.getInstance().registerEntity(this);
    }

    public void update() {
        updateAnimationFrame();
        updateCooldowns();
        executeAction(actionSelection());
        updateMagicAttacks();
    }

    public void draw(Buffer buffer) {
        drawBoss(buffer);
        drawMagicAttacks(buffer);
        drawLifeBar(buffer);
    }

    public void receiveDamage(double damage) {
        life -= damage;
        if (life < 0) {
            life = 0;
        }
    }

    private void updateCooldowns() {
        updateAttackCooldown();
        updateTeleportCooldown();
        updateLifeStealCooldown();
    }

    private void updateTeleportCooldown() {
        teleportCooldown--;
        if (teleportCooldown < 0) {
            teleportCooldown = 0;
        }
    }

    private void updateLifeStealCooldown() {
        lifeStealCooldown--;
        if (lifeStealCooldown < 0) {
            lifeStealCooldown = 0;
        }
    }

    // Returns lifeSteal / attack / move / teleport
    private String actionSelection() {
        if (life <= 100 && canDoAction(lifeStealCooldown)) {
            return "lifeSteal";
        } else if (player.getWeaponName().equals("Laser Beam") && canDoAction(teleportCooldown)) {
            return "teleport";
        } else if (canDoAction(attackCooldown)) {
            return "attack";
        }
        return "move";
    }

    private boolean canDoAction(int cooldown) {
        return cooldown == 0;
    }

    private void executeAction(String action) {
        if (player.getY() < 514) {                  // SO he doesnt charge as soon as i enter the boss floor
            if (action.equals("lifeSteal")) {
                lifeSteal();
            } else if (action.equals("attack")) {
                attackPlayer();
            } else if (action.equals("teleport")) {
                teleportToSafety();
            } else {
                super.moveTowardsPlayer();
            }
        }
    }

    private void lifeSteal() {
        teleportBehindPlayer();
        lifeStealCooldown = 1000;
        damagePlayer();
    }

    private void teleportBehindPlayer() {
        Direction playerDirection = player.getDirection();
        if (playerDirection == Direction.LEFT) {
            teleport(player.getX() + player.getWidth() + 30, (player.getY() + player.getHeight() / 2) - height / 2); // width + 30
        } else if (playerDirection == Direction.RIGHT) {
            teleport(player.getX() - 30, (player.getY() + player.getHeight() / 2) - height / 2); // x - 30
        } else if (playerDirection == Direction.UP) {
            teleport((player.getX() + player.getWidth() / 2) - width / 2, player.getY() + player.getHeight() + 30); // height + 30
        } else if (playerDirection == Direction.DOWN) {
            teleport((player.getX() + player.getWidth() / 2) - width / 2, player.getY() - 30); // y - 30
        }
    }

    private void teleportToSafety() {
        Position position;
        if (player.getDirection() == Direction.LEFT) {
            position = safeLeftDirection(new Random().nextInt(4));
        } else if (player.getDirection() == Direction.RIGHT) {
            position = safeRightLocation(new Random().nextInt(4));
        } else if (player.getDirection() == Direction.UP) {
            position = safeUpLocation(new Random().nextInt(4));
        } else {
            position = safeDownLocation(new Random().nextInt(4));
        }
        teleport(position.getX() + 400, position.getY());
        teleportCooldown = 300;
    }

    private void attackPlayer() {
        super.moveTowardsPlayer();
        attacks.add(new MagicAttack(this));
        attackCooldown = 160;
    }

    private void updateMagicAttacks() {
        ArrayList<MagicAttack> killedAttacks = new ArrayList<>();
        for (MagicAttack attack : attacks) {
            attack.update();
            if (attack.collisionBoundIntersectWith(player)) {
                attack.damagePlayer();
                //killedAttacks.add(attack);
            }
            if (new Collision(attack).getAllowedSpeed(attack.getDirection()) == 0) {
                killedAttacks.add(attack);
            }

        }
        removeKilledAttacks(killedAttacks);
    }

    private void removeKilledAttacks(ArrayList<MagicAttack> killedAttacks) {
        for (MagicAttack attack : killedAttacks) {
            attacks.remove(attack);
            CollidableRepository.getInstance().unregisterEntity(attack);
        }
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

    private void loadAnimationFrames() {
        loadDownFrames();
        loadUpFrames();
        loadLeftFrames();
        loadRightFrames();
    }

    private void loadDownFrames() {
        downFrames = new BufferedImage[4];
        downFrames[0] = spriteSheet.getSubimage(5, 0, 22, 38);
        downFrames[1] = spriteSheet.getSubimage(37, 0, 22, 38);
        downFrames[2] = spriteSheet.getSubimage(69, 0, 22, 38);
        downFrames[3] = spriteSheet.getSubimage(101, 0, 22, 38);
    }

    private void loadUpFrames() {
        upFrames = new BufferedImage[4];
        upFrames[0] = spriteSheet.getSubimage(4, 49, 23, 37);
        upFrames[1] = spriteSheet.getSubimage(36, 49, 23, 37);
        upFrames[2] = spriteSheet.getSubimage(68, 49, 23, 37);
        upFrames[3] = spriteSheet.getSubimage(100, 49, 23, 37);
    }

    private void loadLeftFrames() {
        leftFrames = new BufferedImage[4];
        leftFrames[0] = spriteSheet.getSubimage(5, 96, 23, 37);
        leftFrames[1] = spriteSheet.getSubimage(37, 96, 23, 37);
        leftFrames[2] = spriteSheet.getSubimage(69, 96, 23, 37);
        leftFrames[3] = spriteSheet.getSubimage(101, 96, 23, 37);
    }

    private void loadRightFrames() {
        rightFrames = new BufferedImage[4];
        rightFrames[0] = spriteSheet.getSubimage(4, 144, 23, 37);
        rightFrames[1] = spriteSheet.getSubimage(36, 144, 23, 37);
        rightFrames[2] = spriteSheet.getSubimage(68, 144, 23, 37);
        rightFrames[3] = spriteSheet.getSubimage(100, 144, 23, 37);
    }

    private Position safeLeftDirection(int index) {
        // green
        switch (index) {
            case 0: return new Position(442, 185);
            case 1: return new Position(442, 473);
            case 2: return new Position(184, 360);
            default: return new Position(855, 362);
        }
    }

    private Position safeRightLocation(int index) {
        //blue
        switch (index) {
            case 0: return new Position(242, 188);
            case 1: return new Position(242, 363);
            case 2: return new Position(479, 473);
            default: return new Position(672, 473);
        }
    }

    private Position safeUpLocation(int index) {
        // yellow
        switch (index) {
            case 0: return new Position(885, 155);
            case 1: return new Position(653, 340);
            case 2: return new Position(86, 441);
            default: return new Position(460, 244);
        }
    }

    private Position safeDownLocation(int index) {
        // Magenta
        switch (index) {
            case 0: return new Position(461, 416);
            case 1: return new Position(888, 402);
            case 2: return new Position(653, 145);
            default: return new Position(212, 228);
        }
    }

    protected boolean bulletCollidesWithAttack(Bullet bullet) {
        boolean collision = false;
        ArrayList<MagicAttack> killedAttacks = new ArrayList<>();
        for (MagicAttack attack : attacks) {
            if (bullet.collisionBoundIntersectWith(attack)) {
                killedAttacks.add(attack);
                collision = true;
            }
        }
        removeKilledAttacks(killedAttacks);
        return collision;
    }

    private void drawBoss(Buffer buffer) {
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

    private void drawMagicAttacks(Buffer buffer) {
        int limit = attacks.size();
        MagicAttack[] attacksArray = new MagicAttack[limit];
        initialiseArray(attacksArray, limit);
        for (MagicAttack attack : attacksArray) {
            attack.draw(buffer);
        }
    }

    private void initialiseArray(MagicAttack[] attacksArray, int limit) {
        for (int i = 0; i < limit; i++) {
            attacksArray[i] = attacks.get(i);
        }
    }

    private void drawLifeBar(Buffer buffer) {
        int screenWidth = RenderingEngine.getInstance().getViewportWidth();
        int screenHeight = RenderingEngine.getInstance().getViewportHeight();
        buffer.drawText("Rat Mage", (screenWidth / 2) - 10, screenHeight - 60, Color.WHITE);
        buffer.drawRectangle((screenWidth - 500) / 2, screenHeight - 52, 502, 50, Color.BLACK);
        buffer.drawRectangle((screenWidth - 500) / 2, screenHeight - 52, (int)life, 50, getColor());
    }

    private Color getColor() {
        if (life >= 250) {
            return Color.GREEN;
        } else if (life > 100) {
            return Color.yellow;
        }
        return Color.RED;
    }

    protected void damagePlayer() {
        if (!attackSound.isRunning()) {
            attackSound.play();
        }
        life += 100;
        player.receiveDamage(damage);
    }

    protected void moveTowardsPlayer() {
        int ogXDistance = distanceToPlayer("x") + 100;
        int ogYDistance = distanceToPlayer("y") + 100;
        int xDistance = ogXDistance > 0 ? ogXDistance: ogXDistance * -1;
        int yDistance = ogYDistance > 0 ? ogYDistance: ogYDistance * -1;
        if (xDistance > yDistance) {
            //go to appropriate x coordinate
            move(ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT);
        } else {
            //go to appropriate y coordinate
            move(ogYDistance > 0 ? Direction.DOWN : Direction.UP);
        }
    }
}
