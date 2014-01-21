package net.milkbowl.vault.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;


public class ItemTest {

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
}