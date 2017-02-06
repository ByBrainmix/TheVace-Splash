package me.brainmix.splash.listeners;

import me.brainmix.splash.Splash;
import me.brainmix.splash.SplashPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

public class GameListener implements Listener {

    private Splash game;

    public GameListener(Splash game) {
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    private SplashPlayer player(PlayerEvent event) {
        return game.getPlayer(event.getPlayer());
    }

}
