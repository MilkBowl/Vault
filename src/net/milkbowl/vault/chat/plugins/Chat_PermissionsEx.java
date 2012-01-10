package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

public class Chat_PermissionsEx extends Chat {
	private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "PermissionsEx_Chat";
    
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private PermissionsEx chat = null;
    private PermissionServerListener permissionServerListener = null;

    public Chat_PermissionsEx(Plugin plugin, Permission permissions) {
    	super(permissions);
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (chat == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
            if (perms != null) {
                if (perms.isEnabled()) {
                    chat = (PermissionsEx) perms;
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }
    
    private class PermissionServerListener extends ServerListener {
        Chat_PermissionsEx chat = null;

        public PermissionServerListener(Chat_PermissionsEx chat) {
            this.chat = chat;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (chat.chat == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("PermissionsEx");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        chat.chat = (PermissionsEx) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), chat.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (chat.chat != null) {
                if (event.getPlugin().getDescription().getName().equals("PermissionsEx")) {
                    chat.chat = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), chat.name));
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
		if (chat == null)
			return false;
		else
			return chat.isEnabled();
	}
	
    @Override
    public int getPlayerInfoInteger(String world, String playerName, String node, int defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionInteger(node, world, defaultValue);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionDouble(node, world, defaultValue);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOptionBoolean(node, world, defaultValue);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        return PermissionsEx.getPermissionManager().getUser(playerName).getOption(node, world, defaultValue);
    }
    
    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            user.setOption(node, String.valueOf(value), world);
        }
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionInteger(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionDouble(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOptionBoolean(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, String.valueOf(value));
        }
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return defaultValue;
        } else {
            return group.getOption(node, world, defaultValue);
        }
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        PermissionGroup group = PermissionsEx.getPermissionManager().getGroup(groupName);
        if (group == null) {
            return;
        } else {
            group.setOption(node, world, value);
        }
    }
    
    @Override
    public String getPlayerPrefix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getPrefix(world);
        } else {
            return null;
        }
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);
        if (user != null) {
            return user.getSuffix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setSuffix(suffix, world);
        }
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        if (user != null) {
            user.setPrefix(prefix, world);
        }
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getPrefix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setPrefix(prefix, world);
        }

    }

    @Override
    public String getGroupSuffix(String world, String group) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            return pGroup.getSuffix(world);
        } else {
            return null;
        }
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        PermissionGroup pGroup = PermissionsEx.getPermissionManager().getGroup(group);
        if (group != null) {
            pGroup.setSuffix(suffix, world);
        }
    }
}
