package net.milkbowl.vault.chat.plugins;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class Chat_DroxPerms extends Chat {
	
	private final Logger log;
	private final String name = "DroxPerms";
	private final Plugin plugin;
	private DroxPermsAPI API;
	
	public Chat_DroxPerms(final Plugin plugin, final Permission perms) {
		super(perms);
		this.plugin = plugin;
		log = plugin.getLogger();
		
		// Load Plugin in case it was loaded before
		if (this.API == null) {
			final DroxPerms p = (DroxPerms) plugin.getServer().getPluginManager().getPlugin("DroxPerms");
			if (p != null) {
				this.API = p.getAPI();
				this.log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), this.name));
			}
		}
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
	}
	
	public class PermissionServerListener implements Listener {
		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(final PluginEnableEvent event) {
			if (Chat_DroxPerms.this.API == null) {
				final Plugin permPlugin = event.getPlugin();
				if (permPlugin.getDescription().getName().equals("DroxPerms")) {
					Chat_DroxPerms.this.API = ((DroxPerms) permPlugin).getAPI();
					Chat_DroxPerms.this.log.info(String.format("[%s][Chat] %s hooked.", Chat_DroxPerms.this.plugin.getDescription().getName(), Chat_DroxPerms.this.name));
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
		return true;
	}
	
	@Override
	public String getPlayerPrefix(final String world, final String player) {
		String prefix = this.API.getPlayerInfo(player, "prefix");
		if (prefix == null) {
			final String prigroup = this.API.getPlayerGroup(player);
			prefix = this.API.getGroupInfo(prigroup, "prefix");
		}
		return prefix;
	}
	
	@Override
	public void setPlayerPrefix(final String world, final String player, final String prefix) {
		this.API.setPlayerInfo(player, "prefix", prefix);
	}
	
	@Override
	public String getPlayerSuffix(final String world, final String player) {
		return this.API.getPlayerInfo(player, "suffix");
	}
	
	@Override
	public void setPlayerSuffix(final String world, final String player, final String suffix) {
		this.API.setPlayerInfo(player, "suffix", suffix);
	}
	
	@Override
	public String getGroupPrefix(final String world, final String group) {
		return this.API.getGroupInfo(group, "prefix");
	}
	
	@Override
	public void setGroupPrefix(final String world, final String group, final String prefix) {
		this.API.setGroupInfo(group, "prefix", prefix);
	}
	
	@Override
	public String getGroupSuffix(final String world, final String group) {
		return this.API.getGroupInfo(group, "suffix");
	}
	
	@Override
	public void setGroupSuffix(final String world, final String group, final String suffix) {
		this.API.setGroupInfo(group, "suffix", suffix);
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
		this.API.setPlayerInfo(player, node, String.valueOf(value));
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
		this.API.setGroupInfo(group, node, String.valueOf(value));
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
		this.API.setPlayerInfo(player, node, String.valueOf(value));
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
		this.API.setGroupInfo(group, node, String.valueOf(value));
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
		this.API.setPlayerInfo(player, node, String.valueOf(value));
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
		this.API.setGroupInfo(group, node, String.valueOf(value));
	}
	
	@Override
	public String getPlayerInfoString(final String world, final String player, final String node, final String defaultValue) {
		final String val = this.API.getPlayerInfo(player, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setPlayerInfoString(final String world, final String player, final String node, final String value) {
		this.API.setPlayerInfo(player, node, value);
	}
	
	@Override
	public String getGroupInfoString(final String world, final String group, final String node, final String defaultValue) {
		final String val = this.API.getGroupInfo(group, node);
		return val != null ? val : defaultValue;
	}
	
	@Override
	public void setGroupInfoString(final String world, final String group, final String node, final String value) {
		this.API.setGroupInfo(group, node, value);
	}
	
}