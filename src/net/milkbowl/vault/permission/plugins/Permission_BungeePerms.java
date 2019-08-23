package net.milkbowl.vault.permission.plugins;

import java.util.List;
import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.BungeePermsAPI;
import net.alpenblock.bungeeperms.platform.bukkit.BukkitPlugin;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class Permission_BungeePerms extends Permission
{

    private final String name = "BungeePerms";

    private Plugin plugin = null;
    private BungeePerms perms;

    public Permission_BungeePerms(Plugin plugin)
    {
        super();
        //check if api is present
        try
        {
            Class.forName("net.alpenblock.bungeeperms.BungeePermsAPI");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Please update BungeePerms to at least dev-#77!");
        }

        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(), BukkitPlugin.getInstance());

        // Load Plugin in case it was loaded before
        Plugin p = plugin.getServer().getPluginManager().getPlugin("BungeePerms");
        if (p != null)
        {
            this.perms = BungeePerms.getInstance();
            log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
        }
    }

    public class PermissionServerListener implements Listener
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
                    log.info(String.format("[%s][Permission] %s hooked.", plugin.getDescription().getName(), name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event)
        {
            if (perms != null)
            {
                if (event.getPlugin().getDescription().getName().equals("BungeePerms"))
                {
                    perms = null;
                    log.info(String.format("[%s][Permission] %s un-hooked.", plugin.getDescription().getName(), name));
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
    public boolean hasSuperPermsCompat()
    {
        return BungeePermsAPI.hasSuperPermsCompat();
    }

    @Override
    public boolean playerHas(String world, String player, String permission)
    {
        return BungeePermsAPI.userHasPermission(player, permission, "", world);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission)
    {
        return BungeePermsAPI.userAdd(player, permission, "", world);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission)
    {
        return BungeePermsAPI.userRemove(player, permission, "", world);
    }

    @Override
    public boolean groupHas(String world, String group, String permission)
    {
        return BungeePermsAPI.groupHas(group, permission, "", world);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission)
    {
        return BungeePermsAPI.groupAdd(group, permission, "", world);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission)
    {
        return BungeePermsAPI.groupRemove(group, permission, "", world);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group)
    {
        return BungeePermsAPI.userInGroup(player, group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group)
    {
        return BungeePermsAPI.userAddGroup(player, group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group)
    {
        return BungeePermsAPI.userRemoveGroup(player, group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player)
    {
        List<String> groups = BungeePermsAPI.userGroups(player);
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public String getPrimaryGroup(String world, String player)
    {
        return BungeePermsAPI.userMainGroup(player);
    }

    @Override
    public String[] getGroups()
    {
        List<String> groups = BungeePermsAPI.groups();
        return groups.toArray(new String[groups.size()]);
    }

    @Override
    public boolean hasGroupSupport()
    {
        return true;
    }
}
