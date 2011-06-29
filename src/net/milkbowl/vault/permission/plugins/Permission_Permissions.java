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

import com.nijiko.permissions.Group;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Permission_Permissions implements Permission {
    private String name = "Permissions (Phoenix)";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private Permissions permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_Permissions(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms != null) {
                if (perms.isEnabled()) {
                    permission = (Permissions) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        if(permission == null) {
            return false;
        } else {
            return permission.isEnabled();
        }
    }

    @Override
    public boolean playerHasPermission(Player player, String permission) {
        return this.permission.getHandler().has(player, permission);
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return this.permission.getHandler().inGroup(worldName, playerName, groupName);
    }

    private class PermissionServerListener extends ServerListener {
        Permission_Permissions permission = null;

        public PermissionServerListener(Permission_Permissions permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (perms != null) {
                    if (perms.isEnabled()) {
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
        return this.permission.getHandler().getInfoInteger(world, playerName, node, false);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        return this.permission.getHandler().getInfoDouble(world, playerName, node, false);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        return this.permission.getHandler().getInfoBoolean(world, playerName, node, false);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        return this.permission.getHandler().getInfoString(world, playerName, node, false);
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
    public boolean playerAddPermission(String worldName, String playerName, String permission) {
        this.permission.getHandler().addUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean playerRemovePermission(String worldName, String playerName, String permission) {
        this.permission.getHandler().removeUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean groupAddPermission(String worldName, String groupName, String permission) {
        this.permission.getHandler().addGroupPermission(worldName, groupName, permission);
        return true;
    }

    @Override
    public boolean groupRemovePermission(String worldName, String groupName, String permission) {
        this.permission.getHandler().removeGroupPermission(worldName, groupName, permission);
        return true;
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        try {
            this.permission.getHandler().safeGetUser(world, playerName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        try {
            this.permission.getHandler().safeGetUser(world, playerName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        try {
            this.permission.getHandler().safeGetUser(world, playerName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }        
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        try {
            this.permission.getHandler().safeGetUser(world, playerName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        return this.permission.getHandler().getInfoInteger(world, groupName, node, true);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        try {
            this.permission.getHandler().safeGetGroup(world, groupName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        return this.permission.getHandler().getInfoDouble(world, groupName, node, true);
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        try {
            this.permission.getHandler().safeGetGroup(world, groupName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        return this.permission.getHandler().getInfoBoolean(world, groupName, node, true);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        try {
            this.permission.getHandler().safeGetGroup(world, groupName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        return this.permission.getHandler().getInfoString(world, groupName, node, true);
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        try {
            this.permission.getHandler().safeGetGroup(world, groupName).setData(node, value);
        } catch(Exception e) {
            // lolwut?
        }
    }

    @Override
    public boolean groupHasPermission(String worldName, String groupName, String permission) {
        try {
            Group group = this.permission.getHandler().safeGetGroup(worldName, groupName);
            return group.hasPermission(permission);
        } catch (Exception e) {
            // lowut?
            return false;
        }
    }
}