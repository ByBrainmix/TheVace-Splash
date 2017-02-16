package me.brainmix.splash.item;

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
        getOptions().setItemStack(config.getItemStack("Items." + configName));
        getOptions().setInteractWithOthers(true);
        getOptions().setMove(false);
        getOptions().setDrop(false);
        getOptions().setCancellDefaults(true);
    }

    protected String getPath() {
        return "Items." + configName + ".";
    }

    public String getConfigName() {
        return configName;
    }

    protected ItemStack getItemStack() {
        return getOptions().getItemStack();
    }

}
