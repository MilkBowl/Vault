package net.milkbowl.vault.permission.plugins;

import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.Group;
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

public class Permission_Privileges extends Permission {
	
	private final String name = "Privileges";
	private Privileges privs;
	
	public Permission_Privileges(final Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		// Load service in case it was loaded before
		if (this.privs == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("Privileges");
			if (perms != null && perms.isEnabled()) {
				privs = (Privileges) perms;
				Permission.log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Permission_Privileges permission;
		
		public PermissionServerListener(final Permission_Privileges permission) {
			this.permission = permission;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.permission.privs == null) {
				final Plugin perms = event.getPlugin();
				if (perms.getDescription().getName().equals("Privileges")) {
					this.permission.privs = (Privileges) perms;
					Permission.log.info(String.format("[%s][Permission] %s hooked.", Permission_Privileges.this.plugin.getDescription().getName(), this.permission.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.permission.privs != null) {
				if (event.getPlugin().getDescription().getName().equals("Privileges")) {
					this.permission.privs = null;
					Permission.log.info(String.format("[%s][Permission] %s un-hooked.", Permission_Privileges.this.plugin.getDescription().getName(), this.permission.name));
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
		return true;
	}
	
	@Override
	public boolean playerHas(final String world, final String player, final String permission) {
		final Player p = this.plugin.getServer().getPlayer(player);
		return p != null && p.hasPermission(permission);
	}
	
	@Override
	public boolean playerAdd(final String world, final String player, final String permission) {
		return false;
	}
	
	// use superclass implementation of playerAddTransient() and playerRemoveTransient()
	
	@Override
	public boolean playerRemove(final String world, final String player, final String permission) {
		return false;
	}
	
	@Override
	public boolean groupHas(final String world, final String group, final String permission) {
		final Group g = this.privs.getGroupManager().getGroup(group);
		return g != null && g.hasPermission(permission, world);
	}
	
	@Override
	public boolean groupAdd(final String world, final String group, final String permission) {
		final Group g = this.privs.getGroupManager().getGroup(group);
		return g != null && g.addPermission(world, permission);
	}
	
	@Override
	public boolean groupRemove(final String world, final String group, final String permission) {
		final Group g = this.privs.getGroupManager().getGroup(group);
		return g != null && g.removePermission(world, permission);
	}
	
	@Override
	public boolean playerInGroup(final String world, final String player, final String group) {
		final OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		final Group g = this.privs.getGroupManager().getGroup(p);
		return g != null && g.isMemberOf(group);
	}
	
	@Override
	public boolean playerAddGroup(final String world, final String player, final String group) {
		final Group g = this.privs.getGroupManager().setGroup(player, group);
		return g != null;
	}
	
	@Override
	public boolean playerRemoveGroup(final String world, final String player, final String group) {
		final Group g = this.privs.getGroupManager().getDefaultGroup();
		return g != null && this.playerAddGroup(world, player, g.getName());
	}
	
	@Override
	public String[] getPlayerGroups(final String world, final String player) {
		final OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("Privileges does not support offline players.");
		}
		final Group g = this.privs.getGroupManager().getGroup(p);
		return g != null ? g.getGroupTree().toArray(new String[0]) : null;
	}
	
	@Override
	public String getPrimaryGroup(final String world, final String player) {
		final OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		final Group g = this.privs.getGroupManager().getGroup(p);
		return g != null ? g.getName() : null;
	}
	
	@Override
	public String[] getGroups() {
		final List<String> groups = new ArrayList<>();
		for (final Group g : this.privs.getGroupManager().getGroups()) {
			groups.add(g.getName());
		}
		return groups.toArray(new String[0]);
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
