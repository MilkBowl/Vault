/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.permission.plugins;

import net.milkbowl.vault.permission.Permission;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Permission_GroupManager extends Permission {
	
	private final String name = "GroupManager";
	private GroupManager groupManager;
	
	public Permission_GroupManager(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.groupManager == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if (perms != null && perms.isEnabled()) {
				this.groupManager = (GroupManager) perms;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_GroupManager permission;
		
		public PermissionServerListener(final Permission_GroupManager permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.groupManager == null) {
				final Plugin p = event.getPlugin();
				if (p.getDescription().getName().equals("GroupManager")) {
					this.permission.groupManager = (GroupManager) p;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_GroupManager.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.groupManager != null) {
				if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
					this.permission.groupManager = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_GroupManager.this.plugin.getDescription().getName(), this.permission.name));
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
		return this.groupManager != null && this.groupManager.isEnabled();
	}
	
	@Override
	public boolean playerHas(final String worldName, final String playerName, final String permission) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return false;
		}
		return handler.permission(playerName, permission);
	}
	
	@Override
	public boolean playerAdd(final String worldName, final String playerName, final String permission) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		
		final User user = owh.getUser(playerName);
		if (user == null) {
			return false;
		}
		
		user.addPermission(permission);
		final Player p = Bukkit.getPlayer(playerName);
		if (p != null) {
			GroupManager.BukkitPermissions.updatePermissions(p);
		}
		return true;
	}
	
	@Override
	public boolean playerRemove(final String worldName, final String playerName, final String permission) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		
		final User user = owh.getUser(playerName);
		if (user == null) {
			return false;
		}
		
		user.removePermission(permission);
		final Player p = Bukkit.getPlayer(playerName);
		if (p != null) {
			GroupManager.BukkitPermissions.updatePermissions(p);
		}
		return true;
	}
	
	@Override
	public boolean groupHas(final String worldName, final String groupName, final String permission) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getDefaultWorld();
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		
		final Group group = owh.getGroup(groupName);
		if (group == null) {
			return false;
		}
		
		return group.hasSamePermissionNode(permission);
	}
	
	@Override
	public boolean groupAdd(final String worldName, final String groupName, final String permission) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getDefaultWorld();
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		
		final Group group = owh.getGroup(groupName);
		if (group == null) {
			return false;
		}
		
		group.addPermission(permission);
		return true;
	}
	
	@Override
	public boolean groupRemove(final String worldName, final String groupName, final String permission) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getDefaultWorld();
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		
		final Group group = owh.getGroup(groupName);
		if (group == null) {
			return false;
		}
		
		group.removePermission(permission);
		return true;
	}
	
	@Override
	public boolean playerInGroup(final String worldName, final String playerName, final String groupName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return false;
		}
		return handler.inGroup(playerName, groupName);
	}
	
	@Override
	public boolean playerAddGroup(final String worldName, final String playerName, final String groupName) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		final User user = owh.getUser(playerName);
		if (user == null) {
			return false;
		}
		final Group group = owh.getGroup(groupName);
		if (group == null) {
			return false;
		}
		if (user.getGroup().equals(owh.getDefaultGroup())) {
			user.setGroup(group);
		} else if (group.getInherits().contains(user.getGroup().getName().toLowerCase())) {
			user.setGroup(group);
		} else {
			user.addSubGroup(group);
		}
		final Player p = Bukkit.getPlayer(playerName);
		if (p != null) {
			GroupManager.BukkitPermissions.updatePermissions(p);
		}
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(final String worldName, final String playerName, final String groupName) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return false;
		}
		final User user = owh.getUser(playerName);
		if (user == null) {
			return false;
		}
		boolean success = false;
		if (user.getGroup().getName().equalsIgnoreCase(groupName)) {
			user.setGroup(owh.getDefaultGroup());
			success = true;
		} else {
			final Group group = owh.getGroup(groupName);
			if (group != null) {
				success = user.removeSubGroup(group);
			}
		}
		if (success) {
			final Player p = Bukkit.getPlayer(playerName);
			if (p != null) {
				GroupManager.BukkitPermissions.updatePermissions(p);
			}
		}
		return success;
	}
	
	@Override
	public String[] getPlayerGroups(final String worldName, final String playerName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return null;
		}
		return handler.getGroups(playerName);
	}
	
	@Override
	public String getPrimaryGroup(final String worldName, final String playerName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return null;
		}
		return handler.getGroup(playerName);
	}
	
	@Override
	public String[] getGroups() {
		final Set<String> groupNames = new HashSet<>();
		for (final World world : Bukkit.getServer().getWorlds()) {
			final OverloadedWorldHolder owh = this.groupManager.getWorldsHolder().getWorldData(world.getName());
			if (owh == null) {
				continue;
			}
			final Collection<Group> groups = owh.getGroupList();
			if (groups == null) {
				continue;
			}
			for (final Group group : groups) {
				groupNames.add(group.getName());
			}
		}
		return groupNames.toArray(new String[0]);
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
