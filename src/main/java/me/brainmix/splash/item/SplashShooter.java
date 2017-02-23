package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.delay.ItemDelay;
import me.brainmix.itemapi.api.events.ItemProjectileFlyTickEvent;
import me.brainmix.itemapi.api.events.ItemProjectileHitEvent;
import me.brainmix.itemapi.api.events.ItemProjectileHitPlayerEvent;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.itemapi.api.interfaces.Shootable;
import me.brainmix.splash.SplashPlayer;
import me.brainmix.splash.utils.BUtils;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class SplashShooter extends CustomSplashItem implements Clickable, Shootable {

    private int tintCost;
    private int fireRate;
    private double velMulti;
    private double damage;
    private ClickSound clickSound;
    private ItemDelay itemDelay;


    public SplashShooter(String configName, int tintCost, int fireRate, double velMulti, double damage, ClickSound clickSound, ItemDelay itemDelay) {
        super(configName);
        this.tintCost = tintCost;
        this.fireRate = fireRate;
        this.velMulti = velMulti;
        this.damage = damage;
        this.clickSound = clickSound;
        this.itemDelay = itemDelay;
    }

    public SplashShooter(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 2);
        this.fireRate = config.getInt(getPath() + "fireRate", 5);
        this.velMulti = config.getDouble(getPath() + "velMulti", 1.0);
        this.damage = config.getDouble(getPath() + "damage", 1.0);
        this.clickSound = BUtils.getSound(config, getPath() + "clickSound");
        this.itemDelay = BUtils.getItemDelay(config, getPath() + "itemDelay");
    }

    @Override
    protected void init(ItemOptions options) {
        options.setClickSound(clickSound);
        options.setAutoItemDelay(itemDelay);
    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {
        SplashPlayer player = game.getPlayer(event.getPlayer());
        if(player.removeTint(tintCost)) return;
        CustomItem self = this;
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                Snowball ball = event.getUser().shootProjectile(self, Snowball.class, event.getPlayer().getLocation().getDirection().multiply(velMulti));
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(ball.getEntityId());
                Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
                if (i == fireRate) cancel();
                i++;
            }
        }.runTaskTimer(game, 0, 1);
    }

    @ItemHandler
    public void onFly(ItemProjectileFlyTickEvent event) {
        game.getPlayer(event.getPlayer()).getTeam().playParticle(event.getProjectile().getLocation(), 0.1, 0.1, 0.1, 0.1f, 1);
        if(event.getTimeInAir() % 3 != 0) return;
        BlockIterator iterator = new BlockIterator(event.getProjectile().getWorld(), event.getProjectile().getLocation().toVector(), new Vector(0, -1, 0), 0, 20);
        for(Block block = null; iterator.hasNext(); block = iterator.next()) {
            if(block == null) continue;
            if(!block.getType().isSolid()) continue;

            game.paintArea(game.getPlayer(event.getPlayer()), block.getLocation(), block.getLocation());
            break;
        }
    }

    @ItemHandler
    public void onHit(ItemProjectileHitEvent event) {
        Location min = event.getProjectile().getLocation().add(-1, -1, -1);
        Location max = min.clone().add(2, 2, 2);
        game.paintArea(game.getPlayer(event.getPlayer()), min, max);
    }

    @ItemHandler
    public void onHitPlayer(ItemProjectileHitPlayerEvent event) {
        game.getPlayer(event.getEntity()).hurt(damage, event.getPlayer());
    }

}
