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

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;
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

public class Chat_bPermissions2 extends Chat {
	private final Logger log;
	private final Plugin plugin;
	private boolean hooked;
	
	public Chat_bPermissions2(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
		
		// Load Plugin in case it was loaded before
		if (!this.hooked) {
			final Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				this.hooked = true;
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions2"));
			}
		}
	}
	
	public class PermissionServerListener implements Listener {
		final Chat_bPermissions2 chat;
		
		public PermissionServerListener(final Chat_bPermissions2 chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (!Chat_bPermissions2.this.hooked) {
				final Plugin chat = event.getPlugin();
				if (chat.getDescription().getName().equals("bPermissions")) {
					Chat_bPermissions2.this.hooked = true;
					Chat_bPermissions2.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (Chat_bPermissions2.this.hooked) {
				if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
					Chat_bPermissions2.this.hooked = false;
					Chat_bPermissions2.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_bPermissions2.this.plugin.getDescription().getName(), "bPermissions2"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		String name = "bInfo";
		return name;
	}
	
	@Override
	public boolean isEnabled() {
		return this.hooked;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return ApiLayer.getValue(world, CalculableType.USER, player, "prefix");
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		ApiLayer.setValue(world, CalculableType.USER, player, "prefix", prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return ApiLayer.getValue(world, CalculableType.USER, player, "suffix");
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		ApiLayer.setValue(world, CalculableType.USER, player, "suffix", suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return ApiLayer.getValue(world, CalculableType.GROUP, group, "prefix");
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		ApiLayer.setValue(world, CalculableType.GROUP, group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return ApiLayer.getValue(world, CalculableType.GROUP, group, "suffix");
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		ApiLayer.setValue(world, CalculableType.GROUP, group, "suffix", suffix);
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
		ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
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
		ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
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
		ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
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
		ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
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
		ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
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
		ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final String val = ApiLayer.getValue(world, CalculableType.USER, player, node);
		return (val == null || val.equals("BLANKWORLD") || val.equals("")) ? defaultValue : val;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		ApiLayer.setValue(world, CalculableType.USER, player, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		final String val = ApiLayer.getValue(world, CalculableType.GROUP, group, node);
		return (val == null || val.equals("BLANKWORLD") || val.equals("")) ? defaultValue : val;
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		ApiLayer.setValue(world, CalculableType.GROUP, group, node, value);
	}
}