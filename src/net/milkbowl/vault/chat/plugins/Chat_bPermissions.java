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

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.info.InfoReader;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Chat_bPermissions extends Chat {
	private final Logger log;
	private final Plugin plugin;
	InfoReader chat;
	
	public Chat_bPermissions(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.chat == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				this.chat = Permissions.getInfoReader();
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions"));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_bPermissions chat;
		
		public PermissionServerListener(final Chat_bPermissions chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (chat.chat == null) {
				final Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("bPermissions")) {
					this.chat.chat = Permissions.getInfoReader();
					Chat_bPermissions.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_bPermissions.this.plugin.getDescription().getName(), "bPermissions"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (chat.chat != null) {
				if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
					chat.chat = null;
					Chat_bPermissions.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_bPermissions.this.plugin.getDescription().getName(), "bPermissions"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "bInfo";
	}
	
	@Override
	public boolean isEnabled() {
		return this.chat != null;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return this.chat.getPrefix(player, world);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return this.chat.getSuffix(player, world);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return this.chat.getGroupPrefix(group, world);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return this.chat.getGroupSuffix(group, world);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(s);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(s);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(s);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		final String s = this.getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		} else {
			final Boolean val = Boolean.valueOf(s);
			return val != null ? val : defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		final String s = this.getGroupInfoString(world, group, node, null);
		if (s == null) {
			return defaultValue;
		} else {
			final Boolean val = Boolean.valueOf(s);
			return val != null ? val : defaultValue;
		}
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final String val = this.chat.getValue(player, world, node);
		return (val == null || val.equals("BLANKWORLD")) ? defaultValue : val;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		final String val = this.chat.getGroupValue(group, world, node);
		return (val == null || val.equals("BLANKWORLD")) ? defaultValue : val;
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
}
