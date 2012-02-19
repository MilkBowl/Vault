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

import net.D3GN.MiracleM4n.mChat.mChatAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Chat_mChat extends Chat {
    private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "mChat";
    private Plugin plugin = null;
    private mChatAPI mChat = null;

    public Chat_mChat(Plugin plugin, Permission permissions) {
        super(permissions);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (mChat == null) {
            Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
            if (chat != null) {
                mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Chat_mChat chat = null;

        public PermissionServerListener(Chat_mChat chat) {
            this.chat = chat;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (this.chat.mChat == null) {
                Plugin chat = plugin.getServer().getPluginManager().getPlugin("mChat");
                if (chat != null) {
                    this.chat.mChat = net.D3GN.MiracleM4n.mChat.mChat.API;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "mChat"));
                }
            }
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
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
        if (s == null) {
            return defaultValue;
        } else {
            Boolean val = Boolean.valueOf(s);
            return val != null ? val : defaultValue;
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
