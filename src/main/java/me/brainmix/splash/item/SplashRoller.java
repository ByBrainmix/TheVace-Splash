package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.events.ItemRightClickBlockEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.splash.SplashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SplashRoller extends CustomSplashItem implements Clickable, Listener {

    private int tintCost;
    private int size;
    private Mobility mobility;
    private double damage;

    public SplashRoller(String configName, int tintCost, int size, Mobility mobility, double damage) {
        super(configName);
        this.tintCost = tintCost;
        this.size = size;
        this.mobility = mobility;
        this.damage = damage;
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    public SplashRoller(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 2);
        this.size = config.getInt(getPath() + "size", 3);
        this.mobility = config.getEnum(Mobility.class, getPath() + "mobility", Mobility.NORMAL);
        this.damage = config.getDouble(getPath() + "damage", 1);
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    @Override
    protected void init(ItemOptions itemOptions) {

    }

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent event) {
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
    public void onClick(ItemRightClickBlockEvent event) {
        if(game.getPlayer(event.getPlayer()).getTint() <= 0) return;
        if(game.getPlayer(event.getPlayer()).isSneaking()) return;

        int r = (int) ((double) size - 1) / 2;
        int x = (size - 1) % 2;

        Location middle = event.getBlock().getLocation();
        Location min = new Location(middle.getWorld(), middle.getX() - (r+x), middle.getY() - (r+x), middle.getZ() - (r+x));
        Location max = new Location(middle.getWorld(), middle.getX() + r, middle.getY() + r, middle.getZ() + r);

        game.getCurrent().paintArea(game.getPlayer(event.getPlayer()), min, max);

        middle.getWorld().getNearbyEntities(middle, r+1, r+1, r+1).forEach(e -> {
            if(!(e instanceof Player)) return;
            Player player = (Player) e;
            if(player == event.getPlayer()) return;
            game.getPlayer(player).hurt(damage, player);
        });

        game.getPlayer(event.getPlayer()).removeTint(tintCost);
    }

}
