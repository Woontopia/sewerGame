import engine.*;

public abstract class HostileGameEntity extends MovableGameEntity {
    protected double damage;
    protected double life;
    protected SoundEffect attackSound;
    protected SoundEffect deathSound;
    protected int currentAnimationFrame = 0;
    protected int attackCooldown;
    protected int nextFrame = 15;
    Player player = Player.getInstance(new MovementController());
    private Collision collision = new Collision(this);

    public abstract void receiveDamage(double damage);

    public double getLife() {
        if (life == 0 && !deathSound.isRunning()) {
            deathSound.play();
        }
        return life;
    }

    protected int distanceToPlayer(String coordinate) {
        return coordinate.equals("x") ? (player.getX() + (player.getWidth() / 2)) - (x + (width / 2)): (player.getY() + (player.getHeight() / 2)) - (y + (height / 2));
    }

    protected void damagePlayer() {
        if (this.collisionBoundIntersectWith(player) && attackCooldown == 0) {
            if (!attackSound.isRunning()) {
                attackSound.play();
            }
            player.receiveDamage(damage);
            attackCooldown = 10;
        }
    }

    protected void updateAttackCooldown() {
        attackCooldown--;
        if (attackCooldown < 0) {
            attackCooldown = 0;
        }
    }

    protected void moveTowardsPlayer() {
        //Direction direction;
        int ogXDistance = distanceToPlayer("x");
        int ogYDistance = distanceToPlayer("y");
        int xDistance = ogXDistance > 0 ? ogXDistance: ogXDistance * -1;
        int yDistance = ogYDistance > 0 ? ogYDistance: ogYDistance * -1;
        if (xDistance > yDistance) {
//            direction = ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT;
//            if (collision.getAllowedSpeed(direction) == 0) {
//                direction = ogYDistance > 0 ? Direction.DOWN : Direction.UP;
//            }
            //go to appropriate x coordinate
            move(ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT);
        } else {
//            direction = ogYDistance > 0 ? Direction.DOWN : Direction.UP;
//            if (collision.getAllowedSpeed(direction) == 0) {
//                direction = ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT;
//            }
            //go to appropriate y coordinate
            move(ogYDistance > 0 ? Direction.DOWN : Direction.UP);
        }
        //move(direction);
//        int ogXDistance = distanceToPlayer("x");
//        int ogYDistance = distanceToPlayer("y");
//        int xDistance = ogXDistance > 0 ? ogXDistance: ogXDistance * -1;
//        int yDistance = ogYDistance > 0 ? ogYDistance: ogYDistance * -1;
//        if (xDistance > yDistance) {
//            //Direction direction = ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT;
//            //go to appropriate x coordinate
//            move(ogXDistance > 0 ? Direction.RIGHT : Direction.LEFT);
//        } else {
//            //go to appropriate y coordinate
//            move(ogYDistance > 0 ? Direction.DOWN : Direction.UP);
//        }
    }

    protected boolean bulletCollidesWithAttack(Bullet bullet) {
        return false;
    }

    protected void setSounds(String attack, String death) {
        attackSound = new SoundEffect(attack);
        attackSound.setVolume(SoundEffect.Volume.LOW);
        deathSound = new SoundEffect(death);
        deathSound.setVolume(SoundEffect.Volume.LOW);
    }
}
