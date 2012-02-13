package net.milkbowl.vault.regions;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class Regions {
    
    /**
     * Gets name of regions method
     * @return Name of Regions Method
     */
    abstract public String getName();

    /**
     * Checks if this regions method is enabled.
     * @return Success or Failure
     */
    abstract public boolean isEnabled();
    
    abstract public boolean playerInRegion(Player player, String name);
    
    abstract public Set<String> getRegions(Player player);
    
    /**
     * Get members of region
     * @param region Name of region
     * @return
     */
    abstract public List<String> getMembers(String region);
    
    abstract public List<String> getOwners(String region);
    
    abstract public String getWelcomeMessage(String region);
    
    abstract public String getFarewellMessage(String region);
    
    abstract public boolean canBuildAt(Player player, Location loc);
    
    abstract public boolean canBuildAt(Player player, Block block);
    
    abstract public Location getFirstCorner(String region);
    
    abstract public Location getSecondCorner(String region);
    
    abstract public boolean canUse(Player player);
    
    abstract public boolean canUse(Player player, Location location);
    
}
