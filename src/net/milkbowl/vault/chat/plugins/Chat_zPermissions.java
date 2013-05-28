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
import org.tyrannyofheaven.bukkit.zPermissions.ZPermissionsService;

public class Chat_zPermissions extends Chat {

    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "zPermissions";

    private final Plugin plugin;

    private ZPermissionsService service;

    public Chat_zPermissions(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
        // Load service in case it was loaded before
        if (service == null) {
            service = plugin.getServer().getServicesManager().load(ZPermissionsService.class);
            if (service != null)
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
        }
    }

    public class PermissionServerListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (service == null) {
                service = plugin.getServer().getServicesManager().load(ZPermissionsService.class);
                if (service != null)
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (service != null) {
                if (event.getPlugin().getDescription().getName().equals("zPermissions")) {
                    service = null;
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
        return service != null;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        return getPlayerInfoString(world, player, "prefix", "");
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
        setPlayerInfoString(world, player, "prefix", prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
        return getPlayerInfoString(world, player, "suffix", "");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
        setPlayerInfoString(world, player, "suffix", suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return getGroupInfoString(world, group, "prefix", "");
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        setGroupInfoString(world, group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return getGroupInfoString(world, group, "suffix", "");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        setGroupInfoString(world, group, "suffix", suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        Integer result = service.getPlayerMetadata(player, node, Integer.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player " + player + " metadata setint " + node + " " + value);
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        Integer result = service.getGroupMetadata(group, node, Integer.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions group " + group + " metadata setint " + node + " " + value);
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        Double result = service.getPlayerMetadata(player, node, Double.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player " + player + " metadata setreal " + node + " " + value);
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        Double result = service.getGroupMetadata(group, node, Double.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions group " + group + " metadata setreal " + node + " " + value);
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue) {
        Boolean result = service.getPlayerMetadata(player, node, Boolean.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player " + player + " metadata setbool " + node + " " + value);
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue) {
        Boolean result = service.getGroupMetadata(group, node, Boolean.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions group " + group + " metadata setbool " + node + " " + value);
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
        String result = service.getPlayerMetadata(player, node, String.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player " + player + " metadata set " + node + " " + quote(value));
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        String result = service.getGroupMetadata(group, node, String.class);
        if (result == null)
            return defaultValue;
        else
            return result;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions group " + group + " metadata set " + node + " " + quote(value));
    }

    private String quote(String input) {
        input = input.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
        if (input.matches(".*\\s.*"))
            return "\"" + input + "\""; // Enclose in quotes
        else
            return input;
    }

}
