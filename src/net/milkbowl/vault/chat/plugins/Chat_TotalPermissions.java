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

import net.ae97.totalpermissions.PermissionManager;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionBase;
import net.ae97.totalpermissions.permission.PermissionGroup;
import net.ae97.totalpermissions.permission.PermissionUser;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 * @author Lord_Ralex
 * @version 1.0
 */
public class Chat_TotalPermissions extends Chat {
	
	private final Plugin plugin;
	private TotalPermissions totalPermissions;
	private final String name = "TotalPermissions-Chat";
	
	public Chat_TotalPermissions(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		if (this.totalPermissions == null) {
			final Plugin chat = plugin.getServer().getPluginManager().getPlugin("TotalPermissions");
			if (chat != null) {
				if (chat.isEnabled()) {
					this.totalPermissions = (TotalPermissions) chat;
					plugin.getLogger().info(String.format("[Chat] %s hooked.", this.name));
				}
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		final Chat_TotalPermissions chat;
		
		public PermissionServerListener(final Chat_TotalPermissions chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.chat.totalPermissions == null) {
				final Plugin perms = event.getPlugin();
				
				if (perms != null) {
					if (perms.getDescription().getName().equals("TotalPermissions")) {
						if (perms.isEnabled()) {
							this.chat.totalPermissions = (TotalPermissions) perms;
							Chat_TotalPermissions.this.plugin.getLogger().info(String.format("[Chat] %s hooked.", this.chat.getName()));
						}
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.chat.totalPermissions != null) {
				if (event.getPlugin().getDescription().getName().equals("TotalPermissions")) {
					this.chat.totalPermissions = null;
					Chat_TotalPermissions.this.plugin.getLogger().info(String.format("[Chat] %s un-hooked.", this.chat.name));
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
		return (this.totalPermissions != null && this.totalPermissions.isEnabled());
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return getPlayerInfoString(world, player, "prefix", null);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		setPlayerInfoString(world, player, "prefix", prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return getPlayerInfoString(world, player, "suffix", null);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		setPlayerInfoString(world, player, "suffix", suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return getGroupInfoString(world, group, "prefix", null);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		setGroupInfoString(world, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return getGroupInfoString(world, group, "suffix", null);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		setGroupInfoString(world, group, "suffix", suffix);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		final Object pre = this.getPlayerInfo(world, player, node);
		if (pre instanceof Integer) {
			return (Integer) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		this.setPlayerInfo(world, player, node, value);
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		final Object pre = this.getGroupInfo(world, group, node);
		if (pre instanceof Integer) {
			return (Integer) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		this.setGroupInfo(world, group, node, value);
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		final Object pre = this.getPlayerInfo(world, player, node);
		if (pre instanceof Double) {
			return (Double) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		this.setPlayerInfo(world, player, node, value);
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		final Object pre = this.getGroupInfo(world, group, node);
		if (pre instanceof Double) {
			return (Double) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		this.setGroupInfo(world, group, node, value);
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		final Object pre = this.getPlayerInfo(world, player, node);
		if (pre instanceof Boolean) {
			return (Boolean) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		this.setPlayerInfo(world, player, node, value);
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		final Object pre = this.getGroupInfo(world, group, node);
		if (pre instanceof Boolean) {
			return (Boolean) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		this.setGroupInfo(world, group, node, value);
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final Object pre = this.getPlayerInfo(world, player, node);
		if (pre instanceof String) {
			return (String) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		this.setPlayerInfo(world, player, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		final Object pre = this.getGroupInfo(world, group, node);
		if (pre instanceof String) {
			return (String) pre;
		}
		return defaultValue;
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		this.setGroupInfo(world, group, node, value);
	}
	
	private PermissionUser getUser(final String name) {
		final PermissionManager manager = this.totalPermissions.getManager();
		return manager.getUser(name);
	}
	
	private PermissionGroup getGroup(final String name) {
		final PermissionManager manager = this.totalPermissions.getManager();
		return manager.getGroup(name);
	}
	
	private void setPlayerInfo(final String world, final String player, final String node, final Object value) {
		final PermissionBase base = this.getUser(player);
		base.setOption(node, value, world);
	}
	
	private void setGroupInfo(final String world, final String group, final String node, final Object value) {
		final PermissionBase base = this.getGroup(group);
		base.setOption(node, value, world);
	}
	
	private Object getPlayerInfo(final String world, final String player, final String node) {
		final PermissionBase base = this.getUser(player);
		return base.getOption(node);
	}
	
	private Object getGroupInfo(final String world, final String group, final String node) {
		final PermissionBase base = this.getUser(group);
		return base.getOption(node);
	}
}
