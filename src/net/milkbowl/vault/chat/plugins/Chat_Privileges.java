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

import net.krinsoft.privileges.Privileges;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Chat_Privileges extends Chat {
	private static final String FRIENDLY_NAME = "Privileges - Chat";
	private static final String PLUGIN_NAME = "Privileges";
	private static final String CHAT_PREFIX_KEY = "prefix";
	private static final String CHAT_SUFFIX_KEY = "suffix";
	
	private Privileges privs;
	private final Plugin plugin;
	
	public Chat_Privileges(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
		// Load service in case it was loaded before
		if (this.privs == null) {
			final Plugin privsPlugin = plugin.getServer().getPluginManager().getPlugin(Chat_Privileges.PLUGIN_NAME);
			if (privsPlugin != null && privsPlugin.isEnabled()) {
				privs = (Privileges) privsPlugin;
				plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), Chat_Privileges.FRIENDLY_NAME));
			}
		}
	}
	
	@Override
	public String getName() {
		return Chat_Privileges.FRIENDLY_NAME;
	}
	
	@Override
	public boolean isEnabled() {
		return this.privs != null && this.privs.isEnabled();
	}
	
	private String getPlayerOrGroupInfoString(final String world, final String player, final String key) {
		String value = this.getPlayerInfoString(world, player, key, null);
		if (value != null) return value;
		
		value = this.getGroupInfoString(world, this.getPrimaryGroup(world, player), key, null);
		if (value != null) return value;
		
		return null;
	}
	
	private void worldCheck(final String world) {
		if (world != null && !world.isEmpty()) {
			throw new UnsupportedOperationException("Privileges does not support multiple worlds for player/group metadata.");
		}
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return this.getPlayerOrGroupInfoString(world, player, Chat_Privileges.CHAT_PREFIX_KEY);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		this.setPlayerInfoString(world, player, Chat_Privileges.CHAT_PREFIX_KEY, prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return this.getPlayerOrGroupInfoString(world, player, Chat_Privileges.CHAT_SUFFIX_KEY);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		this.setPlayerInfoString(world, player, Chat_Privileges.CHAT_SUFFIX_KEY, suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return this.getGroupInfoString(world, group, Chat_Privileges.CHAT_PREFIX_KEY, null);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		this.setGroupInfoString(world, group, Chat_Privileges.CHAT_PREFIX_KEY, prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return this.getGroupInfoString(world, group, Chat_Privileges.CHAT_SUFFIX_KEY, null);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		this.setGroupInfoString(world, group, Chat_Privileges.CHAT_SUFFIX_KEY, suffix);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		return this.privs.getUserNode(player).getInt(node, defaultValue);
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int value) {
		this.worldCheck(world);
		this.privs.getUserNode(player).set(node, value);
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		return this.privs.getGroupNode(group).getInt(node, defaultValue);
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int value) {
		this.worldCheck(world);
		this.privs.getGroupNode(group).set(node, value);
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		return this.privs.getUserNode(player).getDouble(node, defaultValue);
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double value) {
		this.worldCheck(world);
		this.privs.getUserNode(player).set(node, value);
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		return this.privs.getGroupNode(group).getDouble(node, defaultValue);
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double value) {
		this.worldCheck(world);
		this.privs.getGroupNode(group).set(node, value);
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		return this.privs.getUserNode(player).getBoolean(node, defaultValue);
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean value) {
		this.worldCheck(world);
		this.privs.getUserNode(player).set(node, value);
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		return this.privs.getGroupNode(group).getBoolean(node, defaultValue);
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean value) {
		this.worldCheck(world);
		this.privs.getGroupNode(group).set(node, value);
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		return this.privs.getUserNode(player).getString(node, defaultValue);
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		this.worldCheck(world);
		this.privs.getUserNode(player).set(node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		return this.privs.getGroupNode(group).getString(node, defaultValue);
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		this.worldCheck(world);
		this.privs.getGroupNode(group).set(node, value);
	}
	
	public class PermissionServerListener implements Listener {
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Chat_Privileges.this.privs == null) {
				final Plugin permChat = event.getPlugin();
				if (Chat_Privileges.PLUGIN_NAME.equals(permChat.getDescription().getName())) {
					if (permChat.isEnabled()) {
						Chat_Privileges.this.privs = (Privileges) permChat;
						Chat_Privileges.this.plugin.getLogger().info(String.format("[Chat] %s hooked.", Chat_Privileges.FRIENDLY_NAME));
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Chat_Privileges.this.privs != null) {
				if (Chat_Privileges.PLUGIN_NAME.equals(event.getPlugin().getDescription().getName())) {
					Chat_Privileges.this.privs = null;
					Chat_Privileges.this.plugin.getLogger().info(String.format("[Chat] %s un-hooked.", Chat_Privileges.FRIENDLY_NAME));
				}
			}
		}
	}
}
