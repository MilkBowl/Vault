package net.milkbowl.vault.permission.plugins;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
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

public class Permission_GroupManager extends Permission {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "GroupManager";
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private GroupManager groupManager;
    private AnjoPermissionsHandler perms;
    private PermissionServerListener permissionServerListener = null;

    @SuppressWarnings("deprecation")
    public Permission_GroupManager(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (groupManager == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
            if (perms != null) {
                if (perms.isEnabled()) {
                    groupManager = (GroupManager) perms;
                    this.perms = groupManager.getPermissionHandler();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        Permission_GroupManager permission = null;

        public PermissionServerListener(Permission_GroupManager permission) {
            this.permission = permission;
        }

        @SuppressWarnings("deprecation")
        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.groupManager == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.groupManager = (GroupManager) perms;
                        permission.perms = groupManager.getPermissionHandler();
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.groupManager != null) {
                if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
                    permission.groupManager = null;
                    permission.perms = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
                }
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isEnabled() {
        if (groupManager == null) {
            return false;
        } else {
            return groupManager.isEnabled();
        }
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
		Player p = plugin.getServer().getPlayer(playerName);
		if (p != null) {
			if (p.hasPermission(permission))
				return true;
		}
        return perms.has(plugin.getServer().getPlayer(playerName), permission);
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        return false;
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
        String[] groups = perms.getGroups(playerName);
        for (String group : groups)
            if (group.equalsIgnoreCase(groupName))
                return true;

        return false;
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        return false;
    }

    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        return perms.getPermissionInteger(playerName, node);
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        return perms.getGroupPermissionInteger(groupName, node);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        return perms.getPermissionDouble(playerName, node);
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        return perms.getGroupPermissionDouble(groupName, node);
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        return perms.getPermissionBoolean(playerName, node);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        return perms.getGroupPermissionBoolean(groupName, node);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        return perms.getPermissionString(playerName, node);
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        return perms.getGroupPermissionString(groupName, node);
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
    	throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String[] getPlayerGroups(String world, String playerName) {
        return perms.getGroups(playerName);
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        return perms.getGroup(playerName);
    }

    @Override
    public String getPlayerPrefix(String world, String playerName) {
        return perms.getGroupPrefix(getPrimaryGroup(world, playerName));
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        return perms.getGroupSuffix(getPrimaryGroup(world, playerName));
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
        return perms.getGroupPrefix(group);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return perms.getGroupSuffix(group);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
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
			Collection<Group> groups = groupManager.getWorldsHolder().getWorldData(world.getName()).getGroupList();
			for (Group group : groups) {
				groupNames.add(group.getName());
			}
		}
		return groupNames.toArray(new String[0]);
	}
}
