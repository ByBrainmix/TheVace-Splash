package me.brainmix.splash.item;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.delay.ItemDelay;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.splash.utils.BUtils;

public class SplashShooter extends CustomSplashItem implements Clickable {

    private int tintCost;
    private double fireRate;
    private double range;
    private double damage;
    private ClickSound clickSound;

    public SplashShooter(String configName, int tintCost, double fireRate, double range, double damage, ClickSound clickSound) {
        super(configName);
        this.tintCost = tintCost;
        this.fireRate = fireRate;
        this.range = range;
        this.damage = damage;
        this.clickSound = clickSound;
    }

    public SplashShooter(String configName) {
        super(configName);
        this.tintCost = config.getInt(getPath() + "tintCost", 2);
        this.fireRate = config.getDouble(getPath() + "fireRate", 1.0);
        this.range = config.getDouble(getPath() + "range", 1.0);
        this.damage = config.getDouble(getPath() + "damage", 1.0);
        this.clickSound = BUtils.getSound(config, getPath() + "clickSound");
    }

    @Override
    protected void init(ItemOptions options) {
        options.setClickSound(clickSound);
        options.setAutoItemDelay(new ItemDelay(5));
    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {

    }

}
