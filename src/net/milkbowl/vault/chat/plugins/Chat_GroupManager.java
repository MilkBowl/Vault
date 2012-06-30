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

    public Chat_GroupManager(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (groupManager == null) {
            Plugin chat = plugin.getServer().getPluginManager().getPlugin("GroupManager");
            if (chat != null) {
                if (chat.isEnabled()) {
                    groupManager = (GroupManager) chat;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
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
                        log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), chat.name));
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (chat.groupManager != null) {
                if (event.getPlugin().getDescription().getName().equals("GroupManager")) {
                    chat.groupManager = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), chat.name));
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
    public int getPlayerInfoInteger(String worldName, String playerName, String node, int defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Integer val = handler.getUserPermissionInteger(playerName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoInteger(String worldName, String playerName, String node, int value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public int getGroupInfoInteger(String worldName, String groupName, String node, int defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Integer val = handler.getGroupPermissionInteger(groupName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoInteger(String worldName, String groupName, String node, int value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String worldName, String playerName, String node, double defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Double val = handler.getUserPermissionDouble(playerName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoDouble(String worldName, String playerName, String node, double value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public double getGroupInfoDouble(String worldName, String groupName, String node, double defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Double val = handler.getGroupPermissionDouble(groupName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoDouble(String worldName, String groupName, String node, double value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String worldName, String playerName, String node, boolean defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Boolean val = handler.getUserPermissionBoolean(playerName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoBoolean(String worldName, String playerName, String node, boolean value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String worldName, String groupName, String node, boolean defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        Boolean val = handler.getGroupPermissionBoolean(groupName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoBoolean(String worldName, String groupName, String node, boolean value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public String getPlayerInfoString(String worldName, String playerName, String node, String defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        String val = handler.getUserPermissionString(playerName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoString(String worldName, String playerName, String node, String value) {
        setPlayerValue(worldName, playerName, node, value);
    }

    @Override
    public String getGroupInfoString(String worldName, String groupName, String node, String defaultValue) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getDefaultWorld().getPermissionsHandler();
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return defaultValue;
        }
        String val = handler.getGroupPermissionString(groupName, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoString(String worldName, String groupName, String node, String value) {
        setGroupValue(worldName, groupName, node, value);
    }

    @Override
    public String getPlayerPrefix(String worldName, String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return "";
        }
        return handler.getUserPrefix(playerName);
    }

    @Override
    public String getPlayerSuffix(String worldName, String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        if (handler == null) {
            return "";
        }
        return handler.getUserSuffix(playerName);
    }

    @Override
    public void setPlayerSuffix(String worldName, String player, String suffix) {
        setPlayerInfoString(worldName, player, "suffix", suffix);
    }

    @Override
    public void setPlayerPrefix(String worldName, String player, String prefix) {
        setPlayerInfoString(worldName, player, "prefix", prefix);
    }

    @Override
    public String getGroupPrefix(String worldName, String group) {
        return getGroupInfoString(worldName, group, "prefix", "");
    }

    @Override
    public void setGroupPrefix(String worldName, String group, String prefix) {
        setGroupInfoString(worldName, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String worldName, String group) {
        return getGroupInfoString(worldName, group, "suffix", "");
    }

    @Override
    public void setGroupSuffix(String worldName, String group, String suffix) {
        setGroupInfoString(worldName, group, "suffix", suffix);
    }

    @Override
    public String getPrimaryGroup(String worldName, String playerName) {
        AnjoPermissionsHandler handler;
        if (worldName == null) {
            handler = groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
        } else {
            handler = groupManager.getWorldsHolder().getWorldPermissions(worldName);
        }
        return handler.getGroup(playerName);
    }

    private void setPlayerValue(String worldName, String playerName, String node, Object value) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
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

    private void setGroupValue(String worldName, String groupName, String node, Object value) {
        OverloadedWorldHolder owh;
        if (worldName == null) {
            owh = groupManager.getWorldsHolder().getDefaultWorld();
        } else {
            owh = groupManager.getWorldsHolder().getWorldData(worldName);
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
