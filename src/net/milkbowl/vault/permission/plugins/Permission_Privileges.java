package net.milkbowl.vault.permission.plugins;

import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.Group;
import net.krinsoft.privileges.groups.GroupManager;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_Privileges extends Permission {

    private final String name = "";
    private final Vault plugin;
    private GroupManager perms;
    private Privileges privs;

    public Permission_Privileges(Vault plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
        // Load service in case it was loaded before
        if (perms == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Privileges");
            if (perms != null && perms.isEnabled()) {
                this.privs = (Privileges) perms;
                this.perms = privs.getGroupManager();
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
            if (permission.perms == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("SimplyPerms");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.privs = (Privileges) perms;
                        permission.perms = permission.privs.getGroupManager();
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.perms != null) {
                if (event.getPlugin().getDescription().getName().equals("SimplyPerms")) {
                    permission.perms = null;
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
        return p != null ? p.hasPermission(permission) : false;
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
        ConfigurationSection priv = privs.getGroupNode(group);
        if (priv == null) {
            return false;
        } else {
            return priv.getBoolean(permission);
        }
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        throw new UnsupportedOperationException("Privileges does not support modifying permissions.");
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        throw new UnsupportedOperationException("Privileges does not support modifying permissions.");
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            throw new UnsupportedOperationException("Privileges does not support offline players.");
        }
        Group g = perms.getGroup(p);
        return g != null && g.getName().equalsIgnoreCase(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world != null) {
            return false;
        }
        perms.setGroup(player, group);
        return true;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        Group g = perms.getDefaultGroup();
        if (g == null) {
            return false;
        }
        return playerAddGroup(world, player, g.getName());
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            throw new UnsupportedOperationException("Privileges does not support offline players.");
        }
        Group g = perms.getGroup(p);
        return g != null ? new String[] { g.getName() } : null;
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        Player p = Bukkit.getPlayer(player);
        if (p == null) {
            throw new UnsupportedOperationException("Privileges does not support offline players.");
        }
        Group g = perms.getGroup(p);
        return g != null ? g.getName() : null;
    }

    @Override
    public String[] getGroups() {
        FileConfiguration file = privs.getGroups();
        ConfigurationSection groups = file.getConfigurationSection("groups");
        return groups != null ? groups.getKeys(false).toArray(new String[0]) : null;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }
}
