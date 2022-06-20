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

import com.nijiko.permissions.Group;
import com.nijiko.permissions.ModularControl;
import com.nijikokun.bukkit.Permissions.Permissions;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class Permission_Permissions3 extends Permission {
	
	private String name = "Permissions3";
	private ModularControl perms;
	private Permissions permission;
	
	public Permission_Permissions3(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.permission == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (perms == null) {
				plugin.getServer().getPluginManager().getPlugin("vPerms");
				this.name = "vPerms";
			}
			if (perms != null) {
				if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
					this.permission = (Permissions) perms;
					this.perms = (ModularControl) this.permission.getHandler();
					Permission.log.severe("Your permission system is outdated and no longer fully supported! It is highly advised to update!");
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
	
	@Override
	public boolean playerInGroup(final String worldName, final String playerName, final String groupName) {
		return permission.getHandler().inGroup(worldName, playerName, groupName);
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Permission_Permissions3.this.permission == null) {
				final Plugin permi = event.getPlugin();
				if ((permi.getDescription().getName().equals("Permissions") || permi.getDescription().getName().equals("vPerms")) && permi.getDescription().getVersion().startsWith("3")) {
					if (permi.isEnabled()) {
						Permission_Permissions3.this.permission = (Permissions) permi;
						Permission_Permissions3.this.perms = (ModularControl) Permission_Permissions3.this.permission.getHandler();
						Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_Permissions3.this.plugin.getDescription().getName(), Permission_Permissions3.this.name));
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Permission_Permissions3.this.permission != null) {
				if (event.getPlugin().getDescription().getName().equals("Permissions") || event.getPlugin().getDescription().getName().equals("vPerms")) {
					Permission_Permissions3.this.permission = null;
					Permission_Permissions3.this.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_Permissions3.this.plugin.getDescription().getName(), Permission_Permissions3.this.name));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean has(final CommandSender sender, final String permission) {
		if (sender.isOp() || !(sender instanceof Player)) {
			return true;
		} else {
			return this.has(((Player) sender).getWorld().getName(), sender.getName(), permission);
		}
	}
	
	@Override
	public boolean has(final Player player, final String permission) {
		return this.has(player.getWorld().getName(), player.getName(), permission);
	}
	
	public boolean playerAddGroup(String worldName, final String playerName, final String groupName) {
		if (worldName == null) {
			worldName = "*";
		}
		
		final Group g = this.perms.getGroupObject(worldName, groupName);
		if (g == null) {
			return false;
		}
		try {
			this.perms.safeGetUser(worldName, playerName).addParent(g);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(String worldName, final String playerName, final String groupName) {
		if (worldName == null) {
			worldName = "*";
		}
		
		final Group g = this.perms.getGroupObject(worldName, groupName);
		if (g == null) {
			return false;
		}
		try {
			this.perms.safeGetUser(worldName, playerName).removeParent(g);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean playerAdd(final String worldName, final String playerName, final String permission) {
		perms.addUserPermission(worldName, playerName, permission);
		return true;
	}
	
	@Override
	public boolean playerRemove(final String worldName, final String playerName, final String permission) {
		perms.removeUserPermission(worldName, playerName, permission);
		return true;
	}
	
	@Override
	public boolean groupAdd(String worldName, final String groupName, final String permission) {
		if (worldName == null) {
			worldName = "*";
		}
		
		this.perms.addGroupPermission(worldName, groupName, permission);
		return true;
	}
	
	@Override
	public boolean groupRemove(String worldName, final String groupName, final String permission) {
		if (worldName == null) {
			worldName = "*";
		}
		this.perms.removeGroupPermission(worldName, groupName, permission);
		return true;
	}
	
	@Override
	public boolean groupHas(String worldName, final String groupName, final String permission) {
		if (worldName == null) {
			worldName = "*";
		}
		try {
			return this.perms.safeGetGroup(worldName, groupName).hasPermission(permission);
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String playerName) {
		return perms.getGroups(world, playerName);
	}
	
	public String getPrimaryGroup(final String world, final String playerName) {
		return this.getPlayerGroups(world, playerName)[0];
	}
	
	@Override
	public boolean playerHas(final String worldName, final String playerName, final String permission) {
		final Player p = this.plugin.getServer().getPlayer(playerName);
		if (p != null) {
			if (p.hasPermission(permission))
				return true;
		}
		return perms.has(worldName, playerName, permission);
	}
	
	@Override
	public boolean playerAddTransient(final Player player, final String permission) {
		return this.playerAddTransient(null, player.getName(), permission);
	}
	
	@Override
	public boolean playerAddTransient(final String worldName, final Player player, final String permission) {
		return this.playerAddTransient(worldName, player.getName(), permission);
	}
	
	private boolean playerAddTransient(String worldName, final String player, final String permission) {
		if (worldName == null) {
			worldName = "*";
		}
		try {
			this.perms.safeGetUser(worldName, player).addTransientPermission(permission);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
	
	
	@Override
	public boolean playerRemoveTransient(final Player player, final String permission) {
		return this.pRemoveTransient(null, player.getName(), permission);
	}
	
	@Override
	public boolean playerRemoveTransient(final String worldName, final Player player, final String permission) {
		return this.pRemoveTransient(worldName, player.getName(), permission);
	}
	
	@Override
	public String[] getGroups() {
		
		final Set<String> groupNames = new HashSet<>();
		for (final World world : Bukkit.getServer().getWorlds()) {
			for (final Group group : this.perms.getGroups(world.getName())) {
				groupNames.add(group.getName());
			}
		}
		return groupNames.toArray(new String[0]);
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return false;
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
	
	private boolean pRemoveTransient(String worldName, final String player, final String permission) {
		if (worldName == null) {
			worldName = "*";
		}
		
		try {
			this.perms.safeGetUser(worldName, player).removeTransientPermission(permission);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}
	
}
