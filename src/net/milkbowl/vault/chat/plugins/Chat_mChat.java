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

import net.D3GN.MiracleM4n.mChat.mChatAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Chat_mChat extends Chat {
	private final Logger log;
	private final Plugin plugin;
	private mChatAPI mChat;
	
	public Chat_mChat(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.mChat == null) {
			final Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
			if (chat != null) {
				this.mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_mChat chat;
		
		public PermissionServerListener(final Chat_mChat chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (chat.mChat == null) {
				final Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("mChat")) {
					this.chat.mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
					Chat_mChat.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_mChat.this.plugin.getDescription().getName(), "mChat"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (chat.mChat != null) {
				if (event.getPlugin().getDescription().getName().equals("mChat")) {
					chat.mChat = null;
					Chat_mChat.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_mChat.this.plugin.getDescription().getName(), "mChat"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		String name = "mChat";
		return name;
	}
	
	@Override
	public boolean isEnabled() {
		return this.mChat != null;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		final Player p = Bukkit.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		return this.mChat.getPrefix(p);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		final Player p = Bukkit.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		return this.mChat.getSuffix(p);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
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
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
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
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
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
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final Player p = Bukkit.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		final String s = this.mChat.getInfo(p, node);
		return s == null ? defaultValue : s;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
}
