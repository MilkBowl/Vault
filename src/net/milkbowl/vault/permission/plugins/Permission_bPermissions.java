package net.milkbowl.vault.permission.plugins;

import java.util.List;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.HasPermission;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class Permission_bPermissions extends Permission {

	private String name = "bPermissions";
	private PluginManager pluginManager = null;
	private WorldPermissionsManager perms;
	private PermissionServerListener permissionServerListener = null;

	public Permission_bPermissions(Vault plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();

		permissionServerListener = new PermissionServerListener();

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (perms == null) {
			Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				perms = Permissions.getWorldPermissionsManager();
				log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	private class PermissionServerListener extends ServerListener {
		public void onPluginEnable(PluginEnableEvent event) {
			if (perms == null) {
				Plugin p = event.getPlugin();
				if(p.getDescription().getName().equals("bPermissions")) {
					if (p.isEnabled()) {
						perms = Permissions.getWorldPermissionsManager();
						log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
					}
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
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		return HasPermission.has(player, world, permission);
	}

	@Override
	public boolean playerAdd(String world, String player, String permission) {
		throw new UnsupportedOperationException("Player specific permissions are not supported!");
	}

	@Override
	public boolean playerRemove(String world, String player, String permission) {
		throw new UnsupportedOperationException("Player specific permissions are not supported!");
	}

	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean groupHas(String world, String group, String permission) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		if (set.getGroupNodes(group) == null)
			return false;
		
		return set.getGroupNodes(group).contains(permission);
	}

	@Override
	public boolean groupAdd(String world, String group, String permission) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		if (set.getGroupNodes(group) == null)
			return false;
		
		set.addNode(permission, group);
		return true;
	}

	@Override
	public boolean groupRemove(String world, String group, String permission) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		if (set.getGroupNodes(group) == null)
			return false;
		
		set.removeNode(permission, group);
		return true;
	}

	@Override
	public boolean playerInGroup(String world, String player, String group) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		if (set.getGroups(player) == null)
			return false;
		
		return set.getGroups(player).contains(group);
	}

	@Override
	public boolean playerAddGroup(String world, String player, String group) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		if (set.getGroupNodes(group) == null)
			return false;
		
		set.addGroup(player, group);
		return true;
	}

	@Override
	public boolean playerRemoveGroup(String world, String player, String group) {
		if (world == null)
			return false;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return false;
		
		set.removeGroup(player, group);
		return true;
	}

	@Override
	public String[] getPlayerGroups(String world, String player) {
		if (world == null)
			return null;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return null;
		
		List<String> groups = set.getGroups(player);
		return groups == null ? null : groups.toArray(new String[0]);
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		if (world == null)
			return null;
		
		PermissionSet set = perms.getPermissionSet(world);
		if (set == null)
			return null;
		
		List<String> groups = set.getGroups(player);
		if (groups == null || groups.isEmpty())
			return null;
		else
			return groups.get(0);
	}

	@Override
	public String[] getGroups() {
		throw new UnsupportedOperationException("bPermissions does not support server-wide groups");
	}
}
