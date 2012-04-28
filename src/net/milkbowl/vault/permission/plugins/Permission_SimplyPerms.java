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
package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.crystalyx.bukkit.simplyperms.SimplyPlugin;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_SimplyPerms extends Permission{

    private final String name = "SimplyPerms";
    private SimplyPlugin service;
    private final ConsoleCommandSender ccs;
    
    public Permission_SimplyPerms(Vault plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
        ccs = Bukkit.getServer().getConsoleSender();
        // Load service in case it was loaded before
        if (service == null) {
            service = plugin.getServer().getServicesManager().load(SimplyPlugin.class);
            if (service != null)
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
        }
    }
    
    public class PermissionServerListener implements Listener {
        Permission_SimplyPerms permission = null;

        public PermissionServerListener(Permission_SimplyPerms permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.service == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("SimplyPerms");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.service = (SimplyPlugin) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.service != null) {
                if (event.getPlugin().getDescription().getName().equals("SimplyPerms")) {
                    permission.service = null;
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
    public boolean isEnabled() {
        return service != null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Map<String, Boolean> playerPermissions = this.service.getAPI().getPlayerPermissions(player, world);
        return playerPermissions.containsKey(permission) && playerPermissions.get(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        Map<String, Boolean> groupPermissions = this.service.getAPI().getGroupPermissions(group, world);
        return groupPermissions.containsKey(permission) && groupPermissions.get(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (world != null) {
            for (String g : service.getAPI().getPlayerGroups(player)) {
                if (g.equals(group)) {
                    return service.getAPI().getGroupWorlds(group).contains(world);
                }
            }
            return false;
        }

        if (!service.getAPI().getAllGroups().contains(group)) {
            return false;
        }
        return service.getAPI().getPlayerGroups(player).contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        group = group.toLowerCase();
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        List<String> groupList = new ArrayList<String>();
        if (world != null && service.getAPI().isPlayerInDB(player)) {
            for (String group : service.getAPI().getPlayerGroups(player)) {
                if (service.getAPI().getGroupWorlds(group).contains(world)) {
                    groupList.add(group);
                }
            }
            return groupList.toArray(new String[0]);
        }
        if (service.getAPI().isPlayerInDB(player)) {
            for (String group : service.getAPI().getPlayerGroups(player)) {
                groupList.add(group);
            }
        }
        return groupList.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        if (!service.getAPI().isPlayerInDB(player)) {
            return null;
        } else if (service.getAPI().getPlayerGroups(player) != null && !service.getAPI().getPlayerGroups(player).isEmpty() ) {
            return service.getAPI().getPlayerGroups(player).get(0);
        }
        return null;
    }

    @Override
    public String[] getGroups() {
        return service.getAPI().getAllGroups().toArray(new String[0]);
    }
    
}
