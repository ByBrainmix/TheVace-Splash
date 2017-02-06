package me.brainmix.splash.utils;

import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.delay.ItemDelayChar;
import me.vicevice.general.api.Config;
import org.bukkit.Sound;

public class BUtils {

    public static ClickSound getSound(Config config, String path) {
        Sound sound = config.getEnum(Sound.class, path + ".name", Sound.CLICK);
        double volume = config.getDouble(path + ".volume", 1.0);
        double pitch = config.getDouble(path + ".pitch", 1.0);
        return new ClickSound(sound, (float) volume, (float) pitch);
    }

    public static ItemDelayChar getItemDelay(Config config, String path) {
        int delay = config.getInt(path + ".delay", 40);
        String pattern = config.getString(path + ".pattern", "<< %delay% >>");
        String character = config.getString(path + ".char", "|");
        int size = config.getInt(path + ".size", 50);
        return new ItemDelayChar(delay, pattern, character, size);
    }

}
