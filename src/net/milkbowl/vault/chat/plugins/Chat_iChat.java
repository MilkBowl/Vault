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

import net.TheDgtl.iChat.iChat;
import net.TheDgtl.iChat.iChatAPI;
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

public class Chat_iChat extends Chat {
	
	private final Logger log;
	private final Plugin plugin;
	private iChatAPI iChat;
	
	public Chat_iChat(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.iChat == null) {
			final Plugin chat = plugin.getServer().getPluginManager().getPlugin("iChat");
			if (chat != null) {
				this.iChat = ((iChat) chat).API;
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "iChat"));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_iChat chat;
		
		public PermissionServerListener(final Chat_iChat chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (chat.iChat == null) {
				final Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("iChat")) {
					this.chat.iChat = ((iChat) chat).API;
					Chat_iChat.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_iChat.this.plugin.getDescription().getName(), "iChat"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (chat.iChat != null) {
				if (event.getPlugin().getDescription().getName().equals("iChat")) {
					chat.iChat = null;
					Chat_iChat.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_iChat.this.plugin.getDescription().getName(), "iChat"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		String name = "iChat";
		return name;
	}
	
	@Override
	public boolean isEnabled() {
		return this.iChat != null;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		final Player p = this.plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}
		
		if (!p.getWorld().getName().equals(world)) {
			return null;
		}
		
		return this.iChat.getPrefix(p);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		final Player p = this.plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}
		
		if (!p.getWorld().getName().equals(world)) {
			return null;
		}
		
		return this.iChat.getSuffix(p);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		final String val = this.getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		}
		
		int i;
		try {
			i = Integer.parseInt(val);
			return i;
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		final String val = this.getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		}
		
		double d;
		try {
			d = Double.parseDouble(val);
			return d;
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		final String val = this.getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		} else {
			final Boolean v = Boolean.valueOf(val);
			return v != null ? v : defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final Player p = this.plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}
		
		if (!p.getWorld().getName().equals(world)) {
			return null;
		}
		
		final String val = this.iChat.getInfo(p, node);
		
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
}
