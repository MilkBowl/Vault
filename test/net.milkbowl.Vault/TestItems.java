package test.net.milkbowl.Vault;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Author: gabizou
 */
public class TestItems {

    private Map<String,ItemInfo> itemMap = new HashMap<String, ItemInfo>();

    @BeforeClass
    public static void setUp() {
        TestItems testItems = new TestItems();
        List<ItemInfo> itemsList = Items.getItemList();
        for (ItemInfo item : itemsList) {
            testItems.itemMap.put(item.getName(),item);
        }
    }

    @Test
    public void testItems() {
        for (Map.Entry<String, ItemInfo> itemEntry : itemMap.entrySet()) {
            String name = itemEntry.getKey();
            ItemInfo storedInfo = itemEntry.getValue();
            ItemInfo queriedInfo = Items.itemByString(name);
            assertEquals(storedInfo,queriedInfo);
        }
    }

}
