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

import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.Group;
import com.nijiko.permissions.ModularControl;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Permission_Permissions3 extends Permission {

    private String name = "Permissions3";
    private ModularControl perms;
    private Permissions permission = null;

    public Permission_Permissions3(Vault plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms == null) {
                plugin.getServer().getPluginManager().getPlugin("vPerms");
                name = "vPerms";
            }
            if (perms != null) {
                if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
                    permission = (Permissions) perms;
                    this.perms = (ModularControl) permission.getHandler();
                    log.severe("Your permission system is outdated and no longer fully supported! It is highly advised to update!");
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
        return this.permission.getHandler().inGroup(worldName, playerName, groupName);
    }

    public class PermissionServerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission == null) {
                Plugin permi = event.getPlugin();
                if((permi.getDescription().getName().equals("Permissions") || permi.getDescription().getName().equals("vPerms")) && permi.getDescription().getVersion().startsWith("3")) {
                    if (permi.isEnabled()) {
                        permission = (Permissions) permi;
                        perms = (ModularControl) permission.getHandler();
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions") || event.getPlugin().getDescription().getName().equals("vPerms")) {
                    permission = null;
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
    public boolean has(CommandSender sender, String permission) {
        if (sender.isOp() || sender instanceof ColouredConsoleSender) {
            return true;
        } else {
            return has(((Player) sender).getWorld().getName(), sender.getName(), permission);
        }
    }

    @Override
    public boolean has(Player player, String permission) {
        return has(player.getWorld().getName(), player.getName(), permission);
    }

    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        if (worldName == null)
            worldName = "*";

        Group g = perms.getGroupObject(worldName, groupName);
        if (g == null) {
            return false;
        }
        try {
            perms.safeGetUser(worldName, playerName).addParent(g);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        if (worldName == null)
            worldName = "*";

        Group g = perms.getGroupObject(worldName, groupName);
        if (g == null) {
            return false;
        }
        try {
            perms.safeGetUser(worldName, playerName).removeParent(g);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        this.perms.addUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        this.perms.removeUserPermission(worldName, playerName, permission);
        return true;
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        if (worldName == null)
            worldName = "*";

        perms.addGroupPermission(worldName, groupName, permission);
        return true;
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        if (worldName == null)
            worldName = "*";
        perms.removeGroupPermission(worldName, groupName, permission);
        return true;
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        if (worldName == null)
            worldName = "*";
        try {
            return perms.safeGetGroup(worldName, groupName).hasPermission(permission);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        return this.perms.getGroups(world, playerName);
    }

    public String getPrimaryGroup(String world, String playerName) {
        return getPlayerGroups(world, playerName)[0];
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        Player p = plugin.getServer().getPlayer(playerName);
        if (p != null) {
            if (p.hasPermission(permission))
                return true;
        }
        return this.perms.has(worldName, playerName, permission);
    }


    @Override
    public boolean playerAddTransient(String player, String permission) {
        return playerAddTransient(null, player, permission);
    }

    @Override
    public boolean playerAddTransient(Player player, String permission) {
        return playerAddTransient(null, player.getName(), permission);
    }

    @Override
    public boolean playerAddTransient(String worldName, Player player, String permission) {
        return playerAddTransient(worldName, player.getName(), permission);
    }

    @Override
    public boolean playerAddTransient(String worldName, String player, String permission) {
        if (worldName == null)
            worldName = "*";
        try {
            perms.safeGetUser(worldName, player).addTransientPermission(permission);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public boolean playerRemoveTransient(String player, String permission) {
        return playerRemoveTransient(null, player, permission);
    }

    @Override
    public boolean playerRemoveTransient(Player player, String permission) {
        return playerRemoveTransient(null, player.getName(), permission);
    }

    @Override
    public boolean playerRemoveTransient(String worldName, Player player, String permission) {
        return playerRemoveTransient(worldName, player.getName(), permission);
    }

    @Override
    public boolean playerRemoveTransient(String worldName, String player, String permission) {
        if (worldName == null)
            worldName = "*";

        try {
            perms.safeGetUser(worldName, player).removeTransientPermission(permission);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String[] getGroups() {

        Set<String> groupNames = new HashSet<String>();
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Group group : perms.getGroups(world.getName())) {
                groupNames.add(group.getName());
            }
        }
        return groupNames.toArray(new String[0]);
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }
}
