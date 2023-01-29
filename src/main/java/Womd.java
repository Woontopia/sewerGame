import engine.SoundEffect;

import java.awt.*;

public class Womd extends Firearm {
    private SoundEffect fireSound;

    public Womd(String name, double damage, int cooldown) {
        super.name = name;
        super.damage = damage;
        super.cooldown = cooldown;
        super.ammo = 99999;
    }

    public void updateCooldown() {
        if (cooldown <= 0) {
            cooldown = 30;//filthy thing
        }
    }

    public boolean canFire() {
        if (cooldown <= 5) {
            cooldown--;
            return false;
        }
        return true;
    }

    public Bullet fire(Player player) {
        if (!fireSound.isRunning()) {
            fireSound.play();
        }
        cooldown--;
        return new Bullet(player, 4, 8, damage, Color.red);
    }

    public void reload() {

    }

    public void setSound(String sound) {
        fireSound = new SoundEffect(sound);
        fireSound.setVolume(SoundEffect.Volume.MEDIUM);
    }
}
