package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsPlugin;

import net.milkbowl.vault.permission.Permission;

public class Permission_zPermissions extends Permission {

    private static final String name = "zPermissions";
    private ZPermissionsPlugin perms;
    private final Plugin plugin;
    private PermissionServerListener permissionServerListener = null;
    private ConsoleCommandSender ccs;

    public Permission_zPermissions(Plugin plugin) {
        this.plugin = plugin;
        ccs = Bukkit.getServer().getConsoleSender();
        permissionServerListener = new PermissionServerListener();

        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.plugin.getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (perms == null) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("zPermissions");
            if (p != null) {
                perms = (ZPermissionsPlugin) p;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        public void onPluginEnable(PluginEnableEvent event) {
            if (perms == null) {
                Plugin p = event.getPlugin();
                if(p.getDescription().getName().equals("zPermissions") && p.isEnabled()) {
                    perms = (ZPermissionsPlugin) p;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

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
