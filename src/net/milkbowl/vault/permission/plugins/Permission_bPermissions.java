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

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.HasPermission;
import de.bananaco.permissions.worlds.WorldPermissionsManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Permission_bPermissions extends Permission {
	
	private final String name = "bPermissions";
	private WorldPermissionsManager perms;
	
	public Permission_bPermissions(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.perms == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				this.perms = Permissions.getWorldPermissionsManager();
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Permission_bPermissions.this.perms == null) {
				final Plugin p = event.getPlugin();
				if (p.getDescription().getName().equals("bPermissions") && p.isEnabled()) {
					Permission_bPermissions.this.perms = Permissions.getWorldPermissionsManager();
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_bPermissions.this.plugin.getDescription().getName(), Permission_bPermissions.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Permission_bPermissions.this.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
					Permission_bPermissions.this.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_bPermissions.this.plugin.getDescription().getName(), Permission_bPermissions.this.name));
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
		return perms != null;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		return HasPermission.has(player, world, permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		set.addPlayerNode(permission, player);
		return true;
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		set.removePlayerNode(permission, player);
		return true;
	}
	
	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		if (set.getGroupNodes(group) == null) {
			return false;
		}
		
		return set.getGroupNodes(group).contains(permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		if (set.getGroupNodes(group) == null) {
			return false;
		}
		
		set.addNode(permission, group);
		return true;
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		if (set.getGroupNodes(group) == null) {
			return false;
		}
		
		set.removeNode(permission, group);
		return true;
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		if (set.getGroups(player) == null) {
			return false;
		}
		
		return set.getGroups(player).contains(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		if (set.getGroupNodes(group) == null) {
			return false;
		}
		
		set.addGroup(player, group);
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		if (world == null) {
			return false;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return false;
		}
		
		set.removeGroup(player, group);
		return true;
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		if (world == null) {
			return null;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return null;
		}
		
		final List<String> groups = set.getGroups(player);
		return groups == null ? null : groups.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		if (world == null) {
			return null;
		}
		
		final PermissionSet set = this.perms.getPermissionSet(world);
		if (set == null) {
			return null;
		}
		
		final List<String> groups = set.getGroups(player);
		if (groups == null || groups.isEmpty()) {
			return null;
		} else {
			return groups.get(0);
		}
	}
	
	@Override
	public String[] getGroups() {
		throw new UnsupportedOperationException("bPermissions does not support server-wide groups");
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
