package net.milkbowl.vault;

public final class FoliaUtil {
    private FoliaUtil() {}

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
