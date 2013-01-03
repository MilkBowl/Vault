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

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;

public class Chat_bPermissions2 extends Chat {
    private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = "bInfo";
    private Plugin plugin = null;
    private boolean hooked = false;

    public Chat_bPermissions2(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;

        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);

        // Load Plugin in case it was loaded before
        if (!hooked) {
            Plugin p = plugin.getServer().getPluginManager().getPlugin("bPermissions");
            if (p != null) {
                hooked = true;
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions2"));
            }
        }
    }

    public class PermissionServerListener implements Listener {
        Chat_bPermissions2 chat = null;

        public PermissionServerListener(Chat_bPermissions2 chat) {
            this.chat = chat;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (!hooked) {
                Plugin chat = plugin.getServer().getPluginManager().getPlugin("bPermissions");
                if (chat != null) {
                    hooked = true;
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), "bPermissions2"));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (hooked) {
                if (event.getPlugin().getDescription().getName().equals("bPermissions")) {
                    hooked = false;
                    log.info(String.format("[%s][Chat] %s un-hooked.", plugin.getDescription().getName(), "bPermissions2"));
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
        return hooked;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return ApiLayer.getValue(world, CalculableType.USER, player, "prefix");
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        ApiLayer.setValue(world, CalculableType.USER, player, "prefix", prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return ApiLayer.getValue(world, CalculableType.USER, player, "suffix");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        ApiLayer.setValue(world, CalculableType.USER, player, "suffix", suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return ApiLayer.getValue(world, CalculableType.GROUP, group, "prefix");
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return ApiLayer.getValue(world, CalculableType.GROUP, group, "suffix");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, "suffix", suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        String s = getPlayerInfoString(world, player, node, null);
        if (s == null) {
            return defaultValue;
        }
        try {
            int i = Integer.valueOf(s);
            return i;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        String s = getGroupInfoString(world, group, node, null);
        if (s == null) {
            return defaultValue;
        }
        try {
            int i = Integer.valueOf(s);
            return i;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        String s = getPlayerInfoString(world, player, node, null);
        if (s == null) {
            return defaultValue;
        }
        try {
            double d = Double.valueOf(s);
            return d;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        String s = getGroupInfoString(world, group, node, null);
        if (s == null) {
            return defaultValue;
        }
        try {
            double d = Double.valueOf(s);
            return d;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));
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
        ApiLayer.setValue(world, CalculableType.USER, player, node, String.valueOf(value));
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        String s = getGroupInfoString(world, group, node, null);
        if (s == null) {
            return defaultValue;
        } else {
            Boolean val = Boolean.valueOf(s);
            return val != null ? val : defaultValue;
        }
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, node, String.valueOf(value));;
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        String val = ApiLayer.getValue(world, CalculableType.USER, player, node);
        return (val == null || val == "BLANKWORLD" || val == "") ? defaultValue : val;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        ApiLayer.setValue(world, CalculableType.USER, player, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        String val = ApiLayer.getValue(world, CalculableType.GROUP, group, node);
        return (val == null || val == "BLANKWORLD" || val == "") ? defaultValue : val;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        ApiLayer.setValue(world, CalculableType.GROUP, group, node, value);
    }
}