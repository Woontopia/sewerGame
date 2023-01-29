import engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Background extends MovableGameEntity {
    private final String SPRITE_PATH;

    private BufferedImage spriteSheet;
    private int rightGap = 300;
    private int leftGap = 300;
    private ArrayList<Border> borders = new ArrayList<>();
    private ArrayList<Point> points = new ArrayList<>();
    private String[] possibleEnemies;

    public Background(String path, int width, int height) {
        SPRITE_PATH = path;
        teleport(0, 0);
        setDimension(width, height);
        setSpeed(3);
        loadSpriteSheet();
    }

    public void update(Player player, Monk monk, ArrayList<HostileGameEntity> enemies) {
        if (player.hasMoved()) {
            moveCameraRight(player, monk, enemies);
            moveCameraLeft(player, monk, enemies);
            updateLocations();
        }
    }

    public void draw(Buffer buffer) {
        buffer.drawImage(spriteSheet, x, y);
        /*for (Border border : borders) {
            buffer.drawRectangle(border.getX(), border.getY(), border.getWidth(), border.getHeight(), Color.GREEN);
        }*/
        /*for (Point point : points) {
           buffer.drawRectangle(point.getX(), point.getY(), point.getWidth(), point.getHeight(), Color.red);
        }*/
    }

    public void addBorder(int x, int y, int width, int height) {
        borders.add(new Border(x + this.x, y + this.y, width, height));
    }

    public void addPoint(String name, int x, int y, int width, int height) {
        points.add(new Point(name, x + this.x, y + this.y, width, height));
    }

    public void center() {
        this.x = (RenderingEngine.getInstance().getViewportWidth() / 2) - (width / 2);
        centerHeight();
    }

    public void centerHeight() {
        this.y = (RenderingEngine.getInstance().getViewportHeight() / 2) - (height / 2);
    }

    public boolean doesBulletCollide(Bullet bullet) {
        return borderCollidesWithBullet(bullet) || pointCollidesWithBullet(bullet);
    }

    public String pointTouchedBy(Player player) {
        for (Point point: points) {
            if (player.collisionBoundIntersectWith(point)) {
                return point.getName();
            }
        }
        return "none";
    }

    public void unregisterEntities() {
        unregisterBorders();
        unregisterPoints();
    }

    public void addPotentialEnemies(String enemy) {
        possibleEnemies = new String[1];
        possibleEnemies[0] = enemy;
    }

    public void addPotentialEnemies(String enemyOne, String enemyTwo) {
        possibleEnemies = new String[2];
        possibleEnemies[0] = enemyOne;
        possibleEnemies[1] = enemyTwo;
    }
    
    public void registerEntities() {
        registerBorders();
        registerPoints();
    }

    public HostileGameEntity spawnEnemy() {
        return spawnNewEnemyOfType(getRandomEnemy());
    }

    public int getXLocation(Player player) {
        for (Point point: points) {
            if (player.collisionBoundIntersectWith(point)) {
                return point.getX() + (point.getWidth() / 2);
            }
        }
        return 0;
    }

    public int locatePoint(String pointName, String coordinate) {
        for (Point point: points) {
            if (point.getName().equals(pointName)) {
                return coordinate.equals("x") ? point.getX(): point.getY();
            }
        }
        return 0;
    }

    public int getYLocation(Player player) {
        for (Point point: points) {
            if (player.collisionBoundIntersectWith(point)) {
                return oppositePointY(point);
            }
        }
        return 0;
    }

    private void moveCameraRight(Player player, Monk monk, ArrayList<HostileGameEntity> enemies) {
        if (shouldMoveBackgroundRight(player)) {
            moveLeft();
            player.teleport(player.getX() - 3, player.getY());
            monk.teleport(monk.getX() - 3, monk.getY());
            moveAllEnemies(enemies, "back");
            rightGap += 5;
        } else {
            rightGap = 300;
        }
    }

    private void moveCameraLeft(Player player, Monk monk, ArrayList<HostileGameEntity> enemies) {
        if (shouldMoveBackgroundLeft(player)) {
            player.teleport(player.getX() + 3, player.getY());
            monk.teleport(monk.getX() + 3, monk.getY());
            moveAllEnemies(enemies, "forward");
            moveRight();
            leftGap += 5;
        } else {
            leftGap = 300;
        }
    }

    private void updateLocations() {
        if (hasMoved()) {
            updatePointsLocation();
            updateBordersLocation();
        }
    }

    private void updatePointsLocation() {
        for (Point point: points) {
            point.teleport(point.getOriginalX() + x, point.getY());
        }
    }

    private void updateBordersLocation() {
        for (Border border: borders) {
            border.teleport(border.getOriginalX() + x, border.getY());
        }
    }

    private void moveAllEnemies(ArrayList<HostileGameEntity> enemies, String direction) {
        for (HostileGameEntity enemy : enemies) {
            int distance = direction.equals("back") ? -3 : 3;
            enemy.teleport(enemy.getX() + distance, enemy.getY());
        }
    }

    private boolean shouldMoveBackgroundRight(Player player) {
        return player.getX() + rightGap >= RenderingEngine.getInstance().getViewportWidth() && width + x > RenderingEngine.getInstance().getViewportWidth() && player.getDirection() == Direction.RIGHT;
    }

    private boolean shouldMoveBackgroundLeft(Player player) {
        return player.getX() - leftGap <= 0 && x != 0 && player.getDirection() == Direction.LEFT;
    }

    private int oppositePointY(Point oppositePoint) {
        for (Point point: points) {
            if ((point.getX() == oppositePoint.getX()) && point.getY() != oppositePoint.getY()) {
                return point.getY();
            }
        }
        return 0;
    }

    private String getRandomEnemy() {
        return possibleEnemies[new Random().nextInt(possibleEnemies.length)];
    }

    private HostileGameEntity spawnNewEnemyOfType(String enemyType) {
        Point randomPoint = getRandomSpawnPointOfType(enemyType);
        if (enemyType.equalsIgnoreCase("Zombie")) {
            return new Zombie(randomPoint.getX(), randomPoint.getY());
        } else if (enemyType.equalsIgnoreCase("boss")) {
            return new Boss(randomPoint.getX(), randomPoint.getY());
        }
        return new Rat(randomPoint.getX(), randomPoint.getY());
    }

    private Point getRandomSpawnPointOfType(String enemyType) {
        Point randomPoint;
        do {
            randomPoint = points.get(new Random().nextInt(points.size()));
        }while (!pointIsForCorrectEnemy(randomPoint.getName(), enemyType));
        return randomPoint;
    }

    private boolean pointIsForCorrectEnemy(String pointName, String enemType) {
        return pointName.charAt(0) == enemType.charAt(0);
    }

    private boolean borderCollidesWithBullet(Bullet bullet) {
        for (Border border: borders) {
            if (bullet.collisionBoundIntersectWith(border)) {
                return true;
            }
        }
        return false;
    }

    private boolean pointCollidesWithBullet(Bullet bullet) {
        for (Point point: points) {
            if (bullet.collisionBoundIntersectWith(point)) {
                return true;
            }
        }
        return false;
    }

    private void unregisterBorders() {
        for (Border border: borders) {
            CollidableRepository.getInstance().unregisterEntity(border);
        }
    }

    private void unregisterPoints() {
        for (Point point: points) {
            CollidableRepository.getInstance().unregisterEntity(point);
        }
    }
    
    private void registerPoints() {
        for (Point point: points) {
            if (!point.getName().equals("zSpawn") && !point.getName().equals("rSpawn") && !point.getName().equals("bSpawn")) {
                CollidableRepository.getInstance().registerEntity(point);
            }
        }
    }
    
    private void registerBorders() {
        for (Border border: borders) {
            CollidableRepository.getInstance().registerEntity(border);
        }
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(this.getClass().getResource(SPRITE_PATH));
        }catch (IOException ex) {}
    }

    public void update() {

    }
}
