package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

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

public class Chat_GroupManager extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");

	private final String name = "GroupManager - Chat";
	private Plugin plugin = null;
	private GroupManager groupManager;


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
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return defaultValue;
        }
        Integer val = user.getVariables().getVarInteger(node);
		return val != null ? val : defaultValue;
	}

	@Override
	public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
	    setPlayerValue(world, playerName, node, value);
	}

	@Override
	public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
		Group group = owh.getGroup(groupName);
		if (group == null) {
		    return defaultValue;
		}
		Integer val = group.getVariables().getVarInteger(node);
		return val != null ? val : defaultValue;
	}

	@Override
	public void setGroupInfoInteger(String world, String groupName, String node, int value) {
		setGroupValue(world, groupName, node, value);
	}

	@Override
	public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return defaultValue;
        }
        Double val = user.getVariables().getVarDouble(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
	    setPlayerValue(world, playerName, node, value);
	}

    @Override
	public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        Group group = owh.getGroup(groupName);
        if (group == null) {
            return defaultValue;
        }
        Double val = group.getVariables().getVarDouble(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setGroupInfoDouble(String world, String groupName, String node, double value) {
	    setGroupValue(world, groupName, node, value);
	}

	@Override
	public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return defaultValue;
        }
        Boolean val = user.getVariables().getVarBoolean(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
		setPlayerValue(world, playerName, node, value);
	}

	@Override
	public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        Group group = owh.getGroup(groupName);
        if (group == null) {
            return defaultValue;
        }
        Boolean val = group.getVariables().getVarBoolean(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
	    setGroupValue(world, groupName, node, value);
	}

	@Override
	public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return defaultValue;
        }
        String val = user.getVariables().getVarString(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setPlayerInfoString(String world, String playerName, String node, String value) {
	    setPlayerValue(world, playerName, node, value);
	}

	@Override
	public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return defaultValue;
        }
        Group group = owh.getGroup(groupName);
        if (group == null) {
            return defaultValue;
        }
        String val = group.getVariables().getVarString(node);
        return val != null ? val : defaultValue;
	}

	@Override
	public void setGroupInfoString(String world, String groupName, String node, String value) {
	    setGroupValue(world, groupName, node, value);
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
    
    private void setPlayerValue(String world, String playerName, String node, Object value) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return;
        }
        User user = owh.getUser(playerName);
        if (user == null) {
            return;
        }
        user.getVariables().addVar(node, value);
    }
    
    private void setGroupValue(String world, String groupName, String node, Object value) {
        OverloadedWorldHolder owh;
        if (world == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(world);
        }
        if (owh == null) {
            return;
        }
        Group group = owh.getGroup(groupName);
        if (group == null) {
            return;
        }
        group.getVariables().addVar(node, value);
    }
}
