package me.brainmix.splash;

import me.brainmix.itemapi.api.ItemRegister;
import me.brainmix.splash.listeners.GameListener;
import me.brainmix.splash.listeners.GameOptionsListener;
import me.vicevice.general.api.games.GameApi;
import me.vicevice.general.api.games.TeamGame;
import me.vicevice.general.api.games.interfaces.GameOptions;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Splash extends TeamGame<SplashPlayer, SplashMap, SplashTeam> {

    private GameOptions gameOptions;
    private SplashTeam team1;
    private SplashTeam team2;
    private int timeleft;
    private int gameLoop;

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
        gameOptions.setHandleChat(true);
        gameOptions.setDebug(true);

    }

    @Override
    public void loadAfter() {
        log("----------------------------------", "", "   loading Splash", "", "----------------------------------");

        new GameOptionsListener(this);
        new GameListener(this);

        team1 = getTeams().stream().filter(m -> m.getName().equals("red")).findFirst().orElse(null);
        team2 = getTeams().stream().filter(m -> m.getName().equals("blue")).findFirst().orElse(null);

        ItemRegister  itemRegister = new ItemRegister(this);
        itemRegister.registerAll(SplashItem.class);
        timeleft = getGameConfig().getInt("Settings.gametime", 3600);
    }

    @Override
    public void onLobbyTick(int secondsLeft) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (secondsLeft < 6 && secondsLeft != 0) {
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            }
            if (secondsLeft == 0) {
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
            }
        }
    }

    @Override
    public void onStart() {
        gameLoop = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            display();
            if(timeleft == 0) onEnd();
            else timeleft--;
            if(timeleft % 20 == 0) {
                getPlayers().forEach(SplashPlayer::updateDisplayName);
            }
            getPlayers().forEach(SplashPlayer::onUpdate);
        }, 0, 0);
    }

    public void onEnd() {
        Bukkit.getScheduler().cancelTask(gameLoop);
        if(team1.getPercentage() == team2.getPercentage()) {
            noTeamWins();
        } else {
            teamWins(team1.getPercentage() > team2.getPercentage() ? team1 : team2);
        }

    }

    private void noTeamWins() {

    }

    private void teamWins(SplashTeam team) {

    }

    public void display() {
        Bukkit.getOnlinePlayers().forEach(p -> getPlayer(p).updateIngameboard());
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

    public int getTimeleft() {
        return timeleft;
    }

    public SplashTeam getTeam1() {
        return team1;
    }

    public SplashTeam getTeam2() {
        return team2;
    }
}
