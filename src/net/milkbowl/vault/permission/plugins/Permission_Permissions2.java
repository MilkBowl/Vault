package net.milkbowl.vault.permission.plugins;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import net.milkbowl.vault.permission.Permission;

@SuppressWarnings("deprecation")
public class Permission_Permissions2 extends Permission {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "Permissions 2 (Phoenix)";
    private PermissionHandler perms;
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private Permissions permission = null;
    private PermissionServerListener permissionServerListener = null;

    public Permission_Permissions2(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (permission == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms != null) {
                if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("2")) {
                    permission = (Permissions) perms;
                    this.perms = permission.getHandler();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        Permission_Permissions2 permission = null;

        public PermissionServerListener(Permission_Permissions2 permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.permission == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.permission = (Permissions) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.permission != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions")) {
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
    public boolean isEnabled() {
        if (permission == null) {
            return false;
        } else {
            return permission.isEnabled();
        }
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        return this.perms.has(worldName, playerName, permission);
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
    public boolean groupHas(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return this.perms.inGroup(worldName, playerName, groupName);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        int i = this.perms.getPermissionInteger(world, playerName, node);
        return (i == -1) ? defaultValue : i;
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    public void setGroupInfo(String world, String groupName, String node, Object value) {
        this.perms.addGroupInfo(world, groupName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        int i = this.perms.getGroupPermissionInteger(world, groupName, node);
        return (i == -1) ? defaultValue : i;
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        double d = this.perms.getPermissionDouble(world, playerName, node);
        return (d == -1) ? defaultValue : d;
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        double d = this.perms.getGroupPermissionDouble(world, groupName, node);
        return (d == -1) ? defaultValue : d;
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        // Warning does not support default value
        return this.perms.getPermissionBoolean(world, playerName, node);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        return this.perms.getGroupPermissionBoolean(world, groupName, node);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        String s = this.perms.getPermissionString(world, playerName, node);
        return (s == "" || s == null) ? defaultValue : s;
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        String s = this.perms.getGroupPermissionString(world, groupName, node);
        return (s == "" || s == null) ? defaultValue : s;
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        return this.perms.getGroups(world, playerName);
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        return this.perms.getGroup(world, playerName);
    }

    @Override
    public String getPlayerPrefix(String world, String playerName) {
        return this.perms.getPermissionString(world, playerName, "prefix");
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        return this.perms.getPermissionString(world, playerName, "suffix");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return perms.getGroupPrefix(world, group);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return perms.getGroupSuffix(world, group);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean playerAddTransient(String world, String player, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

}
