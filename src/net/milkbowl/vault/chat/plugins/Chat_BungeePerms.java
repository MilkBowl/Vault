package net.milkbowl.vault.chat.plugins;

import java.util.logging.Logger;
import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.ChatColor;
import net.alpenblock.bungeeperms.Group;
import net.alpenblock.bungeeperms.User;
import net.alpenblock.bungeeperms.platform.bukkit.BukkitConfig;
import net.alpenblock.bungeeperms.platform.bukkit.BukkitPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Chat_BungeePerms extends Chat
{

    private static final Logger log = Logger.getLogger("Minecraft");
    private final String name = BungeePerms.getInstance().getPlugin().getPluginName();
    private Plugin plugin;
    private BungeePerms perms;

    public Chat_BungeePerms(Plugin plugin, Permission perms)
    {
        super(perms);
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new ChatServerListener(), BukkitPlugin.getInstance());

        // Load Plugin in case it was loaded before
        Plugin p = plugin.getServer().getPluginManager().getPlugin("BungeePerms");
        if (p != null)
        {
            this.perms = BungeePerms.getInstance();
            log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
        }
    }

    public class ChatServerListener implements Listener
    {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event)
        {
            if (perms == null)
            {
                Plugin p = event.getPlugin();
                if (p.getDescription().getName().equals("BungeePerms"))
                {
                    perms = BungeePerms.getInstance();
                    log.info(String.format("[%s][Chat] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event)
        {

            if (perms != null)
            {
                Plugin permPlugin = event.getPlugin();
                if (permPlugin.getDescription().getName().equals("BungeePerms"))
                {
                    perms = null;
                    log.info(String.format("[%s][Chat] %s unhooked.", plugin.getDescription().getName(), name));
                }
            }
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isEnabled()
    {
        return perms != null && perms.isEnabled();
    }

    @Override
    public String getPlayerPrefix(String world, String player)
    {
        User u = perms.getPermissionsManager().getUser(player);
        return u == null ? "" : u.buildPrefix();
    }

    @Override
    public void setPlayerPrefix(String world, String player, String prefix)
    {
        prefix = prefix.replaceAll("&", ChatColor.COLOR_CHAR + "");

        User u = perms.getPermissionsManager().getUser(player);
        if (u == null)
        {
            return;
        }

        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        perms.getPermissionsManager().setUserPrefix(u, prefix, config.getServername(), world);
    }

    @Override
    public String getPlayerSuffix(String world, String player)
    {
        User u = perms.getPermissionsManager().getUser(player);
        return u == null ? "" : u.buildSuffix();
    }

    @Override
    public void setPlayerSuffix(String world, String player, String suffix)
    {
        suffix = suffix.replaceAll("&", ChatColor.COLOR_CHAR + "");

        User u = perms.getPermissionsManager().getUser(player);
        if (u == null)
        {
            return;
        }

        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        perms.getPermissionsManager().setUserSuffix(u, suffix, config.getServername(), world);
    }

    @Override
    public String getGroupPrefix(String world, String group)
    {
        Group g = perms.getPermissionsManager().getGroup(group);
        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        return g == null ? "" : g.buildPrefix(config.getServername(), world);
    }

    @Override
    public void setGroupPrefix(String world, String group, String prefix)
    {
        prefix = prefix.replaceAll("&", ChatColor.COLOR_CHAR + "");

        Group g = perms.getPermissionsManager().getGroup(group);
        if (g == null)
        {
            return;
        }

        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        perms.getPermissionsManager().setGroupPrefix(g, prefix, config.getServername(), world);
    }

    @Override
    public String getGroupSuffix(String world, String group)
    {
        Group g = perms.getPermissionsManager().getGroup(group);
        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        return g == null ? "" : g.buildSuffix(config.getServername(), world);
    }

    @Override
    public void setGroupSuffix(String world, String group, String suffix)
    {
        suffix = suffix.replaceAll("&", ChatColor.COLOR_CHAR + "");

        Group g = perms.getPermissionsManager().getGroup(group);
        if (g == null)
        {
            return;
        }

        BukkitConfig config = (BukkitConfig) BungeePerms.getInstance().getConfig();
        perms.getPermissionsManager().setGroupSuffix(g, suffix, config.getServername(), world);
    }

    @Override
    public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setPlayerInfoInteger(String world, String player, String node, int value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setGroupInfoInteger(String world, String group, String node, int value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setPlayerInfoDouble(String world, String player, String node, double value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setGroupInfoDouble(String world, String group, String node, double value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setGroupInfoBoolean(String world, String group, String node, boolean value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public String getPlayerInfoString(String world, String player, String node, String defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setPlayerInfoString(String world, String player, String node, String value)
    {
        //not supported? 
        //what the hell is that?
    }

    @Override
    public String getGroupInfoString(String world, String group, String node, String defaultValue)
    {
        return defaultValue; // what the hell is that?
    }

    @Override
    public void setGroupInfoString(String world, String group, String node, String value)
    {
        //not supported? 
        //what the hell is that?
    }
}
