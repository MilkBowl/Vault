package net.milkbowl.vault.regions;

import java.util.Set;

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
    
    abstract public boolean canBuild(Player player);
    
    abstract public boolean canUse(Player player);
}
