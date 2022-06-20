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

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
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

public class Chat_Permissions3 extends Chat {
	
	private final Logger log;
	private String name = "Permissions 3 (Yeti) - Chat";
	private PermissionHandler perms;
	private final Plugin plugin;
	private Permissions chat;
	
	public Chat_Permissions3(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.chat == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (p == null) {
				plugin.getServer().getPluginManager().getPlugin("vPerms");
				this.name = "vPerms - Chat";
			}
			if (p != null) {
				if (p.isEnabled() && p.getDescription().getVersion().startsWith("3")) {
					this.chat = (Permissions) p;
					this.perms = this.chat.getHandler();
					this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), this.name));
				}
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Chat_Permissions3.this.chat == null) {
				final Plugin permChat = event.getPlugin();
				if ((permChat.getDescription().getName().equals("Permissions") || permChat.getDescription().getName().equals("vPerms")) && permChat.getDescription().getVersion().startsWith("3")) {
					if (permChat.isEnabled()) {
						Chat_Permissions3.this.chat = (Permissions) permChat;
						Chat_Permissions3.this.perms = Chat_Permissions3.this.chat.getHandler();
						Chat_Permissions3.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_Permissions3.this.plugin.getDescription().getName(), Chat_Permissions3.this.name));
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Chat_Permissions3.this.chat != null) {
				if (event.getPlugin().getDescription().getName().equals("Permissions") || event.getPlugin().getDescription().getName().equals("vPerms")) {
					Chat_Permissions3.this.chat = null;
					Chat_Permissions3.this.perms = null;
					Chat_Permissions3.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_Permissions3.this.plugin.getDescription().getName(), Chat_Permissions3.this.name));
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
		if (this.chat == null) {
			return false;
		} else {
			return this.chat.isEnabled();
		}
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String playerName, final String node, final int defaultValue) {
		final Integer i = perms.getPermissionInteger(world, playerName, node);
		return (i == null) ? defaultValue : i;
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String playerName, final String node, final double defaultValue) {
		final Double d = perms.getPermissionDouble(world, playerName, node);
		return (d == null) ? defaultValue : d;
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String playerName, final String node, final boolean defaultValue) {
		final Boolean b = perms.getPermissionBoolean(world, playerName, node);
		return (b == null) ? defaultValue : b;
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String playerName, final String node, final String defaultValue) {
		final String s = perms.getPermissionString(world, playerName, node);
		return (s == null) ? defaultValue : s;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String playerName) {
		return this.getPlayerInfoString(world, playerName, "prefix", null);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String playerName) {
		return this.getPlayerInfoString(world, playerName, "suffix", null);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		//this.perms.addUserInfo(world, player, "prefix", prefix);
	}
	
	public void setPlayerInfo(final String world, final String playerName, final String node, final Object value) {
		//this.perms.addUserInfo(world, playerName, node, value);
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String playerName, final String node, final int value) {
		this.setPlayerInfo(world, playerName, node, value);
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String playerName, final String node, final double value) {
		this.setPlayerInfo(world, playerName, node, value);
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String playerName, final String node, final boolean value) {
		this.setPlayerInfo(world, playerName, node, value);
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String playerName, final String node, final String value) {
		this.setPlayerInfo(world, playerName, node, value);
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String groupName, final String node, final int defaultValue) {
		final int i = perms.getGroupPermissionInteger(world, groupName, node);
		return i != -1 ? i : defaultValue;
	}
	
	
	public void setGroupInfo(final String world, final String groupName, final String node, final Object value) {
		perms.addGroupInfo(world, groupName, node, value);
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String groupName, final String node, final int value) {
		this.setGroupInfo(world, groupName, node, value);
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String groupName, final String node, final double defaultValue) {
		final double d = perms.getGroupPermissionDouble(world, groupName, node);
		return d != -1.0d ? d : defaultValue;
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String groupName, final String node, final double value) {
		this.setGroupInfo(world, groupName, node, value);
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String groupName, final String node, final boolean defaultValue) {
		return perms.getGroupPermissionBoolean(world, groupName, node);
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String groupName, final String node, final boolean value) {
		this.setGroupInfo(world, groupName, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String groupName, final String node, final String defaultValue) {
		final String s = perms.getGroupPermissionString(world, groupName, node);
		return s != null ? s : defaultValue;
	}
	
	@Override
	public void setGroupInfoString(final String world, final String groupName, final String node, final String value) {
		this.setGroupInfo(world, groupName, node, value);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return perms.getGroupPrefix(world, group);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		perms.addGroupInfo(world, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return perms.getGroupSuffix(world, group);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		perms.addGroupInfo(world, group, "suffix", suffix);
	}
}
