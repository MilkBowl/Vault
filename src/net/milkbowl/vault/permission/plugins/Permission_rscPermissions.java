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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.simsonic.rscPermissions.MainPluginClass;

public class Permission_rscPermissions extends Permission {
	
	private final Plugin vault;
	private ru.simsonic.rscPermissions.MainPluginClass rscp;
	private ru.simsonic.rscPermissions.rscpAPI rscpAPI;
	
	public Permission_rscPermissions(final Plugin plugin) {
		vault = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), this.vault);
		if (this.rscp == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
			if (perms != null && perms.isEnabled()) {
				rscp = (MainPluginClass) perms;
				this.rscpAPI = this.rscp.API;
				plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "rscPermissions"));
			}
		}
	}
	
	private class PermissionServerListener implements Listener {
		private final Permission_rscPermissions bridge;
		
		public PermissionServerListener(final Permission_rscPermissions bridge) {
			this.bridge = bridge;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.bridge.rscp == null) {
				final Plugin plugin = event.getPlugin();
				if (plugin.getDescription().getName().equals("rscPermissions")) {
					this.bridge.rscp = (MainPluginClass) plugin;
					this.bridge.rscpAPI = this.bridge.rscp.API;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_rscPermissions.this.vault.getDescription().getName(), "rscPermissions"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.bridge.rscpAPI != null) {
				if (event.getPlugin().getDescription().getName().equals(this.bridge.rscpAPI.getName())) {
					this.bridge.rscpAPI = null;
					this.bridge.rscp = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_rscPermissions.this.vault.getDescription().getName(), "rscPermissions"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "rscPermissions";
	}
	
	@Override
	public boolean isEnabled() {
		return this.rscpAPI != null && this.rscpAPI.isEnabled();
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return this.rscpAPI.hasSuperPermsCompat();
	}
	
	@Override
	public boolean hasGroupSupport() {
		return this.rscpAPI.hasGroupSupport();
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		return this.rscpAPI.playerHas(world, player, permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		return this.rscpAPI.playerAdd(world, player, permission);
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		return this.rscpAPI.playerRemove(world, player, permission);
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		return this.rscpAPI.groupHas(world, group, permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		return this.rscpAPI.groupAdd(world, group, permission);
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		return this.rscpAPI.groupRemove(world, group, permission);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		return this.rscpAPI.playerInGroup(world, player, group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		return this.rscpAPI.playerAddGroup(world, player, group);
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		return this.rscpAPI.playerRemoveGroup(world, player, group);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		return this.rscpAPI.getPlayerGroups(world, player);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		return this.rscpAPI.getPrimaryGroup(world, player);
	}
	
	@Override
	public String[] getGroups() {
		return this.rscpAPI.getGroups();
	}
}
