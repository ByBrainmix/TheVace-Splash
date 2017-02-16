package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.delay.ItemDelay;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.splash.SplashPlayer;
import me.brainmix.splash.utils.BUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashShooter extends CustomSplashItem implements Clickable, Listener {

    private static List<Projectile> projectiles = new ArrayList<>();

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
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    public SplashShooter(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 2);
        this.fireRate = config.getInt(getPath() + "fireRate", 5);
        this.velMulti = config.getDouble(getPath() + "velMulti", 1.0);
        this.damage = config.getDouble(getPath() + "damage", 1.0);
        this.clickSound = BUtils.getSound(config, getPath() + "clickSound");
        this.itemDelay = BUtils.getItemDelay(config, getPath() + "itemDelay");
        Bukkit.getPluginManager().registerEvents(this, game);
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

        BukkitTask bukkitTask = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                projectiles.add(new Projectile(event.getPlayer()));
                if (i == fireRate) cancel();
                i++;
            }
        }.runTaskTimer(game, 0, 1);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {

        Projectile projectile = projectiles.stream().filter(p -> p.equals(event.getEntity())).findFirst().orElse(null);
        if (projectile == null) return;

        Location min = projectile.snowball.getLocation().add(-1, -1, -1);
        Location max = min.clone().add(2, 2, 2);

        game.paintArea(projectile.shooter, min, max);
        projectile.remove();

    }

    private class Projectile {

        private Snowball snowball;
        private SplashPlayer shooter;
        private int schedulerID;

        public Projectile(Player player) {
            snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(velMulti));
            shooter = game.getPlayer(player);
            schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(game, () -> {

                BlockIterator iterator = new BlockIterator(snowball.getWorld(), snowball.getLocation().toVector(), new Vector(0, -1, 0), 0, 20);
                for(Block block = null; iterator.hasNext(); block = iterator.next()) {
                    if(block == null) continue;
                    if(!block.getType().isSolid()) continue;

                    game.paintArea(shooter, block.getLocation(), block.getLocation());
                    break;
                }

            }, 0, 3);
        }

        public boolean equals(Entity entity) {
            return entity.getEntityId() == snowball.getEntityId();
        }

        public void remove() {
            Bukkit.getScheduler().cancelTask(schedulerID);
        }

    }

}
