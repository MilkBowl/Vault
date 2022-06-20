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

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionInfo;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
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

public class Permission_PermissionsBukkit extends Permission {
	
	private final String name = "PermissionsBukkit";
	private PermissionsPlugin perms;
	
	public Permission_PermissionsBukkit(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.perms == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsBukkit");
			if (perms != null) {
				this.perms = (PermissionsPlugin) perms;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_PermissionsBukkit permission;
		
		public PermissionServerListener(final Permission_PermissionsBukkit permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.perms == null) {
				final Plugin perms = event.getPlugin();
				if (perms.getDescription().getName().equals("PermissionsBukkit")) {
					this.permission.perms = (PermissionsPlugin) perms;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_PermissionsBukkit.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit")) {
					this.permission.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_PermissionsBukkit.this.plugin.getDescription().getName(), this.permission.name));
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
		if (this.perms == null) {
			return false;
		} else {
			return this.perms.isEnabled();
		}
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		if (Bukkit.getPlayer(player) != null) {
			return Bukkit.getPlayer(player).hasPermission(permission);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player setperm " + player + " " + permission + " true");
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player unsetperm " + player + " " + permission);
	}
	
	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		if (world != null && !world.isEmpty()) {
			return this.perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission) != null && this.perms.getGroup(group).getInfo().getWorldPermissions(world).get(permission);
		}
		if (this.perms.getGroup(group) == null) {
			return false;
		} else if (this.perms.getGroup(group).getInfo() == null) {
			return false;
		} else if (this.perms.getGroup(group).getInfo().getPermissions() == null) {
			return false;
		}
		return this.perms.getGroup(group).getInfo().getPermissions().get(permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group setperm " + group + " " + permission + " true");
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, String permission) {
		if (world != null) {
			permission = world + ":" + permission;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions group unsetperm " + group + " " + permission);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		if (world != null) {
			for (final Group g : this.perms.getPlayerInfo(player).getGroups()) {
				if (g.getName().equals(group)) {
					return g.getInfo().getWorlds().contains(world);
				}
			}
			return false;
		}
		final Group g = this.perms.getGroup(group);
		if (g == null) {
			return false;
		}
		return g.getPlayers().contains(player);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		if (world != null) {
			return false;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		if (world != null) {
			return false;
		}
		return this.plugin.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final List<String> groupList = new ArrayList<>();
		final PermissionInfo info = this.perms.getPlayerInfo(player);
		if (world != null && info != null) {
			for (final Group group : this.perms.getPlayerInfo(player).getGroups()) {
				if (group.getInfo().getWorlds().contains(world)) {
					groupList.add(group.getName());
				}
			}
			return groupList.toArray(new String[0]);
		}
		if (info != null) {
			for (final Group group : info.getGroups()) {
				groupList.add(group.getName());
			}
		}
		return groupList.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		if (this.perms.getPlayerInfo(player) == null) {
			return null;
		} else if (this.perms.getPlayerInfo(player).getGroups() != null && !this.perms.getPlayerInfo(player).getGroups().isEmpty()) {
			return this.perms.getPlayerInfo(player).getGroups().get(0).getName();
		}
		return null;
	}
	
	@Override
	public String[] getGroups() {
		final List<String> groupNames = new ArrayList<>();
		for (final Group group : this.perms.getAllGroups()) {
			groupNames.add(group.getName());
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
