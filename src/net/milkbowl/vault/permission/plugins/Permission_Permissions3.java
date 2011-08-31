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

package net.milkbowl.vault.permission.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Permission_Permissions3 extends Permission {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "Permissions 3 (Yeti)";
    private PermissionHandler perms;
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private Permissions permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_Permissions3(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms != null) {
                if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
                    permission = (Permissions) perms;
                    this.perms = permission.getHandler();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if (permission == null) {
            return false;
        } else {
            return permission.isEnabled();
        }
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return this.permission.getHandler().inGroup(worldName, playerName, groupName);
    }

    private class PermissionServerListener extends ServerListener {
        Permission_Permissions3 permission = null;

        public PermissionServerListener(Permission_Permissions3 permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (perms != null) {
                    if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
                        permission.permission = (Permissions) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions")) {
                    permission.permission = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        Integer i = this.perms.getInfoInteger(world, playerName, node, false);
        return (i == null) ? defaultValue : i;
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        Double d = this.perms.getInfoDouble(world, playerName, node, false);
        return (d == null) ? defaultValue : d;
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        Boolean b = this.perms.getInfoBoolean(world, playerName, node, false);
        return (b == null) ? defaultValue : b;
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        String s = this.perms.getInfoString(world, playerName, node, false);
        return (s == null) ? defaultValue : s;
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        // Not certain if this is possible in P3
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        // Not certain if this is possible in P3
        return false;
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        this.perms.addUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        this.perms.removeUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        this.perms.addGroupPermission(worldName, groupName, permission);
        return true;
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        this.perms.removeGroupPermission(worldName, groupName, permission);
        return true;
    }

    public void setPlayerInfo(String world, String playerName, String node, Object value) {
        this.perms.addUserInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        Integer i = this.perms.getInfoInteger(world, groupName, node, true);
        return (i == null) ? defaultValue : i;
    }

    public void setGroupInfo(String world, String groupName, String node, Object value) {
        this.perms.addGroupInfo(world, groupName, node, value);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        Double d = this.perms.getInfoDouble(world, groupName, node, true);
        return (d == null) ? defaultValue : d;
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        Boolean b = this.perms.getInfoBoolean(world, groupName, node, true);
        return (b == null) ? defaultValue : b;
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        String s = this.permission.getHandler().getInfoString(world, groupName, node, true);
        return (s == null) ? defaultValue : s;
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        try {
            Group group = this.perms.safeGetGroup(worldName, groupName);
            return group.hasPermission(permission);
        } catch (Exception e) {
            // lowut?
            return false;
        }
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        return this.perms.getGroups(world, playerName);
    }

    public String getPrimaryGroup(String world, String playerName) {
        return this.perms.getPrimaryGroup(world, playerName);
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        return this.perms.has(worldName, playerName, permission);
    }

    @Override
    public String getPlayerPrefix(String world, String playerName) {
        return this.perms.getUserPrefix(world, playerName);
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        return this.perms.getUserSuffix(world, playerName);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        this.perms.addUserInfo(world, player, "suffix", suffix);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        this.perms.addUserInfo(world, player, "prefix", prefix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        try {
            return perms.safeGetGroup(world, group).getPrefix();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        this.perms.addGroupInfo(world, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        try {
            return perms.safeGetGroup(world, group).getSuffix();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        this.perms.addGroupInfo(world, group, "suffix", suffix);
    }

    @Override
    public boolean playerAddTransient(String world, String player, String permission) {
        try {
            perms.safeGetUser(world, player).addTimedPermission(permission, 0);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

	@Override
	public boolean playerRemoveTransient(String world, String player, String permission) {
		try {
			perms.safeGetUser(world, player).removeTimedPermission(permission);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}