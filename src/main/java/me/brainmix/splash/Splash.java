package me.brainmix.splash;

import me.brainmix.itemapi.api.ItemRegister;
import me.brainmix.splash.listeners.GameListener;
import me.brainmix.splash.listeners.GameOptionsListener;
import me.brainmix.splash.utils.BUtils;
import me.brainmix.splash.utils.BUtils.SoundWrapper;
import me.brainmix.splash.utils.ColoredBlock;
import me.vicevice.general.Utils;
import me.vicevice.general.api.games.GameApi;
import me.vicevice.general.api.games.GameState;
import me.vicevice.general.api.games.TeamGame;
import me.vicevice.general.api.games.events.GameSendPlayersEvent;
import me.vicevice.general.api.games.interfaces.GameOptions;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.*;
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
        timeleft = getGameConfig().getInt("Settings.gametime", 1000);
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

    @Override
    public void onJoin(Player p) {
        if(!isLobby()) {
            getPlayer(p).setSpectator();
            getPlayer(p).setJoinedAsSpectator(true);
        }
        p.removeAchievement(Achievement.THE_END);
    }

    public void onEnd() {
        Bukkit.getScheduler().cancelTask(gameLoop);
        if(team1.getPercentage() == team2.getPercentage()) {
            noTeamWins();
        } else {
            teamWins(team1.getPercentage() > team2.getPercentage() ? team1 : team2);
        }
        SplashItem.disable();
        getCurrent().clearMap();
        Bukkit.getOnlinePlayers().forEach(p -> p.awardAchievement(Achievement.THE_END));
    }

    private void noTeamWins() {
        setGameState(GameState.END);
        getPlayers().forEach(p -> {

            int cookies = getGameConfig().getInt("cookiesForNoWin", 5);
            if(p.isJoinedAsSpectator()) cookies = 0;

            int points = getGameConfig().getInt("pointsForNoWin", 5);
            if(p.isJoinedAsSpectator()) cookies = 0;

            BUtils.sendTitle(p.getPlayer(), "Messages.noTeamWon", "Kein Team hat gewonnen");
            BUtils.sendListMessage(p.getPlayer(), "Messages.noTeamWon.de", false,
                    "%cookies%", cookies,
                    "%points%", points);

            BUtils.playSound(p.getPlayer(), getGameConfig(), "Sounds.noTeamWon", new SoundWrapper(Sound.WITHER_DEATH, 1, 1));
        });
        delayedStop();
    }

    private void teamWins(SplashTeam winner) {

        SplashTeam loser = team1 == winner ? team2 : team1;

        setGameState(GameState.END);
        getPlayers().forEach(p -> {

            int cookies = p.getTeam() == winner ? getGameConfig().getInt("cookiesForWin", 20) : getGameConfig().getInt("cookiesForLose", 10);
            if(p.isJoinedAsSpectator()) cookies = 0;

            int points = p.getTeam() == winner ? getGameConfig().getInt("pointsForWin", 20) : getGameConfig().getInt("pointsForLose", 10);
            if(p.isJoinedAsSpectator()) cookies = 0;

            if(p.getTeam() == winner) {
                BUtils.sendTitle(p.getPlayer(), "Messages.yourTeamWon", "Dein Team hat gewonnen");
                BUtils.sendListMessage(p.getPlayer(), "Messages.yourTeamWon.de", false,
                        "%cookies%", cookies,
                        "%points%", points,
                        "%winner%", winner.getDisplayname(p.getPlayer()),
                        "%loser%", loser.getDisplayname(p.getPlayer()),
                        "%winnerAmount%", winner.getPercentage() < 10 ? "0" + (int) winner.getPercentage() : (int) winner.getPercentage(),
                        "%loserAmount%", loser.getPercentage() < 10 ? "0" + (int) loser.getPercentage() : (int) loser.getPercentage());
                BUtils.playSound(p.getPlayer(), getGameConfig(), "Sounds.yourTeamWon", new SoundWrapper(Sound.WITHER_DEATH, 1, 1));
            } else {
                BUtils.sendTitle(p.getPlayer(), "Messages.yourTeamLose", "Dein Team hat verloren");
                BUtils.sendListMessage(p.getPlayer(), "Messages.yourTeamLose.de", false,
                        "%cookies%", cookies,
                        "%points%", points,
                        "%winner%", winner.getDisplayname(p.getPlayer()),
                        "%loser%", loser.getDisplayname(p.getPlayer()),
                        "%winnerAmount%", winner.getPercentage() < 10 ? "0" + (int) winner.getPercentage() : (int) winner.getPercentage(),
                        "%loserAmount%", loser.getPercentage() < 10 ? "0" + (int) loser.getPercentage() : (int) loser.getPercentage());
                BUtils.playSound(p.getPlayer(), getGameConfig(), "Sounds.yourTeamLose", new SoundWrapper(Sound.WITHER_DEATH, 1, 1));
            }
        });
        delayedStop();
    }

    public void delayedStop() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            GameSendPlayersEvent gameSendPlayersEvent1 = new GameSendPlayersEvent(this, "lobby");
            Bukkit.getServer().getPluginManager().callEvent(gameSendPlayersEvent1);
            String targetServer = gameSendPlayersEvent1.getServer();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Utils.sendToServer(targetServer, player);
                getLogger().info("sending " + player.getName() + " to " + targetServer);
            }

        }, 120);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, this::stop, 200);
    }

    public void display() {
        Bukkit.getOnlinePlayers().forEach(p -> getPlayer(p).updateIngameboard());
    }

    public void log(String... message) {
        Arrays.asList(message).forEach(getLogger()::info);
    }

    public void paintArea(SplashPlayer player, Location min, Location max) {
        getCurrent().paintArea(player, min, max);
    }

    public ColoredBlock getBlockAt(Location location) {
        return getCurrent().getBlockAt(location);
    }

    public boolean hasBlock(Location location) {
        return getCurrent().hasBlock(location);
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
