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

import net.ae97.totalpermissions.PermissionManager;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionBase;
import net.ae97.totalpermissions.permission.PermissionUser;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Permission_TotalPermissions extends Permission {
	
	private final String name = "TotalPermissions";
	private PermissionManager manager;
	private TotalPermissions totalperms;
	
	public Permission_TotalPermissions(final Plugin pl) {
		plugin = pl;
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Permission_TotalPermissions.this.manager == null || Permission_TotalPermissions.this.totalperms == null) {
				final Plugin permPlugin = event.getPlugin();
				if (permPlugin.getDescription().getName().equals(Permission_TotalPermissions.this.name)) {
					Permission_TotalPermissions.this.totalperms = (TotalPermissions) permPlugin;
					Permission_TotalPermissions.this.manager = Permission_TotalPermissions.this.totalperms.getManager();
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_TotalPermissions.this.plugin.getDescription().getName(), Permission_TotalPermissions.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Permission_TotalPermissions.this.manager != null) {
				if (event.getPlugin().getDescription().getName().equals(Permission_TotalPermissions.this.name)) {
					Permission_TotalPermissions.this.totalperms = null;
					Permission_TotalPermissions.this.manager = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_TotalPermissions.this.plugin.getDescription().getName(), Permission_TotalPermissions.this.name));
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
		return this.plugin != null && this.plugin.isEnabled() && this.totalperms != null && this.totalperms.isEnabled();
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		final PermissionBase user = this.manager.getUser(player);
		return user.has(permission, world);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		try {
			final PermissionBase user = this.manager.getUser(player);
			user.addPerm(permission, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		try {
			final PermissionBase user = this.manager.getUser(player);
			user.remPerm(permission, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		final PermissionBase permGroup = this.manager.getGroup(group);
		return permGroup.has(permission, world);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		try {
			final PermissionBase permGroup = this.manager.getGroup(group);
			permGroup.addPerm(permission, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		try {
			final PermissionBase permGroup = this.manager.getGroup(group);
			permGroup.remPerm(permission, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		final PermissionUser user = this.manager.getUser(player);
		final List<String> groups = user.getGroups(world);
		return groups.contains(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		try {
			final PermissionUser user = this.manager.getUser(player);
			user.addGroup(group, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		try {
			final PermissionUser user = this.manager.getUser(player);
			user.remGroup(group, world);
			return true;
		} catch (final IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE,
					String.format("[%s] An error occured while saving perms", this.totalperms.getDescription().getName()), ex);
			return false;
		}
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final PermissionUser user = this.manager.getUser(player);
		List<String> groups = user.getGroups(world);
		if (groups == null) {
			groups = new ArrayList<>();
		}
		return groups.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		final String[] groups = this.getPlayerGroups(world, player);
		if (groups.length == 0) {
			return "";
		} else {
			return groups[0];
		}
	}
	
	@Override
	public String[] getGroups() {
		return this.manager.getGroups();
	}
}
