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

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Chat_GroupManager extends Chat {
	
	private final Logger log;
	private final String name = "GroupManager - Chat";
	private final Plugin plugin;
	private GroupManager groupManager;
	
	public Chat_GroupManager(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.groupManager == null) {
			final Plugin chat = plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if (chat != null) {
				if (chat.isEnabled()) {
					this.groupManager = (GroupManager) chat;
					this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), this.name));
				}
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		
		final Chat_GroupManager chat;
		
		public PermissionServerListener(final Chat_GroupManager chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.chat.groupManager == null) {
				final Plugin perms = event.getPlugin();
				
				if (perms.getDescription().getName().equals("GroupManager")) {
					this.chat.groupManager = (GroupManager) perms;
					Chat_GroupManager.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_GroupManager.this.plugin.getDescription().getName(), this.chat.name));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.chat.groupManager != null) {
				if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
					this.chat.groupManager = null;
					Chat_GroupManager.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_GroupManager.this.plugin.getDescription().getName(), this.chat.name));
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
		if (this.groupManager == null) {
			return false;
		} else {
			return this.groupManager.isEnabled();
		}
	}
	
	@Override
	public int getPlayerInfoInteger(final String worldName, final String playerName, final String node, final int defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Integer val = handler.getUserPermissionInteger(playerName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoInteger(final String worldName, final String playerName, final String node, final int value) {
		this.setPlayerValue(worldName, playerName, node, value);
	}
	
	@Override
	public int getGroupInfoInteger(final String worldName, final String groupName, final String node, final int defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Integer val = handler.getGroupPermissionInteger(groupName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoInteger(final String worldName, final String groupName, final String node, final int value) {
		this.setGroupValue(worldName, groupName, node, value);
	}
	
	@Override
	public double getPlayerInfoDouble(final String worldName, final String playerName, final String node, final double defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Double val = handler.getUserPermissionDouble(playerName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoDouble(final String worldName, final String playerName, final String node, final double value) {
		this.setPlayerValue(worldName, playerName, node, value);
	}
	
	@Override
	public double getGroupInfoDouble(final String worldName, final String groupName, final String node, final double defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Double val = handler.getGroupPermissionDouble(groupName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoDouble(final String worldName, final String groupName, final String node, final double value) {
		this.setGroupValue(worldName, groupName, node, value);
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String worldName, final String playerName, final String node, final boolean defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Boolean val = handler.getUserPermissionBoolean(playerName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoBoolean(final String worldName, final String playerName, final String node, final boolean value) {
		this.setPlayerValue(worldName, playerName, node, value);
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String worldName, final String groupName, final String node, final boolean defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final Boolean val = handler.getGroupPermissionBoolean(groupName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoBoolean(final String worldName, final String groupName, final String node, final boolean value) {
		this.setGroupValue(worldName, groupName, node, value);
	}
	
	@Override
	public String getPlayerInfoString(final String worldName, final String playerName, final String node, final String defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final String val = handler.getUserPermissionString(playerName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoString(final String worldName, final String playerName, final String node, final String value) {
		this.setPlayerValue(worldName, playerName, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String worldName, final String groupName, final String node, final String defaultValue) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return defaultValue;
		}
		final String val = handler.getGroupPermissionString(groupName, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoString(final String worldName, final String groupName, final String node, final String value) {
		this.setGroupValue(worldName, groupName, node, value);
	}
	
	@Override
	public String getPlayerPrefix(final String worldName, final String playerName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return "";
		}
		return handler.getUserPrefix(playerName);
	}
	
	@Override
	public String getPlayerSuffix(final String worldName, final String playerName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		if (handler == null) {
			return "";
		}
		return handler.getUserSuffix(playerName);
	}
	
	@Override
	public void setPlayerSuffix(final String worldName, final String player, final String suffix) {
		this.setPlayerInfoString(worldName, player, "suffix", suffix);
	}
	
	@Override
	public void setPlayerPrefix(final String worldName, final String player, final String prefix) {
		this.setPlayerInfoString(worldName, player, "prefix", prefix);
	}
	
	@Override
	public String getGroupPrefix(final String worldName, final String group) {
		return this.getGroupInfoString(worldName, group, "prefix", "");
	}
	
	@Override
	public void setGroupPrefix(final String worldName, final String group, final String prefix) {
		this.setGroupInfoString(worldName, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String worldName, final String group) {
		return this.getGroupInfoString(worldName, group, "suffix", "");
	}
	
	@Override
	public void setGroupSuffix(final String worldName, final String group, final String suffix) {
		this.setGroupInfoString(worldName, group, "suffix", suffix);
	}
	
	@Override
	public String getPrimaryGroup(final String worldName, final String playerName) {
		final AnjoPermissionsHandler handler;
		if (worldName == null) {
			handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
		} else {
			handler = this.groupManager.getWorldsHolder().getWorldPermissions(worldName);
		}
		return handler.getGroup(playerName);
	}
	
	private void setPlayerValue(final String worldName, final String playerName, final String node, final Object value) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return;
		}
		final User user = owh.getUser(playerName);
		if (user == null) {
			return;
		}
		user.getVariables().addVar(node, value);
	}
	
	private void setGroupValue(final String worldName, final String groupName, final String node, final Object value) {
		final OverloadedWorldHolder owh;
		if (worldName == null) {
			owh = this.groupManager.getWorldsHolder().getDefaultWorld();
		} else {
			owh = this.groupManager.getWorldsHolder().getWorldData(worldName);
		}
		if (owh == null) {
			return;
		}
		final Group group = owh.getGroup(groupName);
		if (group == null) {
			return;
		}
		group.getVariables().addVar(node, value);
	}
}
