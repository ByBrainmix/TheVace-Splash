package me.brainmix.splash;

import me.brainmix.itemapi.api.CustomItem;
import me.brainmix.itemapi.api.utils.CustomItemEnum;

public enum SplashItem implements CustomItemEnum {

    ;

    private CustomItem customItem;

    SplashItem(CustomItem customItem) {
        this.customItem = customItem;
    }

    @Override
    public CustomItem getCustomItem() {
        return customItem;
    }

}
