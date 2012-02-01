package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.info.InfoReader;

public class Chat_bPermissions extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");
	private final String name = "bInfo";
	private Vault plugin = null;
	InfoReader chat;

	public Chat_bPermissions(Vault plugin, Permission perms) {
		super(perms);
		this.plugin = plugin;
		
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (chat == null) {
			Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
			if (p != null) {
				chat = Permissions.getInfoReader();
				log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions"));
			}
		}
	}

	public class PermissionServerListener implements Listener {
		Chat_bPermissions chat = null;

		public PermissionServerListener(Chat_bPermissions chat) {
			this.chat = chat;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (this.chat.chat == null) {
				Plugin chat = plugin.getServer().getPluginManager().getPlugin("bPermissions");
				if (chat != null) {
				    this.chat.chat = Permissions.getInfoReader();
					log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions"));
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (this.chat.chat != null) {
				if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
					this.chat.chat = null;
					log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "bPermissions"));
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
		return chat != null;
	}

	@Override
	public String getPlayerPrefix(String world, String player) {
	    return chat.getPrefix(player, world);
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public String getPlayerSuffix(String world, String player) {
	    return chat.getSuffix(player, world);
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public String getGroupPrefix(String world, String group) {
	    return chat.getGroupPrefix(group, world);
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public String getGroupSuffix(String world, String group) {
	    return chat.getGroupSuffix(group, world);
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null)
			return defaultValue;
		try {
			int i = Integer.valueOf(s);
			return i;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
		String s = getGroupInfoString(world, group, node, null);
		if (s == null)
			return defaultValue;
		try {
			int i = Integer.valueOf(s);
			return i;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null)
			return defaultValue;
		try {
			double d = Double.valueOf(s);
			return d;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
		String s = getGroupInfoString(world, group, node, null);
		if (s == null)
			return defaultValue;
		try {
			double d = Double.valueOf(s);
			return d;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
		String s = getPlayerInfoString(world, player, node, null);
		if (s == null) {
			return defaultValue;
		} else {
            Boolean val = Boolean.valueOf(s);
            return val != null ? val : defaultValue;
		}
	}

	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
		String s = getGroupInfoString(world, group, node, null);
		if (s == null)
			return defaultValue;
		else {
            Boolean val = Boolean.valueOf(s);
            return val != null ? val : defaultValue;
		}
	}

	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
		String val = chat.getValue(player, world, node);
		return (val == null || val == "BLANKWORLD") ? defaultValue : val;
	}

	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}

	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue) {
		String val = chat.getGroupValue(group, world, node);
		return (val == null || val == "BLANKWORLD") ? defaultValue : val;
	}

	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
		throw new UnsupportedOperationException("bPermissions does not support altering info nodes");
	}
}
