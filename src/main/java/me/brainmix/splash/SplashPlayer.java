package me.brainmix.splash;

import com.google.common.collect.Lists;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.brainmix.splash.item.Mobility;
import me.brainmix.splash.items.WeaponSelector.WeaponSelectInventory;
import me.brainmix.splash.utils.BParticle;
import me.brainmix.splash.utils.BUtils;
import me.brainmix.splash.utils.BUtils.SoundWrapper;
import me.brainmix.splash.utils.ColoredBlock;
import me.vicevice.general.api.games.AbstractPlayer;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import me.vicevice.general.api.games.utils.BScoreboard;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class SplashPlayer extends AbstractPlayer {

    private Splash game;
    private BScoreboard ingameBoard;
    private boolean invisible;
    private double tint = 100;
    private boolean isSneaking;
    private double health = 20;
    private boolean dead;
    private boolean joinedAsSpectator;
    private SplashItem selectedWeapon = SplashItem.SPLATTERSHOT_JR;
    private WeaponSelectInventory weaponSelectInventory;
    private Mobility mobility = Mobility.NORMAL;
    private boolean charging;

    public SplashPlayer(String name, ManageableGame game) {
        super(name, game);
        this.game = (Splash) game;
    }

    @Override
    public void onSetIngameItems() {

        BUtils.sendListMessage(getPlayer(), "Messages.startMessage.de", false,
                "%builders%", game.getVotedMap().getBuilders(),
                "%map%", game.getVotedMap().getDisplayname(getPlayer()));

        ingameBoard = new BScoreboard("splash", game.getMessagesConfig().getTranslated("IngameBoard.displayName", getPlayer(), "display"));
        updateIngameboard();
        ingameBoard.setBoard(getPlayer());

        addItem(selectedWeapon);
        setItem(SplashItem.MAP_RESETER, 8);

        updateItems();
    }

    @Override
    public void onSetLobbyItems() {
        weaponSelectInventory = new WeaponSelectInventory(getPlayer());
        setItem(SplashItem.WEAPON_SELECTOR, game.getItemConfig().getInt(SplashItem.WEAPON_SELECTOR.getPath() + ".slot", 5));

    }

    public void onUpdate() {
        if(dead) return;
        if(isCharging()) return;
        ColoredBlock block = game.getBlockAt(getPlayer().getLocation().add(0, -1, 0));
        ColoredBlock block2 = game.getBlockAt(getPlayer().getLocation().add(0, -2, 0));

        // sneaking over own colored block
        if(block != null && block.hasTeam() && block.getTeam() == getTeam() && getPlayer().isSneaking()) {
            if(!isInvisible()) setInvisible();
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 40, false, false));
            getTeam().playParticle(getPlayer().getLocation(), 0.2, 0.4, 0.2, 0.1f, 10);
            tint += 0.5;
            if(tint > 100) tint = 100;
            if(!isSneaking()) setSneaking(true);
        } else if(block2 != null && block2.hasTeam() && block2.getTeam() == getTeam() && getPlayer().isSneaking()) {
            if(!isInvisible()) setInvisible();
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 40, false, false));
            getTeam().playParticle(getPlayer().getLocation(), 0.2, 0.4, 0.2, 0.1f, 10);
            if(!isSneaking()) setSneaking(true);
        } else {
            if(isInvisible()) setVisible();
            getPlayer().removePotionEffect(PotionEffectType.SPEED);
            if(isSneaking()) setSneaking(false);
            switch (mobility) {
                case FAST:
                   getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
                    break;
                case SLOW:
                    getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 1, false, false));
            }
        }

        // being over other team colored block
        if(block != null && block.hasTeam() && block.getTeam() != getTeam()) {
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 35, 3, false, false));
        } else {
            getPlayer().removePotionEffect(PotionEffectType.SLOW);
            switch (mobility) {
                case FAST:
                    getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
                    break;
                case SLOW:
                    getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 1, false, false));
            }
        }

    }

    public void updateIngameboard() {
        List<String> text = Lists.reverse(game.getMessagesConfig().getStringList("IngameBoard.text", ""));
        int spaces = 1;
        for (int i = 0; i < text.size(); i++) {
            String t = text.get(i);
            if (t.equals("")) {
                for (int x = 0; x < spaces; x++) {
                    t += " ";
                }
                spaces++;
            }

            SplashTeam team1 = game.getTeam1();
            SplashTeam team2 = game.getTeam2();

            t = ItemUtils.format(t,
                    "%team1%", team1.getPercentage() < 10 ? "0" + (int) team1.getPercentage() : (int) team1.getPercentage(),
                    "%team2%", team2.getPercentage() < 10 ? "0" + (int) team2.getPercentage() : (int) team2.getPercentage(),
                    "%map%", game.getVotedMap().getDisplayname(getPlayer()),
                    "%tint%", (int) tint
                    );

            ingameBoard.setText(i, t);
        }
    }

    public void updateDisplayName() {
        double l =  (double) game.getTimeleft() / 20.0;
        int minutes = (int) l / 60;
        int seconds = (int) l % 60;

        String displayName = game.getMessagesConfig().getTranslated("IngameBoard.displayName", getPlayer(), "display");
        ingameBoard.changeObjectiveName(ItemUtils.format(displayName, "%minutes%", minutes < 10 ? "0" + minutes : minutes + "", "%seconds%", seconds < 10 ? "0" + seconds : seconds + ""));
    }

    public void setInvisible() {
        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(getPlayer()));
        invisible = true;
    }

    public void setVisible() {
        Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(getPlayer()));
        invisible = false;
    }

    public void addItem(SplashItem item) {
        getPlayer().getInventory().addItem(item.get());
    }

    private void setItem(SplashItem item, int slot) {
        getPlayer().getInventory().setItem(slot, item.get());
    }

    @Override
    public SplashTeam getTeam() {
        return (SplashTeam) super.getTeam();
    }

    public boolean isInvisible() {
        return invisible;
    }

    public double getTint() {
        return tint;
    }

    public boolean hasTint() {
        return tint > 0;
    }

    public boolean removeTint(double amount) {
        tint -= amount;
        if(tint <= 0) {
            if(tint + amount == amount) return false;
            tint = 0;
            return true;
        }
        return false;
    }

    public void hurt(double damage, Player damager) {
        if(dead) return;
        if(isSpectator()) return;
        health -= damage;
        BParticle.BLOCK_CRACK.playAll(getPlayer().getLocation().add(0, 1, 0), true, 0.3, 0.5, 0.3, 0.2f, 20, 152);
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftPlayer) getPlayer()).getHandle(), 1);
        Bukkit.getOnlinePlayers().forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
        getPlayer().playSound(getPlayer().getLocation(), Sound.HURT_FLESH, 1, 1);

        if(health <= 0) {
            kill(damager);
            return;
        }
        BUtils.playSound(getPlayer(), game.getGameConfig(), "Sounds.damage", new SoundWrapper(Sound.BLAZE_HIT, 1, 1));
        getPlayer().setHealth(health);
    }

    public void kill(Player damager) {
        int deathTime = game.getGameConfig().getInt("Settings.deathTime", 80);
        dead = true;
        health = 20;
        getPlayer().setHealth(health);
        BParticle.EXPLOSION_NORMAL.playAll(getPlayer().getLocation().add(0, 1, 0), true, 0.2, 0.5, 0.2, 0f, 4);
        BParticle.BLOCK_CRACK.playAll(getPlayer().getLocation().add(0, 1, 0), true, 0.3, 0.5, 0.3, 0.2f, 100, 152);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, deathTime + 20, 128, false, false));
        Bukkit.getScheduler().scheduleSyncDelayedTask(game, () -> getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, deathTime + 20, 128, false, false)), 1);
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, deathTime + 20, 255, false, false));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, deathTime, 1, false, false));
        BUtils.sendTitle(getPlayer(), "Messages.deathMsg", "&cDu wurdest eliminiert", "%killer%", game.getPlayer(damager.getPlayer()).getName());
        int deathState = game.getGameConfig().getInt("Death.deathState", 7);
        float deathValue = (float) game.getGameConfig().getDouble("Death.deathvalue", 1);
        int respawnState = game.getGameConfig().getInt("Death.respawnState", 1);
        float respawnValue = (float) game.getGameConfig().getDouble("Death.respawnValue", 0);
        BUtils.playSound(getPlayer(), game.getGameConfig(), "Sounds.death", new SoundWrapper(Sound.BLAZE_DEATH, 1, 1));
        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(deathState, deathValue);
        ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);

        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(getPlayer()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(game, () -> {
            dead = false;
            getPlayer().teleport(getTeam().getTeamSpawn());
            tint = game.getGameConfig().getInt("Settings.tintAfterDeath", 50);
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(getPlayer()));
            PacketPlayOutGameStateChange packet2 = new PacketPlayOutGameStateChange(respawnState, respawnValue);
            ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet2);
        }, deathTime);
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public void setSneaking(boolean sneaking) {
        isSneaking = sneaking;
    }

    public boolean isJoinedAsSpectator() {
        return joinedAsSpectator;
    }

    public void setJoinedAsSpectator(boolean joinedAsSpectator) {
        this.joinedAsSpectator = joinedAsSpectator;
    }

    public SplashItem getSelectedWeapon() {
        return selectedWeapon;
    }

    public void setSelectedWeapon(SplashItem selectedWeapon) {
        this.selectedWeapon = selectedWeapon;
    }

    public WeaponSelectInventory getWeaponSelectInventory() {
        return weaponSelectInventory;
    }

    public boolean isDead() {
        return dead;
    }

    public Mobility getMobility() {
        return mobility;
    }

    public void setMobility(Mobility mobility) {
        this.mobility = mobility;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }
}
