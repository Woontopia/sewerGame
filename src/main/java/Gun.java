import engine.SoundEffect;

import java.awt.*;

public class Gun extends Firearm {
    private int baseAmmo;
    private SoundEffect emptySound;
    private SoundEffect fireSound;
    private SoundEffect rechargeSound;

    public Gun(String name, int damage, int cooldown, int ammo) {
        super.name = name;
        super.damage = damage;
        super.cooldown = cooldown;
        this.baseAmmo = ammo;
        super.ammo = baseAmmo;
    }

    public void updateCooldown() {
        cooldown--;
        if (cooldown <= 0) {
            cooldown = 0;
        }
    }

    public boolean canFire() {
        if (ammo == 0 && !emptySound.isRunning()) {
            emptySound.play();
            return false;
        }
        return cooldown == 0 && ammo > 0;
    }

    public Bullet fire(Player player) {
        if (!fireSound.isRunning()) {
            fireSound.stop();
            //filthy thing
        }
        fireSound.play();
        cooldown = 30;
        ammo--;
        return new Bullet(player, 4, 6, damage, Color.BLACK);
    }

    public void reload() {
        if (!rechargeSound.isRunning()) {
            rechargeSound.play();
        }
        ammo = baseAmmo;
    }

    public void setSound(String empty, String fire, String recharge) {
        this.emptySound = new SoundEffect(empty);
        emptySound.setVolume(SoundEffect.Volume.MEDIUM);
        this.fireSound = new SoundEffect(fire);
        fireSound.setVolume(SoundEffect.Volume.MEDIUM);
        this.rechargeSound = new SoundEffect(recharge);
        rechargeSound.setVolume(SoundEffect.Volume.MEDIUM);
    }
}
