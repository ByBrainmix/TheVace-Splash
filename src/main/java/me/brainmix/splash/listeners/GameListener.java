package me.brainmix.splash.listeners;

import me.brainmix.splash.Splash;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

    private Splash game;

    public GameListener(Splash game) {
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, game);
    }

}
