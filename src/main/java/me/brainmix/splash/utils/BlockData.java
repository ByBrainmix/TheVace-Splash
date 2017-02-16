package me.brainmix.splash.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class BlockData {

    private Material material;
    private byte data;

    public BlockData(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    @SuppressWarnings("deprecation")
    public static BlockData fromBlock(Block block) {
        return new BlockData(block.getType(), block.getData());
    }

    public ItemStack toItemStack() {
        return new ItemStack(material, 1, data);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }
}
