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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    private Vault plugin = null;
    private PluginManager pluginManager = null;
    private Permissions permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_Permissions3(Vault plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener();

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
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission == null) {
                Plugin perms = event.getPlugin();
                if(perms.getDescription().getName().equals("Permissions") && perms.getDescription().getVersion().startsWith("3")) {
                    if (perms.isEnabled()) {
                        permission = (Permissions) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions")) {
                    permission = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return name;
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
		Player p = plugin.getServer().getPlayer(playerName);
		if (p != null) {
			if (p.hasPermission(permission))
				return true;
		}
        return this.perms.has(worldName, playerName, permission);
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

	@Override
	public String[] getGroups() {
		Set<String> groupNames = new HashSet<String>();
		for (World world : Bukkit.getServer().getWorlds()) {
			for (Group group : perms.getGroups(world.getName())) {
				groupNames.add(group.getName());
			}
		}
		return groupNames.toArray(new String[0]);
	}
}
