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

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.overmc.overpermissions.api.GroupManager;
import com.overmc.overpermissions.api.PermissionGroup;
import com.overmc.overpermissions.api.PermissionUser;
import com.overmc.overpermissions.api.UserManager;
import com.overmc.overpermissions.internal.OverPermissions;

public class Permission_OverPermissions extends Permission {

    private final String name = "OverPermissions";
    private OverPermissions overPerms;
    private UserManager userManager;
    private GroupManager groupManager;

    public Permission_OverPermissions(Plugin plugin) {
        super.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        if (overPerms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
            if ((perms != null) && (perms.isEnabled())) {
                overPerms = ((OverPermissions) perms);
                userManager = overPerms.getUserManager();
                groupManager = overPerms.getGroupManager();
                log.info(String.format("[%s][Permission] %s hooked.", new Object[] {plugin.getDescription().getName(), "OverPermissions"}));
            }
        }
    }

    @Override
    public String getName( ) {
        return name;
    }

    @Override
    public boolean isEnabled( ) {
        return (overPerms != null) && (overPerms.isEnabled());
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        if (!userManager.doesUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).getPermission(permission, worldName);
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        if (!userManager.canUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).addPermissionNode(permission, worldName);
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        if (!userManager.canUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).removePermissionNode(permission, worldName);
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        return groupManager.getGroup(groupName).getPermission(permission, worldName);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        if (worldName == null) {
            return groupManager.getGroup(groupName).addGlobalPermissionNode(permission);
        } else {
            return groupManager.getGroup(groupName).addPermissionNode(permission, worldName);
        }
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        if (worldName == null) {
            return groupManager.getGroup(groupName).removeGlobalPermissionNode(permission);
        } else {
            return groupManager.getGroup(groupName).removePermissionNode(permission, worldName);
        }
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        if (!userManager.doesUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).getAllParents().contains(groupManager.getGroup(groupName));
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        if (!userManager.canUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).addParent(groupManager.getGroup(groupName));
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        if (!groupManager.doesGroupExist(groupName)) {
            return false;
        }
        if (!userManager.canUserExist(playerName)) {
            return false;
        }
        return userManager.getPermissionUser(playerName).removeParent(groupManager.getGroup(groupName));
    }

    @Override
    public String[] getPlayerGroups(String worldName, String playerName) {
        ArrayList<String> ret = new ArrayList<String>();
        if (!userManager.doesUserExist(playerName)) {
            return new String[0];
        }
        PermissionUser user = userManager.getPermissionUser(playerName);
        for (PermissionGroup parent : user.getAllParents()) {
            ret.add(parent.getName());
        }
        return ret.toArray(new String[ret.size()]);
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
    public boolean playerAddTransient(String world, String playerName, String permission) {
        if (!userManager.doesUserExist(playerName)) { // Can't add transient permissions to an offline player.
            return false;
        }
        PermissionUser user = userManager.getPermissionUser(playerName);
        return (world == null) ? user.addGlobalTransientPermissionNode(permission) : user.addTransientPermissionNode(permission, world);
    }

    @Override
    public boolean playerRemoveTransient(String world, String playerName, String permission) {
        if (!userManager.doesUserExist(playerName)) {
            return false;
        }
        PermissionUser user = userManager.getPermissionUser(playerName);
        return (world == null) ? user.removeGlobalTransientPermissionNode(permission) : user.removeTransientPermissionNode(permission, world);
    }

    @Override
    public String[] getGroups( ) {
        ArrayList<String> groupNames = new ArrayList<String>();
        for (PermissionGroup s : groupManager.getGroups()) {
            groupNames.add(s.getName());
        }
        return groupNames.toArray(new String[groupNames.size()]);
    }

    @Override
    public boolean hasSuperPermsCompat( ) {
        return true;
    }

    @Override
    public boolean hasGroupSupport( ) {
        return true;
    }

    public class PermissionServerListener
            implements Listener {
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
                    Permission_OverPermissions.log.info(String
                            .format("[%s][Permission] %s hooked.", new Object[] {Permission_OverPermissions.this.plugin.getDescription().getName(), "OverPermissions"}));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event)
        {
            if ((permission.overPerms != null) &&
                    (event.getPlugin().getDescription().getName().equals("OverPermissions"))) {
                permission.overPerms = null;
                Permission_OverPermissions.log.info(String
                        .format("[%s][Permission] %s un-hooked.", new Object[] {Permission_OverPermissions.this.plugin.getDescription().getName(), "OverPermissions"}));
            }
        }
    }
}
