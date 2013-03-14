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
package net.milkbowl.vault.permission;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 * The main Permission API - allows for group and player based permission tests
 *
 */
public abstract class Permission {

	protected static final Logger log = Logger.getLogger("Minecraft");
    protected Plugin plugin = null;

    /**
     * Gets name of permission method
     * @return Name of Permission Method
     */
    abstract public String getName();

    /**
     * Checks if permission method is enabled.
     * @return Success or Failure
     */
    abstract public boolean isEnabled();
    
    /**
     * Returns if the permission system is or attempts to be compatible with super-perms.
     * @return True if this permission implementation works with super-perms
     */
    abstract public boolean hasSuperPermsCompat();
    
    /**
     * Checks if player has a permission node. (Short for playerHas(...)
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(String world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world, player, permission);
    }

    /**
     * Checks if player has a permission node. (Short for playerHas(...)
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(World world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world.getName(), player, permission);
    }

    /**
     * Checks if a CommandSender has a permission node.
     * This will return the result of bukkits, generic .hasPermission() method and is identical in all cases.
     * This method will explicitly fail if the registered permission system does not register permissions in bukkit.
     * 
     * For easy checking of a commandsender
     * @param sender
     * @param permission
     * @return true if the sender has the permission
     */
    public boolean has(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }
    
    /**
     * Checks if player has a permission node. (Short for playerHas(...)
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(Player player, String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Checks if player has a permission node.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerHas(String world, String player, String permission);

    /**
     * Checks if player has a permission node.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerHas(World world, String player, String permission) {
        if (world == null) {
            return playerHas((String) null, player, permission);
        }
        return playerHas(world.getName(), player, permission);
    }

    /**
     * Checks if player has a permission node.
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerHas(Player player, String permission) {
        return has(player, permission);
    }

    /**
     * Add permission to a player.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerAdd(String world, String player, String permission);

    /**
     * Add permission to a player.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAdd(World world, String player, String permission) {
        if (world == null) {
            return playerAdd((String) null, player, permission);
        }
        return playerAdd(world.getName(), player, permission);
    }

    /**
     * Add permission to a player ONLY for the world the player is currently on.
     * This is a world-specific operation, if you want to add global permission you must explicitly use NULL for the world.
     * 
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAdd(Player player, String permission) {
        return playerAdd(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Add transient permission to a player.
     * This implementation can be used by any subclass which implements a "pure" superperms plugin, i.e. 
     * one that only needs the built-in Bukkit API to add transient permissions to a player.  Any subclass
     * implementing a plugin which provides its own API for this needs to override this method. 
     * 
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAddTransient(String player, String permission) throws UnsupportedOperationException {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		return playerAddTransient(p, permission);
	}

    /**
     * Add transient permission to a player.
     * This operation adds a world-unspecific permission onto the player object in bukkit via Bukkit's permission interface.
     * 
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAddTransient(Player player, String permission) {
		for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
			if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}
		}

		PermissionAttachment attach = player.addAttachment(plugin);
		attach.setPermission(permission, true);

		return true;
    }

    /**
     * Adds a world specific transient permission to the player - ONLY WORKS IN PEX/P3 - otherwise it defaults to GLOBAL!
     * @param worldName
     * @param player
     * @param permission
     * @return Success or Failure
     */
    public boolean playerAddTransient(String worldName, Player player, String permission) {
    	return playerAddTransient(player, permission);
    }
    
    /**
     * Adds a world specific transient permission to the player - ONLY WORKS IN PEX/P3 - otherwise it defaults to GLOBAL!
     * @param worldName
     * @param player
     * @param permission
     * @return Success or Failure
     */
    public boolean playerAddTransient(String worldName, String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		return playerAddTransient(p, permission);
    }
    
    /**
     * Removes a world specific transient permission from the player - Only works in PEX/P3 - otherwise it defaults to Global!
     * @param worldName
     * @param player
     * @param permission
     * @return Success or Failure
     */
	public boolean playerRemoveTransient(String worldName, String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null)
			return false;
		
		return playerRemoveTransient(p, permission);
	}
	
    /**
     * Removes a world specific transient permission from the player - Only works in PEX/P3 - otherwise it defaults to Global!
     * @param worldName
     * @param player
     * @param permission
     * @return Success or Failure
     */
    public boolean playerRemoveTransient(String worldName, Player player, String permission) {
    	return playerRemoveTransient(player, permission);
    }
    
    /**
     * Remove permission from a player.
     * @param world World name
     * @param player Name of Player
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerRemove(String world, String player, String permission);

    /**
     * Remove permission from a player.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemove(World world, String player, String permission) {
        if (world == null) {
            return playerRemove((String) null, player, permission);
        }
        return playerRemove(world.getName(), player, permission);
    }

    /**
     * Remove permission from a player.
     * Will attempt to remove permission from the player on the player's current world.  This is NOT a global operation.
     * 
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemove(Player player, String permission) {
        return playerRemove(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Remove transient permission from a player.
     * This implementation can be used by any subclass which implements a "pure" superperms plugin, i.e. 
     * one that only needs the built-in Bukkit API to remove transient permissions from a player.  Any subclass
     * implementing a plugin which provides its own API for this needs to override this method.
     * 
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
	public boolean playerRemoveTransient(String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null)
			return false;
		
		return playerRemoveTransient(p, permission);
	}

    /**
     * Remove transient permission from a player.
     * 
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemoveTransient(Player player, String permission) {
		for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
			if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().unsetPermission(permission);
				return true;
			}
		}
		return false;
    }
    
    /**
     * Checks if group has a permission node.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupHas(String world, String group, String permission);

    /**
     * Checks if group has a permission node.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupHas(World world, String group, String permission) {
        if (world == null) {
            return groupHas((String) null, group, permission);
        }
        return groupHas(world.getName(), group, permission);
    }

    /**
     * Add permission to a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupAdd(String world, String group, String permission);

    /**
     * Add permission to a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupAdd(World world, String group, String permission) {
        if (world == null) {
            return groupAdd((String) null, group, permission);
        }
        return groupAdd(world.getName(), group, permission);
    }

    /**
     * Remove permission from a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupRemove(String world, String group, String permission);

    /**
     * Remove permission from a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupRemove(World world, String group, String permission) {
        if (world == null) {
            return groupRemove((String) null, group, permission);
        }
        return groupRemove(world.getName(), group, permission);
    }

    /**
     * Check if player is member of a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * This method is known to return unexpected results depending on what permission system is being used. Different permission systems
     * will store the player groups differently, It is HIGHLY suggested you test your code out first.
     * 
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerInGroup(String world, String player, String group);

    /**
     * Check if player is member of a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerInGroup(World world, String player, String group) {
        if (world == null) {
            return playerInGroup((String) null, player, group);
        }
        return playerInGroup(world.getName(), player, group);
    }

    /**
     * Check if player is member of a group.
     * This method will ONLY check groups for which the player is in that are defined for the current world.
     * This may result in odd return behaviour depending on what permission system has been registered.
     * 
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerInGroup(Player player, String group) {
        return playerInGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Add player to a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerAddGroup(String world, String player, String group);

    /**
     * Add player to a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerAddGroup(World world, String player, String group) {
        if (world == null) {
            return playerAddGroup((String) null, player, group);
        }
        return playerAddGroup(world.getName(), player, group);
    }

    /**
     * Add player to a group.
     * This will add a player to the group on the current World.  This may return odd results if the permission system
     * being used on the server does not support world-specific groups, or if the group being added to is a global group.
     * 
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerAddGroup(Player player, String group) {
        return playerAddGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Remove player from a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerRemoveGroup(String world, String player, String group);

    /**
     * Remove player from a group.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerRemoveGroup(World world, String player, String group) {
        if (world == null) {
            return playerRemoveGroup((String) null, player, group);
        }
        return playerRemoveGroup(world.getName(), player, group);
    }

    /**
     * Remove player from a group.
     * This will add a player to the group on the current World.  This may return odd results if the permission system
     * being used on the server does not support world-specific groups, or if the group being added to is a global group.
     * 
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerRemoveGroup(Player player, String group) {
        return playerRemoveGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Gets the list of groups that this player has.
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @return Array of groups
     */
    abstract public String[] getPlayerGroups(String world, String player);

    /**
     * Gets the list of groups that this player has
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @return Array of groups
     */
    public String[] getPlayerGroups(World world, String player) {
        if (world == null) {
            return getPlayerGroups((String) null, player);
        }
        return getPlayerGroups(world.getName(), player);
    }

    /**
     * Returns a list of world-specific groups that this player is currently in. May return unexpected results if
     * you are looking for global groups, or if the registered permission system does not support world-specific groups.
     * 
     * @param player Player Object
     * @return Array of groups
     */
    public String[] getPlayerGroups(Player player) {
        return getPlayerGroups(player.getWorld().getName(), player.getName());
    }

    /**
     * Gets players primary group
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World name
     * @param player Player name
     * @return Players primary group
     */
    abstract public String getPrimaryGroup(String world, String player);

    /**
     * Gets players primary group
     * Supports NULL value for World if the permission system registered supports global permissions.
     * But May return odd values if the servers registered permission system does not have a global permission store.
     * 
     * @param world World Object
     * @param player Player name
     * @return Players primary group
     */
    public String getPrimaryGroup(World world, String player) {
        if (world == null) {
            return getPrimaryGroup((String) null, player);
        }
        return getPrimaryGroup(world.getName(), player);
    }

    /**
     * Get players primary group
     * @param player Player Object
     * @return Players primary group
     */
    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getWorld().getName(), player.getName());
    }
    
    /**
     * Returns a list of all known groups
     * @return an Array of String of all groups
     */
    abstract public String[] getGroups();
    
    /**
     * Returns true if the given implementation supports groups.
     * @return true if the implementation supports groups
     */
    abstract public boolean hasGroupSupport();
}