package me.brainmix.splash.items;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.delay.ItemDelayChar;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.brainmix.splash.item.SplashShooter;
import org.bukkit.Material;
import org.bukkit.Sound;

public class TestShooter extends SplashShooter {

    public TestShooter() {
        super("testshooter", 5, 5, 1.2, 1, new ClickSound(Sound.CLICK, 1, 1), new ItemDelayChar(5));
        getOptions().setItemStack(ItemUtils.getItemStack(Material.DIAMOND, "&bTestShooter"));
    }

}
