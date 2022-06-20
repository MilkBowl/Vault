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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.logging.Logger;

public class Chat_PermissionsEx extends Chat {
	private final Logger log;
	private final String name = "PermissionsEx_Chat";
	
	private final Plugin plugin;
	private PermissionsEx chat;
	
	public Chat_PermissionsEx(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (this.chat == null) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
			if (p != null) {
				if (p.isEnabled()) {
					this.chat = (PermissionsEx) p;
					this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), this.name));
				}
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_PermissionsEx chat;
		
		public PermissionServerListener(final Chat_PermissionsEx chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (this.chat.chat == null) {
				final Plugin perms = event.getPlugin();
				
				if (perms.getDescription().getName().equals("PermissionsEx")) {
					if (perms.isEnabled()) {
						this.chat.chat = (PermissionsEx) perms;
						Chat_PermissionsEx.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_PermissionsEx.this.plugin.getDescription().getName(), this.chat.name));
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.chat.chat != null) {
				if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
					this.chat.chat = null;
					Chat_PermissionsEx.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_PermissionsEx.this.plugin.getDescription().getName(), this.chat.name));
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
		if (this.chat == null)
			return false;
		else
			return this.chat.isEnabled();
	}
	
	private PermissionUser getUser(final OfflinePlayer op) {
		return PermissionsEx.getPermissionManager().getUser(op.getUniqueId());
	}
	
	private PermissionUser getUser(final String playerName) {
		return PermissionsEx.getPermissionManager().getUser(playerName);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String playerName, final String node, final int defaultValue) {
		return this.getUser(playerName).getOptionInteger(node, world, defaultValue);
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String playerName, final String node, final double defaultValue) {
		return this.getUser(playerName).getOptionDouble(node, world, defaultValue);
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String playerName, final String node, final boolean defaultValue) {
		return this.getUser(playerName).getOptionBoolean(node, world, defaultValue);
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String playerName, final String node, final String defaultValue) {
		return this.getUser(playerName).getOption(node, world, defaultValue);
	}
	
	public int getPlayerInfoInteger(final String world, final OfflinePlayer op, final String node, final int defaultValue) {
		return this.getUser(op).getOptionInteger(node, world, defaultValue);
	}
	
	public double getPlayerInfoDouble(final String world, final OfflinePlayer op, final String node, final double defaultValue) {
		return this.getUser(op).getOptionDouble(node, world, defaultValue);
	}
	
	public boolean getPlayerInfoBoolean(final String world, final OfflinePlayer op, final String node, final boolean defaultValue) {
		return this.getUser(op).getOptionBoolean(node, world, defaultValue);
	}
	
	public String getPlayerInfoString(final String world, final OfflinePlayer op, final String node, final String defaultValue) {
		return this.getUser(op).getOption(node, world, defaultValue);
	}
	
	public void setPlayerInfoInteger(final String world, final OfflinePlayer op, final String node, final int value) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	public void setPlayerInfoDouble(final String world, final OfflinePlayer op, final String node, final double value) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	public void setPlayerInfoBoolean(final String world, final OfflinePlayer op, final String node, final boolean value) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	public void setPlayerInfoString(final String world, final OfflinePlayer op, final String node, final String value) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String playerName, final String node, final int value) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String playerName, final String node, final double value) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String playerName, final String node, final boolean value) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String playerName, final String node, final String value) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			user.setOption(node, String.valueOf(value), world);
		}
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String groupName, final String node, final int defaultValue) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return defaultValue;
		} else {
			return group.getOptionInteger(node, world, defaultValue);
		}
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String groupName, final String node, final int value) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
		} else {
			group.setOption(node, world, String.valueOf(value));
		}
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String groupName, final String node, final double defaultValue) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return defaultValue;
		} else {
			return group.getOptionDouble(node, world, defaultValue);
		}
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String groupName, final String node, final double value) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
		} else {
			group.setOption(node, world, String.valueOf(value));
		}
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String groupName, final String node, final boolean defaultValue) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return defaultValue;
		} else {
			return group.getOptionBoolean(node, world, defaultValue);
		}
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String groupName, final String node, final boolean value) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
		} else {
			group.setOption(node, world, String.valueOf(value));
		}
	}
	
	@Override
	public String getGroupInfoString(final String world, final String groupName, final String node, final String defaultValue) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
			return defaultValue;
		} else {
			return group.getOption(node, world, defaultValue);
		}
	}
	
	@Override
	public void setGroupInfoString(final String world, final String groupName, final String node, final String value) {
		final PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
		if (group == null) {
		} else {
			group.setOption(node, world, value);
		}
	}
	
	public String getPlayerPrefix(final String world, final OfflinePlayer op) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			return user.getPrefix(world);
		} else {
			return null;
		}
	}
	
	public String getPlayerSuffix(final String world, final OfflinePlayer op) {
		final PermissionUser user = this.getUser(op);
		if (user != null) {
			return user.getSuffix(world);
		} else {
			return null;
		}
	}
	
	public void setPlayerSuffix(final String world, final OfflinePlayer player, final String suffix) {
		final PermissionUser user = this.getUser(player);
		if (user != null) {
			user.setSuffix(suffix, world);
		}
	}
	
	public void setPlayerPrefix(final String world, final OfflinePlayer player, final String prefix) {
		final PermissionUser user = this.getUser(player);
		if (user != null) {
			user.setPrefix(prefix, world);
		}
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String playerName) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			return user.getPrefix(world);
		} else {
			return null;
		}
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String playerName) {
		final PermissionUser user = this.getUser(playerName);
		if (user != null) {
			return user.getSuffix(world);
		} else {
			return null;
		}
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		final PermissionUser user = this.getUser(player);
		if (user != null) {
			user.setSuffix(suffix, world);
		}
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		final PermissionUser user = this.getUser(player);
		if (user != null) {
			user.setPrefix(prefix, world);
		}
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		final PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
		if (group != null) {
			return pGroup.getPrefix(world);
		} else {
			return null;
		}
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		final PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
		if (group != null) {
			pGroup.setPrefix(prefix, world);
		}
		
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		final PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
		if (group != null) {
			return pGroup.getSuffix(world);
		} else {
			return null;
		}
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		final PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
		if (group != null) {
			pGroup.setSuffix(suffix, world);
		}
	}
}
