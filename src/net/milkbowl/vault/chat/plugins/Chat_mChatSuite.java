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

import com.miraclem4n.mchat.api.Reader;
import com.miraclem4n.mchat.api.Writer;
import com.miraclem4n.mchat.types.InfoType;
import in.mDev.MiracleM4n.mChatSuite.mChatSuite;
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

public class Chat_mChatSuite extends Chat {
	private final Logger log;
	private final Plugin plugin;
	private mChatSuite mChat;
	
	public Chat_mChatSuite(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.mChat == null) {
			final Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChatSuite");
			if (chat != null && chat.isEnabled()) {
				this.mChat = (mChatSuite) chat;
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChatSuite"));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Chat_mChatSuite.this.mChat == null) {
				final Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("mChatSuite")) {
					Chat_mChatSuite.this.mChat = (mChatSuite) chat;
					Chat_mChatSuite.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_mChatSuite.this.plugin.getDescription().getName(), "mChatSuite"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Chat_mChatSuite.this.mChat != null) {
				if (event.getPlugin().getDescription().getName().equals("mChatSuite")) {
					Chat_mChatSuite.this.mChat = null;
					Chat_mChatSuite.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_mChatSuite.this.plugin.getDescription().getName(), "mChatSuite"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		String name = "mChatSuite";
		return name;
	}
	
	@Override
	public boolean isEnabled() {
		return this.mChat != null && this.mChat.isEnabled();
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return Reader.getPrefix(player, InfoType.USER, world);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		this.setPlayerInfoValue(world, player, "prefix", prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return Reader.getSuffix(player, InfoType.USER, world);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		this.setPlayerInfoValue(world, player, "suffix", suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return Reader.getPrefix(group, InfoType.GROUP, world);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		this.setGroupInfoValue(world, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return Reader.getSuffix(group, InfoType.GROUP, world);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		this.setGroupInfoValue(world, group, "suffix", suffix);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		final String val = this.getPlayerInfoValue(world, player, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		this.setPlayerInfoValue(world, player, node, value);
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		final String val = this.getGroupInfoValue(world, group, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		this.setGroupInfoValue(world, group, node, value);
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		final String val = this.getPlayerInfoValue(world, player, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(val);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		this.setPlayerInfoValue(world, player, node, value);
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		final String val = this.getGroupInfoValue(world, group, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		try {
			return Double.parseDouble(val);
		} catch (final NumberFormatException e) {
			return defaultValue;
		}
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		this.setGroupInfoValue(world, group, node, value);
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		final String val = this.getPlayerInfoValue(world, player, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		return Boolean.parseBoolean(val);
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		this.setPlayerInfoValue(world, player, node, value);
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		final String val = this.getGroupInfoValue(world, group, node);
		if (val == null || val.equals("")) {
			return defaultValue;
		}
		return Boolean.parseBoolean(val);
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		this.setGroupInfoValue(world, group, node, value);
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final String val = this.getPlayerInfoValue(world, player, node);
		if (val == null) {
			return defaultValue;
		} else {
			return val;
		}
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		this.setPlayerInfoValue(world, player, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		final String val = this.getGroupInfoValue(world, group, node);
		if (val == null) {
			return defaultValue;
		} else {
			return val;
		}
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		this.setGroupInfoValue(world, group, node, value);
	}
	
	private void setPlayerInfoValue(final String world, final String player, final String node, final Object value) {
		if (world != null) {
			Writer.setWorldVar(player, InfoType.USER, world, node, value.toString());
		} else {
			Writer.setInfoVar(player, InfoType.USER, node, value.toString());
		}
	}
	
	private void setGroupInfoValue(final String world, final String group, final String node, final Object value) {
		if (world != null) {
			Writer.setWorldVar(group, InfoType.GROUP, world, node, value);
		} else {
			Writer.setInfoVar(group, InfoType.GROUP, node, value);
		}
	}
	
	private String getPlayerInfoValue(final String world, final String player, final String node) {
		return Reader.getInfo(player, InfoType.USER, world, node);
	}
	
	private String getGroupInfoValue(final String world, final String group, final String node) {
		return Reader.getInfo(group, InfoType.GROUP, world, node);
	}
}
