package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.DroxPermsAPI;
import java.util.UUID;

public class Chat_DroxPerms extends Chat {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "DroxPerms";
    private Plugin plugin;
    private DroxPermsAPI API;

    public Chat_DroxPerms(Plugin plugin, Permission perms) {
        super(perms);
        this.plugin = plugin;

        // Load Plugin in case it was loaded before
        if (API == null) {
            DroxPerms p = (DroxPerms) plugin.getServer().getPluginManager().getPlugin("DroxPerms");
            if (p != null) {
                API = p.getAPI();
                log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), plugin);
    }

    public class PermissionServerListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (API == null) {
                Plugin permPlugin = event.getPlugin();
                if (permPlugin.getDescription().getName().equals("DroxPerms")) {
                    API = ((DroxPerms) permPlugin).getAPI();
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
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
        return true;
    }

    @Override
    public String getPlayerPrefix(String world, String player) {
        UUID uuid = plugin.getServer().getPlayer(player).getUniqueId();
	String prefix = API.getPlayerInfo(uuid, "prefix");
        if (prefix == null) {
            String prigroup = API.getPlayerGroup(uuid);
            prefix = API.getGroupInfo(prigroup, "prefix");
        }
        return prefix;
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix) {
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, "prefix", prefix);
    }

    @Override
    public String getPlayerSuffix(String world, String player) {
	UUID uuid = API.getUUIDFromName(name);
        return API.getPlayerInfo(uuid, "suffix");
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix) {
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, "suffix", suffix);
    }

    @Override
    public String getGroupPrefix(String world, String group) {
        return API.getGroupInfo(group, "prefix");
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix) {
        API.setGroupInfo(group, "prefix", prefix);
    }

    @Override
    public String getGroupSuffix(String world, String group) {
        return API.getGroupInfo(group, "suffix");
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix) {
        API.setGroupInfo(group, "suffix", suffix);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue) {
        String s = getPlayerInfoString(world, player, node, null);
        if (s == null) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value) {
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, node, String.valueOf(value));
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue) {
        String s = getGroupInfoString(world, group, node, null);
        if (s == null) {
            return defaultValue;
        }

        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value) {
        API.setGroupInfo(group, node, String.valueOf(value));
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue) {
        String s = getPlayerInfoString(world, player, node, null);
        if (s == null) {
            return defaultValue;
        }

        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value) {
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, node, String.valueOf(value));
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue) {
        String s = getGroupInfoString(world, group, node, null);
        if (s == null) {
            return defaultValue;
        }

        try {
            return Double.valueOf(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value) {
        API.setGroupInfo(group, node, String.valueOf(value));
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
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, node, String.valueOf(value));
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
        API.setGroupInfo(group, node, String.valueOf(value));
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue) {
	UUID uuid = API.getUUIDFromName(name);
        String val = API.getPlayerInfo(uuid, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value) {
	UUID uuid = API.getUUIDFromName(name);
        API.setPlayerInfo(uuid, node, value);
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue) {
        String val = API.getGroupInfo(group, node);
        return val != null ? val : defaultValue;
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value) {
        API.setGroupInfo(group, node, value);
    }

}