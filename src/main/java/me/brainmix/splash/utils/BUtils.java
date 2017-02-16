package me.brainmix.splash.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.brainmix.itemapi.api.ClickSound;
import me.brainmix.itemapi.api.delay.ItemDelayChar;
import me.brainmix.splash.Splash;
import me.vicevice.general.api.Config;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

    public static void sendListMessage(Player player, String path, boolean prefixEnabled, Object... replace) {
        sendListMessage(Splash.a().getMessagesConfig(), player, path, prefixEnabled, replace);
    }

    public static void sendListMessage(Config config, Player player, String path, boolean prefixEnabled, Object... replace) {
        List<String> list = config.getStringList(path, true, "null", "null2", "null3");

        for (String entry : list) {
            entry = ChatColor.translateAlternateColorCodes('&', entry);
            String prefix = prefixEnabled ? Splash.a().getMessagesConfig().getTranslated("Messages.prefix", player, "Murder:") : "";
            player.sendMessage(prefix + replace(entry, replace));
        }
    }

    public static void sendTitle(Player player, String path, String defaultMsg, String... replace) {
        sendTitle(player, Splash.a().getMessagesConfig(), path, defaultMsg, replace);
    }

    public static void sendTitle(Player player, Config config, String path, String defaultMsg, String... replace) {

        String titleText = config.getTranslated(path + ".title", player, defaultMsg);
        String subtitleText = config.getTranslated(path + ".subtitle", player, "");

        titleText = BUtils.replace(titleText, replace);
        subtitleText = BUtils.replace(subtitleText, replace);

        Title title = new Title();
        title.setTitle(titleText);
        title.setSubtitle(subtitleText);
        title.send(player);

    }

    public static String replace(String text, Object... replace) {

        String output = text;
        List<String> r = new ArrayList<>();
        List<String> o = new ArrayList<>();

        for (int i = 0; i < replace.length; i++) {
            if (i % 2 == 0) {
                r.add(String.valueOf(replace[i]));
            } else {
                o.add(String.valueOf(replace[i]));
            }
        }

        for (String s : r) {
            while (output.contains(s)) {
                output = output.replace(s, ChatColor.translateAlternateColorCodes('&', o.get(r.indexOf(s))));
            }
        }

        return output;
    }

    public static void playSound(Player player, Config config, String path, SoundWrapper defaultSound) {

        String sound = config.getString(path + ".sound", defaultSound.getSound().toString());
        double volume = config.getDouble(path + ".volume", defaultSound.getVolume());
        double pitch = config.getDouble(path + ".pitch", defaultSound.getPitch());

        player.playSound(player.getLocation(), Sound.valueOf(sound), (float) volume, (float) pitch);

    }

    @AllArgsConstructor
    @Data
    public static class SoundWrapper {
        private Sound sound;
        private double volume;
        private double pitch;
    }


}
