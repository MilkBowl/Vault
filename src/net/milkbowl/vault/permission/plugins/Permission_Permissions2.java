package net.milkbowl.vault.permission.plugins;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.Group;
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
		Player p = plugin.getServer().getPlayer(playerName);
		if (p != null) {
			if (p.hasPermission(permission))
				return true;
		}
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
    public String[] getPlayerGroups(String world, String playerName) {
        return this.perms.getGroups(world, playerName);
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        return this.perms.getGroup(world, playerName);
    }

    @Override
    public boolean playerAddTransient(String world, String player, String permission) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
		}
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		
		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment().getPlugin().equals(plugin)) {
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}
		}
		
		PermissionAttachment attach = p.addAttachment(plugin);
		attach.setPermission(permission, true);
		
		return true;
    }

	@Override
	public boolean playerRemoveTransient(String world, String player, String permission) {
		if (world != null) {
			throw new UnsupportedOperationException(getName() + " does not support World based transient permissions!");
		}
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
		}
		for (PermissionAttachmentInfo paInfo : p.getEffectivePermissions()) {
			if (paInfo.getAttachment().getPlugin().equals(plugin)) {
				return paInfo.getAttachment().getPermissions().remove(permission);
			}
		}
		return false;
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
}
