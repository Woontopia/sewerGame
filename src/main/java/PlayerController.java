import engine.MovementController;

import java.awt.event.KeyEvent;

public class PlayerController extends MovementController {

    private final int QUIT = KeyEvent.VK_ESCAPE;
    private final int FIRE = KeyEvent.VK_SPACE;
    private final int FULLSCREEN = KeyEvent.VK_F;
    private final int RELOAD = KeyEvent.VK_R;
    private final int EXPLODE = KeyEvent.VK_E;
    private final int SWITCH_TO_GUN = KeyEvent.VK_1;
    private final int SWITCH_TO_LASER = KeyEvent.VK_2;

    public PlayerController() {
        super.registerKey(QUIT);
        super.registerKey(FIRE);
        super.registerKey(FULLSCREEN);
        super.registerKey(RELOAD);
        super.registerKey(EXPLODE);
        super.registerKey(SWITCH_TO_GUN);
        super.registerKey(SWITCH_TO_LASER);
    }

    public boolean isFirePressed() {
        return super.isKeyPressed(FIRE);
    }

    public boolean isQuitPressed() {
        return super.isKeyPressed(QUIT);
    }

    public boolean isFullScreenPressed() {
        return super.isKeyPressed(FULLSCREEN);
    }

    public boolean isReloadPressed() {
        return super.isKeyPressed(RELOAD);
    }

    public boolean isExplodePressed() {
        return super.isKeyPressed(EXPLODE);
    }

    public boolean isGunKeyPressed() {
        return super.isKeyPressed(SWITCH_TO_GUN);
    }

    public boolean isLaserKeyPressed() {
        return super.isKeyPressed(SWITCH_TO_LASER);
    }
}
