package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class Chat_GroupManager extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final String name = "GroupManager - Chat";
	private Plugin plugin = null;
	private GroupManager groupManager;
	private AnjoPermissionsHandler perms;


	public Chat_GroupManager(Plugin plugin, Permission permissions) {
		super(permissions);
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

		// Load Plugin in case it was loaded before
		if (groupManager == null) {
			Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if (perms != null) {
				if (perms.isEnabled()) {
					groupManager = (GroupManager) perms;
					this.perms = groupManager.getPermissionHandler();
					log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
				}
			}
		}
	}

	public class PermissionServerListener implements Listener {
		Chat_GroupManager chat = null;

		public PermissionServerListener(Chat_GroupManager chat) {
			this.chat = chat;
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginEnable(PluginEnableEvent event) {
			if (chat.groupManager == null) {
				Plugin perms = plugin.getServer().getPluginManager().getPlugin("GroupManager");

				if (perms != null) {
					if (perms.isEnabled()) {
						chat.groupManager = (GroupManager) perms;
						chat.perms = groupManager.getPermissionHandler();
						log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), chat.name));
					}
				}
			}
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPluginDisable(PluginDisableEvent event) {
			if (chat.groupManager != null) {
				if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
					chat.groupManager = null;
					chat.perms = null;
					log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), chat.name));
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
		if (groupManager == null) {
			return false;
		} else {
			return groupManager.isEnabled();
		}
	}
	@Override
	public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
		return perms.getPermissionInteger(playerName, node);
	}

	@Override
	public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
		return perms.getGroupPermissionInteger(groupName, node);
	}

	@Override
	public void setGroupInfoInteger(String world, String groupName, String node, int value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
		return perms.getPermissionDouble(playerName, node);
	}

	@Override
	public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
		return perms.getGroupPermissionDouble(groupName, node);
	}

	@Override
	public void setGroupInfoDouble(String world, String groupName, String node, double value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
		return perms.getPermissionBoolean(playerName, node);
	}

	@Override
	public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
		return perms.getGroupPermissionBoolean(groupName, node);
	}

	@Override
	public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
		return perms.getPermissionString(playerName, node);
	}

	@Override
	public void setPlayerInfoString(String world, String playerName, String node, String value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
		return perms.getGroupPermissionString(groupName, node);
	}

	@Override
	public void setGroupInfoString(String world, String groupName, String node, String value) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}
	@Override
	public String getPlayerPrefix(String world, String playerName) {
	    return getPlayerInfoString(world, playerName, "prefix", "");
	}

	@Override
	public String getPlayerSuffix(String world, String playerName) {
	    return getPlayerInfoString(world, playerName, "suffix", "");
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public String getGroupPrefix(String world, String group) {
		return perms.getGroupPrefix(group);
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		return perms.getGroupSuffix(group);
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		throw new UnsupportedOperationException(getName() + " cannot modify permissions.");
	}
	
    public String getPrimaryGroup(String world, String playerName) {
        return perms.getGroup(playerName);
    }
}
