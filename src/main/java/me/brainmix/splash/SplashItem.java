package me.brainmix.splash;

import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.itemapi.api.utils.CustomItemEnum;
import me.brainmix.splash.item.CustomSplashItem;
import me.brainmix.splash.item.SplashCharger;
import me.brainmix.splash.item.SplashRoller;
import me.brainmix.splash.item.SplashShooter;
import me.brainmix.splash.items.MapReseter;
import me.brainmix.splash.items.TestItem;
import me.brainmix.splash.items.TestShooter;
import me.brainmix.splash.items.WeaponSelector;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum SplashItem implements CustomItemEnum {

    //DEBUG:
    TEST_ITEM(new TestItem(), false),
    TEST_SHOOTER(new TestShooter(), false),
    MAP_RESETER(new MapReseter(), false),

    //LOBBY:
    WEAPON_SELECTOR(new WeaponSelector(), false),

    // SHOOTERS:
    SPLATTERSHOT_JR(new SplashShooter("splattershot_jr"), true),
    SPLATTERSHOT(new SplashShooter("splattershot"), true),
    AEROSPRAY(new SplashShooter("aerospray"), true),
    SQUELCHER(new SplashShooter("squelcher"), true),

    // CHARGER:
    SPLAT_CHARGER(new SplashCharger("splat_charger"), true),
    E_LITER(new SplashCharger("e_liter"), true),
    SQUIFFER(new SplashCharger("squiffer"), true),

    //ROLLER:
    SPLAT_ROLLER(new SplashRoller("splat_roller"), true),
    DYNAMO(new SplashRoller("dynamo_roller"), true);

    private CustomItem customItem;
    private boolean isWeapon;

    SplashItem(CustomItem customItem, boolean isWeapon) {
        this.customItem = customItem;
        this.isWeapon = isWeapon;
    }

    @Override
    public CustomItem getCustomItem() {
        return customItem;
    }

    public ItemStack get() {
        return customItem.getOptions().getItemStack();
    }

    public static void disable() {
        for (SplashItem splashItem : values()) {
            splashItem.getCustomItem().getOptions().setDisabled(true);
        }
    }

    public static Set<SplashItem> getWeapons() {
        return Arrays.asList(values()).stream().filter(SplashItem::isWeapon).collect(Collectors.toSet());
    }

    public boolean hasBought(Player player) {
        return true;
        //if(this == SplashItem.SPLATTERSHOT_JR) return true;
        //return Splash.a().getPlayer(player).getUser().getBoughtAmount("splash." + ((CustomSplashItem) customItem).getConfigName()) != 0;
    }

    public String getPath() {
        return "Items." + ((CustomSplashItem) customItem).getConfigName();
    }

    public boolean isWeapon() {
        return isWeapon;
    }
}
