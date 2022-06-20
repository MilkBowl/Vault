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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;

public class Permission_PermissionsEx extends Permission {
	
	private final String name = "PermissionsEx";
	private PermissionsEx permission;
	
	public Permission_PermissionsEx(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.permission == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
			if (perms != null) {
				if (perms.isEnabled()) {
					try {
						if (Double.parseDouble(perms.getDescription().getVersion()) < 1.16) {
							Permission.log.info(String.format("[%s][Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", plugin.getDescription().getName(), this.name));
						}
					} catch (final NumberFormatException e) {
						// Do nothing
					}
					this.permission = (PermissionsEx) perms;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
				}
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		if (this.permission == null) {
			return false;
		} else {
			return this.permission.isEnabled();
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_PermissionsEx permission;
		
		public PermissionServerListener(final Permission_PermissionsEx permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.permission == null) {
				final Plugin perms = event.getPlugin();
				if (perms.getDescription().getName().equals("PermissionsEx")) {
					try {
						if (Double.parseDouble(perms.getDescription().getVersion()) < 1.16) {
							Permission.log.info(String.format("[%s][Permission] %s below 1.16 is not compatible with Vault! Falling back to SuperPerms only mode. PLEASE UPDATE!", Permission_PermissionsEx.this.plugin.getDescription().getName(), Permission_PermissionsEx.this.name));
							return;
						}
					} catch (final NumberFormatException e) {
						// Do nothing
					}
					this.permission.permission = (PermissionsEx) perms;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_PermissionsEx.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.permission != null) {
				if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
					this.permission.permission = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_PermissionsEx.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean playerInGroup(final String worldName, final OfflinePlayer op, final String groupName) {
		final PermissionUser user = this.getUser(op);
		if (user == null) {
			return false;
		}
		return user.inGroup(groupName, worldName);
	}
	
	@Override
	public boolean playerInGroup(final String worldName, final String playerName, final String groupName) {
		return PermissionsEx.getPermissionManager().getUser(playerName).inGroup(groupName, worldName);
	}
	
	@Override
	public boolean playerAddGroup(final String worldName, final OfflinePlayer op, final String groupName) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		final PermissionUser user = this.getUser(op);
		if (group == null || user == null) {
			return false;
		} else {
			user.addGroup(groupName, worldName);
			return true;
		}
	}
	
	@Override
	public boolean playerAddGroup(final String worldName, final String playerName, final String groupName) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		final PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
		if (group == null || user == null) {
			return false;
		} else {
			user.addGroup(groupName, worldName);
			return true;
		}
	}
	
	@Override
	public boolean playerRemoveGroup(final String worldName, final OfflinePlayer op, final String groupName) {
		final PermissionUser user = this.getUser(op);
		user.removeGroup(groupName, worldName);
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(final String worldName, final String playerName, final String groupName) {
		PermissionsEx.getPermissionManager().getUser(playerName).removeGroup(groupName, worldName);
		return true;
	}
	
	@Override
	public boolean playerAdd(final String worldName, final OfflinePlayer op, final String permission) {
		final PermissionUser user = this.getUser(op);
		if (user == null) {
			return false;
		} else {
			user.addPermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean playerAdd(final String worldName, final String playerName, final String permission) {
		final PermissionUser user = this.getUser(playerName);
		if (user == null) {
			return false;
		} else {
			user.addPermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean playerRemove(final String worldName, final OfflinePlayer op, final String permission) {
		final PermissionUser user = this.getUser(op);
		if (user == null) {
			return false;
		} else {
			user.removePermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean playerRemove(final String worldName, final String playerName, final String permission) {
		final PermissionUser user = this.getUser(playerName);
		if (user == null) {
			return false;
		} else {
			user.removePermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean groupAdd(final String worldName, final String groupName, final String permission) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return false;
		} else {
			group.addPermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean groupRemove(final String worldName, final String groupName, final String permission) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return false;
		} else {
			group.removePermission(permission, worldName);
			return true;
		}
	}
	
	@Override
	public boolean groupHas(final String worldName, final String groupName, final String permission) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return false;
		} else {
			return group.has(permission, worldName);
		}
	}
	
	private PermissionUser getUser(final OfflinePlayer op) {
		return PermissionsEx.getPermissionManager().getUser(op.getUniqueId());
	}
	
	private PermissionUser getUser(final String playerName) {
		return PermissionsEx.getPermissionManager().getUser(playerName);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final OfflinePlayer op) {
		final PermissionUser user = this.getUser(op);
		return user == null ? null : user.getParentIdentifiers(world).toArray(new String[0]);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String playerName) {
		final PermissionUser user = this.getUser(playerName);
		return user == null ? null : user.getParentIdentifiers(world).toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final OfflinePlayer op) {
		final PermissionUser user = this.getUser(op);
		if (user == null) {
			return null;
		} else if (user.getParentIdentifiers(world).size() > 0) {
			return user.getParentIdentifiers(world).get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String playerName) {
		final PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
		if (user == null) {
			return null;
		} else if (user.getParentIdentifiers(world).size() > 0) {
			return user.getParentIdentifiers(world).get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean playerHas(final String worldName, final OfflinePlayer op, final String permission) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			return user.has(permission, worldName);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerHas(final String worldName, final String playerName, final String permission) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			return user.has(permission, worldName);
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean playerAddTransient(final String worldName, final Player player, final String permission) {
		final PermissionUser pPlayer = this.getUser(player);
		if (pPlayer != null) {
			pPlayer.addTimedPermission(permission, worldName, 0);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerAddTransient(final Player player, final String permission) {
		return this.playerAddTransient(null, player, permission);
	}
	
	
	@Override
	public boolean playerRemoveTransient(final Player player, final String permission) {
		return this.playerRemoveTransient(null, player, permission);
	}
	
	@Override
	public boolean playerRemoveTransient(final String worldName, final Player player, final String permission) {
		final PermissionUser pPlayer = this.getUser(player);
		if (pPlayer != null) {
			pPlayer.removeTimedPermission(permission, worldName);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String[] getGroups() {
		final List<PermissionGroup> groups = PermissionsEx.getPermissionManager().getGroupList();
		if (groups == null || groups.isEmpty()) {
			return null;
		}
		final String[] groupNames = new String[groups.size()];
		for (int i = 0; i < groups.size(); i++) {
			groupNames[i] = groups.get(i).getName();
		}
		return groupNames;
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
