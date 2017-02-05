package me.brainmix.splash;

import me.brainmix.itemapi.api.ItemRegister;
import me.brainmix.splash.listeners.GameListener;
import me.brainmix.splash.listeners.GameOptionsListener;
import me.vicevice.general.api.games.GameApi;
import me.vicevice.general.api.games.TeamGame;
import me.vicevice.general.api.games.interfaces.GameOptions;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;


import java.util.Arrays;

public class Splash extends TeamGame<SplashPlayer, SplashMap, SplashTeam> {

    private GameOptions gameOptions;

    public static Splash a() {
        return (Splash) GameApi.getCurrent();
    }

    @Override
    public void loadBefore() {
        gameOptions = new GameOptions();
        gameOptions.setBlockBreak(false);
        gameOptions.setBlockBuild(false);
        gameOptions.setBlockDropping(false);
        gameOptions.setBlockPickup(false);
        gameOptions.setBlockDurability(false);
        gameOptions.setBlockFire(false);
        gameOptions.setClearInventoryOnStart(true);
        gameOptions.setVoteMaps(true);
        gameOptions.setGraceperiod(true);
        gameOptions.setBlockInteract(true);
        gameOptions.setSetNewScoreboardOnStart(true);
        gameOptions.setMapDifficulty(Difficulty.PEACEFUL);
        gameOptions.setIngameMode(GameMode.ADVENTURE);
        gameOptions.setPrepareWorlds(false);
        gameOptions.setHandleChat(false);
        gameOptions.setDebug(true);
    }

    @Override
    public void loadAfter() {
        log("----------------------------------", "", "   loading Splash", "", "----------------------------------");


        new GameOptionsListener(this);
        new GameListener(this);

        ItemRegister  itemRegister = new ItemRegister(this);
    }

    public void log(String... message) {
        Arrays.asList(message).forEach(getLogger()::info);
    }

    @Override
    public GameOptions getOptions() {
        return gameOptions;
    }


    @Override
    public Class<SplashMap> getMapClass() {
        return SplashMap.class;
    }

    @Override
    public Class<SplashPlayer> getPlayerClass() {
        return SplashPlayer.class;
    }

    @Override
    public Class<SplashTeam> getTeamClass() {
        return SplashTeam.class;
    }

}
