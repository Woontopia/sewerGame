public abstract class Firearm {
    protected String name;
    protected double damage;
    protected int cooldown;
    protected int ammo;

    public abstract void updateCooldown();

    public abstract boolean canFire();

    public abstract Bullet fire(Player player);

    public abstract void reload();

    public int getAmmo() {
        return ammo;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }
}
