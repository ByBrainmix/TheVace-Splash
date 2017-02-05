package me.brainmix.splash;

import me.brainmix.splash.utils.BlockData;
import me.vicevice.general.api.games.AbstractTeam;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import org.bukkit.Material;

public class SplashTeam extends AbstractTeam<SplashPlayer> {

    private Splash game;
    private BlockData blockData;

    public SplashTeam(String name, ManageableGame game) {
        super(name, game);
        this.game = (Splash) game;

        Material material = config.getMaterial("Teams." + getName() + ".blockdata.material", Material.STAINED_CLAY);
        short data = config.getShort("Teams." + getName() + ".blockdata.data", (short) 2);
        this.blockData = new BlockData(material, (byte) data);

    }

    public BlockData getBlockData() {
        return blockData;
    }

    public int getColoredAmount() {
        return game.getCurrent().getTeamAmount(this);
    }

    public double getPercentage() {
        return ((double) 100) / ((double) game.getCurrent().getColoredBlocksAmount()) * getColoredAmount();
    }


}

