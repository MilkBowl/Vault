package net.milkbowl.vault.permission.plugins;

import java.util.Map;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.dthielke.starburst.Group;
import com.dthielke.starburst.GroupSet;
import com.dthielke.starburst.StarburstPlugin;
import com.dthielke.starburst.User;

public class Permission_Starburst extends Permission {

    private StarburstPlugin perms;
    private String name = "Starburst";
    private PermissionServerListener permissionServerListener;

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
    private class PermissionServerListener extends ServerListener {
        public void onPluginEnable(PluginEnableEvent event) {
            if (perms == null) {
                Plugin p = event.getPlugin();
                if(p.getDescription().getName().equals("bPermissions") && p.isEnabled()) {
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


    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return perms != null && perms.isEnabled();
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getUser(op);
        if (g == null) {
            return false;
        }
        Map<String, Boolean> effective = g.aggregatePermissions();
        return effective.containsKey(permission) ? effective.get(permission) : false;
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getUser(op);
        if (g == null) {
            return false;
        }
        g.addPermission(permission, true, true, op.isOnline());
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getUser(op);
        if (g == null) {
            return false;
        }
        g.removePermission(permission, op.isOnline());
        return true;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getGroup(group);
        if (g == null) {
            return false;
        }
        Map<String, Boolean> effective = g.aggregatePermissions();
        return effective.containsKey(permission) ? effective.get(permission) : false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getGroup(group);
        if (g == null) {
            return false;
        }
        g.addPermission(permission, true, true, true);
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        Group g = set.getGroup(group);
        if (g == null) {
            return false;
        }
        g.removePermission(permission, true);
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        User u = set.getUser(op);
        Group g = set.getGroup(group);
        if (u == null || g == null) {
            return false;
        }
        return u.getChildren().contains(g);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        User u = set.getUser(op);
        Group g = set.getGroup(group);
        if (u == null || g == null) {
            return false;
        }        
        u.addChild(g, op.isOnline());
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return false;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        User u = set.getUser(op);
        Group g = set.getGroup(group);
        if (u == null || g == null) {
            return false;
        } 
        u.removeChild(g, op.isOnline());
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return null;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        User u = set.getUser(op);
        if (u == null) {
            return null;
        }
        String[] s = new String[u.getChildren().size()];
        int i = 0;
        for (Group g : u.getChildren()) {
            s[i] = g.getName();
            i++;
        }
        return s;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op == null) {
            return null;
        }
        GroupSet set;
        World w = Bukkit.getWorld(world);
        if (w != null) {
            set = perms.getGroupManager().getDefaultGroupSet();
        } else {
            set = perms.getGroupManager().getWorldSet(w);
        }
        User u = set.getUser(op);
        if (u == null) {
            return null;
        }
        if (u.getChildren().isEmpty()) {
            return null;
        }
        return u.getChildren().toArray(new Group[] {})[0].getName();
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
}
