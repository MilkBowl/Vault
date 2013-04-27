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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class Permission_zPermissions extends Permission {

    private static final String PRIMARY_GROUP_TRACK_METADATA_KEY = "Vault.primary-group.track";

    private final String name = "zPermissions";
    private ZPermissionsService service;
    private final ConsoleCommandSender ccs;
    private boolean trackSupport;

    public Permission_zPermissions(Plugin plugin) {
        this.plugin = plugin;
        ccs = Bukkit.getServer().getConsoleSender();
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
        // Load service in case it was loaded before
        if (service == null) {
            service = plugin.getServer().getServicesManager().load(ZPermissionsService.class);
            if (service != null) {
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                detectTrackMethods();
            }
        }
    }

    public class PermissionServerListener implements Listener {
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (service == null) {
                service = plugin.getServer().getServicesManager().load(ZPermissionsService.class);
                if (service != null) {
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                    detectTrackMethods();
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (service != null) {
                if (event.getPlugin().getDescription().getName().equals("zPermissions")) {
                    service = null;
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
    public boolean isEnabled() {
        return service != null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Player p = Bukkit.getServer().getPlayer(player);
        if (p == null) {
            Map<String, Boolean> perms = service.getPlayerPermissions(world, null, player);
            Boolean value = perms.get(permission.toLowerCase());
            if (value != null) {
                return value;
            }
            // Use default at this point
            org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(permission);
            if (perm != null) {
                OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(player);
                return perm.getDefault().getValue(op != null ? op.isOp() : false);
            }
            // Have no clue
            return false;
        } else {
            return playerHas(p, permission);
        }
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions player " + player + " set " + permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions player " + player + " unset " + permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        Map<String, Boolean> perms = service.getGroupPermissions(world, null, group);
        Boolean value = perms.get(permission.toLowerCase());
        if (value != null) {
            return value;
        }
        // Use default, if possible
        org.bukkit.permissions.Permission perm = Bukkit.getPluginManager().getPermission(permission);
        if (perm != null) {
            return perm.getDefault().getValue(false); // OP flag assumed to be false...
        }
        // Who knows...
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions group " + group + " set " + permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world != null) {
            permission = world + ":" + permission;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions group " + group + " unset " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        Set<String> groups = service.getPlayerGroups(player);
        // Groups are case-insensitive...
        for (String g : groups) {
            if (g.equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions group " + group + " add " + player);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        return plugin.getServer().dispatchCommand(ccs, "permissions group " + group + " remove " + player);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return service.getPlayerGroups(player).toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        try {
            if (trackSupport) {
                String track = service.getPlayerMetadata(player, PRIMARY_GROUP_TRACK_METADATA_KEY, String.class);
                if (track != null && !"".equals(track)) {
                    List<String> groups = service.getTrackGroups(track);
                    Collections.reverse(groups); // groups is now high rank to low

                    Set<String> trackGroups = new LinkedHashSet<String>(groups);
                    trackGroups.retainAll(service.getPlayerAssignedGroups(player)); // intersection with all assigned groups

                    if (!trackGroups.isEmpty())
                        return trackGroups.iterator().next(); // return highest-ranked group in given track
                }
            }
        }
        catch (IllegalStateException e) {
            log.warning("Bad property '" + PRIMARY_GROUP_TRACK_METADATA_KEY + "' for " + player + "; is it a string and does the track exist?");
        }

        // Has no concept of primary group... use highest-priority assigned group instead
        List<String> groups = service.getPlayerAssignedGroups(player);
        if (!groups.isEmpty()) {
            return groups.get(0);
        } else {
            return null;
        }
    }

    @Override
    public String[] getGroups() {
        return service.getAllGroups().toArray(new String[0]);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    private void detectTrackMethods() {
        try {
            service.getClass().getMethod("getTrackGroups", String.class);
            trackSupport = true;
        }
        catch (SecurityException e) {
            trackSupport = false;
        }
        catch (NoSuchMethodException e) {
            trackSupport = false;
        }
    }

}
