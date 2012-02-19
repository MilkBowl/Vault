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

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin;

public class Permission_zPermissions extends Permission {

    private final String name = "zPermissions";
    private ZPermissionsPlugin perms;
    private final ConsoleCommandSender ccs;

    public Permission_zPermissions(Vault plugin) {
        this.plugin = plugin;
        ccs = Bukkit.getServer().getConsoleSender();
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
        // Load Plugin in case it was loaded before
        if (perms == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("zPermissions");
            if (p != null) {
                perms = (ZPermissionsPlugin) p;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (perms == null) {
                Plugin p = event.getPlugin();
                if(p.getDescription().getName().equals("zPermissions") && p.isEnabled()) {
                    perms = (ZPermissionsPlugin) p;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (perms != null) {
                if (event.getPlugin().getDescription().getName().equals("zPermissions")) {
                    perms = null;
                    perms = null;
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
        if (perms == null) {
            return false;
        } else {
            return perms.isEnabled();
        }
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Player p = Bukkit.getServer().getPlayer(player);
        if (p == null)
            throw new UnsupportedOperationException(getName() + " does not support offline player resolution.");
        else
            return playerHas(p, permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions player set " + player + " " + permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions player unset " + player + " " + permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions group set " + group + " " + permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions group unset " + group + " " + permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        Player p = Bukkit.getServer().getPlayer(player);
        if (p == null)
            throw new UnsupportedOperationException(getName() + " does not support offline player resolution.");

        return p.hasPermission("group." + group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions group add " + player);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        if (world != null)
            return false;
        return plugin.getServer().dispatchCommand(ccs, "permissions group remove " + player);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Player p = Bukkit.getServer().getPlayer(player);
        if (p == null)
            throw new UnsupportedOperationException(getName() + " does not support offline player resolution.");

        List<String> groups = new ArrayList<String>();
        for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
            if (!pai.getPermission().startsWith("group.") || !pai.getValue())
                continue;
            groups.add(pai.getPermission().substring(6));
        }
        return groups.toArray(new String[0]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        throw new UnsupportedOperationException(getName() + " does not support primary group resolution.");
    }

    @Override
    public String[] getGroups() {
        throw new UnsupportedOperationException(getName() + " does not support group resolution.");
    }
}
