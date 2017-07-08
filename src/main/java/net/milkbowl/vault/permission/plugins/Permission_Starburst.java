/*
 * This file is part of Vault.
 *
 * Copyright (C) 2017 Lukas Nehrke
 * Copyright (C) 2011 Morgan Humes <morgan@lanaddict.com>
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault.permission.plugins;

import com.dthielke.starburst.Group;
import com.dthielke.starburst.GroupManager;
import com.dthielke.starburst.GroupSet;
import com.dthielke.starburst.StarburstPlugin;
import com.dthielke.starburst.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_Starburst extends Permission {
    private final String name = "Starburst";
  private StarburstPlugin perms;

    public Permission_Starburst(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);

        // Load Plugin in case it was loaded before
        if (perms == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("Starburst");
            if (p != null) {
                perms = (StarburstPlugin) p;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    @Override
    public String[] getGroups() {
        String[] s = new String[perms.getGroupManager().getDefaultGroupSet().getGroups().size()];
        int i = 0;
        for (Group g : perms.getGroupManager().getDefaultGroupSet().getGroups()) {
            s[i] = g.getName();
            i++;
        }
        return s;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        Set<Group> children = user.getChildren(true);
        List<String> groups = new ArrayList<String>();
        for (Group child : children) {
            groups.add(child.getName());
        }
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        Set<Group> children = user.getChildren(false);
        if (!children.isEmpty()) {
            return children.iterator().next().getName();
        } else {
            return null;
        }
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        GroupManager gm = perms.getGroupManager();
        GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);

            boolean value = !permission.startsWith("^");
            permission = value ? permission : permission.substring(1);
            g.addPermission(permission, value, true, true);

            for (User user : gm.getAffectedUsers(g)) {
                user.applyPermissions(gm.getFactory());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);
            return g.hasPermission(permission, true);
        } else {
            return false;
        }
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        GroupManager gm = perms.getGroupManager();
        GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);

            boolean value = !permission.startsWith("^");
            permission = value ? permission : permission.substring(1);

            if (g.hasPermission(permission, false)) {
                g.removePermission(permission, true);

                for (User user : gm.getAffectedUsers(g)) {
                    user.applyPermissions(gm.getFactory());
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return perms != null && perms.isEnabled();
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        boolean value = !permission.startsWith("^");
        permission = value ? permission : permission.substring(1);
        user.addPermission(permission, value, true, true);

        if (user.isActive()) {
            user.applyPermissions(perms.getGroupManager().getFactory());
        }
        return true;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);
            if (!user.hasChild(g, false)) {
                user.addChild(g, true);

                if (user.isActive()) {
                    user.applyPermissions(perms.getGroupManager().getFactory());
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);

        if (op.isOnline()) {
            Player p = (Player) op;
            if (p.getWorld().getName().equalsIgnoreCase(world)) {
                return p.hasPermission(permission);
            }
        }

        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        Group user = set.getUser(op);
        return user.hasPermission(permission, true);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);
            return user.hasChild(g, true);
        } else {
            return false;
        }
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        boolean value = !permission.startsWith("^");
        permission = value ? permission : permission.substring(1);
        if (user.hasPermission(permission, false)) {
            user.removePermission(permission, true);
            if (user.isActive()) {
                user.applyPermissions(perms.getGroupManager().getFactory());
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        GroupSet set = perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
        User user = set.getUser(op);

        if (set.hasGroup(group)) {
            Group g = set.getGroup(group);
            if (user.hasChild(g, false)) {
                user.removeChild(g, true);

                if (user.isActive()) {
                    user.applyPermissions(perms.getGroupManager().getFactory());
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

  public class PermissionServerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
      if (perms == null) {
        Plugin p = event.getPlugin();
        if (p.getDescription().getName().equals("Starburst")) {
          perms = (StarburstPlugin) p;
          log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
      if (perms != null) {
        if (event.getPlugin().getDescription().getName().equals("Starburst")) {
          perms = null;
          log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
        }
      }
    }
  }
}
