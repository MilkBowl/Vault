/*
 * This file is part of Vault.
 *
 * Copyright (c) 2017 Lukas Nehrke
 *
 * Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

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

public class Chat_iChat extends Chat {

	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "iChat";
	private Plugin plugin = null;
	private iChatAPI iChat = null;

	public Chat_iChat(Plugin plugin, Permission perms) {
		super(perms);
		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (iChat == null) {
			Plugin chat = plugin.getServer().getPluginManager().getPlugin("iChat");
			if (chat != null) {
				iChat = ((iChat) chat).API;
				log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "iChat"));
			}
		}
	}

	public class PermissionServerListener implements Listener {
		Chat_iChat chat = null;

		public PermissionServerListener(Chat_iChat chat) {
			this.chat = chat;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (this.chat.iChat == null) {
				Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("iChat")) {
					this.chat.iChat = ((iChat) chat).API;
					log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "iChat"));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (this.chat.iChat != null) {
				if (event.getPlugin().getDescription().getName().equals("iChat")) {
					this.chat.iChat = null;
					log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "iChat"));
				}
			}
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEnabled() {
		return iChat != null;
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}

		if (!p.getWorld().getName().equals(world)) {
			return null;
		}

		return iChat.getPrefix(p);
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}

		if (!p.getWorld().getName().equals(world)) {
			return null;
		}

		return iChat.getSuffix(p);
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}
	

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		String val = getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		}

		Integer i = defaultValue;
		try {
			i = Integer.valueOf(val);
			return i;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		String val = getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		}

		double d = defaultValue;
		try {
			d = Double.valueOf(val);
			return d;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		String val = getPlayerInfoString(world, player, node, null);
		if (val == null) {
			return defaultValue;
		} else {
		    Boolean v = Boolean.valueOf(val);
		    return v != null ? v : defaultValue;
		}
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
	    throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		Player p = plugin.getServer().getPlayer(player);
		if (p == null) {
			throw new UnsupportedOperationException("iChat does not support offline player info nodes!");
		}

		if (!p.getWorld().getName().equals(world)) {
			return null;
		}

		String val = iChat.getInfo(p, node);

		return val != null ? val : defaultValue;
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		throw new UnsupportedOperationException("iChat does not support group info nodes!");
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		throw new UnsupportedOperationException("iChat does not support mutable info nodes!");
	}
}
