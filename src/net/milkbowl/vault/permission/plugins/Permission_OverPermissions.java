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

import com.overmc.overpermissions.api.GroupManager;
import com.overmc.overpermissions.api.PermissionGroup;
import com.overmc.overpermissions.api.PermissionUser;
import com.overmc.overpermissions.api.UserManager;
import com.overmc.overpermissions.internal.OverPermissions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Permission_OverPermissions extends Permission {
	private OverPermissions overPerms;
	private UserManager userManager;
	private GroupManager groupManager;
	
	public Permission_OverPermissions(final Plugin plugin) {
		super.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		if (this.overPerms == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
			if ((perms != null) && (perms.isEnabled())) {
				this.overPerms = ((OverPermissions) perms);
				this.userManager = this.overPerms.getUserManager();
				this.groupManager = this.overPerms.getGroupManager();
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "OverPermissions"));
			}
		}
	}
	
	@Override
	public String getName() {
		return "OverPermissions";
	}
	
	@Override
	public boolean isEnabled() {
		return (this.overPerms != null) && (this.overPerms.isEnabled());
	}
	
	@Override
	public boolean playerHas(final String worldName, final String playerName, final String permission) {
		if (!this.userManager.doesUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).getPermission(permission, worldName);
	}
	
	@Override
	public boolean playerAdd(final String worldName, final String playerName, final String permission) {
		if (!this.userManager.canUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).addPermissionNode(permission, worldName);
	}
	
	@Override
	public boolean playerRemove(final String worldName, final String playerName, final String permission) {
		if (!this.userManager.canUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).removePermissionNode(permission, worldName);
	}
	
	@Override
	public boolean groupHas(final String worldName, final String groupName, final String permission) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		return this.groupManager.getGroup(groupName).getPermission(permission, worldName);
	}
	
	@Override
	public boolean groupAdd(final String worldName, final String groupName, final String permission) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		if (worldName == null) {
			return this.groupManager.getGroup(groupName).addGlobalPermissionNode(permission);
		} else {
			return this.groupManager.getGroup(groupName).addPermissionNode(permission, worldName);
		}
	}
	
	@Override
	public boolean groupRemove(final String worldName, final String groupName, final String permission) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		if (worldName == null) {
			return this.groupManager.getGroup(groupName).removeGlobalPermissionNode(permission);
		} else {
			return this.groupManager.getGroup(groupName).removePermissionNode(permission, worldName);
		}
	}
	
	@Override
	public boolean playerInGroup(final String worldName, final String playerName, final String groupName) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		if (!this.userManager.doesUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).getAllParents().contains(this.groupManager.getGroup(groupName));
	}
	
	@Override
	public boolean playerAddGroup(final String worldName, final String playerName, final String groupName) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		if (!this.userManager.canUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).addParent(this.groupManager.getGroup(groupName));
	}
	
	@Override
	public boolean playerRemoveGroup(final String worldName, final String playerName, final String groupName) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return false;
		}
		if (!this.userManager.canUserExist(playerName)) {
			return false;
		}
		return this.userManager.getPermissionUser(playerName).removeParent(this.groupManager.getGroup(groupName));
	}
	
	@Override
	public String[] getPlayerGroups(final String worldName, final String playerName) {
		final ArrayList<String> ret = new ArrayList<>();
		if (!this.userManager.doesUserExist(playerName)) {
			return new String[0];
		}
		final PermissionUser user = this.userManager.getPermissionUser(playerName);
		for (final PermissionGroup parent : user.getAllParents()) {
			ret.add(parent.getName());
		}
		return ret.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String worldName, final String playerName) {
		final String[] playerGroups = this.getPlayerGroups(worldName, playerName);
		if (playerGroups.length == 0) {
			return null;
		}
		return playerGroups[0];
	}
	
	@Override
	public String[] getGroups() {
		final ArrayList<String> groupNames = new ArrayList<>();
		for (final PermissionGroup s : this.groupManager.getGroups()) {
			groupNames.add(s.getName());
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
	
	public class PermissionServerListener
			implements Listener {
		final Permission_OverPermissions permission;
		
		public PermissionServerListener(final Permission_OverPermissions permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.overPerms == null) {
				final Plugin perms = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
				if (perms != null) {
					this.permission.overPerms = ((OverPermissions) perms);
					Permission.log.info(String
							.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "OverPermissions"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if ((this.permission.overPerms != null) &&
					(event.getPlugin().getDescription().getName().equals("OverPermissions"))) {
				this.permission.overPerms = null;
				Permission.log.info(String
						.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), "OverPermissions"));
			}
		}
	}
}
