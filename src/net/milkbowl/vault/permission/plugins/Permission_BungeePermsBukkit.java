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

import java.util.List;
import net.alpenblock.bungeeperms.bukkit.BungeePerms;
import net.alpenblock.bungeeperms.bukkit.Group;
import net.alpenblock.bungeeperms.bukkit.User;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_BungeePermsBukkit extends Permission {

    private final String name = "BungeePermsBukkit";
    private BungeePerms permission = null;

    public Permission_BungeePermsBukkit(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("BungeePermsBukkit");
            if (perms != null) {
                if (perms.isEnabled()) {
                    permission = (BungeePerms) perms;
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
        User u = BungeePerms.getInstance().getPermissionsManager().getUser(playerName);
        if (u == null) {
            throw new NullPointerException("player " + playerName + " doesn't exist");
        }
        for (Group g : u.getGroups()) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                return true;
            }
        }
        return false;
    }

    public class PermissionServerListener implements Listener {

        Permission_BungeePermsBukkit permission = null;

        public PermissionServerListener(Permission_BungeePermsBukkit permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = event.getPlugin();
                if (perms.getDescription().getName().equals("BungeePermsBukkit")) {
                    permission.permission = (BungeePerms) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("BungeePermsBukkit")) {
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
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        Group group = BungeePerms.getInstance().getPermissionsManager().getGroup(groupName);
        if (group == null) {
            return false;
        } else {
            if (worldName == null) {
                return group.hasOnServer(permission, BungeePerms.getInstance().getServerName());
            }
            return group.hasOnServerInWorld(permission, BungeePerms.getInstance().getServerName(), worldName);
        }
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        User u = BungeePerms.getInstance().getPermissionsManager().getUser(playerName);
        if (u == null) {
            return null;
        }

        String[] groups = new String[u.getGroups().size()];
        for (int i = 0; i < u.getGroups().size(); i++) {
            groups[i] = u.getGroups().get(i).getName();
        }
        return groups;
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        User user = BungeePerms.getInstance().getPermissionsManager().getUser(playerName);
        if (user == null) {
            return null;
        }
        Group g = BungeePerms.getInstance().getPermissionsManager().getMainGroup(user);
        if (g == null) {
            return null;
        }
        return g.getName();
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        return BungeePerms.getInstance().getPermissionsManager().hasPermOnServerInWorld(playerName, permission, BungeePerms.getInstance().getServerName(), worldName);
    }

    @Override
    public boolean playerAddTransient(String worldName, String player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerAddTransient(String worldName, Player player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerAddTransient(String player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerAddTransient(Player player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemoveTransient(String worldName, String player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemoveTransient(Player player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemoveTransient(String worldName, Player player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public boolean playerRemoveTransient(String player, String permission) {
        throw new UnsupportedOperationException("BungeePerms permission operations are BungeeCord-only");
    }

    @Override
    public String[] getGroups() {
        List<Group> groups = BungeePerms.getInstance().getPermissionsManager().getGroups();
        String[] groupNames = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNames[i] = groups.get(i).getName();
        }
        return groupNames;
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
