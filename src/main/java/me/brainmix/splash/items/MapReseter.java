package me.brainmix.splash.items;

import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.brainmix.splash.Splash;
import org.bukkit.Material;

public class MapReseter extends CustomItem implements Clickable {

    @Override
    protected void init(ItemOptions itemOptions) {
        itemOptions.setItemStack(ItemUtils.getItemStack(Material.EMERALD, "&aclear map"));
        itemOptions.setInteractWithOthers(true);
        itemOptions.setMove(false);
        itemOptions.setDrop(false);
        itemOptions.setCancellDefaults(true);
    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {
        Splash.a().getCurrent().clearMap();
    }

}
