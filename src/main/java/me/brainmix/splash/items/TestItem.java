package me.brainmix.splash.items;

import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.events.ItemRightClickBlockEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.brainmix.splash.item.CustomSplashItem;
import org.bukkit.Location;
import org.bukkit.Material;

public class TestItem extends CustomSplashItem implements Clickable {

    public TestItem() {
        super("testitem");
    }

    @Override
    protected void init(ItemOptions options) {
        options.setItemStack(ItemUtils.getItemStack(Material.STICK, "&6Basic Painter"));
    }

    @ItemHandler
    public void onClick(ItemRightClickBlockEvent event) {
        Location middle = event.getBlock().getLocation();
        Location min = new Location(middle.getWorld(), middle.getX() - 2, middle.getY() - 2, middle.getZ() - 2);
        Location max = new Location(middle.getWorld(), middle.getX() + 2, middle.getY() + 2, middle.getZ() + 2);

        game.getCurrent().paintArea(game.getPlayer(event.getPlayer()), min, max);
    }

}
