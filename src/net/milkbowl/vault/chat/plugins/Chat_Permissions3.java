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

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Chat_Permissions3 extends Chat {
    private static final Logger log = Logger.getLogger("Minecraft");

    private String name = "Permissions 3 (Yeti) - Chat";
    private PermissionHandler perms;
    private Plugin plugin = null;
    private Permissions chat = null;

    public Chat_Permissions3(Plugin plugin, Permission permissions) {
        super(permissions);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);

        // Load Plugin in case it was loaded before
        if (chat == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("Permissions");
            if (perms == null) {
                plugin.getServer().getPluginManager().getPlugin("vPerms");
                name = "vPerms - Chat";
            }
            if (perms != null) {
                if (perms.isEnabled() && perms.getDescription().getVersion().startsWith("3")) {
                    chat = (Permissions) perms;
                    this.perms = chat.getHandler();
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    public class PermissionServerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (chat == null) {
                Plugin permChat = event.getPlugin();
                if((permChat.getDescription().getName().equals("Permissions") || permChat.getDescription().getName().equals("vPerms")) && permChat.getDescription().getVersion().startsWith("3")) {
                    if (permChat.isEnabled()) {
                        chat = (Permissions) permChat;
                        perms = chat.getHandler();
                        log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (chat != null) {
                if (event.getPlugin().getDescription().getName().equals("Permissions") || event.getPlugin().getDescription().getName().equals("vPerms")) {
                    chat = null;
                    perms = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), name));
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
        Integer i = this.perms.getPermissionInteger(world, playerName, node);
        return (i == null) ? defaultValue : i;
    }

    @Override
    public double getPlayerInfoDouble(String world, String playerName, String node, double defaultValue) {
        Double d = this.perms.getPermissionDouble(world, playerName, node);
        return (d == null) ? defaultValue : d;
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String playerName, String node, boolean defaultValue) {
        Boolean b = this.perms.getPermissionBoolean(world, playerName, node);
        return (b == null) ? defaultValue : b;
    }

    @Override
    public String getPlayerInfoString(String world, String playerName, String node, String defaultValue) {
        String s = this.perms.getPermissionString(world, playerName, node);
        return (s == null) ? defaultValue : s;
    }

    @Override
    public String getPlayerPrefix(String world, String playerName) {
        return getPlayerInfoString(world, playerName, "prefix", null);
    }

    @Override
    public String getPlayerSuffix(String world, String playerName) {
        return getPlayerInfoString(world, playerName, "suffix", null);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        //this.perms.addUserInfo(world, player, "prefix", prefix);
    }

    public void setPlayerInfo(String world, String playerName, String node, Object value) {
        //this.perms.addUserInfo(world, playerName, node, value);
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
        //Integer i = this.perms.getInfoInteger(world, groupName, node, true);
        //return (i == null) ? defaultValue : i;
        return defaultValue;
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
        //Double d = this.perms.getInfoDouble(world, groupName, node, true);
        //return (d == null) ? defaultValue : d;
        return defaultValue;
    }

    @Override
    public void setGroupInfoDouble(String world, String groupName, String node, double value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String groupName, String node, boolean defaultValue) {
        //Boolean b = this.perms.getInfoBoolean(world, groupName, node, true);
        //return (b == null) ? defaultValue : b;
        return defaultValue;
    }

    @Override
    public void setGroupInfoBoolean(String world, String groupName, String node, boolean value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String groupName, String node, String defaultValue) {
        //String s = this.perms.getInfoString(world, groupName, node, true);
        //return (s == null) ? defaultValue : s;
        return null;
    }

    @Override
    public void setGroupInfoString(String world, String groupName, String node, String value) {
        setGroupInfo(world, groupName, node, value);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        //try {
        //	return perms.safeGetGroup(world, group).getPrefix();
        //} catch(Exception e) {
        return null;
        //}
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        this.perms.addGroupInfo(world, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        /*
		try {
			return perms.safeGetGroup(world, group).getSuffix();
		} catch(Exception e) { */
        return null;
        //} 
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        this.perms.addGroupInfo(world, group, "suffix", suffix);
    }
}
