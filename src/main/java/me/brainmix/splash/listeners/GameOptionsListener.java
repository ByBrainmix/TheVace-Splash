package me.brainmix.splash.listeners;

import me.brainmix.splash.Splash;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class GameOptionsListener implements Listener {

    private Splash game;

    public GameOptionsListener(Splash game) {
        this.game = game;
        Bukkit.getPluginManager().registerEvents(this, game);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        switch (event.getEntityType()) {
            case ITEM_FRAME:
            case MINECART:
            case MINECART_CHEST:
            case MINECART_COMMAND:
            case MINECART_FURNACE:
            case MINECART_HOPPER:
            case MINECART_TNT:
            case LEASH_HITCH:
            case PAINTING:
                event.setCancelled(true);
                break;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMoveItem(InventoryClickEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        switch (event.getCause()) {
            case SUICIDE:
            case VOID:
                break;
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        event.setBuild(false);
    }

}
