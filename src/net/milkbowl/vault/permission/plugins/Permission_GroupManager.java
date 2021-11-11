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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.permission.Permission;

public class Permission_GroupManager extends Permission {

    private final String name = "GroupManager";
    private GroupManager groupManager;

    public Permission_GroupManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (groupManager == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
            if (perms != null && perms.isEnabled()) {
                groupManager = (GroupManager) perms;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_GroupManager permission;

        public PermissionServerListener(Permission_GroupManager permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.groupManager == null) {
                Plugin p = event.getPlugin();
                if (p.getDescription().getName().equals("GroupManager")) {
                    permission.groupManager = (GroupManager) p;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.groupManager != null) {
                if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
                    permission.groupManager = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        return groupManager != null && groupManager.isEnabled();
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        }
        else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return false;
        }
        return handler.permission(playerName, permission);
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }

        User user = owh.getUser(playerName);
        if (user == null) {
            return false;
        }

        user.addPermission(permission);
        Player p = Bukkit.getPlayer(playerName);
        if (p != null) {
            GroupManager.BukkitPermissions.updatePermissions(p);
        }
        return true;
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }

        User user = owh.getUser(playerName);
        if (user == null) {
            return false;
        }

        user.removePermission(permission);
        Player p = Bukkit.getPlayer(playerName);
        if (p != null) {
            GroupManager.BukkitPermissions.updatePermissions(p);
        }
        return true;
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }

        Group group = owh.getGroup(groupName);
        if (group == null) {
            return false;
        }

        return group.hasSamePermissionNode(permission);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }

        Group group = owh.getGroup(groupName);
        if (group == null) {
            return false;
        }

        group.addPermission(permission);
        return true;
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }

        Group group = owh.getGroup(groupName);
        if (group == null) {
            return false;
        }

        group.removePermission(permission);
        return true;
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return false;
        }
        return handler.inGroup(playerName, groupName);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return false;
        }
        Group group = owh.getGroup(groupName);
        if (group == null) {
            return false;
        }
        if (user.getGroup().equals(owh.getDefaultGroup())) {
            user.setGroup(group);
        } else if (group.getInherits().contains(user.getGroup().getName().toLowerCase())) {
            user.setGroup(group);
        } else {
            user.addSubGroup(group);
        }
        Player p = Bukkit.getPlayer(playerName);
        if (p != null) {
            GroupManager.BukkitPermissions.updatePermissions(p);
        }
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
        }
        if (owh == null) {
            return false;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return false;
        }
        boolean success = false;
        if (user.getGroup().getName().equalsIgnoreCase(groupName)) {
            user.setGroup(owh.getDefaultGroup());
            success = true;
        } else {
            Group group = owh.getGroup(groupName);
            if (group != null) {
                success = user.removeSubGroup(group);
            }
        }
        if (success) {
            Player p = Bukkit.getPlayer(playerName);
            if (p != null) {
                GroupManager.BukkitPermissions.updatePermissions(p);
            }
        }
        return success;
    }

    @Override
    public String[] getPlayerGroups(String worldName, String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return null;
        }
        return handler.getGroups(playerName);
    }

    @Override
    public String getPrimaryGroup(String worldName, String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return null;
        }
        return handler.getGroup(playerName);
    }

    @Override
    public String[] getGroups() {
        Set<String> groupNames = new HashSet<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(world.getName());
            if (owh == null) {
                continue;
            }
            Collection<Group> groups = owh.getGroupList();
            if (groups == null) {
                continue;
            }
            for (Group group : groups) {
                groupNames.add(group.getName());
            }
        }
        return groupNames.toArray(new String[0]);
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
