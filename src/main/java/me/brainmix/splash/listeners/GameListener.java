package me.brainmix.splash.listeners;

import me.brainmix.splash.Splash;
import me.brainmix.splash.SplashPlayer;
import me.brainmix.splash.utils.ColoredBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class GameListener implements Listener {

    private Splash game;

    public GameListener(Splash game) {
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {

        if(!game.isIngame()) return;
        if(event.getPlayer().isSneaking()) return;

        event.setCancelled(true);
        event.getPlayer().setSneaking(false);

        SplashPlayer player = player(event);

        Location middle = player.getPlayer().getLocation();
        Location min = new Location(middle.getWorld(), middle.getX() - 2, middle.getY() - 2, middle.getZ() - 2);
        Location max = new Location(middle.getWorld(), middle.getX() + 2, middle.getY() + 2, middle.getZ() + 2);

        game.getCurrent().paintArea(player, min, max);

    }

    private SplashPlayer player(PlayerEvent event) {
        return game.getPlayer(event.getPlayer());
    }

}
