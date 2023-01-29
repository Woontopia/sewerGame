import engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Player extends ControllableGameEntity {
    private static final String SPRITE_PATH = "Player.png";
    private static final int ANIMATION_SPEED = 8;
    private static Player instance;

    private BufferedImage spriteSheet;
    private BufferedImage[] downFrames;
    private BufferedImage[] upFrames;
    private BufferedImage[] rightFrames;
    private BufferedImage[] leftFrames;
    private int currentAnimationFrame = 0;
    private int nextFrame = 15;
    private double life = 1000;
    private int weaponIndex;
    private Firearm[] weapons;
    private BufferedImage weaponSprite;

    public static Player getInstance(MovementController controller) {
        if (instance == null) {
            instance = new Player(controller, 550, 350);
        }
        return instance;
    }

    private Player(MovementController controller, int x, int y) {
        super(controller);
        super.setDimension(30, 34);
        super.setSpeed(3);
        super.x = x;
        super.y = y;
        loadSpriteSheet();
        loadAnimationFrame();
        setWeapons();
        changeWeaponSprite();
        CollidableRepository.getInstance().registerEntity(this);
    }

    public void update() {
        super.moveAccordingToHandler();
        updateAnimationFrame();
        updateFireCooldown();
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
        drawHub(buffer, 150, 100);
    }

    public void receiveDamage(double damage) {
        life -= damage;
        if (life < 0) {
            life = 0;
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
        downFrames[0] = spriteSheet.getSubimage(0,1,30,34);
        downFrames[1] = spriteSheet.getSubimage(32, 0, 30, 35);
        downFrames[2] = spriteSheet.getSubimage(64, 1, 29, 34);
    }

    private void loadUPFrames() {
        upFrames = new BufferedImage[3];
        upFrames[0] = spriteSheet.getSubimage(0, 106, 28, 34);
        upFrames[1] = spriteSheet.getSubimage(32, 105, 29,35);
        upFrames[2] = spriteSheet.getSubimage(65, 106, 28, 34);
    }

    private void loadRightFrames() {
        rightFrames = new BufferedImage[3];
        rightFrames[0] = spriteSheet.getSubimage(5,71,22,34);
        rightFrames[1] = spriteSheet.getSubimage(37,70, 21, 34);
        rightFrames[2] = spriteSheet.getSubimage(69,71, 20, 34);
    }

    private void loadLeftFrames() {
        leftFrames = new BufferedImage[3];
        leftFrames[0] = spriteSheet.getSubimage(2, 36, 21, 33);
        leftFrames[1] = spriteSheet.getSubimage(35,35, 20, 35);
        leftFrames[2] = spriteSheet.getSubimage(68, 36, 19, 34);
    }

    private void setWeapons() {
        weapons = new Firearm[2];
        weapons[0] = addGun();
        weapons[1] = addWomd();
    }

    private Gun addGun() {
        Gun gun = new Gun("Gun", 5, 30, 9);
        gun.setSound("empty.wav", "gunshot.wav", "recharge.wav");
        return gun;
    }

    private Womd addWomd() {
        Womd laser = new Womd("Laser Beam", 1, 30);
        laser.setSound("laserBeam.wav");
        return laser;
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

    private void updateFireCooldown() {
        weapons[weaponIndex].updateCooldown();
    }

    private void drawHub(Buffer buffer, int width, int height) {
        int y = RenderingEngine.getInstance().getViewportHeight() - height;
        int x = RenderingEngine.getInstance().getViewportWidth() - width;
        drawWeaponSprite(buffer, x, y, height);
        drawWeaponInfo(buffer, x, y, width, height);
        drawLifeBar(buffer, x, y, height);
    }

    private void drawWeaponSprite(Buffer buffer, int x, int y, int height) {
        buffer.drawRectangle(x - 130, y , 130, height, Color.WHITE);
        buffer.drawRectangle(x - 129, y + 1, 128, height - 2, Color.BLACK);
        buffer.drawImage(weaponSprite, x - 126 , y + 15);
    }

    private void drawWeaponInfo(Buffer buffer, int x, int y, int width, int height) {
        buffer.drawRectangle(x, y, width, height, Color.WHITE);
        buffer.drawRectangle(x + 1, y + 1, width - 2, height - 2, Color.BLACK);
        buffer.drawText("Weapon : " + weapons[weaponIndex].getName(), x + 5, y + 30, Color.WHITE);
        buffer.drawText(getWeaponDescription(), x + 5, y + 50, Color.white);
        buffer.drawText(weapons[weaponIndex].getName().equals("Gun") ? "Ammo: " + weapons[weaponIndex].getAmmo() : "Ammo: Infinite", x + 5, y + 70, Color.white);
    }

    private String getWeaponDescription() {
        if (weaponIndex == 0) {
            return "Normal ass gun";
        }
        return "Gun that sound like a bird";
    }

    private void drawLifeBar(Buffer buffer, int x, int y, int height) {
        buffer.drawRectangle(x - 110, y - 50, 250, 30, Color.DARK_GRAY);
        buffer.drawRectangle(x - 110, y - 50, (int)life / 4, 30, getColor());
    }

    private Color getColor() {
        if (life >= 500) {
            return Color.GREEN;
        } else if (life > 200) {
            return Color.yellow;
        }
        return Color.RED;
    }

    private void changeWeaponSprite() {
        String imageName = weaponIndex == 0 ? "gun.png" : "laserGun.png" ;
        try {
            weaponSprite = ImageIO.read(this.getClass().getResource(imageName));
        }catch (IOException ex) {}
    }

    public Bullet fire() {
        return weapons[weaponIndex].fire(this);
    }

    public boolean canFire() {
        return weapons[weaponIndex].canFire();
    }

    public void reload() {
        weapons[weaponIndex].reload();
    }

    public void switchWeapon(int index) {
        weaponIndex = index;
        changeWeaponSprite();
    }

    public String getWeaponName() {
        return weapons[weaponIndex].name;
    }

    public boolean isDead() {
        return life == 0;
    }

    public void changeDirection(Direction direction) {
        super.setDirection(direction);
    }
}
