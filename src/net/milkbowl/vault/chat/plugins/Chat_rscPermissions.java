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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import ru.simsonic.rscPermissions.MainPluginClass;

import java.util.logging.Logger;

public class Chat_rscPermissions extends Chat {
	
	private final Logger log;
	private final Plugin vault;
	private ru.simsonic.rscPermissions.MainPluginClass rscp;
	private ru.simsonic.rscPermissions.rscpAPI rscpAPI;
	
	public Chat_rscPermissions(final Plugin plugin, final Permission perm) {
		super(perm);
		vault = plugin;
		log = plugin.getLogger();
		Bukkit.getServer().getPluginManager().registerEvents(new ChatServerListener(this), this.vault);
		if (this.rscp == null) {
			final Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
			if (perms != null && perms.isEnabled()) {
				rscp = (MainPluginClass) perms;
				this.rscpAPI = this.rscp.API;
				plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "rscPermissions"));
			}
		}
	}
	
	private class ChatServerListener implements Listener {
		
		private final Chat_rscPermissions bridge;
		
		public ChatServerListener(final Chat_rscPermissions bridge) {
			this.bridge = bridge;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		private void onPluginEnable(final PluginEnableEvent event) {
			if (this.bridge.rscp == null) {
				final Plugin plugin = event.getPlugin();
				if (plugin.getDescription().getName().equals("rscPermissions")) {
					this.bridge.rscp = (MainPluginClass) plugin;
					this.bridge.rscpAPI = this.bridge.rscp.API;
					Chat_rscPermissions.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_rscPermissions.this.vault.getDescription().getName(), "rscPermissions"));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(final PluginDisableEvent event) {
			if (this.bridge.rscpAPI != null) {
				if (event.getPlugin().getDescription().getName().equals(this.bridge.rscpAPI.getName())) {
					this.bridge.rscpAPI = null;
					this.bridge.rscp = null;
					Chat_rscPermissions.this.log.info(String.format("[%s][Chat] %s un-hooked.", Chat_rscPermissions.this.vault.getDescription().getName(), "rscPermissions"));
				}
			}
		}
	}
	
	@Override
	public String getName() {
		return "rscPermissions";
	}
	
	@Override
	public boolean isEnabled() {
		return this.rscpAPI != null && this.rscpAPI.isEnabled();
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		return this.rscpAPI.getPlayerPrefix(world, player);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return this.rscpAPI.getPlayerSuffix(world, player);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return this.rscpAPI.getGroupPrefix(world, group);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return this.rscpAPI.getGroupSuffix(world, group);
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		this.rscpAPI.setPlayerPrefix(world, player, prefix);
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		this.rscpAPI.setPlayerSuffix(world, player, suffix);
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		this.rscpAPI.setGroupPrefix(world, group, prefix);
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		this.rscpAPI.setGroupSuffix(world, group, suffix);
	}
	
	@Override
	public int getPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setPlayerInfoInteger(final String world, final String player, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public int getGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setGroupInfoInteger(final String world, final String group, final String node, final int defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public double getPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setPlayerInfoDouble(final String world, final String player, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public double getGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setGroupInfoDouble(final String world, final String group, final String node, final double defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public boolean getPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setPlayerInfoBoolean(final String world, final String player, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public boolean getGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setGroupInfoBoolean(final String world, final String group, final String node, final boolean defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		throw new UnsupportedOperationException("rscPermissions does not support info nodes");
	}
}
