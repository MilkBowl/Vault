package net.milkbowl.vault.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;


@SuppressWarnings("deprecation")
public class ItemTest {

    // Static list of materials we shouldn't be testing for as they are now longer able to be help in inventory.
    private static final Set<Material> ignoreMats = EnumSet.noneOf(Material.class);
    {
        ignoreMats.add(Material.STATIONARY_WATER);
        ignoreMats.add(Material.STATIONARY_LAVA);
        ignoreMats.add(Material.PISTON_EXTENSION);
        ignoreMats.add(Material.PISTON_MOVING_PIECE);
        ignoreMats.add(Material.REDSTONE_WIRE);
        ignoreMats.add(Material.CROPS);
        ignoreMats.add(Material.BURNING_FURNACE);
        ignoreMats.add(Material.SIGN_POST);
        ignoreMats.add(Material.WOODEN_DOOR);
        ignoreMats.add(Material.WALL_SIGN);
        ignoreMats.add(Material.IRON_DOOR_BLOCK);
        ignoreMats.add(Material.GLOWING_REDSTONE_ORE);
        ignoreMats.add(Material.SUGAR_CANE_BLOCK);
        ignoreMats.add(Material.CAKE_BLOCK);
        ignoreMats.add(Material.DIODE_BLOCK_OFF);
        ignoreMats.add(Material.DIODE_BLOCK_ON);
        ignoreMats.add(Material.LOCKED_CHEST);
        ignoreMats.add(Material.PUMPKIN_STEM);
        ignoreMats.add(Material.MELON_STEM);
        ignoreMats.add(Material.REDSTONE_LAMP_ON);
        ignoreMats.add(Material.SKULL);
        ignoreMats.add(Material.REDSTONE_COMPARATOR_OFF);
        ignoreMats.add(Material.REDSTONE_COMPARATOR_ON);
    }

    @Test
    public void testItems() {
        boolean failed = false;
        for (ItemInfo item : Items.getItemList()) {
            ItemInfo queriedInfo = Items.itemByString(item.getName());
            try {
                assertEquals(item, queriedInfo);
            } catch (AssertionError e) {
                e.printStackTrace();
                failed = true;
            }
        }
        assertEquals(false, failed);
    }
    
    @Test
    public void testItemStacks() {
        boolean failed = false;
        for (ItemInfo item : Items.getItemList()) {
            ItemStack stack = item.toStack();
            try {
                assertEquals(item, Items.itemByStack(stack));
            } catch (AssertionError e) {
                e.printStackTrace();
                failed = true;
            }
        }
        assertEquals(false, failed);
    }
    
    @Test
    public void MissingMaterialtest() {
        for (Material mat : Material.values()) {
            if (ignoreMats.contains(mat)) continue;
            
            assertNotNull("Missing " + mat.toString() + " in item search list", Items.itemByType(mat));
        }
    }
}