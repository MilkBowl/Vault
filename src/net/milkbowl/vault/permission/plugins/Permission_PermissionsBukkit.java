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

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class Permission_PermissionsBukkit extends Permission {

    private final String name = "PermissionsBukkit";
    private PermissionsPlugin perms = null;

    public Permission_PermissionsBukkit(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (perms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
            if (perms != null) {
                this.perms = (PermissionsPlugin) perms;
                log.info(String.format("[Permission] %s hooked.", name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_PermissionsBukkit permission = null;

        public PermissionServerListener(Permission_PermissionsBukkit permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.perms == null) {
                Plugin perms = event.getPlugin();
                if (perms.getDescription().getName().equals("PermissionsBukkit")) {
                    permission.perms = (PermissionsPlugin) perms;
                    log.info(String.format("[Permission] %s hooked.", permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.perms != null) {
                if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit")) {
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
        if (perms == null) {
            return false;
        } else {
            return perms.isEnabled();
        }
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        if (Bukkit.getPlayer(player) != null) {
            return Bukkit.getPlayer(player).hasPermission(permission);
        } else {
            return false;
        }
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
    }

    // use superclass implementation of playerAddTransient() and playerRemoveTransient()

    @Override
    public boolean groupHas(String world, String group, String permission) {
        if (world != null && !world.isEmpty()) {
            return perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission) == null ? false : perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission);
        }
        if (perms.getGroup(group) == null) {
            return false;
        } else if (perms.getGroup(group).getInfo() == null) {
            return false;
        } else if (perms.getGroup(group).getInfo().getPermissions() == null) {
            return false;
        }
        return perms.getGroup(group).getInfo().getPermissions().get(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (world != null) {
            for (Group g : perms.getPlayerInfo(player).getGroups()) {
                if (g.getName().equals(group)) {
                    return g.getInfo().getWorlds().contains(world);
                }
            }
            return false;
        }
        Group g = perms.getGroup(group);
        if (g == null) {
            return false;
        }
        return g.getPlayers().contains(player);
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
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        List<String> groupList = new ArrayList<String>();
        PermissionInfo info = perms.getPlayerInfo(player);
        if (world != null && info != null) {
            for (Group group : perms.getPlayerInfo(player).getGroups()) {
                if (group.getInfo().getWorlds().contains(world)) {
                    groupList.add(group.getName());
                }
            }
            return groupList.toArray(new String[0]);
        }
        if (info != null) {
            for (Group group : info.getGroups()) {
                groupList.add(group.getName());
            }
        }
        return groupList.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        if (perms.getPlayerInfo(player) == null) {
            return null;
        } else if (perms.getPlayerInfo(player).getGroups() != null && !perms.getPlayerInfo(player).getGroups().isEmpty() ) {
            return perms.getPlayerInfo(player).getGroups().get(0).getName();
        }
        return null;
    }

    @Override
    public String[] getGroups() {
        List<String> groupNames = new ArrayList<String>();
        for (Group group : perms.getAllGroups()) {
            groupNames.add(group.getName());
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
