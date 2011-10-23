package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import net.milkbowl.vault.chat.Chat;

public class Chat_Permissions3 extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");

	private String name = "Permissions 3 (Yeti) - Chat";
	private PermissionHandler perms;
	private Plugin plugin = null;
	private PluginManager pluginManager = null;
	private Permissions chat = null;
	private PermissionServerListener permissionServerListener = null;

	public Chat_Permissions3(Plugin plugin) {
		this.plugin = plugin;
		pluginManager = this.plugin.getServer().getPluginManager();

		permissionServerListener = new PermissionServerListener();

		this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
		this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

		// Load Plugin in case it was loaded before
		if (chat == null) {
			Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
			if (perms != null) {
				if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
					chat = (Permissions) perms;
					this.perms = chat.getHandler();
					log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
				}
			}
		}
	}
	private class PermissionServerListener extends ServerListener {
		public void onPluginEnable(PluginEnableEvent event) {
			if (chat == null) {
				Plugin perms = event.getPlugin();
				if(perms.getDescription().getName().equals("Permissions") && perms.getDescription().getVersion().startsWith("3")) {
					if (perms.isEnabled()) {
						chat = (Permissions) perms;
						log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
					}
				}
			}
		}

		public void onPluginDisable(PluginDisableEvent event) {
			if (chat != null) {
				if (event.getPlugin().getDescription().getName().equals("Permissions")) {
					chat = null;
					log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
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
		if (chat == null) {
			return false;
		} else {
			return chat.isEnabled();
		}
	}
	@Override
	public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
		Integer i = this.perms.getInfoInteger(world, playerName, node, false);
		return (i == null) ? defaultValue : i;
	}

	@Override
	public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
		Double d = this.perms.getInfoDouble(world, playerName, node, false);
		return (d == null) ? defaultValue : d;
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
		Boolean b = this.perms.getInfoBoolean(world, playerName, node, false);
		return (b == null) ? defaultValue : b;
	}

	@Override
	public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
		String s = this.perms.getInfoString(world, playerName, node, false);
		return (s == null) ? defaultValue : s;
	}

	@Override
	public String getPlayerPrefix(String world, String playerName) {
		return this.perms.getUserPrefix(world, playerName);
	}

	@Override
	public String getPlayerSuffix(String world, String playerName) {
		return this.perms.getUserSuffix(world, playerName);
	}

	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
		this.perms.addUserInfo(world, player, "suffix", suffix);
	}

	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
		this.perms.addUserInfo(world, player, "prefix", prefix);
	}

    public void setPlayerInfo(String world, String playerName, String node, Object value) {
        this.perms.addUserInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        Integer i = this.perms.getInfoInteger(world, groupName, node, true);
        return (i == null) ? defaultValue : i;
    }

    
    public void setGroupInfo(String world, String groupName, String node, Object value) {
        this.perms.addGroupInfo(world, groupName, node, value);
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        Double d = this.perms.getInfoDouble(world, groupName, node, true);
        return (d == null) ? defaultValue : d;
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        Boolean b = this.perms.getInfoBoolean(world, groupName, node, true);
        return (b == null) ? defaultValue : b;
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        String s = this.perms.getInfoString(world, groupName, node, true);
        return (s == null) ? defaultValue : s;
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        setGroupInfo(world, groupName, node, value);
    }
    
	@Override
	public String getGroupPrefix(String world, String group) {
		try {
			return perms.safeGetGroup(world, group).getPrefix();
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
		this.perms.addGroupInfo(world, group, "prefix", prefix);
	}

	@Override
	public String getGroupSuffix(String world, String group) {
		try {
			return perms.safeGetGroup(world, group).getSuffix();
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
		this.perms.addGroupInfo(world, group, "suffix", suffix);
	}
}
