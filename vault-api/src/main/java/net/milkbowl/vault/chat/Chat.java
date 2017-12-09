/*
 * This file is part of Vault.
 *
 * Copyright (c) 2011 Morgan Humes <morgan@lanaddict.com>
 * Copyright (c) 2017 Neolumia
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault.chat;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The main Chat API - allows for Prefix/Suffix nodes along with generic Info nodes if the linked
 * Chat system supports them.
 */
public abstract class Chat {

  private Permission perms;

  public Chat(Permission perms) {
    this.perms = perms;
  }

  /**
   * Gets name of permission method.
   *
   * @return Name of Permission Method
   */
  public abstract String getName();

  /**
   * Checks if permission method is enabled.
   *
   * @return Success or Failure
   */
  public abstract boolean isEnabled();

  /**
   * Get players prefix.
   *
   * @param world  World name
   * @param player Player name
   * @return Prefix
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerPrefix(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public abstract String getPlayerPrefix(String world, String player);

  /**
   * Get a players prefix in the given world.
   * Use NULL for world if requesting a global prefix.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @return Prefix
   */
  public String getPlayerPrefix(String world, OfflinePlayer player) {
    return getPlayerPrefix(world, player.getName());
  }

  /**
   * Get players prefix.
   *
   * @param world  World Object
   * @param player Player name
   * @return Prefix
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerPrefix(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String getPlayerPrefix(World world, String player) {
    return getPlayerPrefix(world.getName(), player);
  }

  /**
   * Get players prefix from the world they are currently in.
   * May or may not return the global prefix depending on implementation.
   *
   * @param player Player Object
   * @return Prefix
   */
  public String getPlayerPrefix(Player player) {
    return getPlayerPrefix(player.getWorld().getName(), player);
  }

  /**
   * Set players prefix.
   *
   * @param world  World name
   * @param player Player name
   * @param prefix Prefix
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerPrefix(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public abstract void setPlayerPrefix(String world, String player, String prefix);

  /**
   * Sets players prefix in the given world.
   * Use NULL for world for setting in the Global scope.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param prefix Prefix
   */
  public void setPlayerPrefix(String world, OfflinePlayer player, String prefix) {
    setPlayerPrefix(world, player.getName(), prefix);
  }

  /**
   * Set players prefix in the given world.
   *
   * @param world  World Object
   * @param player Player name
   * @param prefix Prefix
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerPrefix(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public void setPlayerPrefix(World world, String player, String prefix) {
    setPlayerPrefix(world.getName(), player, prefix);
  }

  /**
   * Set players prefix in the world they are currently in.
   *
   * @param player Player Object
   * @param prefix Prefix
   */
  public void setPlayerPrefix(Player player, String prefix) {
    setPlayerPrefix(player.getWorld().getName(), player, prefix);
  }

  /**
   * Get players suffix.
   *
   * @param world  World name
   * @param player Player name
   * @return Suffix
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerSuffix(String, OfflinePlayer)} instead.
   */
  @Deprecated
  abstract public String getPlayerSuffix(String world, String player);

  /**
   * Get players suffix in the specified world.
   *
   * @param world  World name
   * @param player OfflinePlayer name
   * @return Suffix
   */
  public String getPlayerSuffix(String world, OfflinePlayer player) {
    return getPlayerSuffix(world, player.getName());
  }

  /**
   * Get players suffix.
   *
   * @param world  World Object
   * @param player Player name
   * @return Suffix
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerSuffix(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String getPlayerSuffix(World world, String player) {
    return getPlayerSuffix(world.getName(), player);
  }

  /**
   * Get players suffix in the world they are currently in.
   *
   * @param player Player Object
   * @return Suffix
   */
  public String getPlayerSuffix(Player player) {
    return getPlayerSuffix(player.getWorld().getName(), player);
  }

  /**
   * Set players suffix.
   *
   * @param world  World name
   * @param player Player name
   * @param suffix Suffix
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerSuffix(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public abstract void setPlayerSuffix(String world, String player, String suffix);

  /**
   * Set players suffix for the world specified.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param suffix Suffix
   */
  public void setPlayerSuffix(String world, OfflinePlayer player, String suffix) {
    setPlayerSuffix(world, player.getName(), suffix);
  }

  /**
   * Set players suffix.
   *
   * @param world  World Object
   * @param player Player name
   * @param suffix Suffix
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerSuffix(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public void setPlayerSuffix(World world, String player, String suffix) {
    setPlayerSuffix(world.getName(), player, suffix);
  }

  /**
   * Set players suffix in the world they currently occupy.
   *
   * @param player Player Object
   * @param suffix Suffix
   */
  public void setPlayerSuffix(Player player, String suffix) {
    setPlayerSuffix(player.getWorld().getName(), player, suffix);
  }

  /**
   * Get group prefix.
   *
   * @param world World name
   * @param group Group name
   * @return Prefix
   */
  public abstract String getGroupPrefix(String world, String group);

  /**
   * Get group prefix.
   *
   * @param world World Object
   * @param group Group name
   * @return Prefix
   */
  public String getGroupPrefix(World world, String group) {
    return getGroupPrefix(world.getName(), group);
  }

  /**
   * Set group prefix.
   *
   * @param world  World name
   * @param group  Group name
   * @param prefix Prefix
   */
  public abstract void setGroupPrefix(String world, String group, String prefix);

  /**
   * Set group prefix.
   *
   * @param world  World Object
   * @param group  Group name
   * @param prefix Prefix
   */
  public void setGroupPrefix(World world, String group, String prefix) {
    setGroupPrefix(world.getName(), group, prefix);
  }

  /**
   * Get group suffix
   *
   * @param world World name
   * @param group Group name
   * @return Suffix
   */
  public abstract String getGroupSuffix(String world, String group);

  /**
   * Get group suffix.
   *
   * @param world World Object
   * @param group Group name
   * @return Suffix
   */
  public String getGroupSuffix(World world, String group) {
    return getGroupSuffix(world.getName(), group);
  }

  /**
   * Set group suffix.
   *
   * @param world  World name
   * @param group  Group name
   * @param suffix Suffix
   */
  public abstract void setGroupSuffix(String world, String group, String suffix);

  /**
   * Set group suffix.
   *
   * @param world  World Object
   * @param group  Group name
   * @param suffix Suffix
   */
  public void setGroupSuffix(World world, String group, String suffix) {
    setGroupSuffix(world.getName(), group, suffix);
  }

  /**
   * Get a players informational node (Integer) value.
   *
   * @param world        World name
   * @param player       OfflinePlayer
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public int getPlayerInfoInteger(String world, OfflinePlayer player, String node,
      int defaultValue) {
    return getPlayerInfoInteger(world, player.getName(), node, defaultValue);
  }

  /**
   * Get a players informational node (Integer) value.
   *
   * @param world        World name
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoInteger(String, OfflinePlayer, String,
   * int)} instead.
   */
  @Deprecated
  public abstract int getPlayerInfoInteger(String world, String player, String node, int defaultValue);

  /**
   * Get a players informational node (Integer) value.
   *
   * @param world        World Object
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoInteger(String, OfflinePlayer, String,
   * int)} instead.
   */
  @Deprecated
  public int getPlayerInfoInteger(World world, String player, String node, int defaultValue) {
    return getPlayerInfoInteger(world.getName(), player, node, defaultValue);
  }

  /**
   * Get a players informational node (Integer) value.
   *
   * @param player       Player Object
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public int getPlayerInfoInteger(Player player, String node, int defaultValue) {
    return getPlayerInfoInteger(player.getWorld().getName(), player, node, defaultValue);
  }

  /**
   * Set a players informational node (Integer) value.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoInteger(String world, OfflinePlayer player, String node, int value) {
    setPlayerInfoInteger(world, player.getName(), node, value);
  }

  /**
   * Set a players informational node (Integer) value.
   *
   * @param world  World name
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoInteger(String, OfflinePlayer, String,
   * int)} instead.
   */
  @Deprecated
  public abstract void setPlayerInfoInteger(String world, String player, String node, int value);

  /**
   * Set a players informational node (Integer) value.
   *
   * @param world  World Object
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoInteger(String, OfflinePlayer, String,
   * int)} instead.
   */
  @Deprecated
  public void setPlayerInfoInteger(World world, String player, String node, int value) {
    setPlayerInfoInteger(world.getName(), player, node, value);
  }

  /**
   * Set a players informational node (Integer) value.
   *
   * @param player Player Object
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoInteger(Player player, String node, int value) {
    setPlayerInfoInteger(player.getWorld().getName(), player, node, value);
  }

  /**
   * Get a groups informational node (Integer) value.
   *
   * @param world        World name
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public abstract int getGroupInfoInteger(String world, String group, String node,
      int defaultValue);

  /**
   * Get a groups informational node (Integer) value.
   *
   * @param world        World Object
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public int getGroupInfoInteger(World world, String group, String node, int defaultValue) {
    return getGroupInfoInteger(world.getName(), group, node, defaultValue);
  }

  /**
   * Set a groups informational node (Integer) value.
   *
   * @param world World name
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public abstract void setGroupInfoInteger(String world, String group, String node, int value);

  /**
   * Set a groups informational node (Integer) value.
   *
   * @param world World Object
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public void setGroupInfoInteger(World world, String group, String node, int value) {
    setGroupInfoInteger(world.getName(), group, node, value);
  }

  /**
   * Get a players informational node (Double) value.
   *
   * @param world        World name
   * @param player       OfflinePlayer
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public double getPlayerInfoDouble(String world, OfflinePlayer player, String node,
      double defaultValue) {
    return getPlayerInfoDouble(world, player.getName(), node, defaultValue);
  }

  /**
   * Get a players informational node (Double) value.
   *
   * @param world        World name
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoDouble(String, OfflinePlayer, String,
   * double)} instead.
   */
  @Deprecated
  public abstract double getPlayerInfoDouble(String world, String player, String node,
      double defaultValue);

  /**
   * Get a players informational node (Double) value.
   *
   * @param world        World Object
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoDouble(String, OfflinePlayer, String,
   * double)} instead.
   */
  @Deprecated
  public double getPlayerInfoDouble(World world, String player, String node, double defaultValue) {
    return getPlayerInfoDouble(world.getName(), player, node, defaultValue);
  }

  /**
   * Get a players informational node (Double) value.
   *
   * @param player       Player Object
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public double getPlayerInfoDouble(Player player, String node, double defaultValue) {
    return getPlayerInfoDouble(player.getWorld().getName(), player, node, defaultValue);
  }

  /**
   * Set a players informational node (Double) value.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoDouble(String world, OfflinePlayer player, String node, double value) {
    setPlayerInfoDouble(world, player.getName(), node, value);
  }

  /**
   * Set a players informational node (Double) value.
   *
   * @param world  World name
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoDouble(String, OfflinePlayer, String,
   * double)} instead. Set a players informational node (Double) value
   */
  @Deprecated
  public abstract void setPlayerInfoDouble(String world, String player, String node, double value);

  /**
   * Set a players informational node (Double) value
   *
   * @param world  World Object
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoDouble(String, OfflinePlayer, String,
   * double)} instead.
   */
  @Deprecated
  public void setPlayerInfoDouble(World world, String player, String node, double value) {
    setPlayerInfoDouble(world.getName(), player, node, value);
  }

  /**
   * Set a players informational node (Double) value.
   *
   * @param player Player Object
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoDouble(Player player, String node, double value) {
    setPlayerInfoDouble(player.getWorld().getName(), player, node, value);
  }

  /**
   * Get a groups informational node (Double) value.
   *
   * @param world        World name
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public abstract double getGroupInfoDouble(String world, String group, String node,
      double defaultValue);

  /**
   * Get a groups informational node (Double) value.
   *
   * @param world        World Object
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public double getGroupInfoDouble(World world, String group, String node, double defaultValue) {
    return getGroupInfoDouble(world.getName(), group, node, defaultValue);
  }

  /**
   * Set a groups informational node (Double) value.
   *
   * @param world World name
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  abstract public void setGroupInfoDouble(String world, String group, String node, double value);

  /**
   * Set a groups informational node (Double) value.
   *
   * @param world World Object
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public void setGroupInfoDouble(World world, String group, String node, double value) {
    setGroupInfoDouble(world.getName(), group, node, value);
  }

  /**
   * Get a players informational node (Boolean) value.
   *
   * @param world        World name
   * @param player       OfflinePlayer
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public boolean getPlayerInfoBoolean(String world, OfflinePlayer player, String node,
      boolean defaultValue) {
    return getPlayerInfoBoolean(world, player.getName(), node, defaultValue);
  }

  /**
   * Get a players informational node (Boolean) value.
   *
   * @param world        World name
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoBoolean(String, OfflinePlayer, String,
   * boolean)} instead.
   */
  @Deprecated
  public abstract boolean getPlayerInfoBoolean(String world, String player, String node,
      boolean defaultValue);

  /**
   * Get a players informational node (Boolean) value.
   *
   * @param world        World Object
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoBoolean(String, OfflinePlayer, String,
   * boolean)} instead.
   */
  @Deprecated
  public boolean getPlayerInfoBoolean(World world, String player, String node,
      boolean defaultValue) {
    return getPlayerInfoBoolean(world.getName(), player, node, defaultValue);
  }

  /**
   * Get a players informational node (Boolean) value.
   *
   * @param player       Player Object
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public boolean getPlayerInfoBoolean(Player player, String node, boolean defaultValue) {
    return getPlayerInfoBoolean(player.getWorld().getName(), player, node, defaultValue);
  }

  /**
   * Set a players informational node (Boolean) value.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoBoolean(String world, OfflinePlayer player, String node, boolean value) {
    setPlayerInfoBoolean(world, player.getName(), node, value);
  }

  /**
   * Set a players informational node (Boolean) value.
   *
   * @param world  World name
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoBoolean(String, OfflinePlayer, String,
   * boolean)} instead.
   */
  @Deprecated
  public abstract void setPlayerInfoBoolean(String world, String player, String node,
      boolean value);

  /**
   * @param world  World Object
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoBoolean(String, OfflinePlayer, String,
   * boolean)} instead. Set a players informational node (Boolean) value
   */
  @Deprecated
  public void setPlayerInfoBoolean(World world, String player, String node, boolean value) {
    setPlayerInfoBoolean(world.getName(), player, node, value);
  }

  /**
   * Set a players informational node (Boolean) value.
   *
   * @param player Player Object
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoBoolean(Player player, String node, boolean value) {
    setPlayerInfoBoolean(player.getWorld().getName(), player, node, value);
  }

  /**
   * Get a groups informational node (Boolean) value.
   *
   * @param world        Name of World
   * @param group        Name of Group
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public abstract boolean getGroupInfoBoolean(String world, String group, String node,
      boolean defaultValue);

  /**
   * Set a players informational node (Boolean) value.
   *
   * @param world        World Object
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public boolean getGroupInfoBoolean(World world, String group, String node, boolean defaultValue) {
    return getGroupInfoBoolean(world.getName(), group, node, defaultValue);
  }

  /**
   * Set a groups informational node (Boolean) value
   *
   * @param world World name
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public abstract void setGroupInfoBoolean(String world, String group, String node, boolean value);

  /**
   * Set a players informational node (Boolean) value.
   *
   * @param world World Object
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public void setGroupInfoBoolean(World world, String group, String node, boolean value) {
    setGroupInfoBoolean(world.getName(), group, node, value);
  }

  /**
   * Get a players informational node (String) value.
   *
   * @param world        World name
   * @param player       OfflinePlayer
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public String getPlayerInfoString(String world, OfflinePlayer player, String node,
      String defaultValue) {
    return getPlayerInfoString(world, player.getName(), node, defaultValue);
  }

  /**
   * Get a players informational node (String) value.
   *
   * @param world        World name
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoString(String, OfflinePlayer, String,
   * String)} instead.
   */
  @Deprecated
  public abstract String getPlayerInfoString(String world, String player, String node, String defaultValue);

  /**
   * Get a players informational node (String) value.
   *
   * @param world        World Object
   * @param player       Player name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerInfoString(String, OfflinePlayer, String,
   * String)} instead.
   */
  @Deprecated
  public String getPlayerInfoString(World world, String player, String node, String defaultValue) {
    return getPlayerInfoString(world.getName(), player, node, defaultValue);
  }

  /**
   * Get a players informational node (String) value.
   *
   * @param player       Player Object
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public String getPlayerInfoString(Player player, String node, String defaultValue) {
    return getPlayerInfoString(player.getWorld().getName(), player, node, defaultValue);
  }

  /**
   * Set a players informational node (String) value.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param node   Permission node
   * @param value  Value to set
   */
  public void setPlayerInfoString(String world, OfflinePlayer player, String node, String value) {
    setPlayerInfoString(world, player.getName(), node, value);
  }

  /**
   * Set a players informational node (String) value.
   *
   * @param world  World name
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoString(String, OfflinePlayer, String,
   * String)} instead.
   */
  @Deprecated
  public abstract void setPlayerInfoString(String world, String player, String node, String value);

  /**
   * Set a players informational node (String) value.
   *
   * @param world  World name
   * @param player Player name
   * @param node   Permission node
   * @param value  Value to set
   * @deprecated As of VaultAPI 1.4 use {{@link #setPlayerInfoString(String, OfflinePlayer, String,
   * String)} instead.
   */
  @Deprecated
  public void setPlayerInfoString(World world, String player, String node, String value) {
    setPlayerInfoString(world.getName(), player, node, value);
  }

  /**
   * Set a players informational node (String) value.
   *
   * @param player Player Object
   * @param node   Permission node
   * @param value  Value ot set
   */
  public void setPlayerInfoString(Player player, String node, String value) {
    setPlayerInfoString(player.getWorld().getName(), player, node, value);
  }

  /**
   * Get a groups informational node (String) value.
   *
   * @param world        Name of World
   * @param group        Name of Group
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public abstract String getGroupInfoString(String world, String group, String node,
      String defaultValue);

  /**
   * Set a players informational node (String) value.
   *
   * @param world        World Object
   * @param group        Group name
   * @param node         Permission node
   * @param defaultValue Default value
   * @return Value
   */
  public String getGroupInfoString(World world, String group, String node, String defaultValue) {
    return getGroupInfoString(world.getName(), group, node, defaultValue);
  }

  /**
   * Set a groups informational node (String) value.
   *
   * @param world World name
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public abstract void setGroupInfoString(String world, String group, String node, String value);

  /**
   * Set a groups informational node (String) value.
   *
   * @param world World name
   * @param group Group name
   * @param node  Permission node
   * @param value Value to set
   */
  public void setGroupInfoString(World world, String group, String node, String value) {
    setGroupInfoString(world.getName(), group, node, value);
  }

  /**
   * Check if player is member of a group.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @param group  Group name
   * @return Success or Failure
   */
  public boolean playerInGroup(String world, OfflinePlayer player, String group) {
    return perms.playerInGroup(world, player, group);
  }

  /**
   * Check if player is member of a group.
   *
   * @param world  World name
   * @param player Player name
   * @param group  Group name
   * @return Success or Failure
   * @deprecated As of VaultAPI 1.4 use {{@link #playerInGroup(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public boolean playerInGroup(String world, String player, String group) {
    return perms.playerInGroup(world, player, group);
  }

  /**
   * Check if player is member of a group.
   *
   * @param world  World Object
   * @param player Player name
   * @param group  Group name
   * @return Success or Failure
   * @deprecated As of VaultAPI 1.4 use {{@link #playerInGroup(String, OfflinePlayer, String)}
   * instead.
   */
  @Deprecated
  public boolean playerInGroup(World world, String player, String group) {
    return playerInGroup(world.getName(), player, group);
  }

  /**
   * Check if player is member of a group.
   *
   * @param player Player Object
   * @param group  Group name
   * @return Success or Failure
   */
  public boolean playerInGroup(Player player, String group) {
    return playerInGroup(player.getWorld().getName(), player, group);
  }

  /**
   * Gets the list of groups that this player has.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @return Array of groups
   */
  public String[] getPlayerGroups(String world, OfflinePlayer player) {
    return perms.getPlayerGroups(world, player);
  }

  /**
   * Gets the list of groups that this player has.
   *
   * @param world  World name
   * @param player Player name
   * @return Array of groups
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerGroups(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String[] getPlayerGroups(String world, String player) {
    return perms.getPlayerGroups(world, player);
  }

  /**
   * Gets the list of groups that this player has.
   *
   * @param world  World Object
   * @param player Player name
   * @return Array of groups
   * @deprecated As of VaultAPI 1.4 use {{@link #getPlayerGroups(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String[] getPlayerGroups(World world, String player) {
    return getPlayerGroups(world.getName(), player);
  }

  /**
   * Gets the list of groups that this player has.
   *
   * @param player Player Object
   * @return Array of groups
   */
  public String[] getPlayerGroups(Player player) {
    return getPlayerGroups(player.getWorld().getName(), player);
  }

  /**
   * Gets players primary group.
   *
   * @param world  World name
   * @param player OfflinePlayer
   * @return Players primary group
   */
  public String getPrimaryGroup(String world, OfflinePlayer player) {
    return perms.getPrimaryGroup(world, player);
  }

  /**
   * Gets players primary group.
   *
   * @param world  World name
   * @param player Player name
   * @return Players primary group
   * @deprecated As of VaultAPI 1.4 use {{@link #getPrimaryGroup(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String getPrimaryGroup(String world, String player) {
    return perms.getPrimaryGroup(world, player);
  }

  /**
   * Gets players primary group.
   *
   * @param world  World Object
   * @param player Player name
   * @return Players primary group
   * @deprecated As of VaultAPI 1.4 use {{@link #getPrimaryGroup(String, OfflinePlayer)} instead.
   */
  @Deprecated
  public String getPrimaryGroup(World world, String player) {
    return getPrimaryGroup(world.getName(), player);
  }

  /**
   * Get players primary group.
   *
   * @param player Player Object
   * @return Players primary group
   */
  public String getPrimaryGroup(Player player) {
    return getPrimaryGroup(player.getWorld().getName(), player);
  }

  /**
   * Returns a list of all known groups.
   *
   * @return an Array of String of all groups
   */
  public String[] getGroups() {
    return perms.getGroups();
  }
}
