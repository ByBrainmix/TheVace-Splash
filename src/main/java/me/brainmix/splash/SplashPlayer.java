package me.brainmix.splash;

import com.google.common.collect.Lists;
import me.brainmix.itemapi.api.utils.ItemUtils;
import me.brainmix.splash.items.WeaponSelector.WeaponSelectInventory;
import me.brainmix.splash.utils.BUtils;
import me.brainmix.splash.utils.ColoredBlock;
import me.vicevice.general.api.games.AbstractPlayer;
import me.vicevice.general.api.games.interfaces.ManageableGame;
import me.vicevice.general.api.games.utils.BScoreboard;
import org.bukkit.Bukkit;
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
    private boolean joinedAsSpectator;
    private SplashItem selectedWeapon = SplashItem.SPLATTERSHOT_JR;
    private WeaponSelectInventory weaponSelectInventory;

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
        addItem(SplashItem.TEST_ITEM);
        addItem(SplashItem.TEST_SHOOTER);
        setItem(SplashItem.MAP_RESETER, 8);

        updateItems();
    }

    @Override
    public void onSetLobbyItems() {
        weaponSelectInventory = new WeaponSelectInventory(getPlayer());
        setItem(SplashItem.WEAPON_SELECTOR, game.getItemConfig().getInt(SplashItem.WEAPON_SELECTOR.getPath() + ".slot", 5));

    }

    public void onUpdate() {
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
        }

        // being over other team colored block
        if(block != null && block.hasTeam() && block.getTeam() != getTeam()) {
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 35, 3, false, false));
        } else {
            getPlayer().removePotionEffect(PotionEffectType.SLOW);
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

    public void hurt(double damage) {
        health -= damage;
        if(health <= 0) {
            kill();
            return;
        }
        getPlayer().setHealth(health);
    }

    public void kill() {
        health = 20;
        getPlayer().setHealth(0);
        getPlayer().setHealth(health);
    }

    public boolean isSneaking() {
        return isSneaking;
    }

    public void setSneaking(boolean sneaking) {
        isSneaking = sneaking;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
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
}
