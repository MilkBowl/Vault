package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.nijiko.permissions.Control;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Chat_Permissions2 extends Chat {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "Permissions 2 (Phoenix) - Chat";
    private Control perms;
    private Plugin plugin = null;
    private PluginManager pluginManager = null;
    private Permissions chat = null;
    private PermissionServerListener permissionServerListener = null;

    public Chat_Permissions2(Plugin plugin) {
        this.plugin = plugin;
        pluginManager = this.plugin.getServer().getPluginManager();

        permissionServerListener = new PermissionServerListener(this);

        this.pluginManager.registerEvent(Type.PLUGIN_ENABLE, permissionServerListener, Priority.Monitor, plugin);
        this.pluginManager.registerEvent(Type.PLUGIN_DISABLE, permissionServerListener, Priority.Monitor, plugin);

        // Load Plugin in case it was loaded before
        if (chat == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms != null) {
                if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("2")) {
                    chat = (Permissions) perms;
                    this.perms = (Control) chat.getHandler();
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    private class PermissionServerListener extends ServerListener {
        Chat_Permissions2 permission = null;

        public PermissionServerListener(Chat_Permissions2 permission) {
            this.permission = permission;
        }

        public void onPluginEnable(PluginEnableEvent event) {
            if (permission.chat == null) {
                Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");

                if (perms != null) {
                    if (perms.isEnabled()) {
                        permission.chat = (Permissions) perms;
                        log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), permission.name));
                    }
                }
            }
        }

        public void onPluginDisable(PluginDisableEvent event) {
            if (permission.chat != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions")) {
                    permission.chat = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), permission.name));
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
		int i = this.perms.getPermissionInteger(world, playerName, node);
        return (i == -1) ? defaultValue : i;
    }

    @Override
    public void setPlayerInfoInteger(String world, String playerName, String node, int value) {
        setPlayerInfo(world, playerName, node, value);
    }

	private void setPlayerInfo(String world, String playerName, String node, Object value) {
		this.perms.addUserInfo(world, playerName, node, value);
	}

	public void setGroupInfo(String world, String groupName, String node, Object value) {
        this.perms.addGroupInfo(world, groupName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String groupName, String node, int defaultValue) {
        int i = this.perms.getGroupPermissionInteger(world, groupName, node);
        return (i == -1) ? defaultValue : i;
    }

    @Override
    public void setGroupInfoInteger(String world, String groupName, String node, int value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        double d = this.perms.getPermissionDouble(world, playerName, node);
        return (d == -1) ? defaultValue : d;
    }

    @Override
    public void setPlayerInfoDouble(String world, String playerName, String node, double value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String groupName, String node, double defaultValue) {
        double d = this.perms.getGroupPermissionDouble(world, groupName, node);
        return (d == -1) ? defaultValue : d;
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        // Warning does not support default value
        return this.perms.getPermissionBoolean(world, playerName, node);
    }

    @Override
    public void setPlayerInfoBoolean(String world, String playerName, String node, boolean value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        return this.perms.getGroupPermissionBoolean(world, groupName, node);
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        String s = this.perms.getPermissionString(world, playerName, node);
        return (s == "" || s == null) ? defaultValue : s;
    }

    @Override
    public void setPlayerInfoString(String world, String playerName, String node, String value) {
        setPlayerInfo(world, playerName, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        String s = this.perms.getGroupPermissionString(world, groupName, node);
        return (s == "" || s == null) ? defaultValue : s;
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        setGroupInfo(world, groupName, node, value);
    }
    
    @Override
    public String getPlayerPrefix(String world, String playerName) {
        return this.perms.getPermissionString(world, playerName, "prefix");
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        return this.perms.getPermissionString(world, playerName, "suffix");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        setPlayerInfo(world, player, "suffix", suffix);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        setPlayerInfo(world, player, "prefix", prefix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return perms.getGroupPrefix(world, group);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        setGroupInfo(world, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return perms.getGroupSuffix(world, group);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        setGroupInfo(world, group, "suffix", suffix);
    }
}
