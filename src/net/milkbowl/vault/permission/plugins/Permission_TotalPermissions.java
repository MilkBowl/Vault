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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.ae97.totalpermissions.PermissionManager;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionBase;
import net.ae97.totalpermissions.permission.PermissionUser;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_TotalPermissions extends Permission {

    private final String name = "TotalPermissions";
    private PermissionManager manager;
    private TotalPermissions totalperms;

    public Permission_TotalPermissions(Plugin pl) {
        this.plugin = pl;
    }

    public class PermissionServerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (manager == null || totalperms == null) {
                Plugin permPlugin = event.getPlugin();
                if (permPlugin.getDescription().getName().equals(name)) {
                    totalperms = (TotalPermissions) permPlugin;
                    manager = totalperms.getManager();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (manager != null) {
                if (event.getPlugin().getDescription().getName().equals(name)) {
                    totalperms = null;
                    manager = null;
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
        return plugin != null && plugin.isEnabled() && totalperms != null && totalperms.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        PermissionBase user = manager.getUser(player);
        return user.has(permission, world);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        try {
            PermissionBase user = manager.getUser(player);
            user.addPerm(permission, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        try {
            PermissionBase user = manager.getUser(player);
            user.remPerm(permission, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        PermissionBase permGroup = manager.getGroup(group);
        return permGroup.has(permission, world);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        try {
            PermissionBase permGroup = manager.getGroup(group);
            permGroup.addPerm(permission, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        try {
            PermissionBase permGroup = manager.getGroup(group);
            permGroup.remPerm(permission, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        PermissionUser user = manager.getUser(player);
        List<String> groups = user.getGroups(world);
        return groups.contains(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        try {
            PermissionUser user = manager.getUser(player);
            user.addGroup(group, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        try {
            PermissionUser user = manager.getUser(player);
            user.remGroup(group, world);
            return true;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    String.format("[%s] An error occured while saving perms", totalperms.getDescription().getName()), ex);
            return false;
        }
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        PermissionUser user = manager.getUser(player);
        List<String> groups = user.getGroups(world);
        if (groups == null) {
            groups = new ArrayList<String>();
        }
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        String[] groups = getPlayerGroups(world, player);
        if (groups.length == 0) {
            return "";
        } else {
            return groups[0];
        }
    }

    @Override
    public String[] getGroups() {
        return manager.getGroups();
    }
}
