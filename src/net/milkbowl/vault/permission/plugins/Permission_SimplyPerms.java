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

import net.crystalyx.bukkit.simplyperms.SimplyAPI;
import net.crystalyx.bukkit.simplyperms.SimplyPlugin;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_SimplyPerms extends Permission{

    private final String name = "SimplyPerms";
    private SimplyAPI perms;

    public Permission_SimplyPerms(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
        // Load service in case it was loaded before
        if (perms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("SimplyPerms");
            if (perms != null && perms.isEnabled()) {
                this.perms = ((SimplyPlugin) perms).getAPI();
                log.info(String.format("[Permission] %s hooked.", name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_SimplyPerms permission = null;

        public PermissionServerListener(Permission_SimplyPerms permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.perms == null) {
                Plugin perms = event.getPlugin();
                if (perms.getDescription().getName().equals("SimplyPerms")) {
                    permission.perms = ((SimplyPlugin) perms).getAPI();
                    log.info(String.format("[Permission] %s hooked.", permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.perms != null) {
                if (event.getPlugin().getDescription().getName().equals("SimplyPerms")) {
                    permission.perms = null;
                    log.info(String.format("[Permission] %s un-hooked.", permission.name));
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
        return perms != null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        permission = permission.toLowerCase();
        Map<String, Boolean> playerPermissions = this.perms.getPlayerPermissions(player, world);
        return playerPermissions.containsKey(permission) && playerPermissions.get(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            this.perms.addPlayerPermission(player, world, permission, true);
        } else {
            this.perms.addPlayerPermission(player, permission, true);
        }
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            this.perms.removePlayerPermission(player, world, permission);
        } else {
            this.perms.removePlayerPermission(player, permission);
        }
        return true;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        permission = permission.toLowerCase();
        Map<String, Boolean> groupPermissions = this.perms.getGroupPermissions(group, world);
        return groupPermissions.containsKey(permission) && groupPermissions.get(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            this.perms.addGroupPermission(group, world, permission, true);
        } else {
            this.perms.addGroupPermission(group, permission, true);
        }
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        permission = permission.toLowerCase();
        if (world != null) {
            permission = world + ":" + permission;
            this.perms.removeGroupPermission(group, world, permission);
        } else {
            this.perms.removeGroupPermission(group, permission);
        }
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (world != null) {
            for (String g : perms.getPlayerGroups(player)) {
                if (g.equals(group)) {
                    return perms.getGroupWorlds(group).contains(world);
                }
            }
            return false;
        }

        if (!perms.getAllGroups().contains(group)) {
            return false;
        }
        return perms.getPlayerGroups(player).contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        group = group.toLowerCase();
        this.perms.addPlayerGroup(player, group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        group = group.toLowerCase();
        this.perms.removePlayerGroup(player, group);
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        List<String> groupList = new ArrayList<String>();
        if (world != null && perms.isPlayerInDB(player)) {
            for (String group : perms.getPlayerGroups(player)) {
                if (perms.getGroupWorlds(group).contains(world)) {
                    groupList.add(group);
                }
            }
            return groupList.toArray(new String[0]);
        }
        if (perms.isPlayerInDB(player)) {
            for (String group : perms.getPlayerGroups(player)) {
                groupList.add(group);
            }
        }
        return groupList.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        if (!perms.isPlayerInDB(player)) {
            return null;
        } else if (perms.getPlayerGroups(player) != null && !perms.getPlayerGroups(player).isEmpty() ) {
            return perms.getPlayerGroups(player).get(0);
        }
        return null;
    }

    @Override
    public String[] getGroups() {
        return perms.getAllGroups().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

}
