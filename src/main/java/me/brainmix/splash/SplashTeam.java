package me.brainmix.splash;

import me.vicevice.general.api.games.AbstractTeam;
import me.vicevice.general.api.games.interfaces.ManageableGame;

public class SplashTeam extends AbstractTeam<SplashPlayer> {

    public SplashTeam(String name, ManageableGame game) {
        super(name, game);
    }

}
