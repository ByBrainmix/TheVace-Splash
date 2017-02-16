package me.brainmix.splash.items;

import me.brainmix.itemapi.api.ItemOptions;
import me.brainmix.itemapi.api.controllers.ItemHandler;
import me.brainmix.itemapi.api.events.ItemRightClickEvent;
import me.brainmix.itemapi.api.interfaces.Clickable;
import me.brainmix.splash.Splash;
import me.brainmix.splash.SplashItem;
import me.brainmix.splash.SplashPlayer;
import me.brainmix.splash.item.CustomSplashItem;
import me.vicevice.general.Utils;
import me.vicevice.general.api.A;
import me.vicevice.general.api.Config;
import me.vicevice.general.api.games.utils.ItemUtils;
import me.vicevice.general.api.games.utils.Message;
import me.vicevice.general.api.inventory.SingleInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WeaponSelector extends CustomSplashItem implements Clickable {

    public WeaponSelector() {
        super("weaponselector");
    }

    @Override
    protected void init(ItemOptions itemOptions) {
    }

    @ItemHandler
    public void onClick(ItemRightClickEvent event) {
        game.getPlayer(event.getPlayer()).getWeaponSelectInventory().updateInventory();
        event.getPlayer().openInventory(game.getPlayer(event.getPlayer()).getWeaponSelectInventory().build());
    }

    public static class WeaponSelectInventory extends SingleInventory {

        private static final Config config = Splash.a().getItemConfig();
        private Splash game = Splash.a();

        public WeaponSelectInventory(Player player) {
            super(player, config, "Inventorys.weaponSelector");
        }

        @Override
        public void updateInventory() {
            Player owner = getOwner();
            SplashPlayer player = game.getPlayer(owner);

            SplashItem.getWeapons().forEach(weapon -> {
                ItemStack item = weapon.get();
                int slot = config.getInt(weapon.getPath() + ".slot", 1);
                ItemMeta itemMeta = item.getItemMeta();
                List<String> lore = new ArrayList<>();


                String hasBought = Utils.simpleColorFormat(config.getTranslated("Weapon.hasBought", owner, "&aDu besitzt dieses Item"));
                String hasNotBought = Utils.simpleColorFormat(config.getTranslated("Weapon.hasNotBought", owner, "&cDu musst dieses Item im Shop zuerst kaufen!"));

                lore.add(weapon.hasBought(owner) ? hasBought : hasNotBought);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);

                if(player.getSelectedWeapon() == weapon) {
                    item = ItemUtils.addGlow(item);
                }

                set(slot, item, e -> {

                    if(!weapon.hasBought(owner)) {
                        Message.send(owner, "&cDu hast das Item %item% nicht gekauft!", "Messages.weaponselector.doNotHaveWeapon", A.a("%item%"), A.a(itemMeta.getDisplayName()));
                        return;
                    }

                    player.setSelectedWeapon(weapon);
                    Message.send(owner, "&aDu hast das Item %item% erfolgreich ausgewählt!", "Messages.weaponselector.selectedWeapon", A.a("%item%"), A.a(itemMeta.getDisplayName()));
                    owner.closeInventory();
                });
            });

        }
    }

}
