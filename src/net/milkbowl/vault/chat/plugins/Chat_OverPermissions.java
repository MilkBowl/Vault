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
package net.milkbowl.vault.chat.plugins;

import com.overmc.overpermissions.api.GroupManager;
import com.overmc.overpermissions.api.PermissionGroup;
import com.overmc.overpermissions.api.PermissionUser;
import com.overmc.overpermissions.api.UserManager;
import com.overmc.overpermissions.internal.OverPermissions;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Chat_OverPermissions extends Chat {
	protected final Plugin plugin;
	private OverPermissions overPerms;
	private UserManager userManager;
	private GroupManager groupManager;
	
	public Chat_OverPermissions(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		
		plugin.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		if (this.overPerms == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("OverPermissions");
			if (p != null) {
				this.overPerms = (OverPermissions) p;
				this.userManager = this.overPerms.getUserManager();
				this.groupManager = this.overPerms.getGroupManager();
				plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "OverPermissions"));
			}
		}
	}
	
	@Override
	public String getName() {
		return "OverPermissions_Chat";
	}
	
	@Override
	public boolean isEnabled() {
		return this.overPerms != null;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return this.getPlayerInfoString(world, player, "prefix", "");
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		this.setPlayerInfoString(world, player, "prefix", prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return this.getPlayerInfoString(world, player, "suffix", "");
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		this.setPlayerInfoString(world, player, "suffix", suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return this.getGroupInfoString(world, group, "prefix", "");
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		this.setGroupInfoString(world, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return this.getGroupInfoString(world, group, "suffix", "");
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		this.setGroupInfoString(world, group, "prefix", suffix);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException ignored) {
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		this.setPlayerInfoString(world, player, node, String.valueOf(value));
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException ignored) {
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		this.setGroupInfoString(world, group, node, String.valueOf(value));
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(s);
		} catch (final NumberFormatException ignored) {
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		this.setPlayerInfoString(world, player, node, String.valueOf(value));
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(s);
		} catch (final NumberFormatException ignored) {
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		this.setGroupInfoString(world, group, node, String.valueOf(value));
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		final Boolean val = Boolean.valueOf(s);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		this.setPlayerInfoString(world, player, node, String.valueOf(value));
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		final Boolean val = Boolean.valueOf(s);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		this.setGroupInfoString(world, group, node, String.valueOf(value));
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String playerName, final String node, final String defaultValue) {
		if (!this.userManager.doesUserExist(playerName)) {
			return defaultValue;
		}
		final PermissionUser user = this.userManager.getPermissionUser(playerName);
		if (world == null) { // Retrieve meta from the global store.
			if (!user.hasGlobalMeta(node)) {
				return defaultValue;
			}
			return user.getGlobalMeta(node);
		} else {
			if (!user.hasMeta(node, world)) {
				return defaultValue;
			}
			return user.getMeta(node, world);
		}
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String playerName, final String node, final String value) {
		if (!this.userManager.canUserExist(playerName)) {
			return;
		}
		final PermissionUser user = this.userManager.getPermissionUser(playerName);
		if (world != null) {
			if (value == null) {
				user.removeMeta(node, world);
			} else {
				user.setMeta(node, value, world);
			}
		} else {
			if (value == null) {
				user.removeGlobalMeta(node);
			} else {
				user.setGlobalMeta(node, value);
			}
		}
	}
	
	@Override
	public String getGroupInfoString(final String world, final String groupName, final String node, final String defaultValue) {
		if (!this.groupManager.doesGroupExist(groupName)) {
			return defaultValue;
		}
		final PermissionGroup group = this.overPerms.getGroupManager().getGroup(groupName);
		if (world == null) { // Retrieve from the global store.
			if (!group.hasGlobalMeta(node)) {
				return defaultValue;
			}
			return group.getGlobalMeta(node);
		} else {
			if (!group.hasMeta(node, world)) {
				return defaultValue;
			}
			return group.getMeta(node, world);
		}
	}
	
	@Override
	public void setGroupInfoString(final String world, final String groupName, final String node, final String value) {
		if (!this.overPerms.getGroupManager().doesGroupExist(groupName)) {
			return;
		}
		final PermissionGroup group = this.overPerms.getGroupManager().getGroup(groupName);
		if (world != null) {
			if (value == null) {
				group.removeMeta(node, world);
			} else {
				group.setMeta(node, value, world);
			}
		} else {
			if (value == null) {
				group.removeGlobalMeta(node);
			} else {
				group.setGlobalMeta(node, value);
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_OverPermissions chat;
		
		public PermissionServerListener(final Chat_OverPermissions chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.chat.overPerms == null) {
				final Plugin chat = Chat_OverPermissions.this.plugin.getServer().getPluginManager().getPlugin("OverPermissions");
				if (chat != null) {
					this.chat.overPerms = (OverPermissions) chat;
					Chat_OverPermissions.this.plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", Chat_OverPermissions.this.plugin.getDescription().getName(), Chat_OverPermissions.this.getName()));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if ((this.chat.overPerms != null) &&
					(event.getPlugin().getDescription().getName().equals("OverPermissions"))) {
				this.chat.overPerms = null;
				Chat_OverPermissions.this.plugin.getLogger().info(String.format("[%s][Chat] %s un-hooked.", Chat_OverPermissions.this.plugin.getDescription().getName(), Chat_OverPermissions.this.getName()));
			}
		}
	}
}