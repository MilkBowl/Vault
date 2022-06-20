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

import com.lightniinja.kperms.KGroup;
import com.lightniinja.kperms.KPermsPlugin;
import com.lightniinja.kperms.KPlayer;
import com.lightniinja.kperms.Utilities;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Permission_KPerms extends Permission {
	
	private final Plugin vault;
	private KPermsPlugin kperms;
	
	public Permission_KPerms(final Plugin plugin) {
		vault = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), this.vault);
		if (this.kperms == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("KPerms");
			if (perms != null && perms.isEnabled()) {
				kperms = (KPermsPlugin) perms;
				plugin.getLogger().info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), "KPerms"));
			}
		}
	}
	
	private class PermissionServerListener implements Listener {
		private final Permission_KPerms bridge;
		
		public PermissionServerListener(final Permission_KPerms bridge) {
			this.bridge = bridge;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.bridge.kperms == null) {
				final Plugin plugin = event.getPlugin();
				if (plugin.getDescription().getName().equals("KPerms")) {
					this.bridge.kperms = (KPermsPlugin) plugin;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_KPerms.this.vault.getDescription().getName(), "KPerms"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.bridge.kperms != null) {
				if (event.getPlugin().getDescription().getName().equals(this.bridge.kperms.getName())) {
					this.bridge.kperms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_KPerms.this.vault.getDescription().getName(), "KPerms"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "KPerms";
	}
	
	@Override
	public boolean isEnabled() {
		return this.kperms.isEnabled();
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
		return new KPlayer(player, this.kperms).hasPermission(permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		return new KPlayer(player, this.kperms).addPermission(permission);
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		return new KPlayer(player, this.kperms).removePermission(permission);
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		return new KGroup(group, this.kperms).hasPermission(permission);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		return new KGroup(group, this.kperms).addPermission(permission);
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		return new KGroup(group, this.kperms).removePermission(permission);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		return new KPlayer(player, this.kperms).isMemberOfGroup(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		return new KPlayer(player, this.kperms).addGroup(group);
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		return new KPlayer(player, this.kperms).removeGroup(group);
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final List<String> groups = new KPlayer(player, this.kperms).getGroups();
		String[] gr = new String[groups.size()];
		gr = groups.toArray(gr);
		return gr;
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		return new KPlayer(player, this.kperms).getPrimaryGroup();
	}
	
	@Override
	public String[] getGroups() {
		return new Utilities(this.kperms).getGroups();
	}
}
