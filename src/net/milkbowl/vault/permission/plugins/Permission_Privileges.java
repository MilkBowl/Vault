package net.milkbowl.vault.permission.plugins;

import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.Group;
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

import java.util.ArrayList;
import java.util.List;

public class Permission_Privileges extends Permission {

    private final String name = "Privileges";
    private Privileges privs;

    public Permission_Privileges(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
        // Load service in case it was loaded before
        if (privs == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Privileges");
            if (perms != null && perms.isEnabled()) {
                this.privs = (Privileges) perms;
                log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Permission_Privileges permission = null;

        public PermissionServerListener(Permission_Privileges permission) {
            this.permission = permission;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.privs == null) {
                Plugin perms = event.getPlugin();
                if (perms.getDescription().getName().equals("Privileges")) {
                    permission.privs = (Privileges) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.privs != null) {
                if (event.getPlugin().getDescription().getName().equals("Privileges")) {
                    permission.privs = null;
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
        return true;
    }

    @Override
    public boolean playerHas(String world, String player, String permission) {
        Player p = plugin.getServer().getPlayer(player);
        return p != null && p.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return false;
    }

    // use superclass implementation of playerAddTransient() and playerRemoveTransient()

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        Group g = privs.getGroupManager().getGroup(group);
        return g != null && g.hasPermission(permission, world);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        Group g = privs.getGroupManager().getGroup(group);
        return g != null && g.addPermission(world, permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        Group g = privs.getGroupManager().getGroup(group);
        return g != null && g.removePermission(world, permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        Group g = privs.getGroupManager().getGroup(p);
        return g != null && g.isMemberOf(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        Group g = privs.getGroupManager().setGroup(player, group);
        return g != null;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        Group g = privs.getGroupManager().getDefaultGroup();
        return g != null && playerAddGroup(world, player, g.getName());
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        if (p == null) {
            throw new UnsupportedOperationException("Privileges does not support offline players.");
        }
        Group g = privs.getGroupManager().getGroup(p);
        return g != null ? g.getGroupTree().toArray(new String[g.getGroupTree().size()]) : null;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(player);
        Group g = privs.getGroupManager().getGroup(p);
        return g != null ? g.getName() : null;
    }

    @Override
    public String[] getGroups() {
        List<String> groups = new ArrayList<String>();
        for (Group g : privs.getGroupManager().getGroups()) {
            groups.add(g.getName());
        }
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}
