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

import net.ae97.totalpermissions.PermissionManager;
import net.ae97.totalpermissions.TotalPermissions;
import net.ae97.totalpermissions.permission.PermissionBase;
import net.ae97.totalpermissions.permission.PermissionGroup;
import net.ae97.totalpermissions.permission.PermissionUser;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 * @version 1.0
 * @author Lord_Ralex
 */
public class Chat_TotalPermissions extends Chat {

    private final Plugin plugin;
    private TotalPermissions totalPermissions;
    private final String name = "TotalPermissions-Chat";

    public Chat_TotalPermissions(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        if (totalPermissions == null) {
            Plugin chat = plugin.getServer().getPluginManager().getPlugin("TotalPermissions");
            if (chat != null) {
                if (chat.isEnabled()) {
                    totalPermissions = (TotalPermissions) chat;
                    plugin.getLogger().info(String.format("[Chat] %s hooked.", name));
                }
            }
        }
    }

    public class PermissionServerListener implements Listener {

        Chat_TotalPermissions chat = null;

        public PermissionServerListener(Chat_TotalPermissions chat) {
            this.chat = chat;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (chat.totalPermissions == null) {
                Plugin perms = event.getPlugin();

                if (perms != null) {
                    if (perms.getDescription().getName().equals("TotalPermissions")) {
                        if (perms.isEnabled()) {
                            chat.totalPermissions = (TotalPermissions) perms;
                            plugin.getLogger().info(String.format("[Chat] %s hooked.", chat.getName()));
                        }
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (chat.totalPermissions != null) {
                if (event.getPlugin().getDescription().getName().equals("TotalPermissions")) {
                    chat.totalPermissions = null;
                    plugin.getLogger().info(String.format("[Chat] %s un-hooked.", chat.name));
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
        return (totalPermissions == null ? false : totalPermissions.isEnabled());
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return this.getPlayerInfoString(world, player, "prefix", null);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        this.setPlayerInfoString(world, player, "prefix", prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return this.getPlayerInfoString(world, player, "suffix", null);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        this.setPlayerInfoString(world, player, "suffix", suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return this.getGroupInfoString(world, group, "prefix", null);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        this.setGroupInfoString(world, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return this.getGroupInfoString(world, group, "suffix", null);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        this.setGroupInfoString(world, group, "suffix", suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        Object pre = getPlayerInfo(world, player, node);
        if (pre instanceof Integer) {
            return (Integer) pre;
        }
        return defaultValue;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        Object pre = getGroupInfo(world, group, node);
        if (pre instanceof Integer) {
            return (Integer) pre;
        }
        return defaultValue;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        Object pre = getPlayerInfo(world, player, node);
        if (pre instanceof Double) {
            return (Double) pre;
        }
        return defaultValue;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        Object pre = getGroupInfo(world, group, node);
        if (pre instanceof Double) {
            return (Double) pre;
        }
        return defaultValue;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        Object pre = getPlayerInfo(world, player, node);
        if (pre instanceof Boolean) {
            return (Boolean) pre;
        }
        return defaultValue;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        Object pre = getGroupInfo(world, group, node);
        if (pre instanceof Boolean) {
            return (Boolean) pre;
        }
        return defaultValue;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        setGroupInfo(world, group, node, value);
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        Object pre = getPlayerInfo(world, player, node);
        if (pre instanceof String) {
            return (String) pre;
        }
        return defaultValue;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        setPlayerInfo(world, player, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        Object pre = getGroupInfo(world, group, node);
        if (pre instanceof String) {
            return (String) pre;
        }
        return defaultValue;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        setGroupInfo(world, group, node, value);
    }

    private PermissionUser getUser(String name) {
        PermissionManager manager = totalPermissions.getManager();
        PermissionUser user = manager.getUser(name);
        return user;
    }

    private PermissionGroup getGroup(String name) {
        PermissionManager manager = totalPermissions.getManager();
        PermissionGroup group = manager.getGroup(name);
        return group;
    }

    private void setPlayerInfo(String world, String player, String node, Object value) {
        PermissionBase base = getUser(player);
        base.setOption(node, value, world);
    }

    private void setGroupInfo(String world, String group, String node, Object value) {
        PermissionBase base = getGroup(group);
        base.setOption(node, value, world);
    }

    private Object getPlayerInfo(String world, String player, String node) {
        PermissionBase base = getUser(player);
        return base.getOption(node);
    }

    private Object getGroupInfo(String world, String group, String node) {
        PermissionBase base = getUser(group);
        return base.getOption(node);
    }
}
