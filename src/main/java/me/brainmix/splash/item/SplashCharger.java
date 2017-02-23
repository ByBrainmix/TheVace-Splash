package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.delay.ItemDelay;
import me.brainmix.itemapi.api.delay.ItemDelayChar;
import me.brainmix.itemapi.api.events.*;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.itemapi.api.interfaces.Shootable;
import me.brainmix.splash.utils.BUtils;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class SplashCharger extends CustomSplashItem implements Clickable, Shootable, Listener {

    private int tintCost;
    private double velMulti;
    private Mobility mobility;
    private ClickSound clickSound;
    private ItemDelayChar chargeTime;
    private double damage;
    private int size;
    private Map<Player, Integer> delays = new HashMap<>();

    public SplashCharger(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 30);
        this.velMulti = config.getDouble(getPath() + "velMulti", 2.0);
        this.mobility = config.getEnum(Mobility.class, getPath() + "mobility", Mobility.NORMAL);
        this.clickSound = BUtils.getSound(config, getPath() + "clickSound");
        this.chargeTime = BUtils.getItemDelay(config, getPath() + "chargeTime");
        this.damage = config.getDouble(getPath() + "damage", 10);
        this.size = config.getInt(getPath() + "size", 3);
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    @Override
    protected void init(ItemOptions itemOptions) {
        itemOptions.setClickSound(clickSound);
        itemOptions.setItemDelay(new ItemDelay(10));
    }

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent event) {
        if(game.getPlayer(event.getPlayer()).isCharging()) {
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            game.getPlayer(event.getPlayer()).setCharging(false);
        }
        if(compare(event.getPlayer().getInventory().getItem(event.getNewSlot()))) {
            switch (mobility) {
                case FAST:
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
                    break;
                case SLOW:
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 1, false, false));
            }
            game.getPlayer(event.getPlayer()).setMobility(mobility);
        } else if(compare(event.getPlayer().getInventory().getItem(event.getPreviousSlot()))) {
            event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            game.getPlayer(event.getPlayer()).setMobility(Mobility.NORMAL);
        }
    }

    @ItemHandler
    public void onClick(ItemRightClickHoldEvent event) {
        if(!delays.containsKey(event.getPlayer())) delays.put(event.getPlayer(), 0);
        int time = delays.get(event.getPlayer());
        delays.put(event.getPlayer(), time + 1);
        chargeTime.display(event.getPlayer(), time);
        game.getPlayer(event.getPlayer()).setCharging(true);
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 2, false ,false));
        new ClickSound(Sound.NOTE_PIANO, 1, ((float) time / (float) chargeTime.getDelay()) + 0.5f).play(event.getPlayer());

        if(time >= chargeTime.getDelay()) {
            if(game.getPlayer(event.getPlayer()).removeTint(tintCost)) return;
            delays.put(event.getPlayer(), 0);
            Fireball ball = event.getUser().shootProjectile(this, Fireball.class, event.getPlayer().getLocation().getDirection().multiply(velMulti));
            ball.setDirection(event.getPlayer().getLocation().getDirection().multiply(velMulti));
            ball.setIsIncendiary(false);
            ball.setYield(0);
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ball.getEntityId());
            Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
        }

    }

    @ItemHandler
    public void onRelease(ItemRightClickReleaseEvent event) {
        chargeTime.display(event.getPlayer(), 0);
        delays.put(event.getPlayer(), 0);
        game.getPlayer(event.getPlayer()).setCharging(false);
    }

    @ItemHandler
    public void onFly(ItemProjectileFlyTickEvent event) {
        game.getPlayer(event.getPlayer()).getTeam().playParticle(event.getProjectile().getLocation(), 0.1, 0.1, 0.1, 0.1f, 4);
        if(event.getTimeInAir() % 3 != 0) return;
    }

    @ItemHandler
    public void onHit(ItemProjectileHitEvent event) {
        int r = (int) ((double) size - 1) / 2;
        int x = (size - 1) % 2;

        Location middle = event.getProjectile().getLocation();
        Location min = new Location(middle.getWorld(), middle.getX() - (r+x), middle.getY() - (r+x), middle.getZ() - (r+x));
        Location max = new Location(middle.getWorld(), middle.getX() + r, middle.getY() + r, middle.getZ() + r);
        game.getCurrent().paintArea(game.getPlayer(event.getPlayer()), min, max);
    }

    @ItemHandler
    public void onHitPlayer(ItemProjectileHitPlayerEvent event) {
        game.getPlayer(event.getEntity()).hurt(damage, event.getPlayer());
        int r = (int) ((double) size - 1) / 2;
        int x = (size - 1) % 2;

        Location middle = event.getProjectile().getLocation();
        Location min = new Location(middle.getWorld(), middle.getX() - (r+x), middle.getY() - (r+x), middle.getZ() - (r+x));
        Location max = new Location(middle.getWorld(), middle.getX() + r, middle.getY() + r, middle.getZ() + r);
        game.getCurrent().paintArea(game.getPlayer(event.getPlayer()), min, max);
    }


}
