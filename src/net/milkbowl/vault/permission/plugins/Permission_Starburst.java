package net.milkbowl.vault.permission.plugins;

import com.dthielke.starburst.*;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Permission_Starburst extends Permission {
    private StarburstPlugin perms;
    private String name = "Starburst";
    private PermissionServerListener permissionServerListener;

    private class PermissionServerListener extends ServerListener {
        public void onPluginEnable(PluginEnableEvent event) {
            if (perms == null) {
                Plugin p = event.getPlugin();
                if (p.getDescription().getName().equals("bPermissions") && p.isEnabled()) {
                    perms = (StarburstPlugin) p;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (perms != null) {
                if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
                    perms = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    public Permission_Starburst(Vault plugin) {
        this.plugin = plugin;

        permissionServerListener = new PermissionServerListener();

        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

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
}
