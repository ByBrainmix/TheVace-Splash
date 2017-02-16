package me.brainmix.splash.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum BParticle {

    EXPLOSION_NORMAL, EXPLOSION_LARGE, EXPLOSION_HUGE, FIREWORKS_SPARK, WATER_BUBBLE, WATER_SPLASH,
    WATER_WAKE, SUSPENDED, SUSPENDED_DEPTH, CRIT, CRIT_MAGIC, SMOKE_NORMAL, SMOKE_LARGE, SPELL, SPELL_INSTANT,
    SPELL_MOB, SPELL_MOB_AMBIENT, SPELL_WITCH, DRIP_WATER, DRIP_LAVA, VILLAGER_ANGRY, VILLAGER_HAPPY, TOWN_AURA,
    NOTE, PORTAL, ENCHANTMENT_TABLE, FLAME, LAVA, FOOTSTEP, CLOUD, REDSTONE, SNOWBALL, SNOW_SHOVEL, SLIME, HEART,
    BARRIER, ITEM_CRACK, BLOCK_CRACK, BLOCK_DUST, WATER_DROP, ITEM_TAKE, MOB_APPEARANCE;

    private Field fieldEnumParticle;
    private static Constructor constructor;
    private static Method handleMethod;
    private static Field playerConnectionField;
    private static Method sendPacketMethod;

    static {
        try {
            constructor = getNMSClass("PacketPlayOutWorldParticles").getConstructor(getNMSClass("EnumParticle"), Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, Class.forName("[I"));
            handleMethod = getCraftPlayer().getMethod("getHandle");
            playerConnectionField = getNMSClass("EntityPlayer").getField("playerConnection");
            sendPacketMethod = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        } catch (NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private BParticle() {
        try {
            this.fieldEnumParticle = getNMSClass("EnumParticle").getField(toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void play(Player player, Location location) {
        play(player, location, true, 0, 0, 0, 0, 0);
    }
    public void play(Player player, Location location, boolean longDistance) {
        play(player, location, longDistance, 0, 0, 0, 0, 0);
    }
    public void play(Player player, Location location, boolean longDistance, double xOffset, double yOffset, double zOffset) {
        play(player, location, longDistance, xOffset, yOffset, zOffset, 0, 0);
    }
    public void play(Player player, Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData) {
        play(player, location, longDistance, xOffset, yOffset, zOffset, particleData, 0);
    }
    public void play(Player player, Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData, int speed) {
        play(player, location, longDistance, xOffset, yOffset, zOffset, particleData, speed, new int[]{});
    }
    public void play(Player player, Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData, int speed, int... data) {
        sendParticlePacket(player, longDistance, location, (float) xOffset, (float) yOffset, (float) zOffset, particleData, speed, data);
    }

    public void playAll(Location location) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location));
    }
    public void playAll(Location location, boolean longDistance) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location, longDistance));
    }
    public void playAll(Location location, boolean longDistance, double xOffset, double yOffset, double zOffset) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location, longDistance, xOffset, yOffset, zOffset));
    }
    public void playAll(Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location, longDistance, xOffset, yOffset, zOffset, particleData));
    }
    public void playAll(Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData, int speed) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location, longDistance, xOffset, yOffset, zOffset, particleData, speed));
    }
    public void playAll(Location location, boolean longDistance, double xOffset, double yOffset, double zOffset, float particleData, int speed, int... data) {
        Bukkit.getOnlinePlayers().forEach(p -> play(p, location, longDistance, xOffset, yOffset, zOffset, particleData, speed, data));
    }

    private void sendParticlePacket(Player player, boolean longDistance, Location location, float xOffset, float yOffset, float zOffset, float particleData, int speed, int... data) {
        try {
            Object enumParticle = fieldEnumParticle.get(null);
            Object packet = constructor.newInstance(enumParticle, longDistance, (float) location.getX(), (float) location.getY(), (float) location.getZ(), xOffset, yOffset, zOffset, particleData, speed, data);
            Object handle = handleMethod.invoke(player);
            Object playerConnection = playerConnectionField.get(handle);
            sendPacketMethod.invoke(playerConnection, packet);

        } catch ( NullPointerException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getNMSClass(String name) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Class<?> getCraftPlayer() {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            return Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}