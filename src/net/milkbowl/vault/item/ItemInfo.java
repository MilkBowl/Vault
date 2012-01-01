package net.milkbowl.vault.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemInfo {

    public final Material material;
    public final short subTypeId;
    public final String name;
    public final String[][] search;
    
    public ItemInfo(String name, String[][] search, Material material) {
        this.material = material;
        this.name = name;
        this.subTypeId = 0;
        this.search = search.clone();
    }

    public ItemInfo(String name, String[][] search, Material material, short subTypeId) {
        this.name = name;
        this.material = material;
        this.subTypeId = subTypeId;
        this.search = search.clone();
    }

    public Material getType() {
        return material;
    }

    public short getSubTypeId() {
        return subTypeId;
    }

    public int getStackSize() {
        return material.getMaxStackSize();
    }

    public int getId() {
        return material.getId();
    }

    public boolean isEdible() {
        return material.isEdible();
    }
    
    public boolean isBlock() {
        return material.isBlock();
    }
    
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.getId();
        hash = 17 * hash + this.subTypeId;
        return hash;
    }

    public boolean isDurable() {
        return (material.getMaxDurability() > 0);
    }

    public ItemStack toStack() {
        return new ItemStack(this.material, 1, subTypeId);
    }

    @Override
    public String toString() {
        return String.format("%s[%d:%d]", name, material.getId(), subTypeId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (this == obj)
            return true;
        else if (!(obj instanceof ItemInfo))
            return false;
        else
            return ((ItemInfo) obj).material == this.material && ((ItemInfo) obj).subTypeId == this.subTypeId;
    }
}
