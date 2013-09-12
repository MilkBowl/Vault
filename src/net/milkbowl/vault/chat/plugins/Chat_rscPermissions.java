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

import ru.simsonic.rscPermissions.MainPluginClass;

public class Chat_rscPermissions extends Chat {

    private static final Logger log = Logger.getLogger("Minecraft");
    private final Plugin vault;
    private ru.simsonic.rscPermissions.MainPluginClass rscp = null;
    private ru.simsonic.rscPermissions.rscpAPI rscpAPI = null;

    public Chat_rscPermissions(Plugin plugin, Permission perm) {
        super(perm);
        this.vault = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new ChatServerListener(this), vault);
        if (rscp == null) {
            Plugin perms = plugin.getServer().getPluginManager().getPlugin("rscPermissions");
            if (perms != null && perms.isEnabled()) {
                this.rscp = (MainPluginClass) perms;
                rscpAPI = rscp.API;
                plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "rscPermissions"));
            }
        }
    }

    private class ChatServerListener implements Listener {

        private final Chat_rscPermissions bridge;
        public ChatServerListener(Chat_rscPermissions bridge) {
            this.bridge = bridge;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (bridge.rscp == null) {
                Plugin plugin = event.getPlugin();
                if (plugin.getDescription().getName().equals("rscPermissions")) {
                    bridge.rscp = (MainPluginClass) plugin;
                    bridge.rscpAPI = bridge.rscp.API;
                    log.info(String.format("[%s][Chat] %s hooked.", vault.getDescription().getName(), bridge.rscpAPI.getName()));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (bridge.rscpAPI != null) {
                if (event.getPlugin().getDescription().getName().equals(bridge.rscpAPI.getName())) {
                    bridge.rscpAPI = null;
                    bridge.rscp = null;
                    log.info(String.format("[%s][Chat] %s un-hooked.", vault.getDescription().getName(), bridge.rscpAPI.getName()));
                }
            }
        }
    }

    @Override
    public String getName() {
        return "rscPermissions";
    }

    @Override
    public boolean isEnabled() {
        return (rscpAPI != null) ? rscpAPI.isEnabled() : false;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return rscpAPI.getPlayerPrefix(world, player);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return rscpAPI.getPlayerSuffix(world, player);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return rscpAPI.getGroupPrefix(world, group);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return rscpAPI.getGroupSuffix(world, group);
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        rscpAPI.setPlayerPrefix(world, player, prefix);
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        rscpAPI.setPlayerSuffix(world, player, suffix);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        rscpAPI.setGroupPrefix(world, group, prefix);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        rscpAPI.setGroupSuffix(world, group, suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String defaultValue) {
        throw new UnsupportedOperationException("rscPermissions does not support info nodes");
    }
}
