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

import com.dthielke.starburst.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Permission_Starburst extends Permission {
	private StarburstPlugin perms;
	private final String name = "Starburst";
	
	public Permission_Starburst(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.perms == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("Starburst");
			if (p != null) {
				this.perms = (StarburstPlugin) p;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Permission_Starburst.this.perms == null) {
				final Plugin p = event.getPlugin();
				if (p.getDescription().getName().equals("Starburst")) {
					Permission_Starburst.this.perms = (StarburstPlugin) p;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_Starburst.this.plugin.getDescription().getName(), Permission_Starburst.this.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Permission_Starburst.this.perms != null) {
				if (event.getPlugin().getDescription().getName().equals("Starburst")) {
					Permission_Starburst.this.perms = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_Starburst.this.plugin.getDescription().getName(), Permission_Starburst.this.name));
				}
			}
		}
	}
	
	@Override
	public String[] getGroups() {
		final String[] s = new String[this.perms.getGroupManager().getDefaultGroupSet().getGroups().size()];
		int i = 0;
		for (final Group g : this.perms.getGroupManager().getDefaultGroupSet().getGroups()) {
			s[i] = g.getName();
			i++;
		}
		return s;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		final Set<Group> children = user.getChildren(true);
		final List<String> groups = new ArrayList<>();
		for (final Group child : children) {
			groups.add(child.getName());
		}
		return groups.toArray(new String[0]);
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		final Set<Group> children = user.getChildren(false);
		if (!children.isEmpty()) {
			return children.iterator().next().getName();
		} else {
			return null;
		}
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, String permission) {
		final GroupManager gm = this.perms.getGroupManager();
		final GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			
			final boolean value = !permission.startsWith("^");
			permission = value ? permission : permission.substring(1);
			g.addPermission(permission, value, true, true);
			
			for (final User user : gm.getAffectedUsers(g)) {
				user.applyPermissions(gm.getFactory());
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			return g.hasPermission(permission, true);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, String permission) {
		final GroupManager gm = this.perms.getGroupManager();
		final GroupSet set = gm.getWorldSet(Bukkit.getWorld(world));
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			
			final boolean value = !permission.startsWith("^");
			permission = value ? permission : permission.substring(1);
			
			if (g.hasPermission(permission, false)) {
				g.removePermission(permission, true);
				
				for (final User user : gm.getAffectedUsers(g)) {
					user.applyPermissions(gm.getFactory());
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return this.perms != null && this.perms.isEnabled();
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, String permission) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		final boolean value = !permission.startsWith("^");
		permission = value ? permission : permission.substring(1);
		user.addPermission(permission, value, true, true);
		
		if (user.isActive()) {
			user.applyPermissions(this.perms.getGroupManager().getFactory());
		}
		return true;
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			if (!user.hasChild(g, false)) {
				user.addChild(g, true);
				
				if (user.isActive()) {
					user.applyPermissions(this.perms.getGroupManager().getFactory());
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		
		if (op.isOnline()) {
			final Player p = (Player) op;
			if (p.getWorld().getName().equalsIgnoreCase(world)) {
				return p.hasPermission(permission);
			}
		}
		
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final Group user = set.getUser(op);
		return user.hasPermission(permission, true);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			return user.hasChild(g, true);
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerRemove(final String world, final String player, String permission) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		final boolean value = !permission.startsWith("^");
		permission = value ? permission : permission.substring(1);
		if (user.hasPermission(permission, false)) {
			user.removePermission(permission, true);
			if (user.isActive()) {
				user.applyPermissions(this.perms.getGroupManager().getFactory());
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		final OfflinePlayer op = Bukkit.getOfflinePlayer(player);
		final GroupSet set = this.perms.getGroupManager().getWorldSet(Bukkit.getWorld(world));
		final User user = set.getUser(op);
		
		if (set.hasGroup(group)) {
			final Group g = set.getGroup(group);
			if (user.hasChild(g, false)) {
				user.removeChild(g, true);
				
				if (user.isActive()) {
					user.applyPermissions(this.perms.getGroupManager().getFactory());
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
}
