package me.brainmix.splash;

import me.vicevice.general.api.games.AbstractMap;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import me.vicevice.general.api.games.interfaces.Map;

public class SplashMap extends AbstractMap {

    public SplashMap(String name, ManageableGame g) {
        super(name, g);
    }

    @Override
    public MapSpawnOptions getSpawnOptions() {
        return MapSpawnOptions.TEAM_SPAWNS;
    }

    @Override
    public MapMobOptions getMobOptions() {
        return MapMobOptions.NONE;
    }

}
