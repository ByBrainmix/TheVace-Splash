package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;

public class SplashRoller extends CustomSplashItem implements Clickable {

    private int tintCost;
    private int size;
    private Mobility mobility;

    public SplashRoller(String configName, int tintCost, int size, Mobility mobility) {
        super(configName);
        this.tintCost = tintCost;
        this.size = size;
        this.mobility = mobility;
    }

    public SplashRoller(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 2);
        this.size = config.getInt(getPath() + "size", 3);
        this.mobility = config.getEnum(Mobility.class, getPath() + "mobility", Mobility.NORMAL);
    }

    @Override
    protected void init(ItemOptions itemOptions) {

    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {

    }

}
