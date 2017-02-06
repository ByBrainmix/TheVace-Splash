package me.brainmix.splash;

import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.itemapi.api.utils.CustomItemEnum;
import me.brainmix.splash.item.SplashCharger;
import me.brainmix.splash.item.SplashRoller;
import me.brainmix.splash.item.SplashShooter;
import me.brainmix.splash.items.TestItem;
import org.bukkit.inventory.ItemStack;

public enum SplashItem implements CustomItemEnum {

    TEST_ITEM(new TestItem()),

    // SHOOTERS:
    SPLATTERSHOT_JR(new SplashShooter("splattershot_jr")),
    SPLATTERSHOT(new SplashShooter("splattershot")),
    AEROSPRAY(new SplashShooter("aerospray")),
    SQUELCHER(new SplashShooter("squelcher")),

    // CHARGER:
    SPLAT_CHARGER(new SplashCharger("splat_charger")),
    E_LITER(new SplashCharger("e_liter")),
    SQUIFFER(new SplashCharger("squiffer")),

    //ROLLER:
    SPLAT_ROLLER(new SplashRoller("splat_roller")),
    DYNAMO(new SplashRoller("dynamo_roller"));

    private CustomItem customItem;

    SplashItem(CustomItem customItem) {
        this.customItem = customItem;
    }

    @Override
    public CustomItem getCustomItem() {
        return customItem;
    }

    public ItemStack get() {
        return customItem.getOptions().getItemStack();
    }

}
