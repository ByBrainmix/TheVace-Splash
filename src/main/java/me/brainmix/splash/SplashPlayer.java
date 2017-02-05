package me.brainmix.splash;

import com.google.common.collect.Lists;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.vicevice.general.api.games.AbstractPlayer;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import me.vicevice.general.api.games.utils.BScoreboard;

import java.util.List;

public class SplashPlayer extends AbstractPlayer {

    private Splash game;
    private BScoreboard ingameBoard;

    public SplashPlayer(String name, ManageableGame game) {
        super(name, game);
        this.game = (Splash) game;
    }

    @Override
    public void onSetIngameItems() {

        ingameBoard = new BScoreboard("splash", game.getMessagesConfig().getTranslated("IngameBoard.displayName", getPlayer(), "display"));
        updateIngameboard();
        ingameBoard.setBoard(getPlayer());

    }

    public void updateIngameboard() {
        List<String> text = Lists.reverse(game.getMessagesConfig().getStringList("IngameBoard.text", ""));
        int spaces = 1;
        for (int i = 0; i < text.size(); i++) {
            String t = text.get(i);
            if (t.equals("")) {
                for (int x = 0; x < spaces; x++) {
                    t += " ";
                }
                spaces++;
            }

            SplashTeam team1 = game.getTeam1();
            SplashTeam team2 = game.getTeam2();

            t = ItemUtils.format(t,
                    "%team1%", team1.getPercentage() < 10 ? "0" + (int) team1.getPercentage() : (int) team1.getPercentage(),
                    "%team2%", team2.getPercentage() < 10 ? "0" + (int) team2.getPercentage() : (int) team2.getPercentage(),
                    "%map%", game.getVotedMap().getDisplayname(getPlayer())
                    );

            ingameBoard.setText(i, t);
        }
    }

    public void updateDisplayName() {
        double l =  (double) game.getTimeleft() / 20.0;
        int minutes = (int) l / 60;
        int seconds = (int) l % 60;

        String displayName = game.getMessagesConfig().getTranslated("IngameBoard.displayName", getPlayer(), "display");
        ingameBoard.changeObjectiveName(ItemUtils.format(displayName, "%minutes%", minutes < 10 ? "0" + minutes : minutes + "", "%seconds%", seconds < 10 ? "0" + seconds : seconds + ""));
    }

    @Override
    public SplashTeam getTeam() {
        return (SplashTeam) super.getTeam();
    }
}
