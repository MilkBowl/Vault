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

import net.crystalyx.bukkit.simplyperms.SimplyAPI;
import net.crystalyx.bukkit.simplyperms.SimplyPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Permission_SimplyPerms extends Permission {
	
	private final String name = "SimplyPerms";
	private SimplyAPI perms;
	
	public Permission_SimplyPerms(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		// Load service in case it was loaded before
		if (this.perms == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("SimplyPerms");
			if (perms != null && perms.isEnabled()) {
				this.perms = ((SimplyPlugin) perms).getAPI();
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_SimplyPerms permission;
		
		public PermissionServerListener(final Permission_SimplyPerms permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.perms == null) {
				final Plugin perms = event.getPlugin();
				if (perms.getDescription().getName().equals("SimplyPerms")) {
					this.permission.perms = ((SimplyPlugin) perms).getAPI();
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_SimplyPerms.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("SimplyPerms")) {
					this.permission.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_SimplyPerms.this.plugin.getDescription().getName(), this.permission.name));
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
		return this.perms != null;
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, String permission) {
		permission = permission.toLowerCase();
		final Map<String, Boolean> playerPermissions = perms.getPlayerPermissions(player, world);
		return playerPermissions.containsKey(permission) && playerPermissions.get(permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, String permission) {
		permission = permission.toLowerCase();
		if (world != null) {
			perms.addPlayerPermission(player, world, permission, true);
		} else {
			perms.addPlayerPermission(player, permission, true);
		}
		return true;
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, String permission) {
		permission = permission.toLowerCase();
		if (world != null) {
			perms.removePlayerPermission(player, world, permission);
		} else {
			perms.removePlayerPermission(player, permission);
		}
		return true;
	}
	
	@Override
	public boolean groupHas(final String world, final String group, String permission) {
		permission = permission.toLowerCase();
		final Map<String, Boolean> groupPermissions = perms.getGroupPermissions(group, world);
		return groupPermissions.containsKey(permission) && groupPermissions.get(permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, String permission) {
		permission = permission.toLowerCase();
		if (world != null) {
			perms.addGroupPermission(group, world, permission, true);
		} else {
			perms.addGroupPermission(group, permission, true);
		}
		return true;
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, String permission) {
		permission = permission.toLowerCase();
		if (world != null) {
			permission = world + ":" + permission;
			perms.removeGroupPermission(group, world, permission);
		} else {
			perms.removeGroupPermission(group, permission);
		}
		return true;
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		if (world != null) {
			for (final String g : this.perms.getPlayerGroups(player)) {
				if (g.equals(group)) {
					return this.perms.getGroupWorlds(group).contains(world);
				}
			}
			return false;
		}
		
		if (!this.perms.getAllGroups().contains(group)) {
			return false;
		}
		return this.perms.getPlayerGroups(player).contains(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, String group) {
		group = group.toLowerCase();
		perms.addPlayerGroup(player, group);
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, String group) {
		group = group.toLowerCase();
		perms.removePlayerGroup(player, group);
		return true;
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final List<String> groupList = new ArrayList<>();
		if (world != null && this.perms.isPlayerInDB(player)) {
			for (final String group : this.perms.getPlayerGroups(player)) {
				if (this.perms.getGroupWorlds(group).contains(world)) {
					groupList.add(group);
				}
			}
			return groupList.toArray(new String[0]);
		}
		if (this.perms.isPlayerInDB(player)) {
			groupList.addAll(this.perms.getPlayerGroups(player));
		}
		return groupList.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		if (!this.perms.isPlayerInDB(player)) {
			return null;
		} else if (this.perms.getPlayerGroups(player) != null && !this.perms.getPlayerGroups(player).isEmpty()) {
			return this.perms.getPlayerGroups(player).get(0);
		}
		return null;
	}
	
	@Override
	public String[] getGroups() {
		return this.perms.getAllGroups().toArray(new String[0]);
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
	
}