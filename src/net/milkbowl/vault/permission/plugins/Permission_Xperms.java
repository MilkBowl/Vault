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

import com.github.sebc722.xperms.core.Main;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_Xperms extends Permission {
	
	private final String name = "Xperms";
	private Main perms;
	
	public Permission_Xperms(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		if (this.perms == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("Xperms");
			if (this.perms != null) {
				if (perms.isEnabled()) {
					try {
						if (Double.parseDouble(perms.getDescription().getVersion()) < 1.1) {
							Permission.log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", plugin.getDescription().getName(), this.name));
						}
					} catch (final NumberFormatException e) {
						// version is first release, numbered 1.0.0
						Permission.log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", plugin.getDescription().getName(), this.name));
					}
				}
				this.perms = (Main) perms;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_Xperms permission;
		
		public PermissionServerListener(final Permission_Xperms permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.perms == null) {
				final Plugin perms = event.getPlugin();
				if (perms.getDescription().getName().equals("Xperms")) {
					try {
						if (Double.parseDouble(perms.getDescription().getVersion()) < 1.1) {
							Permission.log.info(String.format("[%s] [Permission] %s Current version is not compatible with vault! Please Update!", Permission_Xperms.this.plugin.getDescription().getName(), Permission_Xperms.this.name));
						}
					} catch (final NumberFormatException e) {
						// version is first release, numbered 1.0.0
						Permission.log.info(String.format("[%s] [Permission] %s Current version is not compatibe with vault! Please Update!", Permission_Xperms.this.plugin.getDescription().getName(), Permission_Xperms.this.name));
					}
					this.permission.perms = (Main) perms;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_Xperms.this.plugin.getDescription().getName(), Permission_Xperms.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.perms != null) {
				if (event.getPlugin().getName().equals("Xperms")) {
					this.permission.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_Xperms.this.plugin.getDescription().getName(), this.permission.name));
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
		return this.perms.isEnabled();
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		return this.perms.getXplayer().hasPerm(world, player, permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		return this.perms.getXplayer().addNode(world, player, permission);
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		return this.perms.getXplayer().removeNode(world, player, permission);
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		return this.perms.getXgroup().hasPerm(group, permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		this.perms.getXgroup().addNode(group, permission);
		return true;
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		return this.perms.getXgroup().removeNode(group, permission);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		final String groupForWorld = this.perms.getXplayer().getGroupForWorld(player, world);
		return groupForWorld.equals(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		return this.perms.getXplayer().setPlayerGroup(world, player, group);
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		return this.perms.getXplayer().setPlayerDefault(world, player);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		return this.perms.getXplayer().getPlayerGroups(player);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		return this.perms.getXplayer().getGroupForWorld(player, world);
	}
	
	@Override
	public String[] getGroups() {
		return this.perms.getXgroup().getGroups();
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
}