import engine.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SewerGame extends Game {
    private PlayerController controller;
    private Player player;
    private Monk monk;
    private Background[] backgrounds;
    private int currentFloor = 0;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<HostileGameEntity> enemies = new ArrayList<>();
    private boolean bossSpawned = false;
    private boolean animationDone = false;
    private Buffer animationBuffer;

    protected void initialize() {
//        RenderingEngine.getInstance().setViewport(1920, 1080);
        RenderingEngine.getInstance().setViewport(1792, 1120);
        super.setTitle("Sewer with your rat 2020: Do you still sewer with your rat");
        controller = new PlayerController();
        player = Player.getInstance(controller);
        monk = new Monk(controller, 530, 350);
        initialiseBackgrounds();
    }

    public void update() {
        ifButtonsTouched();
        backgrounds[currentFloor].update(player, monk, enemies);
        player.update();
        monk.update();
        spawnEnemies();
        checkPoints(backgrounds[currentFloor].pointTouchedBy(player));
        ArrayList<GameEntity> killedEntities = new ArrayList<>();
        updateBullets(killedEntities);
        updateEnemies(killedEntities);
        removeKilledEntities(killedEntities);
        checkForGameOver();
    }

    public void draw(Buffer buffer) {
        backgrounds[currentFloor].draw(buffer);
        buffer.drawText(String.valueOf(getFps()), 10, 10, Color.WHITE);
        monk.draw(buffer, currentFloor);
        player.draw(buffer);
        drawEnemies(buffer);
        drawBullets(buffer);
        animationBuffer = buffer;
    }

    public void conclude() {
        stop();
    }

    private void ifButtonsTouched() {
        /*if (controller.isFullScreenPressed()) {
            RenderingEngine.getInstance().getScreen().toggleFullScreen();
        }*/
        if (controller.isQuitPressed()) {
            super.stop();
        }
        if (controller.isGunKeyPressed()) {
            player.switchWeapon(0);
        }
        if (controller.isLaserKeyPressed()) {
            player.switchWeapon(1);
        }
        if (controller.isFirePressed() && player.canFire()) {
            bullets.add(player.fire());
        }
        if (controller.isReloadPressed()) {
            player.reload();
        }
        if (controller.isExplodePressed()) {
            monk.explode(currentFloor);
        }
    }

    private void spawnEnemies() {
        if (currentFloor != 2) {
            Random random = new Random();
            if (random.nextInt(1001) < 1) {
                int limit = random.nextInt(3) + 1;
                for (int i = 0; i < limit; i++) {
                    enemies.add(backgrounds[currentFloor].spawnEnemy());
                }
            }
        } else if (!bossSpawned) {
            enemies.add(backgrounds[currentFloor].spawnEnemy());
            bossSpawned = true;
        }
    }

    private void checkPoints(String pointName) {
        if (pointName.equals("up")) {
            changeFloor(currentFloor - 1);
        } else if (pointName.equals("down")) {
            changeFloor(currentFloor + 1);
        } else if (pointName.equals("teleport")) {
            teleportPlayer();
        }
    }

    private void changeFloor(int floor) {
        backgrounds[currentFloor].unregisterEntities();
        backgrounds[floor].registerEntities();
        player.teleport(floorXposition(floor), floorYPosition(floor));
        monk.changeFloor(player.getX(), player.getY());
        currentFloor = floor;
        clearArrays();
    }

    private void clearArrays() {
        for (HostileGameEntity enemy : enemies) {
            CollidableRepository.getInstance().unregisterEntity(enemy);
        }
        enemies.clear();
        bullets.clear();
    }

    private int floorXposition(int floor) {
        if (floor == 0) {
            return backgrounds[0].locatePoint("down", "x");
        }
        if (floor == 1) {
            if (floor > currentFloor) {
                return backgrounds[1].locatePoint("up", "x");
            } else {
                return backgrounds[1].locatePoint("down", "x");
            }
        }
        if (floor == 2) {
            return backgrounds[2].locatePoint("up", "x");
        }
        System.out.println("returned zero");
        return 0;
    }

    private int floorYPosition(int floor) {
        if (floor == 0) {
            return backgrounds[0].locatePoint("down", "x");
        }
        if (floor == 1) {
            if (floor > currentFloor) {
                return backgrounds[1].locatePoint("up", "y");
            } else {
                return backgrounds[1].locatePoint("down", "y");
            }
        }
        if (floor == 2) {
            return backgrounds[2].locatePoint("up", "y");
        }
        System.out.println("returned zero y");
        return 0;
    }

    private void teleportPlayer() {
        //int xPosition = backgrounds[currentFloor].getXLocation(player);
        int yPosition = backgrounds[currentFloor].getYLocation(player);
        player.teleport(player.getX(), yPosition);
    }

    private void updateBullets(ArrayList<GameEntity> killedEntities) {
        int limit = bullets.size();
        Bullet bulletArray[] = new Bullet[limit];
        initialiseBulletArray(bulletArray, limit);
        for (Bullet bullet : bulletArray) {
            bullet.update();
            if (backgrounds[currentFloor].doesBulletCollide(bullet) || bulletCollidesWithEnemy(bullet)) {
                killedEntities.add(bullet);
            }
        }
    }

    private boolean bulletCollidesWithEnemy(Bullet bullet) {
        int limit = enemies.size();
        HostileGameEntity enemyArray[] = new HostileGameEntity[limit];
        initialiseEnemyArray(enemyArray, limit);
        for (HostileGameEntity enemy : enemyArray) {
            if (bullet.collisionBoundIntersectWith(enemy)) {
                enemy.receiveDamage(bullet.getDamage());
                return true;
            }
            if (enemy instanceof Boss && enemy.bulletCollidesWithAttack(bullet)) {
                return true;
            }
        }
        return false;
    }

    private void initialiseBulletArray(Bullet[] bulletArray, int limit) {
        for (int i = 0; i < limit; i++) {
            bulletArray[i] = bullets.get(i);
        }
    }

    private void updateEnemies(ArrayList<GameEntity> killedEntities) {
        int limit = enemies.size();
        HostileGameEntity enemyArray[] = new HostileGameEntity[limit];
        initialiseEnemyArray(enemyArray, limit);
        for (HostileGameEntity enemy : enemyArray) {
            enemy.update();
            if (enemy.getLife() == 0) {
                killedEntities.add(enemy);
            }
        }
    }

    private void checkForGameOver() {
        if (secretEndingPossible()) {
            new SecretEnding(monk).run();
            stop();
        }
        if (player.isDead()) {
            stop();
        }
    }

    private boolean secretEndingPossible() {
        return currentFloor == 2 && monk.isDead() && enemies.size() == 0 && bossSpawned;
    }

    private void initialiseEnemyArray(MovableGameEntity[] enemyArray, int limit) {
        for (int i = 0; i < limit; i++) {
            enemyArray[i] = enemies.get(i);
        }
    }

    private void removeKilledEntities(ArrayList<GameEntity> killedEntities) {
        for (GameEntity killedEntity: killedEntities) {
            if (killedEntity instanceof Bullet) {
                bullets.remove(killedEntity);
            } else if (killedEntity instanceof HostileGameEntity) {
                enemies.remove(killedEntity);
            }
            CollidableRepository.getInstance().unregisterEntity(killedEntity);
        }
    }

    private void initialiseBackgrounds() {
        backgrounds = new Background[3];
        setupBackgroundOne();
        setupBackgroundTwo();
        setupBackgroundThree();
    }

    private void setupBackgroundOne() {
        backgrounds[0] = new Background("Town.png", 1280, 1280);
        backgrounds[0].center();
        backgrounds[0].addPotentialEnemies("zombie");
        addBordersTown();
        addPointsTown();
        backgrounds[0].registerEntities();
    }

    private void setupBackgroundTwo() {
        backgrounds[1] = new Background("SideSewer.png", 2800, 800);
        backgrounds[1].centerHeight();
        backgrounds[1].addPotentialEnemies("zombie", "rat");
        addBordersSewer();
        addPointsSewer();
    }

    private void setupBackgroundThree() {
        backgrounds[2] = new Background("Boss.png", 1120, 1072);
        backgrounds[2].center();
        backgrounds[2].addPotentialEnemies("boss");
        addBordersBoss();
        addPointsBoss();
    }

    private void drawEnemies(Buffer buffer) {
        int limit = enemies.size();
        MovableGameEntity enemyArray[] = new MovableGameEntity[limit];
        initialiseEnemyArray(enemyArray, limit);
        for (MovableGameEntity enemy : enemyArray) {
            enemy.draw(buffer);
        }
        /*for (int i = 0; i < limit; i++) {
            enemyArray[i].draw(buffer);
        }*/
    }

    private void drawBullets(Buffer buffer) {
        int limit = bullets.size();
        Bullet bulletArray[] = new Bullet[limit];
        initialiseBulletArray(bulletArray, limit);
        for (Bullet bullet : bulletArray) {
            bullet.draw(buffer);
        }
        /*for (int i = 0; i < limit; i++) {
            bulletArray[i].draw(buffer);
        }*/
    }

    private void addBordersTown() {
        backgrounds[0].addBorder(159, 231, 897, 107);
        backgrounds[0].addBorder(18, 338, 144, 560);
        backgrounds[0].addBorder(8, 898, 10, 76);
        backgrounds[0].addBorder(17, 974, 145, 147);
        backgrounds[0].addBorder(159, 1118, 902, 67);
        backgrounds[0].addBorder(1054, 337, 110, 784);
        backgrounds[0].addBorder(206, 798, 212, 100);
        backgrounds[0].addBorder(206, 942, 308, 132);
        backgrounds[0].addBorder(702, 942, 308, 132);
        backgrounds[0].addBorder(798, 798, 212, 100);
        backgrounds[0].addBorder(526, 638, 164, 116);
        backgrounds[0].addBorder(206, 526, 276, 148);
        backgrounds[0].addBorder(482, 526, 48, 84);
        backgrounds[0].addBorder(482, 610, 32, 16);
        backgrounds[0].addBorder(482, 626, 16, 16);
        backgrounds[0].addBorder(530, 526, 480, 68);
        backgrounds[0].addBorder(734, 594, 276, 80);
        backgrounds[0].addBorder(686, 594, 48, 16);
        backgrounds[0].addBorder(702, 610, 32, 16);
        backgrounds[0].addBorder(702, 610, 32, 16);
        backgrounds[0].addBorder(718, 626, 16, 16);
        backgrounds[0].addBorder(866, 494, 46, 18);
        backgrounds[0].addBorder(306, 494, 46, 18);
        backgrounds[0].addBorder(498, 862, 46, 18);
        backgrounds[0].addBorder(674, 862, 46, 18);
        backgrounds[0].addBorder(546, 594, 46, 14);
        backgrounds[0].addBorder(626, 594, 49, 14);
        backgrounds[0].addBorder(734, 718, 276, 52);
        backgrounds[0].addBorder(718, 750, 52, 100);
        backgrounds[0].addBorder(702, 766, 16, 84);
        backgrounds[0].addBorder(686, 782, 16, 68);
        backgrounds[0].addBorder(478, 798, 260, 68);
        backgrounds[0].addBorder(206, 718, 276, 52);
        backgrounds[0].addBorder(478, 750, 20, 48);
        backgrounds[0].addBorder(498, 766, 16, 32);
        backgrounds[0].addBorder(514, 782, 16, 16);
        backgrounds[0].addBorder(446, 770, 32, 80);
    }

    private void addPointsTown() {
        backgrounds[0].addPoint("down", 48, 928, 16, 16);
        backgrounds[0].addPoint("zSpawn", 480, 675, 3, 3);
        backgrounds[0].addPoint("zSpawn", 733, 675, 3, 3);
        backgrounds[0].addPoint("zSpawn", 1008, 675, 3, 3);
        backgrounds[0].addPoint("zSpawn", 205, 675, 3, 3);
        backgrounds[0].addPoint("zSpawn", 157, 911, 3, 3);
        backgrounds[0].addPoint("zSpawn", 205, 1075, 3, 3);
        backgrounds[0].addPoint("zSpawn", 512, 1075, 3, 3);
        backgrounds[0].addPoint("zSpawn", 701, 1075, 3, 3);
        backgrounds[0].addPoint("zSpawn", 1008, 1075, 3, 3);
        backgrounds[0].addPoint("zSpawn", 502, 381, 3, 3);
        backgrounds[0].addPoint("zSpawn", 672, 381, 3, 3);
    }

    private void addBordersSewer() {
        backgrounds[1].addBorder(256, 192, 80, 79);
        backgrounds[1].addBorder(152, 203, 104, 34);
        backgrounds[1].addBorder(0, 236, 157, 116);
        backgrounds[1].addBorder(336, 196, 113, 41);
        backgrounds[1].addBorder(435, 237, 317, 115);
        backgrounds[1].addBorder(82, 481, 78, 109);
        backgrounds[1].addBorder(160, 481, 22, 64);
        backgrounds[1].addBorder(219, 481, 21, 63);
        backgrounds[1].addBorder(182, 481, 37, 33);
        backgrounds[1].addBorder(0, 482, 47, 107);
        backgrounds[1].addBorder(0, 589, 13, 102);
        backgrounds[1].addBorder(0, 691, 440, 29);
        backgrounds[1].addBorder(433, 546, 38, 145);
        backgrounds[1].addBorder(240, 544, 112, 62);
        backgrounds[1].addBorder(240, 481, 133, 63);
        backgrounds[1].addBorder(411, 481, 213, 63);
        backgrounds[1].addBorder(373, 481, 38, 33);
        backgrounds[1].addBorder(156, 351, 282, 1);
        backgrounds[1].addBorder(0, 350, 1, 134);
        backgrounds[1].addBorder(752, 75, 320, 517);
        backgrounds[1].addBorder(601, 542, 23, 196);
        backgrounds[1].addBorder(624, 720, 518, 18);
        backgrounds[1].addBorder(1136, 688, 65, 32);
        backgrounds[1].addBorder(1168, 672, 33, 16);
        backgrounds[1].addBorder(1184, 592, 16, 80);
        backgrounds[1].addBorder(1168, 592, 16, 32);
        backgrounds[1].addBorder(1168, 592, 16, 32); //
        backgrounds[1].addBorder(1200, 209, 105, 384);
        backgrounds[1].addBorder(1071, 62, 580, 18); //
        backgrounds[1].addBorder(1302, 209, 234, 127);
        backgrounds[1].addBorder(1515, 336, 37, 61);
        backgrounds[1].addBorder(1502, 395, 15, 139);
        backgrounds[1].addBorder(1511, 528, 1289, 44);
        backgrounds[1].addBorder(1648, 76, 52, 260);
        backgrounds[1].addBorder(1600, 336, 32, 64);
        backgrounds[1].addBorder(1632, 336, 38, 16);
        backgrounds[1].addBorder(1631, 386, 1069, 14); //
        backgrounds[1].addBorder(2700, 353, 4, 47);
        backgrounds[1].addBorder(2701, 343, 55, 9); //
        backgrounds[1].addBorder(2752, 352, 13, 48);
        backgrounds[1].addBorder(2765, 382, 35, 18); //
        backgrounds[1].addBorder(2799, 398, 1, 137);
        backgrounds[1].addBorder(2320, 399, 79, 7); //
        backgrounds[1].addBorder(2512, 399, 79, 7); //
        backgrounds[1].addBorder(1519, 80, 17, 16); //
        backgrounds[1].addBorder(542, 352, 17, 16); //
        backgrounds[1].addBorder(992, 593, 16, 15); //
    }

    private void addPointsSewer() {
        backgrounds[1].addPoint("up", 288, 271, 16, 11);
        backgrounds[1].addPoint("down", 2720, 368, 16, 16);
        backgrounds[1].addPoint("teleport", 191, 344, 20, 7);
        backgrounds[1].addPoint("teleport", 191, 514, 20, 7);
        backgrounds[1].addPoint("teleport", 382, 344, 20, 7);
        backgrounds[1].addPoint("teleport", 382, 515, 20, 7);
        backgrounds[1].addPoint("zSpawn", 672, 353, 16, 16);
        backgrounds[1].addPoint("zSpawn", 1168, 626 , 16, 16);
        backgrounds[1].addPoint("zSpawn", 1136, 83, 16, 16);
        backgrounds[1].addPoint("zSpawn", 1312, 83, 16, 16);
        backgrounds[1].addPoint("zSpawn", 1440, 83, 16, 16);
        backgrounds[1].addPoint("zSpawn", 1600, 83, 16, 16);
        backgrounds[1].addPoint("rSpawn", 1680, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 1856, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 2032, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 2176, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 2432, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 2640, 403, 3, 3);
        backgrounds[1].addPoint("rSpawn", 2768, 403, 3, 3);
    }

    private void addBordersBoss() {
        backgrounds[2].addBorder(493, 512, 51, 160);
        backgrounds[2].addBorder(592, 512, 53, 160);
        backgrounds[2].addBorder(486, 671, 10, 310);
        backgrounds[2].addBorder(496, 976, 152, 5);
        backgrounds[2].addBorder(640, 672, 8, 305);
        backgrounds[2].addBorder(521, 672, 16, 10);
        backgrounds[2].addBorder(600, 672, 17, 10);
        backgrounds[2].addBorder(624, 693, 16, 52);
        backgrounds[2].addBorder(624, 757, 16, 52);
        backgrounds[2].addBorder(624, 821, 16, 52);
        backgrounds[2].addBorder(624, 896, 16, 16);
        backgrounds[2].addBorder(496, 896, 16, 16);
        backgrounds[2].addBorder(496, 821, 15, 52);
        backgrounds[2].addBorder(496, 757, 16, 53);
        backgrounds[2].addBorder(496, 693, 15, 52);
        backgrounds[2].addBorder(643, 512, 403, 19);
        backgrounds[2].addBorder(1040, 57, 11, 460);
        backgrounds[2].addBorder(158, 39, 817, 9);
        backgrounds[2].addBorder(77, 46, 83, 18);
        backgrounds[2].addBorder(73, 60, 7, 457);
        backgrounds[2].addBorder(79, 512, 418, 7);
        backgrounds[2].addBorder(960, 48, 84, 16);
        backgrounds[2].addBorder(480, 47, 16, 81);
        backgrounds[2].addBorder(496, 113, 49, 15);
        backgrounds[2].addBorder(591, 113, 65, 14);
        backgrounds[2].addBorder(640, 48, 16, 66);
        backgrounds[2].addBorder(495, 47, 17, 17);
        backgrounds[2].addBorder(496, 80, 12, 32);
        backgrounds[2].addBorder(628, 80, 12, 32);
        backgrounds[2].addBorder(624, 48, 16, 16);
        backgrounds[2].addBorder(466, 108, 12, 33);
        backgrounds[2].addBorder(466, 188, 12, 33);
        backgrounds[2].addBorder(466, 284,12, 33);
        backgrounds[2].addBorder(466, 380, 12, 33);
        backgrounds[2].addBorder(466, 476, 12, 33);
        backgrounds[2].addBorder(658, 476, 12, 33);
        backgrounds[2].addBorder(658, 380, 12, 33);
        backgrounds[2].addBorder(658, 284, 12, 33);
        backgrounds[2].addBorder(658, 188, 12, 33);
        backgrounds[2].addBorder(658, 108, 12, 33);
        backgrounds[2].addBorder(208, 194, 32, 30);
        backgrounds[2].addBorder(208, 354, 32, 30);
        backgrounds[2].addBorder(208, 383, 16, 17);
        backgrounds[2].addBorder(416, 48, 16, 16);
        backgrounds[2].addBorder(80, 480, 32, 30);
        backgrounds[2].addBorder(1008, 480, 32, 30);
        backgrounds[2].addBorder(880, 354, 32, 30);
        backgrounds[2].addBorder(896, 384, 16, 16);
        backgrounds[2].addBorder(880, 194, 32, 30);
        backgrounds[2].addBorder(704, 48, 16, 16);
    }

    private void addPointsBoss() {
        backgrounds[2].addPoint("up", 560, 930, 16, 11);
        backgrounds[2].addPoint("bSpawn", 557, 134, 3, 3);
    }
}
