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

package net.milkbowl.vault.chat.plugins;

import net.krinsoft.privileges.Privileges;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Chat_Privileges extends Chat {
  private static final String FRIENDLY_NAME = "Privileges - Chat";
  private static final String PLUGIN_NAME = "Privileges";
  private static final String CHAT_PREFIX_KEY = "prefix";
  private static final String CHAT_SUFFIX_KEY = "suffix";
  private final Plugin plugin;
  private Privileges privs;

  public Chat_Privileges(Plugin plugin, Permission perms) {
    super(perms);
    this.plugin = plugin;
    Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
    // Load service in case it was loaded before
    if (privs == null) {
      Plugin privsPlugin = plugin.getServer().getPluginManager().getPlugin(PLUGIN_NAME);
      if (privsPlugin != null && privsPlugin.isEnabled()) {
        this.privs = (Privileges) privsPlugin;
        plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), FRIENDLY_NAME));
      }
    }
  }

  @Override
  public String getName() {
    return FRIENDLY_NAME;
  }

  @Override
  public boolean isEnabled() {
    return privs != null && privs.isEnabled();
  }

  private String getPlayerOrGroupInfoString(String world, String player, String key, String defaultValue) {
    String value = getPlayerInfoString(world, player, key, null);
    if (value != null) { return value; }

    value = getGroupInfoString(world, getPrimaryGroup(world, player), key, null);
    if (value != null) { return value; }

    return defaultValue;
  }

  private void worldCheck(String world) {
    if (world != null && !world.isEmpty()) {
      throw new UnsupportedOperationException("Privileges does not support multiple worlds for player/group metadata.");
    }
  }

  @Override
  public String getPlayerPrefix(String world, String player) {
    return getPlayerOrGroupInfoString(world, player, CHAT_PREFIX_KEY, null);
  }

  @Override
  public void setPlayerPrefix(String world, String player, String prefix) {
    setPlayerInfoString(world, player, CHAT_PREFIX_KEY, prefix);
  }

  @Override
  public String getPlayerSuffix(String world, String player) {
    return getPlayerOrGroupInfoString(world, player, CHAT_SUFFIX_KEY, null);
  }

  @Override
  public void setPlayerSuffix(String world, String player, String suffix) {
    setPlayerInfoString(world, player, CHAT_SUFFIX_KEY, suffix);
  }

  @Override
  public String getGroupPrefix(String world, String group) {
    return getGroupInfoString(world, group, CHAT_PREFIX_KEY, null);
  }

  @Override
  public void setGroupPrefix(String world, String group, String prefix) {
    setGroupInfoString(world, group, CHAT_PREFIX_KEY, prefix);
  }

  @Override
  public String getGroupSuffix(String world, String group) {
    return getGroupInfoString(world, group, CHAT_SUFFIX_KEY, null);
  }

  @Override
  public void setGroupSuffix(String world, String group, String suffix) {
    setGroupInfoString(world, group, CHAT_SUFFIX_KEY, suffix);
  }

  @Override
  public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
    return privs.getUserNode(player).getInt(node, defaultValue);
  }

  @Override
  public void setPlayerInfoInteger(String world, String player, String node, int value) {
    worldCheck(world);
    privs.getUserNode(player).set(node, value);
  }

  @Override
  public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
    return privs.getGroupNode(group).getInt(node, defaultValue);
  }

  @Override
  public void setGroupInfoInteger(String world, String group, String node, int value) {
    worldCheck(world);
    privs.getGroupNode(group).set(node, value);
  }

  @Override
  public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
    return privs.getUserNode(player).getDouble(node, defaultValue);
  }

  @Override
  public void setPlayerInfoDouble(String world, String player, String node, double value) {
    worldCheck(world);
    privs.getUserNode(player).set(node, value);
  }

  @Override
  public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
    return privs.getGroupNode(group).getDouble(node, defaultValue);
  }

  @Override
  public void setGroupInfoDouble(String world, String group, String node, double value) {
    worldCheck(world);
    privs.getGroupNode(group).set(node, value);
  }

  @Override
  public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
    return privs.getUserNode(player).getBoolean(node, defaultValue);
  }

  @Override
  public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
    worldCheck(world);
    privs.getUserNode(player).set(node, value);
  }

  @Override
  public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
    return privs.getGroupNode(group).getBoolean(node, defaultValue);
  }

  @Override
  public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
    worldCheck(world);
    privs.getGroupNode(group).set(node, value);
  }

  @Override
  public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
    return privs.getUserNode(player).getString(node, defaultValue);
  }

  @Override
  public void setPlayerInfoString(String world, String player, String node, String value) {
    worldCheck(world);
    privs.getUserNode(player).set(node, value);
  }

  @Override
  public String getGroupInfoString(String world, String group, String node, String defaultValue) {
    return privs.getGroupNode(group).getString(node, defaultValue);
  }

  @Override
  public void setGroupInfoString(String world, String group, String node, String value) {
    worldCheck(world);
    privs.getGroupNode(group).set(node, value);
  }

  public class PermissionServerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (privs == null) {
        Plugin permChat = event.getPlugin();
        if (PLUGIN_NAME.equals(permChat.getDescription().getName())) {
          if (permChat.isEnabled()) {
            privs = (Privileges) permChat;
            plugin.getLogger().info(String.format("[Chat] %s hooked.", FRIENDLY_NAME));
          }
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
      if (privs != null) {
        if (PLUGIN_NAME.equals(event.getPlugin().getDescription().getName())) {
          privs = null;
          plugin.getLogger().info(String.format("[Chat] %s un-hooked.", FRIENDLY_NAME));
        }
      }
    }
  }
}
