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

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.overmc.overpermissions.OverPermissions;
import com.overmc.overpermissions.OverPermissionsAPI;

public class Permission_OverPermissions extends Permission {

    private final String name = "OverPermissions";
    private OverPermissions overPerms;
    private OverPermissionsAPI api;

    public Permission_OverPermissions(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        if (overPerms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
            if ((perms != null) && (perms.isEnabled())) {
                overPerms = ((OverPermissions) perms);
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "OverPermissions"));
            }
        }

        if ((api == null) && (overPerms != null)) {
            api = overPerms.getAPI();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return (overPerms != null) && (overPerms.isEnabled());
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        return api.playerHas(worldName, playerName, permission);
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        return api.playerAdd(worldName, playerName, permission);
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        return api.playerRemove(worldName, playerName, permission);
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        return api.groupHas(groupName, permission);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        return api.groupAdd(groupName, permission);
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        return api.groupRemove(groupName, permission);
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return api.groupHasPlayer(playerName, groupName);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        return api.playerAddGroup(playerName, groupName);
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        return api.playerRemoveGroup(playerName, groupName);
    }

    @Override
    public String[] getPlayerGroups(String worldName, String playerName) {
        return api.getPlayerGroups(worldName, playerName);
    }

    @Override
    public String getPrimaryGroup(String worldName, String playerName) {
        String[] playerGroups = getPlayerGroups(worldName, playerName);
        if (playerGroups.length == 0) {
            return null;
        }
        return playerGroups[0];
    }

    @Override
    public boolean playerAddTransient(String world, String player, String permission) {
        return api.playerAddTransient(world, player, permission);
    }

    @Override
    public boolean playerRemoveTransient(String world, String player, String permission) {
        return api.playerRemoveTransient(world, player, permission);
    }

    @Override
    public String[] getGroups() {
        return api.getGroupsArray();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    public class PermissionServerListener implements Listener {
        Permission_OverPermissions permission = null;

        public PermissionServerListener(Permission_OverPermissions permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.overPerms == null) {
                Plugin perms = Permission_OverPermissions.this.plugin.getServer().getPluginManager().getPlugin("OverPermissions");
                if (perms != null) {
                    permission.overPerms = ((OverPermissions) perms);
                    Permission_OverPermissions.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "OverPermissions"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if ((permission.overPerms != null) &&
                    (event.getPlugin().getDescription().getName().equals("OverPermissions"))) {
                permission.overPerms = null;
                Permission_OverPermissions.log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), "OverPermissions"));
            }
        }
    }
}