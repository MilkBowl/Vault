/**
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */

package net.milkbowl.vault.permission;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * The main Permission API - allows for group and player based permission tests
 *
 */
public abstract class Permission {

	protected static final Logger log = Logger.getLogger("Minecraft");
    protected Vault plugin = null;

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
     * Checks if player has a permission node. (Short for playerHas(...)
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(String world, String player, String permission) {
        return playerHas(world, player, permission);
    }

    /**
     * Checks if player has a permission node. (Short for playerHas(...)
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(World world, String player, String permission) {
        return playerHas(world.getName(), player, permission);
    }

    /**
     * Checks if player has a permission node. (Short for playerHas(...)
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(Player player, String permission) {
        return playerHas(player.getWorld().getName(), player.getName(), permission);
    }

    /**
     * Checks if player has a permission node.
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerHas(String world, String player, String permission);

    /**
     * Checks if player has a permission node.
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerHas(World world, String player, String permission) {
        return playerHas(world.getName(), player, permission);
    }

    /**
     * Checks if player has a permission node.
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerHas(Player player, String permission) {
        return playerHas(player.getWorld().getName(), player.getName(), permission);
    }

    /**
     * Add permission to a player.
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerAdd(String world, String player, String permission);

    /**
     * Add permission to a player.
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAdd(World world, String player, String permission) {
        return playerAdd(world.getName(), player, permission);
    }

    /**
     * Add permission to a player.
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
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAddTransient(String world, String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}

		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}
		}

		PermissionAttachment attach = p.addAttachment(plugin);
		attach.setPermission(permission, true);

		return true;
	}

    
    /**
     * Add transient permission to a player.
     * @param world World Object
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAddTransient(World world, String player, String permission) {
        return playerAddTransient(world.getName(), player, permission);
    }
    /**
     * Add transient permission to a player.
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerAddTransient(Player player, String permission) {
        return playerAddTransient(player.getWorld().getName(), player.getName(), permission);
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
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemove(World world, String player, String permission) {
        return playerRemove(world.getName(), player, permission);
    }

    /**
     * Remove permission from a player.
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
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
	public boolean playerRemoveTransient(String world, String player, String permission) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null)
			return false;
		
		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().unsetPermission(permission);
				return true;
			}
		}
		return false;
	}

    /**
     * Remove transient permission from a player.
     * @param world World name
     * @param player Player name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemoveTransient(World world, String player, String permission) {
        return playerRemoveTransient(world.getName(), player, permission);
    }

    /**
     * Remove transient permission from a player.
     * @param player Player Object
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean playerRemoveTransient(Player player, String permission) {
        return playerRemoveTransient(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Checks if group has a permission node.
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupHas(String world, String group, String permission);

    /**
     * Checks if group has a permission node.
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupHas(World world, String group, String permission) {
        return groupHas(world.getName(), group, permission);
    }

    /**
     * Add permission to a group.
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupAdd(String world, String group, String permission);

    /**
     * Add permission to a group.
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupAdd(World world, String group, String permission) {
        return groupAdd(world.getName(), group, permission);
    }

    /**
     * Remove permission from a group.
     * @param world World name
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupRemove(String world, String group, String permission);

    /**
     * Remove permission from a group.
     * @param world World Object
     * @param group Group name
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean groupRemove(World world, String group, String permission) {
        return groupRemove(world.getName(), group, permission);
    }

    /**
     * Check if player is member of a group.
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerInGroup(String world, String player, String group);

    /**
     * Check if player is member of a group.
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerInGroup(World world, String player, String group) {
        return playerInGroup(world.getName(), player, group);
    }

    /**
     * Check if player is member of a group.
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerInGroup(Player player, String group) {
        return playerInGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Add player to a group.
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerAddGroup(String world, String player, String group);

    /**
     * Add player to a group.
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerAddGroup(World world, String player, String group) {
        return playerAddGroup(world.getName(), player, group);
    }

    /**
     * Add player to a group.
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerAddGroup(Player player, String group) {
        return playerAddGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Remove player from a group.
     * @param world World name
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    abstract public boolean playerRemoveGroup(String world, String player, String group);

    /**
     * Remove player from a group.
     * @param world World Object
     * @param player Player name
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerRemoveGroup(World world, String player, String group) {
        return playerRemoveGroup(world.getName(), player, group);
    }

    /**
     * Remove player from a group.
     * @param player Player Object
     * @param group Group name
     * @return Success or Failure
     */
    public boolean playerRemoveGroup(Player player, String group) {
        return playerRemoveGroup(player.getWorld().getName(), player.getName(), group);
    }

    /**
     * Gets the list of groups that this player has
     * @param world World name
     * @param player Player name
     * @return Array of groups
     */
    abstract public String[] getPlayerGroups(String world, String player);

    /**
     * Gets the list of groups that this player has
     * @param world World Object
     * @param player Player name
     * @return Array of groups
     */
    public String[] getPlayerGroups(World world, String player) {
        return getPlayerGroups(world.getName(), player);
    }

    /**
     * Gets the list of groups that this player has
     * @param player Player Object
     * @return Array of groups
     */
    public String[] getPlayerGroups(Player player) {
        return getPlayerGroups(player.getWorld().getName(), player.getName());
    }

    /**
     * Gets players primary group
     * @param world World name
     * @param player Player name
     * @return Players primary group
     */
    abstract public String getPrimaryGroup(String world, String player);

    /**
     * Gets players primary group
     * @param world World Object
     * @param player Player name
     * @return Players primary group
     */
    public String getPrimaryGroup(World world, String player) {
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
}