package me.brainmix.splash;

import me.vicevice.general.api.games.AbstractPlayer;
import me.vicevice.general.api.games.interfaces.ManageableGame;

public class SplashPlayer extends AbstractPlayer {

    private Splash game;

    public SplashPlayer(String name, ManageableGame game) {
        super(name, game);
        this.game = (Splash) game;
    }


}
