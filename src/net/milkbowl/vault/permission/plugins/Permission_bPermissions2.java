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

import de.bananaco.bpermissions.api.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
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

public class Permission_bPermissions2 extends Permission {
	
	private final String name = "bPermissions2";
	private boolean hooked;
	
	public Permission_bPermissions2(final Plugin plugin) {
		this.plugin = plugin;
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (!this.hooked) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				this.hooked = true;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (!Permission_bPermissions2.this.hooked) {
				final Plugin p = event.getPlugin();
				if (p.getDescription().getName().equals("bPermissions")) {
					Permission_bPermissions2.this.hooked = true;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_bPermissions2.this.plugin.getDescription().getName(), Permission_bPermissions2.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Permission_bPermissions2.this.hooked) {
				if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
					Permission_bPermissions2.this.hooked = false;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_bPermissions2.this.plugin.getDescription().getName(), Permission_bPermissions2.this.name));
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
		return this.hooked;
	}
	
	@Override
	public boolean has(final Player player, final String permission) {
		return this.playerHas(player.getWorld().getName(), player.getName(), permission);
	}
	
	@Override
	public boolean has(final String world, final String player, final String permission) {
		return this.playerHas(world, player, permission);
	}
	
	@Override
	public boolean has(final CommandSender sender, final String permission) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
			return this.has(player, permission);
		}
		return sender.hasPermission(permission);
	}
	
	@Override
	public boolean has(final org.bukkit.World world, final String player, final String permission) {
		return this.playerHas(world.getName(), player, permission);
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		return ApiLayer.hasPermission(world, CalculableType.USER, player, permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		ApiLayer.addPermission(world, CalculableType.USER, player, de.bananaco.bpermissions.api.Permission.loadFromString(permission));
		return true;
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		ApiLayer.removePermission(world, CalculableType.USER, player, permission);
		return true;
	}
	
	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		return ApiLayer.hasPermission(world, CalculableType.GROUP, group, permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		ApiLayer.addPermission(world, CalculableType.GROUP, group, de.bananaco.bpermissions.api.Permission.loadFromString(permission));
		return true;
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		ApiLayer.removePermission(world, CalculableType.GROUP, group, permission);
		return true;
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		return ApiLayer.hasGroup(world, CalculableType.USER, player, group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		ApiLayer.addGroup(world, CalculableType.USER, player, group);
		return true;
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		ApiLayer.removeGroup(world, CalculableType.USER, player, group);
		return true;
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		return ApiLayer.getGroups(world, CalculableType.USER, player);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		final String[] groups = this.getPlayerGroups(world, player);
		return groups != null && groups.length > 0 ? groups[0] : null;
	}
	
	@Override
	public String[] getGroups() {
		String[] groups;
		final Set<String> gSet = new HashSet<>();
		for (final World world : WorldManager.getInstance().getAllWorlds()) {
			final Set<Calculable> gr = world.getAll(CalculableType.GROUP);
			for (final Calculable c : gr) {
				gSet.add(c.getNameLowerCase());
			}
		}
		// Convert to String
		groups = gSet.toArray(new String[0]);
		return groups;
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
