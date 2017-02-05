package me.brainmix.splash;

import me.brainmix.splash.utils.ColoredBlock;
import me.vicevice.general.api.games.AbstractMap;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SplashMap extends AbstractMap {

    private List<Material> canbeColored = new ArrayList<>();
    private Location minMapLocation;
    private Location maxMapLocation;
    private Set<ColoredBlock> coloredBlocks = new HashSet<>();

    public SplashMap(String name, ManageableGame g) {
        super(name, g);
    }

    @Override
    public void onLoad() {

        canbeColored = getConfig().getStringList(path + "canbeColored", "QUARTZ_BLOCK", "CLAY").stream().map(Material::valueOf).collect(Collectors.toList());
        minMapLocation = getConfig().getLocation(path + "maplocation.min");
        maxMapLocation = getConfig().getLocation(path + "maplocation.max");

        for(double x = minMapLocation.getX(); x <= maxMapLocation.getX(); x++) {
            for(double y = minMapLocation.getY(); y <= maxMapLocation.getY(); y++) {
                for(double z = minMapLocation.getZ(); z <= maxMapLocation.getZ(); z++) {

                    Location loc = new Location(getBukkitWorld(), x, y, z);
                    if(canbeColored.contains(loc.getBlock().getType())) {
                        coloredBlocks.add(new ColoredBlock(loc));
                    }
                }
            }
        }

    }

    public void paintArea(SplashPlayer player, Location min, Location max) {
        for(double x = min.getX(); x <= max.getX(); x++) {
            for(double y = min.getY(); y <= max.getY();  y++) {
                for(double z = min.getZ(); z <= max.getZ(); z++) {
                    ColoredBlock block = getBlockAt(new Location(min.getWorld(), x, y, z));
                    if(block != null) block.paint(player);
                }
            }
        }
    }

    public ColoredBlock getBlockAt(Location location) {
        return coloredBlocks.stream().filter(b -> b.isAtLocation(location)).findFirst().orElse(null);
    }

    public int getColoredBlocksAmount() {
        return coloredBlocks.size();
    }

    public int getTeamAmount(SplashTeam team) {
        return (int) coloredBlocks.stream().filter(b -> b.getTeam() == team).count();
    }

    @Override
    public MapSpawnOptions getSpawnOptions() {
        return MapSpawnOptions.TEAM_SPAWNS;
    }

    @Override
    public MapMobOptions getMobOptions() {
        return MapMobOptions.NONE;
    }

}
