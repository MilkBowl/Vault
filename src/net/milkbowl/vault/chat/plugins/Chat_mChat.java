package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.D3GN.MiracleM4n.mChat.mChatAPI;
import net.milkbowl.vault.chat.Chat;

public class Chat_mChat extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "mChat";
	private Plugin plugin = null;
	private PluginManager pluginManager = null;
	private mChatAPI mChat = null;
	private PermissionServerListener permissionServerListener = null;
	
	public Chat_mChat(Plugin plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();
		
		permissionServerListener = new PermissionServerListener(this);

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (mChat == null) {
			Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
			if (chat != null) {
				mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
				log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
			}
		}
	}
	
	private class PermissionServerListener extends ServerListener {
		Chat_mChat chat = null;

		public PermissionServerListener(Chat_mChat chat) {
			this.chat = chat;
		}

		public void onPluginEnable(PluginEnableEvent event) {
			if (this.chat.mChat == null) {
				Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
				if (chat != null) {
					this.chat.mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
					log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
				}
			}
		}

		public void onPluginDisable(PluginDisableEvent event) {
			if (this.chat.mChat != null) {
				if (event.getPlugin().getDescription().getName().equals("mChat")) {
					this.chat.mChat = null;
					log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "mChat"));
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
		return mChat != null;
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
		Player p = Bukkit.getServer().getPlayer(player);
		if (p ==null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		return mChat.getPrefix(p);
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
		Player p = Bukkit.getServer().getPlayer(player);
		if (p ==null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		return mChat.getSuffix(p);
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null)
			return defaultValue;
		
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null)
			return defaultValue;
		
		try {
			return Double.valueOf(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node,double defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null)
			return defaultValue;
		
		try {
			return Boolean.valueOf(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		Player p = Bukkit.getServer().getPlayer(player);
		if (p ==null) {
			throw new UnsupportedOperationException("mChat does not support offline player prefixes");
		}
		String s = mChat.getInfo(p, node);
		return s == null ? defaultValue : s;
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw new UnsupportedOperationException("mChat does not support setting info nodes");
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		throw new UnsupportedOperationException("mChat does not support group info nodes");
	}
}
