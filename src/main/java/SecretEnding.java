import engine.*;

import static java.lang.Thread.sleep;

public class SecretEnding {
    private Player player = Player.getInstance(new MovementController());
    private Monk monk;
    private Buffer buffer = RenderingEngine.getInstance().getRenderingBuffer();
    private Background background = new Background("Boss.png", 1120, 1072);

    public SecretEnding(Monk monk) {
        this.monk = monk;
        monk.reanimate();
        background.center();
        player.changeDirection(Direction.UP);
        monk.changeDirection(Direction.DOWN);
        monk.stopSinging();
    }

    public void run() {
        playerConfusion();
        monkAppearence();
        monkAttack();
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }

    private void playerConfusion() {
        player.teleport(955, 300);
        sleep(1000);
        player.changeDirection(Direction.RIGHT);
        sleep(1000);
        player.changeDirection(Direction.LEFT);
        sleep(1000);
        player.changeDirection(Direction.UP);
    }

    private void monkAppearence() {
        sleep(1000);
        monk.teleport(955, 206);
        sleep(500);
        new SoundEffect("fool.wav").play();
        sleep(10000);
    }

    private void monkAttack() {
        int distance = player.getY() - monk.getY() - 20;
        for (int i = 0; i < distance; i++) {
            monk.teleport(monk.getX(), monk.getY() + 1);
            sleep(10);
        }
        new SoundEffect("playerDeath.wav").play();
        sleep(1000);
    }
}
