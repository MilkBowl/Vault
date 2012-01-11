package net.milkbowl.vault.permission.plugins;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class Permission_bPermissions2 extends Permission {

    private String name = "bPermissions2";
    private WorldManager perms;
    private PermissionServerListener permissionServerListener = null;

    public Permission_bPermissions2(Vault plugin) {
        this.plugin = plugin;

        permissionServerListener = new PermissionServerListener();

        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (perms == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
            if (p != null) {
                perms = WorldManager.getInstance();
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        public void onPluginEnable(PluginEnableEvent event) {
            if (perms == null) {
                Plugin p = event.getPlugin();
                if(p.getDescription().getName().equals("bPermissions") && p.isEnabled()) {
                    perms = WorldManager.getInstance();
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
        return this.perms != null;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        return user == null ? false : user.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        if (user == null) {
            return false;
        }
        user.addPermission(permission, true);
        return true;
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        if (user == null) {
            return false;
        }
        user.removePermission(permission);
        return true;
    }

    // use superclass implementation of playerAddTransient() and playerRemoveTransient()

    @Override
    public boolean groupHas(String world, String group, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        Group g = w.getGroup(group);
        return g != null ? g.getPermissionsAsString().contains(permission) : false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        Group g = w.getGroup(group);
        if (g == null) {
            return false;
        }
        g.addPermission(permission, true);
        return true;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        Group g = w.getGroup(group);
        if (g == null) {
            return false;
        }
        g.removePermission(permission);
        return true;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        return user != null ? user.getGroupsAsString().contains(group) : false;
        
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        if (user == null) {
            return false;
        }
        
        user.addGroup(group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        World w = perms.getWorld(world);
        if (w == null) {
            return false;
        }
        User user = w.getUser(player);
        if (user == null) {
            return false;
        }

        user.removeGroup(group);
        return true;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        World w = perms.getWorld(world);
        if (w == null) {
            return null;
        }
        User user = w.getUser(player);
        return user != null ? user.getGroupsAsString().toArray(new String[0]) : null;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        String[] groups = getPlayerGroups(world, player);
        return groups != null ? groups[0] : null;
    }

    @Override
    public String[] getGroups() {
        throw new UnsupportedOperationException("bPermissions does not support server-wide groups");
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }
}
