package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.delay.ItemDelay;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.splash.utils.BUtils;

public class SplashCharger extends CustomSplashItem implements Clickable {

    private int tintCost;
    private double range;
    private Mobility mobility;
    private ClickSound clickSound;
    private ItemDelay chargeTime;

    public SplashCharger(String configName, int tintCost, double range, Mobility mobility, ClickSound clickSound, ItemDelay chargeTime) {
        super(configName);
        this.tintCost = tintCost;
        this.range = range;
        this.mobility = mobility;
        this.clickSound = clickSound;
        this.chargeTime = chargeTime;
    }

    public SplashCharger(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 30);
        this.range = config.getDouble(getPath() + "range", 10);
        this.mobility = config.getEnum(Mobility.class, getPath() + "mobility", Mobility.NORMAL);
        this.clickSound = BUtils.getSound(config, getPath() + "clickSound");
        this.chargeTime = BUtils.getItemDelay(config, getPath() + "chargeTime");
    }

    @Override
    protected void init(ItemOptions itemOptions) {
        itemOptions.setClickSound(clickSound);
    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {

    }

}
