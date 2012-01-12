package net.milkbowl.vault.permission.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class Permission_PermissionsBukkit extends Permission {

	private final String name = "PermissionsBukkit";
	private PluginManager pluginManager = null;
	private PermissionsPlugin perms = null;
	private PermissionServerListener permissionServerListener = null;

	public Permission_PermissionsBukkit(Vault plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();

		permissionServerListener = new PermissionServerListener(this);

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);
		
		// Load Plugin in case it was loaded before
		if (perms == null) {
			Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (perms != null) {
				perms = (PermissionsPlugin) perms;
				log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
			}
		}
	}

	private class PermissionServerListener extends ServerListener {
		Permission_PermissionsBukkit permission = null;

		public PermissionServerListener(Permission_PermissionsBukkit permission) {
			this.permission = permission;
		}

		public void onPluginEnable(PluginEnableEvent event) {
			if (permission.perms == null) {
				Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");

				if (perms != null) {
					if (perms.isEnabled()) {
						permission.perms = (PermissionsPlugin) perms;
						log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
					}
				}
			}
		}

		public void onPluginDisable(PluginDisableEvent event) {
			if (permission.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit")) {
					permission.perms = null;
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
		if (perms == null) {
			return false;
		} else {
			return perms.isEnabled();
		}
	}

	@Override
	public boolean playerHas(String world, String player, String permission) {
		if (plugin.getServer().getPlayer(player) != null)
			return plugin.getServer().getPlayer(player).hasPermission(permission);
		else
			return false;
	}

	@Override
	public boolean playerAdd(String world, String player, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
	}

	@Override
	public boolean playerRemove(String world, String player, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
	}

	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean groupHas(String world, String group, String permission) {
		if (world != null && !world.isEmpty()) {
			return perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission) == null ? false : perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission);
		}
		if (perms.getGroup(group) == null)
			return false;
		else if (perms.getGroup(group).getInfo() == null)
			return false;
		else if (perms.getGroup(group).getInfo().getPermissions() == null)
			return false;
		return perms.getGroup(group).getInfo().getPermissions().get(permission);
	}

	@Override
	public boolean groupAdd(String world, String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
	}

	@Override
	public boolean groupRemove(String world, String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
	}

	@Override
	public boolean playerInGroup(String world, String player, String group) {
		if (world != null) {
			for (Group g : perms.getPlayerInfo(player).getGroups()) {
				if (g.getName().equals(group)) {
					return g.getInfo().getWorlds().contains(world);
				}
			}
			return false;
		}
		return perms.getGroup(group).getPlayers().contains(player);
	}

	@Override
	public boolean playerAddGroup(String world, String player, String group) {
		if (world != null) {
			return false;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
	}

	@Override
	public boolean playerRemoveGroup(String world, String player, String group) {
		if (world != null) {
			return false;
		}
		return plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
	}

	@Override
	public String[] getPlayerGroups(String world, String player) {
		List<String> groupList = new ArrayList<String>();
		if (world != null && perms.getPlayerInfo(player) != null) {
			for (Group group : perms.getPlayerInfo(player).getGroups()) {
				if (group.getInfo().getWorlds().contains(world)) {
					groupList.add(group.getName());
				}
			}
			return groupList.toArray(new String[0]);
		}
		for (Group group : perms.getPlayerInfo(player).getGroups()) {
			groupList.add(group.getName());
		}
		return groupList.toArray(new String[0]);
	}

	@Override
	public String getPrimaryGroup(String world, String player) {
		if (perms.getPlayerInfo(player) == null)
			return null;
		else if (perms.getPlayerInfo(player).getGroups() != null ) {
			return perms.getPlayerInfo(player).getGroups().get(0).getName();
		}
		return null;
	}

	@Override
	public String[] getGroups() {
		List<String> groupNames = new ArrayList<String>();
		for (Group group : perms.getAllGroups()) {
			groupNames.add(group.getName());
		}

		return groupNames.toArray(new String[0]);
	}

	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
}
