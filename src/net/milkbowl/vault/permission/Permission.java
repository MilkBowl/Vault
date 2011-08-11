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

import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class Permission {

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
     * Checks if player has a permission node.  (Short for playerHas(...)
     * @param worldName
     * @param playerName 
     * @param permission Permission node
     * @return Success or Failure
     */
    public boolean has(String worldName, String playerName, String permission) {
        return playerHas(worldName, playerName, permission);
    }
    public boolean has(World world, String playerName, String permission) {
        return playerHas(world.getName(), playerName, permission);
    }
    public boolean has(Player player, String permission) {
        return playerHas(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Checks if player has a permission node.
     * @param player Player instance
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerHas(String worldName, String playerName, String permission);
    public boolean playerHas(World world, String playerName, String permission) {
        return playerHas(world.getName(), playerName, permission);
    }
    public boolean playerHas(Player player, String permission) {
        return playerHas(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Add permission to a player.
     * @param worldName Name of World
     * @param playerName Name of Player
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerAdd(String worldName, String playerName, String permission);
    public boolean playerAdd(World world, String playerName, String permission) {
        return playerAdd(world.getName(), playerName, permission);
    }
    public boolean playerAdd(Player player, String permission) {
        return playerAdd(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Remove permission from a player.
     * @param worldName Name of World
     * @param playerName Name of Player
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean playerRemove(String worldName, String playerName, String permission);
    public boolean playerRemove(World world, String playerName, String permission) {
        return playerRemove(world.getName(), playerName, permission);
    }
    public boolean playerRemove(Player player, String permission) {
        return playerRemove(player.getWorld().getName(), player.getName(), permission);
    }
    
    /**
     * Checks if group has a permission node.
     * @param worldName Name of World
     * @param groupName Name of Group
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupHas(String worldName, String groupName, String permission);
    public boolean groupHas(World world, String groupName, String permission) {
        return groupHas(world.getName(), groupName, permission);
    }
    
    /**
     * Add permission to a group.
     * @param worldName Name of World
     * @param groupName Name of Group
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupAdd(String worldName, String groupName, String permission);
    public boolean groupAdd(World world, String groupName, String permission) {
        return groupAdd(world.getName(), groupName, permission);
    }
    
    /**
     * Remove permission from a group.
     * @param worldName Name of World
     * @param groupName Name of Group
     * @param permission Permission node
     * @return Success or Failure
     */
    abstract public boolean groupRemove(String worldName, String groupName, String permission);
    public boolean groupRemove(World world, String groupName, String permission) {
        return groupRemove(world.getName(), groupName, permission);
    }
    
    /**
     * Check if player is member of a group.
     * @param worldName Name of World
     * @param playerName Name of Player
     * @param groupName Name of Group
     * @return Success or Failure
     */
    abstract public boolean playerInGroup(String worldName, String playerName, String groupName);
    public boolean playerInGroup(World world, String playerName, String groupName) {
        return playerInGroup(world.getName(), playerName, groupName);
    }
    public boolean playerInGroup(Player player, String groupName) {
        return playerInGroup(player.getWorld().getName(), player.getName(), groupName);
    }
    
    /**
     * Add player to a group.
     * @param worldName Name of World
     * @param playerName Name of Player
     * @param groupName Name of Group
     * @return Success or Failure
     */
    abstract public boolean playerAddGroup(String worldName, String playerName, String groupName);
    public boolean playerAddGroup(World world, String playerName, String groupName) {
        return playerAddGroup(world.getName(), playerName, groupName);
    }
    public boolean playerAddGroup(Player player, String groupName) {
        return playerAddGroup(player.getWorld().getName(), player.getName(), groupName);
    }
    
    /**
     * Remove player from a group.
     * @param worldName Name of World
     * @param playerName Name of Player
     * @param groupName Name of Group
     * @return Success or Failure
     */
    abstract public boolean playerRemoveGroup(String worldName, String playerName, String groupName);
    public boolean playerRemoveGroup(World world, String playerName, String groupName) {
        return playerRemoveGroup(world.getName(), playerName, groupName);
    }
    public boolean playerRemoveGroup(Player player, String groupName) {
        return playerRemoveGroup(player.getWorld().getName(), player.getName(), groupName);
    }
    
    /**
     * Get a players informational node (Integer) value
     * @param world Name of World 
     * @param playerName Name of Player
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue);
    public int getPlayerInfoInteger(World world, String playerName, String node, int defaultValue) {
        return getPlayerInfoInteger(world.getName(), playerName, node, defaultValue);
    }
    public int getPlayerInfoInteger(Player player, String node, int defaultValue) {
        return getPlayerInfoInteger(player.getWorld().getName(), player.getName(), node, defaultValue);
    }
    
    /**
     * Set a players informational node (Integer) value
     * @param world Name of World
     * @param playerName Name of Player
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setPlayerInfoInteger(String world, String playerName, String node, int value);
    public void setPlayerInfoInteger(World world, String playerName, String node, int value) {
        setPlayerInfoInteger(world.getName(), playerName, node, value);
    }
    public void setPlayerInfoInteger(Player player, String node, int value) {
        setPlayerInfoInteger(player.getWorld().getName(), player.getName(), node, value);
    }
    
    /**
     * Get a groups informational node (Integer) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue);
    public int getGroupInfoInteger(World world, String groupName, String node, int defaultValue) {
        return getGroupInfoInteger(world.getName(), groupName, node, defaultValue);
    }
    
    /**
     * Set a groups informational node (Integer) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setGroupInfoInteger(String world, String groupName, String node, int value);
    public void setGroupInfoInteger(World world, String groupName, String node, int value) {
        setGroupInfoInteger(world.getName(), groupName, node, value);
    }
    
    /**
     * Get a players informational node (Double) value
     * @param world Name of World
     * @param playerName Name of Group
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue);
    public double getPlayerInfoDouble(World world, String playerName, String node, double defaultValue) {
        return getPlayerInfoDouble(world.getName(), playerName, node, defaultValue);
    }
    public double getPlayerInfoDouble(Player player, String node, double defaultValue) {
        return getPlayerInfoDouble(player.getWorld().getName(), player.getName(), node, defaultValue);
    }
    
    /**
     * Set a players informational node (Double) value
     * @param world Name of World
     * @param playerName Name of Group
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setPlayerInfoDouble(String world, String playerName, String node, double value);
    public void setPlayerInfoDouble(World world, String playerName, String node, double value) {
        setPlayerInfoDouble(world.getName(), playerName, node, value);
    }
    public void setPlayerInfoDouble(Player player, String node, double value) {
        setPlayerInfoDouble(player.getWorld().getName(), player.getName(), node, value);
    }
    
    /**
     * Get a groups informational node (Double) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue);
    public double getGroupInfoDouble(World world, String groupName, String node, double defaultValue) {
        return getGroupInfoDouble(world.getName(), groupName, node, defaultValue);
    }
    
    /**
     * Set a groups informational node (Double) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setGroupInfoDouble(String world, String groupName, String node, double value);
    public void setGroupInfoDouble(World world, String groupName, String node, double value) {
        setGroupInfoDouble(world.getName(), groupName, node, value);
    }
    
    /**
     * Get a players informational node (Boolean) value
     * @param world Name of World
     * @param playerName Name of Player
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue);
    public boolean getPlayerInfoBoolean(World world, String playerName, String node, boolean defaultValue) {
        return getPlayerInfoBoolean(world.getName(), playerName, node, defaultValue);
    }
    public boolean getPlayerInfoBoolean(Player player, String node, boolean defaultValue) {
        return getPlayerInfoBoolean(player.getWorld().getName(), player.getName(), node, defaultValue);
    }
    
    /**
     * Set a players informational node (Boolean) value
     * @param world Name of World
     * @param playerName Name of Player
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value);
    public void setPlayerInfoBoolean(World world, String playerName, String node, boolean value) {
        setPlayerInfoBoolean(world.getName(), playerName, node, value);
    }
    public void setPlayerInfoBoolean(Player player, String node, boolean value) {
        setPlayerInfoBoolean(player.getWorld().getName(), player.getName(), node, value);
    }
    
    /**
     * Get a groups informational node (Boolean) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue);
    public boolean getGroupInfoBoolean(World world, String groupName, String node, boolean defaultValue) {
        return getGroupInfoBoolean(world.getName(), groupName, node, defaultValue);
    }
    
    /**
     * Set a groups informational node (Boolean) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setGroupInfoBoolean(String world, String groupName, String node, boolean value);
    public void setGroupInfoBoolean(World world, String groupName, String node, boolean value) {
        setGroupInfoBoolean(world.getName(), groupName, node, value);
    }
    
    /**
     * Get a players informational node (String) value
     * @param world Name of World
     * @param playerName Name of Player
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public String getPlayerInfoString(String world, String playerName, String node, String defaultValue);
    public String getPlayerInfoString(World world, String playerName, String node, String defaultValue) {
        return getPlayerInfoString(world.getName(), playerName, node, defaultValue);
    }
    public String getPlayerInfoString(Player player, String node, String defaultValue) {
        return getPlayerInfoString(player.getWorld().getName(), player.getName(), node, defaultValue);
    }
    
    /**
     * Set a players informational node (String) value
     * @param world Name of World
     * @param playerName Name of Player
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setPlayerInfoString(String world, String playerName, String node, String value);
    public void setPlayerInfoString(World world, String playerName, String node, String value) {
        setPlayerInfoString(world.getName(), playerName, node, value);
    }
    public void setPlayerInfoString(Player player, String node, String value) {
        setPlayerInfoString(player.getWorld().getName(), player.getName(), node, value);
    }
    
    /**
     * Get a groups informational node (String) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param defaultValue Default value (if node is not defined)
     * @return Value
     */
    abstract public String getGroupInfoString(String world, String groupName, String node, String defaultValue);
    public String getGroupInfoString(World world, String groupName, String node, String defaultValue) {
        return getGroupInfoString(world.getName(), groupName, node, defaultValue);
    }
    
    /**
     * Set a groups informational node (String) value
     * @param world Name of World
     * @param groupName Name of Group
     * @param node Permission node
     * @param value Value to set
     */
    abstract public void setGroupInfoString(String world, String groupName, String node, String value);
    public void setGroupInfoString(World world, String groupName, String node, String value) {
        setGroupInfoString(world.getName(), groupName, node, value);
    }
    
    /**
     * Gets the list of groups that this player has
     * @param world Name of World
     * @param playerName Name of Player
     * @return Array of groups 
     */
    abstract public String[] getPlayerGroups(String world, String playerName);
    public String[] getPlayerGroups(World world, String playerName) {
        return getPlayerGroups(world.getName(), playerName);
    }
    public String[] getPlayerGroups(Player player) {
        return getPlayerGroups(player.getWorld().getName(), player.getName());
    }
    
    /**
     * Gets users primary group
     * @param world
     * @param playerName
     * @return Players primary group
     */
    abstract public String getPrimaryGroup(String world, String playerName);
    public String getPrimaryGroup(World world, String playerName) {
        return getPrimaryGroup(world.getName(), playerName);
    }
    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getWorld().getName(), player.getName());
    }
    
    abstract public String getPlayerPrefix(String world, String playerName);
    public String getPlayerPrefix(World world, String playerName) {
        return getPlayerPrefix(world.getName(), playerName);
    }
    public String getPlayerPrefix(Player player) {
        return getPlayerPrefix(player.getWorld().getName(), player.getName());
    }
    
    abstract public String getPlayerSuffix(String world, String playerName);
    public String getPlayerSuffix(World world, String playerName) {
        return getPlayerSuffix(world.getName(), playerName);
    }
    public String getPlayerSuffix(Player player) {
        return getPlayerSuffix(player.getWorld().getName(), player.getName());
    }
    
    abstract public void setPlayerSuffix(Player player, String suffix); 
}