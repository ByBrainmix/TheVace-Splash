package me.brainmix.splash.utils;

import me.brainmix.splash.SplashPlayer;
import me.brainmix.splash.SplashTeam;
import org.bukkit.Location;

public class ColoredBlock {

    private Location location;
    private BlockData originalBlock;
    private BlockData currentBlock;
    private SplashTeam team;

    public ColoredBlock(Location location) {
        this.location = location;
        this.originalBlock = BlockData.fromBlock(location.getBlock());
        this.currentBlock = BlockData.fromBlock(location.getBlock());
    }

    @SuppressWarnings("deprecation")
    public void paint(SplashPlayer player) {
        team = player.getTeam();
        currentBlock = team.getBlockData();
        location.getBlock().setType(currentBlock.getMaterial());
        location.getBlock().setData(currentBlock.getData());
    }

    @SuppressWarnings("deprecation")
    public void clear() {
        team = null;
        currentBlock = originalBlock;
        location.getBlock().setType(originalBlock.getMaterial());
        location.getBlock().setData(originalBlock.getData());
    }

    public Location getLocation() {
        return location;
    }

    public boolean isAtLocation(Location location) {
        return this.location.getBlockX() == location.getBlockX() && this.location.getBlockY() == location.getBlockY() && this.location.getBlockZ() == location.getBlockZ();
    }

    public BlockData getOriginalBlock() {
        return originalBlock;
    }

    public BlockData getCurrentBlock() {
        return currentBlock;
    }

    public SplashTeam getTeam() {
        return team;
    }

    public boolean hasTeam() {
        return team != null;
    }

}
