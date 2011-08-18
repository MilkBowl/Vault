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

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Permission_PermissionsEx extends Permission {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "PermissionsEx";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private PermissionsEx permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_PermissionsEx(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (perms != null) {
                if (perms.isEnabled()) {
                    permission = (PermissionsEx) perms;
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
    public boolean has(Player player, String permission) {
        return playerHas(player, permission);
    }

    @Override
    public boolean playerHas(Player player, String permission) {
        return this.permission.has(player, permission);
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        // Try catch the check because we don't know if the objects will
        // actually exist Good Job on the crap Permissions plugin, why do we
        // support this again?
        try {
            PermissionUser[] userList = PermissionsEx.getPermissionManager().getGroup(groupName).getUsers();
            for (PermissionUser user : userList) {
                if (user.getName() == playerName)
                    return true;
                else
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private class PermissionServerListener extends ServerListener {
        Permission_PermissionsEx permission = null;

        public PermissionServerListener(Permission_PermissionsEx permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.permission = (PermissionsEx) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
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
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionInteger(node, world, defaultValue);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionDouble(node, world, defaultValue);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionBoolean(node, world, defaultValue);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOption(node, world, defaultValue);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (group == null || user == null) {
            return false;
        } else {
            user.addGroup(group);
            return true;
        }
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (group == null || user == null) {
            return false;
        } else {
            user.removeGroup(group);
            return true;
        }
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user == null) {
            return false;
        } else {
            user.addPermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user == null) {
            return false;
        } else {
            user.removePermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            group.addPermission(permission, worldName);
            return true;
        }
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            group.removePermission(permission, worldName);
            return true;
        }
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionInteger(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionDouble(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionBoolean(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOption(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, value);
        }
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            return group.has(permission, worldName);
        }
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getGroupsNames();
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        if (PermissionsEx.getPermissionManager().getUser(playerName).getGroupsNames().length > 0)
            return PermissionsEx.getPermissionManager().getUser(playerName).getGroupsNames()[0];
        else
            return null;
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.has(permission, worldName);
        } else {
            return false;
        }
    }

    @Override
    public String getPlayerPrefix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getPrefix();
        } else {
            return null;
        }
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getSuffix();
        } else {
            return null;
        }
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setSuffix(suffix);
        }
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setPrefix(prefix);
        }
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getPrefix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setPrefix(prefix);
        }

    }

    @Override
    public String getGroupSuffix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getSuffix();
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setSuffix(suffix);
        }
    }

    @Override
    public boolean playerAddTransient(String world, String player, String permission) {
        PermissionUser pPlayer = PermissionsEx.getPermissionManager().getUser(player);
        if (pPlayer != null) {
            pPlayer.addTimedPermission(permission, world, 0);
            return true;
        } else {
            return false;
        }
    }

	@Override
	public boolean playerRemoveTransient(String world, String player, String permission) {
		PermissionUser pPlayer = PermissionsEx.getPermissionManager().getUser(player);
		if (pPlayer != null) {
			pPlayer.removeTimedPermission(permission, world);
			return true;
		} else {
			return false;
		}
	}
}