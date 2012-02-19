/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
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
