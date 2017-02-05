package me.brainmix.splash.items;

import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.splash.Splash;
import me.vicevice.general.api.Config;
import org.bukkit.inventory.ItemStack;

public abstract class CustomSplashItem extends CustomItem {

    protected Splash game = Splash.a();
    protected Config config = game.getItemConfig();
    private String configName;

    public CustomSplashItem(String configName) {
        this.configName = configName;
        getOptions().setItemStack(config.getItemStack(getPath()));
        getOptions().setInteractWithOthers(true);
        getOptions().setDisabled(true);
        getOptions().setMove(false);
        getOptions().setDrop(false);
    }

    protected String getPath() {
        return "Items." + configName;
    }

    protected ItemStack getItemStack() {
        return getOptions().getItemStack();
    }

}
