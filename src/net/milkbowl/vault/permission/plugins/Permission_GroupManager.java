package net.milkbowl.vault.permission.plugins;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
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

    private String name = "GroupManager";
    private PluginManager pluginManager = null;
    private GroupManager groupManager;
    private AnjoPermissionsHandler perms;
    private PermissionServerListener permissionServerListener = null;

    @SuppressWarnings("deprecation")
    public Permission_GroupManager(Vault plugin) {
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
        OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(worldName);
        if (owh == null)
        	return false;
        
        User user = owh.getUser(playerName);
        if (user == null)
        	return false;
        
        user.addPermission(permission);
        return true;
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(worldName);
        if (owh == null)
        	return false;
        
        User user = owh.getUser(playerName);
        if (user == null)
        	return false;
        
        user.removePermission(permission);
        return true;
    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
    	OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(worldName);
    	if (owh == null)
    		return false;
        
    	Group group = owh.getGroup(groupName);
    	if (group == null)
    		return false;
    	
    	return group.hasSamePermissionNode(permission);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
    	OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(worldName);
    	if (owh == null)
    		return false;
        
    	Group group = owh.getGroup(groupName);
    	if (group == null)
    		return false;
    	
    	group.addPermission(permission);
    	return true;
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
    	OverloadedWorldHolder owh = groupManager.getWorldsHolder().getWorldData(worldName);
    	if (owh == null)
    		return false;
        
    	Group group = owh.getGroup(groupName);
    	if (group == null)
    		return false;
    	
    	group.removePermission(permission);
    	return true;
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        return perms.inGroup(playerName, worldName);
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
    public String[] getPlayerGroups(String world, String playerName) {
        return perms.getGroups(playerName);
    }

    @Override
    public String getPrimaryGroup(String world, String playerName) {
        return perms.getGroup(playerName);
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

	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
}
